import { DescribeMethod } from './describe-method.model';
import { TestFile } from './test-file.model';
import { ImportLine } from './import-line.model';
import { ImportLineService } from '../services/import-line.service';
import { TestCaseEntity } from '../../db/entities/test-case.entity';

export class DescribeClass {

    className: string = undefined;
    code: string = undefined;
    describeMethods: DescribeMethod[] = [];
    describeMethodsCode = '';
    importsCode: string = undefined;
    importLines: ImportLine[] = [];
    testCaseEntities: TestCaseEntity[] = [];
    testFile: TestFile = undefined;


    constructor(testFile: TestFile, testCaseEntity: TestCaseEntity) {
        this.testFile = testFile;
        this.testCaseEntities.push(testCaseEntity);
        this.className = testCaseEntity?.mock?.classUT?.name;
    }


    setCode(): void {
        this.importsCode = ImportLineService.getCode(this.importLines);
        this.setDescribeMethodsCode();
        let code = this.importsCode;
        code = `${code}describe('${this.className}', () => {\n\n`;
        code = `${code}${this.describeMethodsCode}`;
        code = `${code}});\n`;
        this.code = code;
    }


    private setDescribeMethodsCode(): void {
        this.describeMethodsCode = '';
        for (const describeMethod of this.describeMethods) {
            describeMethod.setCode();
            this.describeMethodsCode = `${this.describeMethodsCode}${describeMethod.code}\n`;
        }
    }


}
