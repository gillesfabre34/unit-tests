import { It } from './it.model';
import { DescribeClass } from './describe-class.model';
import { TestCaseEntity } from '../../db/entities/test-case.entity';
import { tab } from '../../../shared/utils/strings.util';

export class DescribeMethod {

    code = '';
    describeClass: DescribeClass = undefined;
    its: It[] = [];
    itsCode = '';
    methodName: string = undefined;
    testCaseEntities: TestCaseEntity[] = [];
    title: string = undefined;


    constructor(describeClass: DescribeClass, testCaseEntity: TestCaseEntity) {
        this.describeClass = describeClass;
        this.testCaseEntities.push(testCaseEntity);
        this.methodName = testCaseEntity?.methodOrFunctionName;
    }


    setCode(): void {
        this.title = this.methodName;
        this.setItsCode();
        let code = `${tab}describe('${this.title}', () => {\n`;
        code = `${code}${this.itsCode}`;
        code = `${code}${tab}});\n`
        this.code = code;
    }


    private setItsCode(): void {
        this.itsCode = '';
        for (const it of this.its) {
            this.itsCode = `${this.itsCode}${it.code}\n`;
        }
    }

}
