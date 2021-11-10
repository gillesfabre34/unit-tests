import { ClassUTEntity } from '../db/entities/class-ut.entity';
import { PostFileUTDtoProperty } from '../../../dtos/file-ut/post-file-uts.dto';
import { EnumUTEntity } from '../db/entities/enum-ut.entity';
import { ClassOrEnumUTEntity } from '../db/types/class-or-enum-entity.type';


export class ReferencedClassOrEnumUTEntity {

    classUTEntity: ClassUTEntity = undefined;
    enumUTEntity: EnumUTEntity = undefined;
    properties: PostFileUTDtoProperty[] = [];
    reference: number = undefined;

    constructor(classOrEnumUTEntity: ClassOrEnumUTEntity, reference: number, properties: PostFileUTDtoProperty[]) {
        if (classOrEnumUTEntity instanceof ClassUTEntity) {
            this.classUTEntity = classOrEnumUTEntity;
        } else {
            this.enumUTEntity = classOrEnumUTEntity;
        }
        this.properties = properties;
        this.reference = reference;
    }
}
