import { ClassDeclaration, FunctionDeclaration, Project, SourceFile } from 'ts-morph';
import { GLOBALS } from '../../../../../agnostic/init/constants/globals.const';
import { SutClass } from './sut-class.model';
import { SutMethod } from './sut-method.model';
import { SutFileService } from '../services/sut-file.service';
import { AgnosticSutFile } from '../../../../../agnostic/test-suites/sut/models/agnostic-sut-file.model';
import { AgnosticSutFileService } from '../../../../../agnostic/test-suites/sut/services/agnostic-sut-file.service';

export class SutFile extends AgnosticSutFile {

    describesClassesCode: string = '';
    importsCode: string = '';
    sutClasses: SutClass<any>[] = [];

    newSutFileService(): AgnosticSutFileService {
        return new SutFileService();
    }


    async addCrashValues(): Promise<void> {
        return Promise.resolve(undefined);
    }


    getClassDeclarations(sourceFile: SourceFile): ClassDeclaration[] {
        return sourceFile.getClasses();
    }


    getFunctionDeclarations(sourceFile: SourceFile): FunctionDeclaration[] {
        return sourceFile.getFunctions();
    }


    getSourceFile(path: string): SourceFile {
        const project: Project = GLOBALS.project;
        project.addSourceFileAtPath(path);
        return project.getSourceFile(path);
    }


    newSutClass(): SutClass<any> {
        return new SutClass();
    }


    newSutFunction(): SutMethod<any> {
        return new SutMethod();
    }

}
