export function isPrimitive(element: any): boolean {
    if (element === undefined || element === null) {
        return false;
    }
    return primitiveTypes.includes(typeof element);
}

export const primitiveTypes = ['string', 'number', 'boolean'];

export type PrimitiveType = 'string' | 'number' | 'boolean';

