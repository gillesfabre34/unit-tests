import { DescribeClass } from '../models/describe-class.model';
import { TestFile } from '../models/test-file.model';
import { DescribeMethodService } from './describe-method.service';
import { TestCaseEntity } from '../../db/entities/test-case.entity';

export class DescribeClassService {


    static generateDescribeClass(testFile: TestFile, testCaseEntity: TestCaseEntity): void {
        const describeClass = new DescribeClass(testFile, testCaseEntity);
        testFile.describeClasses.push(describeClass);
    }


    static addTestCase(testFile: TestFile, testCaseEntity: TestCaseEntity): void {
        const describeClass: DescribeClass = testFile.describeClasses.find(d => d.className === testCaseEntity.mock.classUT.name);
        if (describeClass) {
            describeClass.testCaseEntities.push(testCaseEntity);
        }
    }


    static generateDescribeMethods(testFiles: TestFile[]): void {
        for (const testFile of testFiles) {
            this.addDescribeMethodsToTestFile(testFile);
        }
    }


    private static addDescribeMethodsToTestFile(testFile: TestFile): void {
        for (const describeClass of testFile.describeClasses) {
            this.addDescribeMethodsToDescribeClass(describeClass);
        }
    }


    private static addDescribeMethodsToDescribeClass(describeClass: DescribeClass): void {
        let methodNames: Set<string> = new Set<string>();
        for (const testCaseEntity of describeClass.testCaseEntities) {
            if (methodNames.has(testCaseEntity.methodOrFunctionName)) {
                DescribeMethodService.addTestCase(describeClass, testCaseEntity);
            } else {
                methodNames.add(testCaseEntity.methodOrFunctionName);
                DescribeMethodService.generateDescribeMethod(describeClass, testCaseEntity);
            }
        }
        DescribeMethodService.generateIts(describeClass);
    }

}
