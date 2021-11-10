import { ImportLine } from '../models/import-line.model';
import { ImportDefault } from '../models/import-default.model';
import { mergeWithoutDuplicates } from '../../../shared/utils/arrays.util';

export class ImportLineService {


    static getCode(importLines: ImportLine[] = []): string {
        let importsCode = '';
        for (const importLine of importLines) {
            importsCode = `${importsCode}${importLine.code}`;
        }
        importsCode = `${importsCode}\n`;
        return importsCode;
    }


    static addImport(importDefault: ImportDefault, importLines: ImportLine[]): void {
        const importLine: ImportLine = importLines.find(i => i.moduleSpecifier === importDefault.moduleSpecifier);
        if (importLine) {
            if (!importLine.identifiers.includes(importDefault.identifier)) {
                importLine.identifiers.push(importDefault.identifier);
            }
        } else {
            importLines.push(new ImportLine(importDefault.moduleSpecifier, [importDefault.identifier]));
        }
    }


    static addImportLines(currentImportLines: ImportLine[] = [], newImportLines: ImportLine[] = []): void {
        for (const newImportLine of newImportLines) {
            this.addImportLine(currentImportLines, newImportLine);
        }
    }


    static addImportLine(currentImportLines: ImportLine[], newImportLine: ImportLine): void {
        const importLine: ImportLine = currentImportLines.find(c => c?.moduleSpecifier === newImportLine.moduleSpecifier);
        if (importLine) {
            importLine.identifiers = mergeWithoutDuplicates(importLine.identifiers, newImportLine.identifiers);
        } else {
            currentImportLines.push(newImportLine);
        }
    }

}
