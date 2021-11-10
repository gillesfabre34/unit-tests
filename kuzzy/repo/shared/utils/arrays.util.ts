export function flat(array: any[]): any[] {
    if(!array || array.length === 0) {
        return [];
    }
    else if(Array.isArray(array[0])) {
        return flat(array[0]).concat(flat(array.slice(1)));
    }
    else {
        return [array[0]].concat(flat(array.slice(1)));
    }
}


export function mergeWithoutDuplicates(array1: any[], array2: any[]): any[] {
    return [...new Set(array1.concat(array2))];
}


export function lastElement<T>(array: T[]): T {
    return Array.isArray(array) && array.length > 0 ? array[array.length - 1] : undefined;
}


/**
 * Checks if two arrays have the same values
 * @param arr1
 * @param arr2
 * @private
 */
export function arrayOfNumbersAreEqual(arr1: number[], arr2: number[]): boolean {
    if (!Array.isArray(arr1) || !Array.isArray(arr2) || arr1.length !== arr2.length) {
        return false;
    }
    for (let i = 0; i < arr1.length; i++) {
        if (arr1[i] !== arr2[i]) {
            return false;
        }
    }
    return true;
}
