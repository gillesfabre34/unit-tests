import { AgnosticSutClass } from './agnostic-sut-class.model';
import { AgnosticSutFolder } from './agnostic-sut-folder.model';
import { AgnosticSutMethod } from './agnostic-sut-method.model';
import { MethodStatsService } from '../../../reports/dashboard/services/method-stats.service';
import { Bug } from '../../../reports/core/bugs/models/bug.model';
import { AgnosticSutFileService } from '../services/agnostic-sut-file.service';
import { BugType } from '../../../reports/core/bugs/enums/bug-type.enum';

export abstract class AgnosticSutFile {

    code: string = undefined;
    flaggedSourceFile: any = undefined;
    path: string = undefined;
    sourceFile: any = undefined;
    sutClasses: AgnosticSutClass[] = [];
    sutFolder: AgnosticSutFolder = undefined;
    sutFunctions: AgnosticSutMethod[] = [];
    abstract getClassDeclarations(sourceFile: any): any[];
    abstract getFunctionDeclarations(sourceFile: any): any[];
    abstract getSourceFile(path: string): any;
    abstract newSutClass(): AgnosticSutClass;ç
    abstract newSutFileService(): AgnosticSutFileService;
    abstract newSutFunction(): AgnosticSutMethod;


    generate(sutFolder: AgnosticSutFolder, filePath: string): AgnosticSutFile {
        this.path = filePath;
        this.sourceFile = this.getSourceFile(filePath);
        this.addClassDeclarations();
        this.addFunctionDeclarations();
        return this;
    }


    private addClassDeclarations(): void {
        const classDeclarations: any[] = this.getClassDeclarations(this.sourceFile) ?? [];
        for (const classDeclaration of classDeclarations) {
            this.sutClasses.push(this.newSutClass().generate(this, classDeclaration));
        }
    }


    private addFunctionDeclarations(): void {
        const functionDeclarations: any[] = this.getFunctionDeclarations(this.sourceFile) ?? [];
        for (const functionDeclaration of functionDeclarations) {
            this.sutFunctions.push(this.newSutFunction().generateFunction(this, functionDeclaration));
        }
    }
g

    setCode(): void {
        this.newSutFileService().setCode();
    }


    addConstraints(): void {
        for (const sutClass of this.sutClasses) {
            sutClass.addConstraints();
        }
        for (const sutFunction of this.sutFunctions) {
            sutFunction.addConstraints();
        }
    }


    async addTestSuites(): Promise<void> {
        for (const sutClass of this.sutClasses) {
            await sutClass.addTestSuites();
        }
        for (const sutFunction of this.sutFunctions) {
            MethodStatsService.addBug(new Bug(BugType.FUNCTIONS_NOT_YET_IMPLEMENTED), this.path, sutFunction.name, undefined, true);
            // TODO
            // await sutFunction.addTestSuites();
        }
    }


    async write(): Promise<void> {
        await this.newSutFileService().generate(this);
    }

}
