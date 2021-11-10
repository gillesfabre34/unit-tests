import { DescribeMethod } from './describe-method.model';
import { SutClass } from '../../test-suites/sut/models/sut-class.model';
import { SutMethod } from '../../test-suites/sut/models/sut-method.model';
import { PropertyValue } from '../../../../agnostic/test-suites/generation/models/property-value.model';
import { prettify } from '../../../../agnostic/tools/utils/prettify.util';
import { tab, tabs, unCapitalize } from '../../../../agnostic/tools/utils/strings.util';
import * as chalk from 'chalk';


export class DescribeClass<T> {

    code = '';
    describeMethods: DescribeMethod<T>[] = [];
    describeMethodsCode = '';
    importsCode = '';
    sutClass: SutClass<T> = undefined;
    title: string = undefined;


    constructor(title?: string) {
        this.title = title;
    }


    generate(sutClass: SutClass<T>): DescribeClass<T> {
        this.sutClass = sutClass;
        this.generateImports(sutClass);
        this.generateDescribeMethods();
        this.setCode();
        return this;
    }


    private generateImports(sutClass: SutClass<T>): void {
        let importsCode = `${sutClass.sutFile?.sourceFile?.getImportDeclarations().map(i => i.getText())}\n\n`;
        this.importsCode = importsCode.replace(',', '\n');
    }


    private generateDescribeMethods(): void {
        for (const sutMethod of this.sutClass.sutMethods) {
            const describeMethod: DescribeMethod<T> = this.generateDescribeMethod(sutMethod as SutMethod<T>);
            this.describeMethods.push(describeMethod);
        }
    }


    private generateDescribeMethod(sutMethod: SutMethod<T>): DescribeMethod<T> {
        return new DescribeMethod<T>().generate(sutMethod);
    }


    setCode(): void {
        this.title = this.sutClass.name;
        this.setDescribeMethodsCode();
        let code = this.importsCode;
        code = `${code}describe('${this.title}', () => {\n`;
        code = `${code}${tab}${this.initObjectCode()}`;
        code = `${code}${this.beforeEachCode()}\n`;
        code = `${code}${this.describeMethodsCode}\n`;
        code = `${code}});\n`;
        this.code = code;
    }


    private initObjectCode(): string {
        let constructorParams = '';
        if (this.sutClass?.classDeclaration?.getConstructors()?.[0]?.getParameters()) {
            const numberOfParams = this.sutClass?.classDeclaration?.getConstructors()?.[0]?.getParameters().length;
            for (let i = 0; i < numberOfParams; i++) {
                constructorParams = `${constructorParams}undefined, `;
            }
            constructorParams = constructorParams.slice(0, -2);
        }
        return `let ${unCapitalize(this.sutClass.name)} = new ${this.sutClass.name}(${constructorParams});\n`;
    }


    private beforeEachCode(): string {
        const instanceProperties: PropertyValue[] = this.sutClass.testSuites[0]?.instanceProperties.filter(i => !!i.name);
        let code = '';
        if (Array.isArray(instanceProperties) && instanceProperties.length > 0) {
            code = `\n${tab}beforeEach(() => {\n`;
            for (const instanceProperty of instanceProperties) {
                code = `${code}${tabs(2)}${unCapitalize(this.sutClass.name)}.${instanceProperty.name} = ${prettify(instanceProperty.value)};\n`;
            }
            code = `${code}${tab}});\n`
        }
        return code;
    }


    private setDescribeMethodsCode(): void {
        this.describeMethodsCode = '';
        for (const describeMethod of this.describeMethods) {
            this.describeMethodsCode = `${this.describeMethodsCode}${describeMethod.code}\n`;
        }
    }


}
