import { FlagStatement } from '../models/flag-statement.model';
import { GLOBALS } from '../constants/globals.const';
import * as fs from 'fs-extra';
import { AgnosticMethod } from '../../test-suites/sut/models/agnostic-method.model';
import { AgnosticSutFolder } from '../../test-suites/sut/models/agnostic-sut-folder.model';
import { AgnosticSutFile } from '../../test-suites/sut/models/agnostic-sut-file.model';
import { getFilename, neoryxFlagPath } from '../../tools/utils/file-system.util';
import * as chalk from 'chalk';
import { SourceFile } from 'ts-morph';
import { AgnosticBranchesService } from './agnostic-branches.service';
import { AgnosticSourceFileService } from '../../tools/services/agnostic-sourcefile.service';
import { copySync } from 'fs-extra';
import { IgnoreService } from './ignore.service';
import { CopyProjectService } from './copy-project.service';

export abstract class AgnosticFlagsService {

    abstract addImportDeclarations(sourceFile: any): void;
    abstract addToProject(filePath: any, project: any): void;
    abstract duplicateStaticMethods(classDeclaration: any): void;
    abstract getClassDeclarations(sourceFile: any): any[];
    abstract getDescendantStatements(methodDeclaration: any): any[];
    abstract getFlaggedMethod(className: string, methodName: string, flaggedSourceFile: any): AgnosticMethod;
    abstract createFlaggedProject(): any;
    abstract getFunctionDeclarations(sourceFile: any): any[];
    abstract getMethodDeclarations(classDeclaration: any): any[];
    abstract getPath(sourceFile: any): string;
    abstract getSourceFile(filePath: string, project: any): any;
    abstract getSourceFilePathOfMethod(methodDeclaration: any): string;
    abstract getSourceFiles(project: any): any[];
    abstract isAbstractMethod(methodDeclaration: any): boolean;
    abstract isFileToFlag(filePath: string): boolean;
    abstract newBranchesService(): AgnosticBranchesService;
    abstract newSourceFileService(): AgnosticSourceFileService;
    abstract async saveSourceFile(sourceFile: any): Promise<void>;
    abstract async setFlagToStatement(methodDeclaration: any, statement: any): Promise<void>;
    abstract async wrapMethod(methodDeclaration: any): Promise<void>;


    async flagProject(gitChanges: string[]): Promise<AgnosticSutFolder> {
        console.log(chalk.yellowBright('Flag files...'));
        const flagFolderWasReset: boolean = this.flagFolderWasReset();
        const pathsFilesToFlag: string[] = await this.cloneFilesToFlag(gitChanges, flagFolderWasReset);
        for (const pathFileToFlag of pathsFilesToFlag) {
            await this.flagSourceFile(pathFileToFlag);
        }
        return this.addFlaggedSourceFilesToSutFolder(GLOBALS.mainSutFolder);
    }


    private flagFolderWasReset(): boolean {
        const flagFolder: string = `${GLOBALS.neoryxPath}/flag`;
        if (!fs.existsSync(flagFolder)) {
            return true;
        }
        return fs.readdirSync(flagFolder, 'utf-8').length === 1;
    }


    private async cloneFilesToFlag(gitChanges: string[], flagFolderWasReset: boolean): Promise<string[]> {
        let pathsFilesToFlag: string[] = [];
        if (!flagFolderWasReset) {
            this.removePreviousFlaggedFiles(gitChanges);
            GLOBALS.flaggedProject = await this.createFlaggedProject();
            // throw Error('zzz')
            for (const filePath of gitChanges) {
                if (!IgnoreService.isIgnored(filePath)) {
                    fs.removeSync(neoryxFlagPath(filePath));
                    copySync(filePath, neoryxFlagPath(filePath));
                    if(this.isFileToFlag(filePath)) {
                        this.addToProject(neoryxFlagPath(filePath), GLOBALS.flaggedProject);
                        pathsFilesToFlag.push(neoryxFlagPath(filePath))
                    }
                }
            }
        } else {
            await CopyProjectService.clone(GLOBALS.projectPath, true);
            GLOBALS.flaggedProject = await this.createFlaggedProject();
            pathsFilesToFlag = this.getSourceFiles(GLOBALS.flaggedProject).map(s => this.getPath(s));
        }
        return pathsFilesToFlag;
    }


    private removePreviousFlaggedFiles(filePaths: string[]): void {
        for (const filePath of filePaths) {
            fs.removeSync(neoryxFlagPath(filePath));
        }
    }


    async flagSourceFile(filePath: string): Promise<void> {
        if (['flags.service.ts', 'agnostic-flags.service.ts', 'flag-statement.model.ts', 'wrapper-decorator.service'].includes(getFilename(filePath))) {
            return;
        }
        const flaggedSourceFile: SourceFile = this.getSourceFile(filePath, GLOBALS.flaggedProject);
        this.addImportDeclarations(flaggedSourceFile)
        await this.flagClasses(flaggedSourceFile);
        await this.saveSourceFile(flaggedSourceFile);
    }


    private async flagClasses(sourceFile: any): Promise<void> {
        for (const classDeclaration of this.getClassDeclarations(sourceFile)) {
            await this.flagMethods(classDeclaration);
            this.duplicateStaticMethods(classDeclaration);
        }
    }


    private async flagMethods(classDeclaration: any): Promise<void> {
        for (const methodDeclaration of this.getMethodDeclarations(classDeclaration)) {
            this.transformBranchesInStatements(methodDeclaration);
            if (!this.isAbstractMethod(methodDeclaration)) {
                await this.flagStatements(methodDeclaration);
                await this.wrapMethod(methodDeclaration);
            }
        }
    }


    private transformBranchesInStatements(methodDeclaration: any): void {
        if (this.newSourceFileService().isInSutFolder(this.getSourceFilePathOfMethod(methodDeclaration))) {
            this.newBranchesService().transformBranchesInStatements(methodDeclaration);
        }
    }


    async flagStatements(methodDeclaration: any): Promise<void> {
        for (const statement of this.getDescendantStatements(methodDeclaration)) {
            await this.setFlagToStatement(methodDeclaration, statement);
        }
    }


    private addFlaggedSourceFilesToSutFolder(sutFolder: AgnosticSutFolder): AgnosticSutFolder {
        for (const child of sutFolder.children) {
            this.addFlaggedSourceFilesToSutFolder(child);
        }
        for (const sutFile of sutFolder.sutFiles) {
            sutFile.flaggedSourceFile = this.getSourceFile(neoryxFlagPath(sutFile.path), GLOBALS.flaggedProject);
            this.addFlaggedMethodsToSutFile(sutFile)
        }
        return sutFolder;
    }


    private addFlaggedMethodsToSutFile(sutFile: AgnosticSutFile): AgnosticSutFile {
        for (const sutClass of sutFile.sutClasses) {
            for (const sutMethod of sutClass.sutMethods) {
                sutMethod.agnosticFlaggedMethod = this.getFlaggedMethod(sutClass.name, sutMethod.name, sutFile.flaggedSourceFile)
            }
        }
        return sutFile;
    }


    static flag(flagStatement: FlagStatement) {
        console.error('Please override flag() method in the concrete FlagsService');
    }

}
