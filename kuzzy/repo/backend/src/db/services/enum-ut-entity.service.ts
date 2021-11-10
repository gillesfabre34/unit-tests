import { FileUTEntity } from '../entities/file-ut.entity';
import { EnumDeclaration, SourceFile } from 'ts-morph';
// import { db } from '../../init/const/db.const';
import { EnumUTEntity } from '../entities/enum-ut.entity';
import { KeyValueEntity } from '../entities/key-value.entity';
import { ValueEntity } from '../entities/value.entity';
import { DeclarationEntity } from '../entities/declaration.entity';
// import { ClassEnum } from '../../flag/models/class-enum.model';
// import { originalPath } from '../../utils/file-system.util';

export class EnumUtEntityService {

    // static async saveEnumUTEntities(fileUTEntity: FileUTEntity, sourceFile: SourceFile): Promise<void> {
    //     for (const enumDeclaration of sourceFile.getEnums()) {
    //         const enumName: string = enumDeclaration.getName();
    //         let enumUTEntity: EnumUTEntity = await this.existingEnum(fileUTEntity, enumName);
    //         if (!enumUTEntity) {
    //             enumUTEntity = await this.saveEnumEntity(fileUTEntity, enumDeclaration);
    //         }
    //     }
    // }


    // private static async existingEnum(fileUTEntity: FileUTEntity, enumName: string): Promise<EnumUTEntity> {
    //     return await db.connection.getRepository(EnumUTEntity)
    //         .createQueryBuilder('enumUT')
    //         .having('enumUT.name = :name', { name: enumName })
    //         .innerJoinAndSelect('enumUT.fileUT', 'fileUT')
    //         .where(`fileUT.id = ${fileUTEntity.id}`)
    //         .getOne();
    // }


    // static async saveEnumEntity(fileUTEntity: FileUTEntity, enumDeclaration: EnumDeclaration): Promise<EnumUTEntity> {
    //     const enumUTEntity = new EnumUTEntity(fileUTEntity, enumDeclaration.getName());
    //     enumUTEntity.keyValues = await this.saveAndReturnKeyValueEntities(enumDeclaration);
    //     const declarationEntity = new DeclarationEntity();
    //     declarationEntity.enumUT = enumUTEntity;
    //     await declarationEntity.putAndReturnId();
    //     enumUTEntity.declaration = declarationEntity;
    //     return await enumUTEntity.putAndReturnId();
    // }
    //
    //
    // private static async saveAndReturnKeyValueEntities(enumDeclaration: EnumDeclaration): Promise<KeyValueEntity[]> {
    //     const keyValueEntities: KeyValueEntity[] = [];
    //     for (const member of enumDeclaration.getMembers()) {
    //         const keyValueEntity = new KeyValueEntity(member.getName(), false, [new ValueEntity(member.getValue().toString())]);
    //         // TODO : other types possible for enums ?
    //         // keyValueEntity.primitiveType = typeof member.getValue() as 'string' | 'number';
    //         await keyValueEntity.putAndReturnId();
    //         keyValueEntities.push(keyValueEntity);
    //     }
    //     return keyValueEntities;
    // }


    // static async findEnumUTDeclaration(classEnum: ClassEnum): Promise<DeclarationEntity> {
    //     return await db.connection.getRepository(DeclarationEntity)
    //         .createQueryBuilder('declaration')
    //         .leftJoinAndSelect('declaration.enumUT', 'enumUT')
    //         .leftJoinAndSelect('enumUT.fileUT', 'fileUT')
    //         .where('fileUT.path = :path', { path: originalPath(classEnum.enumerable?.filePath) })
    //         .getOne();
    // }

}
