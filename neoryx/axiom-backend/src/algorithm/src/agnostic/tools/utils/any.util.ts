


export function isPrimitive(value: any): boolean {
    switch (typeof value) {
        case 'boolean':
        case 'number':
        case 'string':
        case 'bigint':
            return true;
        default:
            return false;
    }
}
