import { ParameterDeclaration } from 'ts-morph';
import { ElementType } from '../enums/element-type.enum';
import * as chalk from 'chalk';

/**
 * get a random number
 * @param avoid the number to avoid
 * @returns {number}
 */
export function randomNumber(...avoid: number[]): number {
    let res: number;
    do {
        res = +(Math.random() * 10).toFixed(0);
    } while ((avoid || []).includes(res));
    return res;
}


/**
 * get a random Array
 * @param length the length of the result array
 * @param baseArray the base array
 * @param type the type of array data
 * @returns {any[]}
 */
export function randomArray(length: number, baseArray: any[] = [], type: string = 'number'): any[] {
    const res: any[] = [...baseArray];
    for (let i = 0; i < length; i++) {
        res.push(randomNumber());
    }
    return res;
}


/**
 * Transform data into an array
 * @param v the data
 * @returns {any[]}
 */
export function arrayify(v: any): any[] {
    if (!Array.isArray(v)) return [v];
    return v;
}

/**
 * Slot arrays together
 * @param arrays the arrays to slot
 * @returns {any[]}
 */
export function slotArrays(...arrays: any[]): any[] {
    const SLOTED_ARRAY: any[] = [];
    arrays.forEach((a: any[]) => {
        SLOTED_ARRAY.push(a.shift());
    });
    const REST_ARRAYS: any[] = arrays.filter((a: any[]) => a.length > 0);
    return REST_ARRAYS.length > 0 ? [...SLOTED_ARRAY, ...slotArrays(...REST_ARRAYS)] : [];
}


/**
 * Get primitive type from node type
 * @param parameterDeclaration the node
 * @returns {ElementType}
 */
export function getParameterElementType(parameterDeclaration: ParameterDeclaration): ElementType {
    return syntaxKindToElementType(getParameterKindName(parameterDeclaration));
}



function getParameterKindName(parameterDeclaration: ParameterDeclaration): string {
    if (!parameterDeclaration?.getChildren()?.[2]) {
        return 'AnyKeyword';
    }
    return parameterDeclaration.getChildren()[2].getKindName();
}


export function syntaxKindToElementType(kindName: string): ElementType {
    switch (kindName) {
        case 'NumberKeyword':
        case 'NumberLiteral':
            return ElementType.NUMBER;
        case 'StringKeyword':
        case 'StringLiteral':
            return ElementType.STRING;
        case 'ObjectKeyword':
        case 'TypeLiteral':
            return ElementType.OBJECT;
        case 'ArrayType':
            return ElementType.ARRAY;
        case 'BooleanKeyword':
            return ElementType.BOOLEAN;
        case 'TypeReference':
            // TODO : Use TYPED_OBJECTS (to implement)
            return ElementType.OBJECT;
        default:
            // TODO : Use ElementType.ANY
            return ElementType.NUMBER;
    }
}


export function getElementType(value: any): ElementType {
    if (Array.isArray(value)) {
        return ElementType.ARRAY;
    }
    return getElementTypeFromType(typeof value);
}


export function getElementTypeFromType(type: string): ElementType {
    switch(type) {
        case 'string':
            return ElementType.STRING;
        case 'number':
        case 'bigint':
            return ElementType.NUMBER;
        case 'boolean':
            return ElementType.BOOLEAN;
        case 'object':
            return ElementType.OBJECT;
        default:
            return undefined;
    }
}

