import { PrimitiveType } from "../../../constraints/enums/primitive-type.enum";

export class Parameter {

    name: string;
    primitiveType: PrimitiveType;
    value: any;

    constructor(name?: string, value?: any, type?: any) {
        this.name = name;
        this.primitiveType = type;
        this.value = value;
    }

}
