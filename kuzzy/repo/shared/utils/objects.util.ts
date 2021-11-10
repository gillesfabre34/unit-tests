
/**
 * clone object with deep copy
 * @param model
 */

export function clone<T>(model: T): T {
    if (!model) {
        return model;
    }
    let response: any;
    if (Array.isArray(model)) {
        const newArray = [];
        model.forEach((item) => newArray.push(clone(item)));
        response = newArray;
    } else if (typeof model === 'object') {
        const newObject = {};
        Object.keys(model).forEach((key) => (newObject[key] = clone(model[key])));
        response = newObject;
    } else {
        response = model;
    }
    return response;
}

/**
 * Check if two objects have the same values for every key
 * @param obj1
 * @param obj2
 */

export function isSameObject(obj1: any, obj2: any): boolean {
    if (obj1 === obj2) {
        return true;
    }
    if (typeof obj1 === 'number') {
        return obj1.toString() === obj2?.toString();
    }
    if (
        (obj1 === undefined || obj2 === undefined)
        || (Array.isArray(obj1) && !Array.isArray(obj2))
        || (!Array.isArray(obj1) && Array.isArray(obj2))
        || (Array.isArray(obj1) && Array.isArray(obj2) && obj1.length !== obj2.length)
    ) {
        return false;
    }
    if (Array.isArray(obj1) && Array.isArray(obj2)) {
        let index = 0;
        for (const element of obj1) {
            if (!isSameObject(element, obj2[index])) {
                return false;
            }
            index++;
        }
        return true;
    } else {
        for (const key of Object.keys(obj1)) {
            if (!obj2[key] && !!obj1[key]) {
                return false;
            }
            if (Array.isArray(obj1[key])) {
                if (!isSameObject(obj1[key], obj2[key])) {
                    return false;
                }
            } else {
                if (typeof obj1[key] === 'object') {
                    if (!isSameObject(obj1[key], obj2[key])) {
                        return false;
                    }
                } else {
                    if (!obj1[key] && obj2[key] !== obj1[key] || obj1[key] && !obj2[key]) {
                        return false;
                    }
                    if (obj1[key] && obj2[key] && obj1[key].toString() !== obj2[key].toString()) {
                        return false;
                    }
                }
            }
        }

    }
    return true;
}


/**
 * Test if two arrays are identical
 * @param array1
 * @param array2
 * @returns boolean
 */
export function areIdenticalArrays(array1: Array<Object>, array2: Array<Object>): boolean {
    if (array1 === undefined || array2 === undefined) {
        return false;
    }
    return array1.length === array2.length && array1.sort().every(
        (value, index) => {
            return value === array2.sort()[index];
        }
    );
}


export function isCyclic(obj): boolean {
    var seenObjects = [];

    function detect (obj) {
        if (obj && typeof obj === 'object') {
            if (seenObjects.indexOf(obj) !== -1) {
                return true;
            }
            seenObjects.push(obj);
            for (var key in obj) {
                if (obj.hasOwnProperty(key) && detect(obj[key])) {
                    console.log(obj, 'cycle at ' + key);
                    return true;
                }
            }
        }
        return false;
    }

    return detect(obj);
}
