import { DescribeClass } from '../../../write/models/describe-class.model';
import { ClassDeclaration, MethodDeclaration } from 'ts-morph';
import { SutMethod } from './sut-method.model';
import { AgnosticSutClass } from '../../../../../agnostic/test-suites/sut/models/agnostic-sut-class.model';

export class SutClass extends AgnosticSutClass {

    classDeclaration: ClassDeclaration = undefined;
    describeClass: DescribeClass = undefined;
    importsCode: string = '';


    getClassName(): string {
        return this.classDeclaration.getName();
    }


    getMethodDeclarations(): MethodDeclaration[] {
        return this.classDeclaration.getMethods();
    }


    newSutMethod(): SutMethod {
        return new SutMethod();
    }


}
