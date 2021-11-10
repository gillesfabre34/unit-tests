import { KeyValue } from './key-value.model';
import { getModuleSpecifier } from '../../utils/file-system.util';

export class Enumerable {

    enumerableName: string = undefined;
    filePath: string = undefined;
    keyValues: KeyValue[] = [];
    originalEnumName: string = undefined;

    constructor(enumerableName: string, originalEnumName: string, filePath: string) {
        this.enumerableName = enumerableName;
        this.filePath = filePath;
        this.originalEnumName = originalEnumName;
    }


    get moduleSpecifier(): string {
        return getModuleSpecifier(this.filePath);
    }


    getKeyValue(value: any): KeyValue {
        return this.keyValues.find(k => k.value === value);
    }


    getKeyValueCode(value: any): string {
        const keyValue: KeyValue = this.getKeyValue(value);
        if (!keyValue) {
            return '';
        }
        return `${this.originalEnumName}.${keyValue.key}`;
    }

}
