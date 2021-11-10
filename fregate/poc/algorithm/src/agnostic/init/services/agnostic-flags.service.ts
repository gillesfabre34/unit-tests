import { FlagStatement } from '../models/flag-statement.model';
import { GLOBALS } from '../constants/globals.const';
import * as fs from 'fs-extra';
import { AgnosticMethod } from '../../test-suites/sut/models/agnostic-method.model';
import { AgnosticSutFolder } from '../../test-suites/sut/models/agnostic-sut-folder.model';
import { AgnosticSutFile } from '../../test-suites/sut/models/agnostic-sut-file.model';
import { copySync, neoryxPath } from '../../tools/utils/file-system.util';
import { DEBUG } from '../constants/debug.const';

export abstract class AgnosticFlagsService {

    abstract addToProject(filePath: any, project: any): void;
    abstract addImportDeclarations(sourceFile: any): void;
    abstract duplicateStaticMethods(classDeclaration: any): void;
    abstract getClassDeclarations(sourceFile: any): any[];
    abstract getDescendantStatements(methodDeclaration: any): any[];
    abstract getFlaggedMethod(className: string, methodName: string, flaggedSourceFile: any): AgnosticMethod;
    abstract getFlaggedProject(): any;
    abstract getFunctionDeclarations(sourceFile: any): any[];
    abstract getMethodDeclarations(classDeclaration: any): any[];
    abstract getPath(sourceFile: any): string;
    abstract getSourceFile(filePath: string, project: any): any;
    abstract getSourceFiles(project: any): any[];
    abstract isAbstractMethod(methodDeclaration: any): boolean;
    abstract async saveSourceFile(sourceFile: any): Promise<void>;
    abstract async setFlagToStatement(methodDeclaration: any, statement: any): Promise<void>;
    abstract async wrapMethod(methodDeclaration: any): Promise<void>;


    async copyProject(): Promise<void> {
        if (DEBUG.clearNeoryxFolder) {
            fs.removeSync(`${GLOBALS.appRoot}/neoryx/src`);
            await this.cloneProject();
        }
        GLOBALS.flaggedConfigPath = await this.copyFileToNeoryxFolder(GLOBALS.configPath);
        GLOBALS.flaggedProject = this.getFlaggedProject();
        for (const sourceFile of this.getSourceFiles(GLOBALS.project)) {
            const flaggedFilePath: string = await this.copyFileToNeoryxFolder(this.getPath(sourceFile));
            this.addToProject(flaggedFilePath, GLOBALS.flaggedProject);
        }
        await this.copyTemplates();
    }


    private async cloneProject(): Promise<void> {
        await copySync(`${GLOBALS.appRoot}/src`, neoryxPath(`${GLOBALS.appRoot}/src/tests`));
        // await copySync(`${GLOBALS.appRoot}/tsconfig.json`, neoryxPath(`${GLOBALS.appRoot}/src/tests/tsconfig.json`));
        await copySync(`${GLOBALS.appRoot}/package.json`, neoryxPath(`${GLOBALS.appRoot}/package.json`));
    }


    private async copyTemplates(): Promise<void> {
        await copySync(`${GLOBALS.appRoot}/src/agnostic/reports/dashboard/templates`, `${GLOBALS.appRoot}/neoryx/reports`)
    }


    private async copyFileToNeoryxFolder(originalPath: string): Promise<string> {
        const targetPath = neoryxPath(originalPath);
        await copySync(originalPath, targetPath);
        return targetPath;
    }


    async flagProject(): Promise<AgnosticSutFolder> {
        for (const sourceFile of this.getSourceFiles(GLOBALS.project)) {
            await this.addFlagsAndWrappers(sourceFile);
        }
        return this.addFlaggedSourceFilesToSutFolder(GLOBALS.mainSutFolder);
    }


    async addFlagsAndWrappers(sourceFile: any): Promise<void> {
        const flaggedFilePath: string = neoryxPath(this.getPath(sourceFile));
        const flaggedSourceFile = this.getSourceFile(flaggedFilePath, GLOBALS.flaggedProject);
        this.addImportDeclarations(flaggedSourceFile)
        await this.addFlagsAndWrappersToSourceFile(flaggedSourceFile);
        await this.saveSourceFile(flaggedSourceFile);
    }


    private async addFlagsAndWrappersToSourceFile(sourceFile: any): Promise<void> {
        for (const classDeclaration of this.getClassDeclarations(sourceFile)) {
            await this.addFlagsAndWrappersToClass(classDeclaration);
            this.duplicateStaticMethods(classDeclaration);
        }
    }


    private async addFlagsAndWrappersToClass(classDeclaration: any): Promise<void> {
        for (const methodDeclaration of this.getMethodDeclarations(classDeclaration)) {
            if (!this.isAbstractMethod(methodDeclaration)) {
                await this.addFlagsToMethod(methodDeclaration);
                await this.wrapMethod(methodDeclaration);
            }
        }
    }


    async addFlagsToMethod(methodDeclaration: any): Promise<void> {
        for (const statement of this.getDescendantStatements(methodDeclaration)) {
            await this.setFlagToStatement(methodDeclaration, statement);
        }
    }


    private addFlaggedSourceFilesToSutFolder(sutFolder: AgnosticSutFolder): AgnosticSutFolder {
        for (const child of sutFolder.children) {
            this.addFlaggedSourceFilesToSutFolder(child);
        }
        for (const sutFile of sutFolder.sutFiles) {
            sutFile.flaggedSourceFile = this.getSourceFile(neoryxPath(sutFile.path), GLOBALS.flaggedProject);
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
