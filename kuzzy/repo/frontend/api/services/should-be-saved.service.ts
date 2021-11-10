import { SourceFile } from 'ts-morph';
import { flat } from '../../../shared/utils/arrays.util';

export class ShouldBeSavedService {


    /**
     * Returns sourceFiles which should be saved because they contain classes, enums, or functions with statements
     * @param sourceFiles
     */
    static shouldBeSaved(sourceFiles: SourceFile[]): SourceFile[] {
        return sourceFiles.filter(s => this.isSourceFileToSAve(s));
    }


    private static isSourceFileToSAve(sourceFile: SourceFile): boolean {
        return flat(sourceFile.getClasses()).length > 0
            || sourceFile.getFunctions().map(c => c.getDescendantStatements()).length > 0
            || sourceFile.getEnums().length > 0;
    }
}
