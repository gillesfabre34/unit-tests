import { ElementType } from "../../../constraints/enums/element-type.enum";

export class Parameter {

    name: string;
    primitiveType: ElementType;
    value: any;

    constructor(name?: string, value?: any, type?: any) {
        this.name = name;
        this.primitiveType = type;
        this.value = value;
    }

}
