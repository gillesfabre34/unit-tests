import { DescribeClass } from '../../../write/models/describe-class.model';
import { ClassDeclaration, MethodDeclaration } from 'ts-morph';
import { SutMethod } from './sut-method.model';
import { AgnosticSutClass } from '../../../../../agnostic/test-suites/sut/models/agnostic-sut-class.model';

export class SutClass<T> extends AgnosticSutClass<T> {

    classDeclaration: ClassDeclaration = undefined;
    describeClass: DescribeClass<T> = undefined;
    importsCode: string = '';


    getClassName(): string {
        return this.classDeclaration.getName();
    }


    getMethodDeclarations(): MethodDeclaration[] {
        return this.classDeclaration.getMethods();
    }


    newSutMethod(): SutMethod<T> {
        return new SutMethod<T>();
    }


}
