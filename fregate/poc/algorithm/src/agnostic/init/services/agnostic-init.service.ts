import { GLOBALS } from '../constants/globals.const';
import { FolderStats } from '../../reports/dashboard/models/folder-stats.model';
import * as fs from 'fs-extra';
import * as chalk from 'chalk';
import { FileStats } from '../../reports/dashboard/models/file-stats.model';
import { MethodStats } from '../../reports/dashboard/models/method-stats.model';
import { GlobalStats } from '../../reports/dashboard/models/global-stats.model';
import { ReportStats } from '../../reports/dashboard/models/report-stats.model';
import { Project } from 'ts-morph';
import { AgnosticSourceFileService } from '../../tools/services/agnostic-sourcefile.service';
import { AgnosticSutFolder } from '../../test-suites/sut/models/agnostic-sut-folder.model';
import { DEBUG } from '../constants/debug.const';
import { copySync, neoryxPath } from '../../tools/utils/file-system.util';

export abstract class AgnosticInitService {

    abstract getClassDeclarations(sourceFile: any): any[];
    abstract getClassName(classDeclaration: any): string;
    abstract getFilePath(sourceFile: any): string;
    abstract getFunctionDeclarations(sourceFile: any): any[];
    abstract getGlobalStats(project: any): GlobalStats;
    abstract getMethodDeclarations(classDeclaration: any): any[];
    abstract getMethodName(methodDeclaration: any): string;
    abstract getNumberOfMethodStatements(methodDeclaration: any): number;
    abstract getNumberOfSourceFileStatements(sourceFile: any): number;
    abstract getProject(): any;
    abstract getSourceFiles(project: any): any[];
    abstract isFileToTest(sourceFile: any): boolean;
    abstract newSourceFileService(): AgnosticSourceFileService;
    abstract newSutFolder(): AgnosticSutFolder;
    abstract removeTestFiles(project: any): any;
    abstract setUnitTestFrameworks(): void;


    async start(path: string): Promise<AgnosticSutFolder> {
        GLOBALS.fileOrDirPathToTest = path;
        await this.initContext();
        this.initStats();
        return this.newSutFolder().generate(path);
    }


    private async initContext(): Promise<void> {
        GLOBALS.start = Date.now();
        GLOBALS.historicPath = `${GLOBALS.appRoot}/src/agnostic/reports/historic/archives/historic.json`;
        this.createProject();
        this.setUnitTestFrameworks();
    }


    private createProject(): void {
        const project: any = this.getProject();
        GLOBALS.project = this.removeTestFiles(project);
    }


    private initStats(): void {
        GLOBALS.stats = new ReportStats();
        GLOBALS.stats.projectGlobalStats = this.getGlobalStats(GLOBALS.project);
        GLOBALS.stats.sutGlobalStats = this.getSutGlobalStats();
        GLOBALS.stats.report = this.initReport();
    }


    getSutGlobalStats(): GlobalStats {
        return this.getGlobalStats(this.getSutProject());
    }


    initReport(): FolderStats {
        const sourceFiles: any[] = this.getSourceFiles(this.getSutProject());
        const folderStats = new FolderStats();
        for (const sourceFile of sourceFiles) {
            const numberOfStatements: number = this.getNumberOfSourceFileStatements(sourceFile) ?? 0;
            if (numberOfStatements > 0 && this.isFileToTest(sourceFile)) {
                folderStats.filesStats.push(this.newFileStats(sourceFile, numberOfStatements));
            }
        }
        return folderStats;
    }


    private newFileStats(sourceFile: any, numberOfStatements: number): FileStats {
        const fileStats = new FileStats(this.getFilePath(sourceFile), numberOfStatements);
        for (const classDeclaration of this.getClassDeclarations(sourceFile)) {
            for (const methodDeclaration of this.getMethodDeclarations(classDeclaration)) {
                fileStats.methodsStats.push(this.newMethodStats(methodDeclaration, this.getClassName(classDeclaration)));
            }
        }
        for (const functionDeclaration of this.getFunctionDeclarations(sourceFile)) {
            fileStats.functionsStats.push(this.newMethodStats(functionDeclaration, undefined, true));
        }
        return fileStats;
    }


    private newMethodStats(methodDeclaration: any, className: string, isFunction = false): MethodStats {
        return new MethodStats(this.getMethodName(methodDeclaration), className, isFunction, this.getNumberOfMethodStatements(methodDeclaration));
    }


    getSutProject(): Project {
        return this.getSubFolderProject(GLOBALS.fileOrDirPathToTest);
    }


    private getSubFolderProject(folderPath: string, project: Project = new Project()): Project {
        const filesOrSubFoldersPaths: string[] = fs.readdirSync(folderPath).map(p => `${folderPath}/${p}`);
        for (const path of filesOrSubFoldersPaths) {
            if (fs.statSync(path).isDirectory()) {
                this.getSubFolderProject(path, project);
            } else if (this.newSourceFileService().isSutFilePath(path)) {
                project.addSourceFileAtPath(path);
            }
        }
        return project;
    }
}
