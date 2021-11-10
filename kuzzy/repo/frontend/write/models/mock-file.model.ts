import { ImportLine } from './import-line.model';
import { ImportLineService } from '../services/import-line.service';
import { MockEntity } from '../../db/entities/mock.entity';

export class MockFile {

    code = undefined;
    fileUTPath: string = undefined;
    importLines: ImportLine[] = [];
    importsCode: string = undefined;
    mockFilePath: string = undefined;
    mock: MockEntity = undefined;

    constructor(mockEntity: MockEntity) {
        this.fileUTPath = mockEntity.fileUTPath;
        this.mockFilePath = mockEntity.path;
        this.mock = mockEntity;
    }


    setImportLines(): void {
        ImportLineService.addImport(this.mock.importDefaultClassUT, this.importLines);
        if (Array.isArray(this.mock?.mocksDependencies)) {
            for (const mockDependency of this.mock.mocksDependencies) {
                ImportLineService.addImport(mockDependency.importDefaultClassUT, this.importLines);
                ImportLineService.addImport(mockDependency.importDefaultMockFile, this.importLines);
            }
        }
    }


    setCode(): void {
        this.importsCode = ImportLineService.getCode(this.importLines);
        this.code = this.importsCode;
        this.code = `${this.code}${this.mock.code}\n`;
    }

}
