import { Type } from 'ts-morph';
import { PrimitiveType } from '../enums/primitive-type.enum';

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
 * @param node the node
 * @returns {PrimitiveType}
 */
export function getTypeNodeAsPrimitiveType(node: Type): PrimitiveType {
    if (node.isNumber()) {
        return PrimitiveType.NUMBER;
    }

    if (node.isString()) {
        return PrimitiveType.STRING;
    }

    if (node.isArray()) {
        return PrimitiveType.ARRAY;
    }

    if (node.isObject()) {
        return PrimitiveType.OBJECT
    }

    return PrimitiveType.NUMBER;
}

/**
 * Get the primitive type from a value
 * @param value the value
 * @returns {PrimitiveType}
 */
export function getValuePrimitiveType(value: any): PrimitiveType {
    if (Array.isArray(value)) {
        return PrimitiveType.ARRAY;
    } else if (typeof value === 'string') {
        return PrimitiveType.STRING;
    } else if (typeof value === 'number') {
        return PrimitiveType.NUMBER;
    }
    return PrimitiveType.OBJECT;
}
