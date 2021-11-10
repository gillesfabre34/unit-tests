import { AgnosticSourceFileService } from '../../../../agnostic/tools/services/agnostic-sourcefile.service';
import { Project, SourceFile } from 'ts-morph';

export class SourceFileService extends AgnosticSourceFileService {

    getSourceFile(path: string): SourceFile {
        const project = new Project();
        project.addSourceFileAtPath(path);
        return project.getSourceFile(path);
    }


    hasStatements(sourceFile: SourceFile): boolean {
        return !!sourceFile.getClasses().find(c => c.getDescendantStatements().length > 0);
    }


    hasCorrectExtension(sourceFile: SourceFile): boolean {
        return sourceFile?.getFilePath()?.slice(-3) === '.ts';
    }


    isTestFile(sourceFile: SourceFile): boolean {
        return sourceFile?.getFilePath()?.slice(-8) === '.spec.ts';
    }


}
