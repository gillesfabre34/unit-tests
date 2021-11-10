import { isPrimitive } from './primitives.util';


export function getInstanceId(element: object): number {
    if (!element || Array.isArray(element) || isPrimitive(element)) {
        return undefined;
    }
    return Reflect.getMetadata('getInstanceId', element);
}

export function setInstanceId(element: object, id: number): void {
    if (!Array.isArray(element) && !isPrimitive(element)) {
        Reflect.defineMetadata('getInstanceId', id, element);
    }
}


// --------------------------------------------------------------------------------


export function getFilePath(element: object): number {
    if (!element || Array.isArray(element) || isPrimitive(element)) {
        return undefined;
    }
    return Reflect.getMetadata('getFilePath', element);
}

export function setFilePath(element: object, filePath: string): void {
    if (!Array.isArray(element) && !isPrimitive(element)) {
        Reflect.defineMetadata('getFilePath', filePath, element);
    }
}
