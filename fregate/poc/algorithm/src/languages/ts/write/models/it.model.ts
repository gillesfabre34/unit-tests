import { SutMethod } from '../../test-suites/sut/models/sut-method.model';
import { TestSuite } from '../../test-suites/generation/models/test-suite.model';
import { prettify, prettifyEachValue } from '../../../../agnostic/tools/utils/prettify.util';
import { SpyMethod } from '../../../../agnostic/write/models/spy-method.model';
import { tabs, unCapitalize } from '../../../../agnostic/tools/utils/strings.util';

export class It<T> {

    code = '';
    execution = '';
    expectSpies = '';
    expectReturnedValue = '';
    spyOns = ''
    sutMethod: SutMethod<T> = undefined;
    testSuite: TestSuite<T> = undefined;
    title = '';

    generate(sutMethod: SutMethod<T>, testSuite: TestSuite<T>): It<T> {
        this.sutMethod = sutMethod;
        this.testSuite = testSuite;
        this.setCode();
        return this;
    }


    setCode(): void {
        this.setTitle();
        this.setExecuteLine();
        this.setExpects();
        this.setSpies();
        let code = `${tabs(2)}it(${prettify(this.title)}, () => {\n`;
        code = `${code}${this.spyOns}`;
        code = `${code}${tabs(3)}${this.execution}\n`;
        code = `${code}${this.expectSpies}`;
        code = `${code}${tabs(3)}${this.expectReturnedValue}\n`;
        code = `${code}${tabs(2)}});\n`;
        this.code = code;
    }


    private setTitle(): void {
        this.title = `${tabs(3)}Should`;
        if (this.testSuite.output.returnedValue !== undefined) {
            this.title = `${this.title} return \n`;
            this.title = `${this.title}${tabs(4)}${this.testSuite?.output?.returnedValue}`;
            if (this.testSuite?.parameters && this.testSuite?.parameters.length > 0) {
                this.title = `\n${this.title}${tabs(3)}with\n`;
                for (const parameter of this.testSuite?.parameters) {
                    this.title = `${this.title}${tabs(4)}${parameter.name} = ${parameter.value}\n`;
                }
                this.title = this.title.slice(0, -1);
            }
        }
    }


    private setSpies(): void {
        this.spyOns = '';
        for (const spyMethod of this.testSuite?.output?.spies) {
            if (spyMethod.spiedObject && spyMethod.filePath.replace('flagged-', '') !== this.sutMethod.sutFile.path) {
                const spiedObject = this.getSpiedObject(spyMethod);
                this.addSpyOns(spiedObject, spyMethod);
                this.addExpectSpies(spiedObject, spyMethod);
            }
        }
        const spyOns = this.spyOns.slice(0, -1);
        this.spyOns = spyOns?.length > 0 ? `${spyOns}\n` : spyOns;
        const expectSpies = this.spyOns.slice(0, -1);
        this.expectSpies = expectSpies?.length > 0 ? `${expectSpies}\n` : expectSpies;
    }


    private getSpiedObject(spyMethod: SpyMethod): string {
        return spyMethod.spiedObject.replace('this', unCapitalize(this.sutMethod.sutClass.name));;
    }


    private addSpyOns(spiedObject: string, spyMethod: SpyMethod): void {
        this.spyOns = `${this.spyOns}${tabs(3)}spyOn(${spiedObject}, '${spyMethod.methodName}')`;
        if (spyMethod.returnedValues.length > 0) {
            const prettifiedValues: string[] = prettifyEachValue(spyMethod.returnedValues);
            this.spyOns = `${this.spyOns}.and.returnValues(${prettifiedValues.join()})`;
        }
        this.spyOns = `${this.spyOns};\n`;
    }


    private addExpectSpies(spiedObject: string, spyMethod: SpyMethod): void {
        this.expectSpies = `${this.expectSpies}${tabs(3)}expect(${spiedObject}.${spyMethod.methodName})`;
        if (spyMethod.calledWith.length > 0) {
            this.expectSpies = `${this.expectSpies}.toHaveBeenCalledWith(${spyMethod.calledWith.map(c => prettify(c)).join(', ')})`;
        }
        this.expectSpies = `${this.expectSpies};\n`;
    }


    private setExecuteLine(): void {
        const params: string = this.testSuite.parameters.map(p => prettify(p.value)).join(', ');
        const className: string = this.sutMethod.sutClass.name;
        const caller: string = this.
        sutMethod.agnosticMethod.isStatic ? className : unCapitalize(className);
        this.execution = `const result = ${caller}.${this.sutMethod.name}(${params});`;
    }


    private setExpects(): void {
        this.expectReturnedValue = `expect(result).toEqual(${prettify(this.testSuite.output.returnedValue)});`;
    }

}
