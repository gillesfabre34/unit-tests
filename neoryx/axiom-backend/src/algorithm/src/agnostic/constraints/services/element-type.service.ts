import { ElementType } from '../enums/element-type.enum';

export class ElementTypeService {

    static get(type: string): ElementType {
        switch (type) {
            case 'string':
                return ElementType.STRING;
            case 'number':
                return ElementType.NUMBER;
            default:
                return undefined;
        }
    }

}
