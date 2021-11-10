import { DescribeClass } from './describe-class.model';
import { DescribeClassService } from '../services/describe-class.service';
import { ImportLine } from './import-line.model';
import { ImportLineService } from '../services/import-line.service';
import { TestCaseEntity } from '../../db/entities/test-case.entity';
import { EnumUTEntity } from '../../db/entities/enum-ut.entity';
import { MockEntity } from '../../db/entities/mock.entity';
import { TestFileService } from '../services/test-file.service';
import { writeFile } from '../../../shared/utils/file-system.util';

export class TestFile {

    code: string = undefined;
    describeClasses: DescribeClass[] = [];
    fileUTPath: string = undefined;
    importLines: ImportLine[] = [];
    testCaseEntities: TestCaseEntity[] = [];
    testFileName: string = undefined;
    testFilePath: string = undefined;

    constructor(testCaseEntity: TestCaseEntity) {
        this.fileUTPath = testCaseEntity.fileUTPath;
        this.testCaseEntities = [testCaseEntity];
    }


    addTestCaseEntity(testCaseEntity: TestCaseEntity): void {
        this.testCaseEntities.push(testCaseEntity);
    }


    addDescribeClasses(): void {
        let classNames: Set<string> = new Set<string>();
        for (const testCaseEntity of this.testCaseEntities) {
            if (classNames.has(testCaseEntity.mock.classUT.name)) {
                DescribeClassService.addTestCase(this, testCaseEntity);
            } else {
                classNames.add(testCaseEntity.mock.classUT.name);
                DescribeClassService.generateDescribeClass(this, testCaseEntity);
            }
            this.addEnumsImports(testCaseEntity);
            this.addMockDependenciesImports(testCaseEntity);
        }
    }


    private addEnumsImports(testCaseEntity: TestCaseEntity): void {
        const enumUTEntity: EnumUTEntity = testCaseEntity.returnedValue?.declaration?.enumUT;
        if (enumUTEntity) {
            ImportLineService.addImport(enumUTEntity.importDefault, this.importLines);
        }
    }


    private addMockDependenciesImports(testCaseEntity: TestCaseEntity): void {
        const mockEntity: MockEntity = testCaseEntity.returnedValue?.declaration?.mock;
        if (mockEntity) {
            ImportLineService.addImport(mockEntity.importDefaultMockFile, this.importLines);
        }
    }



    setProperties(): void {
        this.setTestFileName();
        this.setTestFilePath();
        this.setImportLines();
        this.setCode();
    }


    private setTestFileName(): void {
        this.testFileName = TestFileService.testFileName(this.fileUTPath);
    }


    private setTestFilePath(): void {
        this.testFilePath = TestFileService.testFilePath(this.fileUTPath);
    }


    private setImportLines(): void {
        for (const testCaseEntity of this.testCaseEntities) {
            testCaseEntity.setImportLines();
            ImportLineService.addImportLines(this.importLines, testCaseEntity.importLines);
        }
    }

    setCode(): void {
        let code = ImportLineService.getCode(this.importLines);
        for (const describeClass of this.describeClasses) {
            describeClass.setCode();
            code = `${code}${describeClass.code}\n`;
        }
        this.code = code;
    }


    async writeFile(): Promise<void> {
        await writeFile(this.testFilePath, this.code ?? '');
    }

}
