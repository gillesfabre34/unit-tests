import { GLOBALS } from '../../init/constants/globals.const';
import { originalFlagPath } from '../utils/file-system.util';

export abstract class AgnosticSourceFileService {

    abstract getSourceFile(path: string): any;
    abstract hasCorrectExtension(sourceFile: any): boolean;
    abstract hasStatements(sourceFile: any): boolean;
    abstract isTestFile(sourceFile: any): boolean;


    isSutFile(sourceFile: any): boolean {
        return sourceFile && !this.isTestFile(sourceFile) && this.hasCorrectExtension(sourceFile) && this.hasStatements(sourceFile);
    }


    isSutFilePath(path: string): boolean {
        return this.isSutFile(this.getSourceFile(path));
    }


    isInSutFolder(filePath: string): boolean {
        return originalFlagPath(filePath).includes(GLOBALS.sutPath);
    }
}
