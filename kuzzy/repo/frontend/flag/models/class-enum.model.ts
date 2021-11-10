import { Enumerable } from './enumerable.model';

export class ClassEnum {

    defaultImport: string = undefined;
    enumerable: Enumerable = undefined;
    moduleSpecifier: string = undefined;
    isArrayType: boolean = undefined;
    isProperty: boolean = undefined;
    propertyOrMethod: string = undefined;

    constructor(propertyOrMethod: string, defaultImport: string, moduleSpecifier: string, enumerable: Enumerable, isProperty: boolean, isArrayType: boolean) {
        this.propertyOrMethod = propertyOrMethod;
        this.defaultImport = defaultImport;
        this.moduleSpecifier = moduleSpecifier;
        this.enumerable = enumerable;
        this.isProperty = isProperty;
        this.isArrayType = isArrayType;
    }
}
