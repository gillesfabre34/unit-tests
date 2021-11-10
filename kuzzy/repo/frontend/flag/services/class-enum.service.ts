import { ClassEnum } from '../models/class-enum.model';

export class ClassEnumService {

    static getClassEnum(name: string, classEnums: ClassEnum[] = []): ClassEnum {
        return classEnums.find(c => c.propertyOrMethod === name);
    }


    static getEnumValueAsString(classEnum: ClassEnum, value: any): string {
        return classEnum.enumerable?.getKeyValueCode(value);
    }

}
