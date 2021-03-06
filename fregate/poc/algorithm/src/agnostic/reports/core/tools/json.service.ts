
/**
 * Service managing the transformation of a given JsonFile in a JsonAst format
 */
export class JsonService {

    /**
     * Converts a javascript object into a prettified Json with break lines
     * @param obj               // The javascript object
     * @param indent            // The initial indentation of the object
     */
    static prettifyJson(obj: object, indent = ''): string {
        const indentation = `${indent}\t`;
        let json = `{\n`;
        obj = JsonService.deleteUndefinedProperties(obj);
        json = JsonService.addProperties(obj, indentation, json);
        json = `${json}${indentation.slice(0, -1)}}`;
        return json;
    }


    /**
     * Adds properties to a json object
     * @param obj               // The javascript object
     * @param indentation       // The previous indentation
     * @param json              // The initial json object
     */
    private static addProperties(obj: object, indentation: string, json: string): string {
        for (const key of Object.keys(obj)) {
            json = `${json}${indentation}"${key}": `;
            switch (typeof obj[key]) {
                case 'number':
                case 'bigint':
                case 'boolean':
                    json = `${json}${obj[key]}${JsonService.comma(obj, key)}\n`;
                    break;
                case 'string':
                    json = JsonService.getStringProperty(obj, key, json)
                    break;
                case 'object':
                    obj[key] = JsonService.deleteUndefinedProperties(obj[key]);
                    json = Array.isArray(obj[key])
                        ? JsonService.jsonArray(obj, key, indentation, json)
                        : JsonService.jsonObject(obj, key, indentation, json);
                    break;
            }
        }

        return json;
    }


    /**
     * Removes all properties with undefined values
     * @param obj       // The object to clean
     */
    private static deleteUndefinedProperties(obj: object): object {
        for (const key of Object.keys(obj)) {
            if (obj[key] === undefined) {
                delete obj[key];
            }
        }

        return obj;
    }


    /**
     * Returns the Json property value when this property os a string
     * In the case of this property is called "text", we know that this property contains source code, so we return it by taking car of the quotes and the break lines
     * @param obj       // The ts object
     * @param key       // The key of this object
     * @param json      // The corresponding json object
     */
    private static getStringProperty(obj: object, key: string, json: string): string {
        const text = key === 'text' ? JsonService.convertCodeToString(obj[key]) : obj[key];
        return `${json}"${text}"${JsonService.comma(obj, key)}\n`;
    }


    /**
     * Replaces the special chars in source code which could be problematic when inserting this code in a json property (`"`, `\`)
     * @param text      // The source code
     */
    private static convertCodeToString(text: string): string {
        let stringified: string = JSON.stringify({'text': text});
        stringified = stringified.slice(9, -2);
        return stringified;
    }


    /**
     * Returns a comma at the end of a line if this line is the last one of a given object
     * @param key       // The key to check if it's the last one
     * @param obj       // The object to check
     */
    private static comma(obj: object, key: string): string {
        return this.isLastKey(key, obj) ? '' : ',';
    }


    /**
     * If a property of a given object is an array, it adds to the corresponding json all the lines corresponding to the elements of this array
     * @param obj           // The object with a key which is an array
     * @param key           // The key where obj[key] is an array
     * @param indentation   // The current indentation of the json object
     * @param json          // The corresponding json object
     */
    private static jsonArray(obj: object, key: string, indentation: string, json: string): string {
        json = `${json}[\n`;
        for (let i = 0; i < obj[key].length; i++) {
            if (obj[key][i]) {
                json = `${json}${indentation}\t${JsonService.prettifyJson(obj[key][i], `${indentation}\t`)}`;
                json = this.isLastIndex(i, obj[key]) ? `${json}\n` : `${json},\n`;
            }
        }
        return `${json}${indentation}]${JsonService.comma(obj, key)}\n`;
    }


    /**
     * If a property of a given object is an object, it adds to the corresponding json all the lines corresponding to the elements of this object
     * @param obj          // The object with a key which is an object
     * @param key           // The key where obj[key] is an object
     * @param indentation   // The current indentation of the json object
     * @param json          // The corresponding json object
     */
    private static jsonObject(obj: object, key: string, indentation: string, json: string): string {
        return `${json}${JsonService.prettifyJson(obj[key], indentation)}${JsonService.comma(obj, key)}\n`;
    }

    /**
     * Checks if a key is the last one of a given object
     * @param key       // The key of the object
     * @param obj       // The object
     */
    private static isLastKey(key: string, obj: object): boolean {
        return (key === Object.keys(obj).slice(-1)[0]);
    }


    /**
     * Checks if a number is the last index of a given array
     * @param i         // The index
     * @param arr       // The array
     */
    private static isLastIndex(i: number, arr: any[]): boolean {
        return (i === arr.length - 1);
    }
}
