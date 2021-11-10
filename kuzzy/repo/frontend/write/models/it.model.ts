import { DescribeMethod } from './describe-method.model';
import { TestCaseEntity } from '../../db/entities/test-case.entity';
import { prettify } from '../../../shared/utils/prettify.util';
import { tabs } from '../../../shared/utils/strings.util';

export class It {
    code = '';
    describeMethod: DescribeMethod = undefined;
    expectReturnedValueCode = '';
    expectSpiesCode = '';
    runCode = '';
    spyOns = '';
    testCaseEntity: TestCaseEntity = undefined;
    title = '';

    constructor(describeMethod: DescribeMethod, testCaseEntity: TestCaseEntity) {
        this.describeMethod = describeMethod;
        this.testCaseEntity = testCaseEntity;
        this.setRunCode();
        this.setExpectReturnedValueCode();
        this.setCode();
    }


    get before(): string {
        return `const instance = ${this.testCaseEntity.mock?.name};\n`;
    }


    private setRunCode(): void {
        const awaitCall: string = this.testCaseEntity.isAsync ? 'await ' : '';
        let run = `const result = ${awaitCall}instance.${this.testCaseEntity.methodOrFunctionName}(`;
        if (this.testCaseEntity.parameters?.length > 0) {
            for (const parameter of this.testCaseEntity.parameters) {
                run = `${run}${parameter?.keyValue?.text}, `;
            }
            run = `${run.slice(0, -2)}`;
        }
        this.runCode =`${run});`;
    }


    private setExpectReturnedValueCode(): void {
        const returnedValue: string = this.testCaseEntity?.returnedValue?.text ?? '';
        if (!this.testCaseEntity.isVoid) {
            this.expectReturnedValueCode = `expect(result).toEqual(${returnedValue});`;
        }
    }


    private setCode(): void {
        this.setTitle();
        this.setSpies();
        const asyncCall: string = this.testCaseEntity.isAsync ? 'async ' : '';
        let code = `${tabs(2)}it(${prettify(this.title)}, ${asyncCall}() => {\n`;
        code = `${code}${tabs(3)}${this.before}`;
        code = `${code}${this.spyOns}`;
        code = `${code}${tabs(3)}${this.runCode}\n`;
        code = `${code}${this.expectSpiesCode}`;
        code = `${code}${tabs(3)}${this.expectReturnedValueCode}\n`;
        code = `${code}${tabs(2)}});\n`;
        this.code = code;
    }


// TODO: Complete
    private setTitle(): void {
        let title = `Should`;
        this.title = title;
    }


// TODO
    private setSpies(): void {
        this.spyOns = '';
    }

}
