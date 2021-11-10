import { Enumerable } from '../models/enumerable.model';

export class EnumerableService {

    // TODO : caution : will crash if there are multiple enums with the same name
    static getEnumerableWithOriginalEnumName(enumerables: Enumerable[], enumName: string): Enumerable {
        return enumerables.find(e => e.originalEnumName === enumName);
    }

}
