


export function flat(array): any[] {
    if(array.length == 0) {
        return array;
    }
    else if(Array.isArray(array[0])) {
        return flat(array[0]).concat(flat(array.slice(1)));
    }
    else {
        return [array[0]].concat(flat(array.slice(1)));
    }
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
