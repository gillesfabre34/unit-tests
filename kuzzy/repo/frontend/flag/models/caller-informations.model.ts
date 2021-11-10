import { ClassDeclaration, MethodDeclaration } from 'ts-morph';
import { MethodOrFunctionDeclaration } from '../types/method-or-function-declaration.type';

export class CallerInformations {

    classDeclaration: ClassDeclaration = undefined;
    declaration: MethodOrFunctionDeclaration = undefined;
    isInheritedFromOutOfSystem: boolean = undefined;


    constructor(declaration: MethodOrFunctionDeclaration, classDeclaration?: ClassDeclaration, isInheritedFromOutOfSystem?: boolean) {
        this.classDeclaration = classDeclaration;
        this.declaration = declaration;
        this.isInheritedFromOutOfSystem = isInheritedFromOutOfSystem;
    }


    get className(): string {
        return this.isMethodDeclaration ? this.classDeclaration?.getName() : this.declaration.getName();
    }


    get isMethodDeclaration(): boolean {
        return this.declaration?.constructor.name === 'MethodDeclaration';
    }


    get name(): string {
        return this.declaration.getName();
    }


    isAbstract(): boolean {
        return this.isMethodDeclaration && (this.declaration as MethodDeclaration).isAbstract();
    }

}
