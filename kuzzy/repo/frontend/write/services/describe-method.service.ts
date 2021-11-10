import { DescribeClass } from '../models/describe-class.model';
import { DescribeMethod } from '../models/describe-method.model';
import { It } from '../models/it.model';
import { TestCaseEntity } from '../../db/entities/test-case.entity';

export class DescribeMethodService {


    static generateDescribeMethod(describeClass: DescribeClass, testCaseEntity: TestCaseEntity): void {
        const describeMethod = new DescribeMethod(describeClass, testCaseEntity);
        describeClass.describeMethods.push(describeMethod);
    }


    static addTestCase(describeClass: DescribeClass, testCaseEntity: TestCaseEntity): void {
        const describeMethod: DescribeMethod = describeClass.describeMethods.find(d => d.methodName === testCaseEntity.methodOrFunctionName);
        if (describeMethod) {
            describeMethod.testCaseEntities.push(testCaseEntity);
        }
    }


    static generateIts(describeClass: DescribeClass): void {
        for (const describeMethod of describeClass.describeMethods) {
            this.addItToDescribeMethod(describeMethod);
        }
    }


    private static addItToDescribeMethod(describeMethod: DescribeMethod): void {
        for (const testCaseEntity of describeMethod.testCaseEntities) {
            const it = new It(describeMethod, testCaseEntity);
            describeMethod.its.push(it);
        }
    }

}
