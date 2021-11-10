import { GLOBALS } from '../../../../agnostic/init/constants/globals.const';
import { ClassDeclaration, FunctionDeclaration, MethodDeclaration, Project, SourceFile } from 'ts-morph';
import { SutFolder } from '../../test-suites/sut/models/sut-folder.model';
import { AgnosticInitService } from '../../../../agnostic/init/services/agnostic-init.service';
import { GlobalStats } from '../../../../agnostic/reports/dashboard/models/global-stats.model';
import { sum } from '../../../../agnostic/tools/utils/numbers.util';
import { SourceFileService } from '../../tools/services/sourcefile.service';
import * as chalk from 'chalk';

export class InitService extends AgnosticInitService {

    getClassDeclarations(sourceFile: SourceFile): ClassDeclaration[] {
        return sourceFile.getClasses();
    }


    getClassName(classDeclaration: ClassDeclaration): string {
        return classDeclaration.getName();
    }


    getFilePath(sourceFile: SourceFile): string {
        return sourceFile.getFilePath();
    }

    // TODO : check when there are different tsConfig.json files
    getConfigPath(projectPath: string): string {
        return `${projectPath}/tsconfig.json`;
    }


    getFunctionDeclarations(sourceFile: SourceFile): FunctionDeclaration[] {
        return sourceFile.getFunctions();
    }


    getMethodDeclarations(classDeclaration: ClassDeclaration): MethodDeclaration[] {
        return classDeclaration.getMethods();
    }


    getMethodName(methodDeclaration: MethodDeclaration): string {
        return methodDeclaration.getName();
    }


    getSourceFiles(project: Project): SourceFile[] {
        return project.getSourceFiles();
    }


    isFileToTest(sourceFile: SourceFile): boolean {
        return !this.isTestFile(sourceFile.getFilePath());
    }


    private isTestFile(path: string): boolean {
        return path.slice(-7) === 'spec.ts';
    }


    getGlobalStats(project: Project): GlobalStats {
        const sourceFiles: SourceFile[] = project.getSourceFiles();
        let functions = 0;
        let methods = 0;
        let statements = 0;
        for (const sourceFile of sourceFiles) {
            functions += sourceFile.getFunctions().length;
            methods += sum(sourceFile.getClasses().map(c => c.getMethods().length));
            statements += sum(sourceFile.getClasses().map(c => c.getDescendantStatements().length));
            statements -= this.getNumberOfConstructorStatements(sourceFile)
        }
        return new GlobalStats(sourceFiles?.length, functions, methods, statements);
    }



    setUnitTestFrameworks(): void {
        GLOBALS.unitTestFrameworks = ['Jasmine', 'Jest'];
    }


    getProject(): Project {
        return new Project({
            tsConfigFilePath: GLOBALS.configFilePath,
        });
    }


    removeTestFiles(project: Project): any {
        const testFiles : SourceFile[] = project.getSourceFiles().filter(s => s.getFilePath().slice(-8) === '.spec.ts');
        for (const sourceFile of testFiles) {
            project.removeSourceFile(sourceFile);
        }
        return project;
    }


    newSutFolder(): SutFolder {
        return new SutFolder();
    }


    // TODO : test constructors too ?
    getNumberOfSourceFileStatements(sourceFile: SourceFile): number {
        const methodsAndFunctionsStatements: number = sum(sourceFile.getClasses().map(c => c.getDescendantStatements().length)) + sum(sourceFile.getFunctions().map(c => c.getDescendantStatements().length))
        const constructorStatements: number = this.getNumberOfConstructorStatements(sourceFile);
        return methodsAndFunctionsStatements - constructorStatements;
    }


    getNumberOfMethodStatements(methodDeclaration: MethodDeclaration): number {
        return methodDeclaration.getDescendantStatements().length;
    }


    getNumberOfConstructorStatements(sourceFile: SourceFile): number {
        return sum(sourceFile.getClasses().map(c => sum(c.getConstructors().map(co => co.getDescendantStatements().length))));
    }


    newSourceFileService(): SourceFileService {
        return new SourceFileService();
    }

}
