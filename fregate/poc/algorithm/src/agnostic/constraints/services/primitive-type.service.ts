import { PrimitiveType } from '../enums/primitive-type.enum';

export class PrimitiveTypeService {

    static get(type: string): PrimitiveType {
        switch (type) {
            case 'string':
                return PrimitiveType.STRING;
            case 'number':
                return PrimitiveType.NUMBER;
            default:
                return undefined;
        }
    }

}
