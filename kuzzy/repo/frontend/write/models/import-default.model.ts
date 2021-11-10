import { SourceFile } from 'ts-morph';
import { isOutOfProject } from '../../../shared/utils/ast-sourcefile.util';

export class ImportDefault {

    identifier: string = undefined;
    importDeclarationSourceFile: SourceFile = undefined;
    moduleSpecifier: string = undefined;

    constructor(identifier: string, moduleSpecifier: string, importDeclarationSourceFile: SourceFile = undefined) {
        this.identifier = identifier;
        this.moduleSpecifier = moduleSpecifier;
        this.importDeclarationSourceFile = importDeclarationSourceFile;
    }


    get isOutOfProject(): boolean {
        return isOutOfProject(this.importDeclarationSourceFile);
    }


    get declarationPath(): string {
        return this.importDeclarationSourceFile?.getFilePath();
    }
}
