import { Injectable } from '@nestjs/common';
import { FileUTEntity } from '../db/entities/file-ut.entity';
import * as chalk from 'chalk';
import { DeleteResult } from 'typeorm';
import { db } from '../db/const/db.const';
import {
    PostFileUTDto,
    PostFileUTDtoCallerUT,
    PostFileUTDtoClassUT,
    PostFileUTDtoEnumUT,
    PostFileUTsDto
} from '../../../dtos/file-ut/post-file-uts.dto';
import { SystemUTEntity } from '../db/entities/system-ut.entity';
import { ClassUTEntity } from '../db/entities/class-ut.entity';
import { MethodUTEntity } from '../db/entities/method-ut.entity';
import { StatementUTEntity } from '../db/entities/statement-ut.entity';
import { FunctionUTEntity } from '../db/entities/function-ut.entity';
import { EnumUTEntity } from '../db/entities/enum-ut.entity';
import { DeclarationEntity } from '../db/entities/declaration.entity';
import { KeyValueEntity } from '../db/entities/key-value.entity';
import { ValueEntity } from '../db/entities/value.entity';
import { PropertyEntity } from '../db/entities/property.entity';
import { ReferencedClassOrEnumUTEntity } from './referenced-class-ut-entity.model';
import { throwHttpException } from '../../../shared/utils/errors.util';
import { saveIfNotExists } from '../db/utils/save-if-not-exists.util';

@Injectable()
export class FileUtService {

    async getFileUTPaths(): Promise<string[]> {
        try {
            return (await FileUTEntity.find()).map(f => f.path);
        } catch (err) {
            return throwHttpException('Error getting FileUT', 400, err);
        }
    }


    async deleteFileUTPaths(fileUTPaths: string[]): Promise<number> {
        try {
            const response: DeleteResult = await db.connection.createQueryBuilder()
                .delete()
                .from(FileUTEntity)
                .where('path IN (:...paths)', {paths: fileUTPaths})
                .execute();
            console.log(chalk.yellowBright('Delete response : '), response);
            return response.affected;
        } catch (err) {
            return throwHttpException('Error deleting FileUT', 400, err);
        }
    }


    // ----------------------------------------------   Save FileUT entities   -------------------------------------------------


    private referencedClassOrEnumUTEntities: ReferencedClassOrEnumUTEntity[] = [];


    async saveFileUTs(postFileUTsDto: PostFileUTsDto): Promise<number> {
        try {
            const systemUTEntity: SystemUTEntity = await SystemUTEntity.findOne(postFileUTsDto.systemId);
            let numberOfSavedFileUtEntities = 0;
            for (const fileUTDto of postFileUTsDto.fileUTs) {
                await this.saveFileUTEntity(fileUTDto, systemUTEntity);
                numberOfSavedFileUtEntities++;
            }
            await this.savePropertyEntities();
            this.referencedClassOrEnumUTEntities = [];
            return numberOfSavedFileUtEntities;
        } catch (err) {
            this.referencedClassOrEnumUTEntities = [];
            return throwHttpException('Error creating FileUTs', 400, err);
        }
    }


    private async saveFileUTEntity(fileUTDto: PostFileUTDto, systemUTEntity: SystemUTEntity): Promise<void> {
        const fileUTEntity: FileUTEntity = await saveIfNotExists(new FileUTEntity(fileUTDto.path, systemUTEntity), {path: fileUTDto.path, id: systemUTEntity.id });
        await this.saveClassUTEntities(fileUTDto.classUTs, fileUTEntity);
        await this.saveEnumUTEntities(fileUTDto.enumUTs, fileUTEntity);
        await this.saveFunctionUTEntities(fileUTDto.functionUTs, fileUTEntity);
    }


    // ----------------------------------------------   Save ClassUT entities   -------------------------------------------------


    private async saveClassUTEntities(classUTDtos: PostFileUTDtoClassUT[], fileUTEntity: FileUTEntity): Promise<void> {
        for (const classUTDto of classUTDtos) {
            const classUTEntity: ClassUTEntity = await this.saveClassUTEntity(classUTDto, fileUTEntity);
            this.referencedClassOrEnumUTEntities.push(new ReferencedClassOrEnumUTEntity(classUTEntity, classUTDto.reference, classUTDto.properties));
        }
    }


    private async saveClassUTEntity(classUTDto: PostFileUTDtoClassUT, fileUTEntity: FileUTEntity): Promise<ClassUTEntity> {
        const classUTEntity: ClassUTEntity = await saveIfNotExists(new ClassUTEntity(classUTDto.name, fileUTEntity), { fileUT: fileUTEntity, name: classUTDto.name });
        await this.saveMethodUTEntities(classUTDto, classUTEntity);
        const declarationEntity = new DeclarationEntity();
        declarationEntity.classUT = classUTEntity;
        classUTEntity.declaration = await saveIfNotExists(declarationEntity, { classUT: classUTEntity });
        return classUTEntity;
    }


    // ----------------------------------------------   Save MethodUT entities   -------------------------------------------------



    private async saveMethodUTEntities(classUTDto: PostFileUTDtoClassUT, classUTEntity: ClassUTEntity): Promise<void> {
        for (const methodUTDto of classUTDto.methodUTs) { await this.saveMethodUTEntity(methodUTDto, classUTEntity);
        }
    }


    private async saveMethodUTEntity(methodUTDto: PostFileUTDtoCallerUT, classUTEntity: ClassUTEntity): Promise<void> {
        const methodUTEntity = await saveIfNotExists(new MethodUTEntity(methodUTDto.name, classUTEntity), {name: methodUTDto.name, classUT: classUTEntity });
        await this.saveStatementUTEntities(methodUTEntity, methodUTDto.numberOfStatementUTs);
    }


    // ----------------------------------------------   Save EnumUT entities   -------------------------------------------------



    private async saveEnumUTEntities(enumUTDtos: PostFileUTDtoEnumUT[], fileUTEntity: FileUTEntity): Promise<void> {
        for (const enumUTDto of enumUTDtos) {
            const enumUTEntity: EnumUTEntity = await this.saveEnumUTEntity(enumUTDto, fileUTEntity);
            this.referencedClassOrEnumUTEntities.push(new ReferencedClassOrEnumUTEntity(enumUTEntity, enumUTDto.reference, undefined));
        }
    }


    private async saveEnumUTEntity(enumUTDto: PostFileUTDtoEnumUT, fileUTEntity: FileUTEntity): Promise<EnumUTEntity> {
        const enumUTEntity = await saveIfNotExists(new EnumUTEntity(fileUTEntity, enumUTDto.name), { name: enumUTDto.name, fileUT: fileUTEntity });
        let declarationEntity = new DeclarationEntity();
        declarationEntity.enumUT = enumUTEntity;
        declarationEntity = await saveIfNotExists(declarationEntity, { enumUT: enumUTEntity });
        for (const keyValue of enumUTDto.keyValues) {
            let keyValueEntity = new KeyValueEntity(keyValue.key, false);
            keyValueEntity.declaration = declarationEntity;
            await keyValueEntity.save();
            await new ValueEntity(keyValue.value, keyValueEntity).save();
        }
        enumUTEntity.declaration = declarationEntity;
        return enumUTEntity;
    }


    // ----------------------------------------------   Save FunctionUT entities   -------------------------------------------------



    private async saveFunctionUTEntities(functionUTDtos: PostFileUTDtoCallerUT[], fileUTEntity: FileUTEntity): Promise<void> {
        for (const functionUTDto of functionUTDtos) {
            await this.saveFunctionUTEntity(functionUTDto, fileUTEntity);
        }
    }



    private async saveFunctionUTEntity(functionUTDto: PostFileUTDtoCallerUT, fileUTEntity: FileUTEntity): Promise<void> {
        const functionUTEntity = await saveIfNotExists(new FunctionUTEntity(functionUTDto.name, fileUTEntity), { name: functionUTDto.name, fileUT: fileUTEntity });
        await this.saveStatementUTEntities(functionUTEntity, functionUTDto.numberOfStatementUTs);
    }


    // ----------------------------------------------   Save StatementUT entities   -------------------------------------------------


    private async saveStatementUTEntities(callerEntity: MethodUTEntity | FunctionUTEntity, numberOfStatementUTs: number): Promise<void> {
        for (let i = 0; i < numberOfStatementUTs; i++) {
            if (callerEntity?.constructor.name === 'MethodUTEntity') {
                await saveIfNotExists(new StatementUTEntity(i, callerEntity, true), { index: i, methodUT: callerEntity });
            }
        }
    }


    // ----------------------------------------------   Save Property entities   -------------------------------------------------


    private async savePropertyEntities(): Promise<void> {
        for (const referencedClassUTEntity of this.referencedClassOrEnumUTEntities) {
            await this.saveClassPropertyEntities(referencedClassUTEntity);
        }
    }


    private async saveClassPropertyEntities(referencedClassOrEnumUTEntity: ReferencedClassOrEnumUTEntity): Promise<void> {
        if (!!referencedClassOrEnumUTEntity.enumUTEntity) {
            return;
        }
        for (const property of referencedClassOrEnumUTEntity.properties) {
            const propertyEntity: PropertyEntity = new PropertyEntity(property.name, referencedClassOrEnumUTEntity.classUTEntity);
            // TODO : check why r.reference is sometimes undefined
            const correspondingReferencedEntity: ReferencedClassOrEnumUTEntity = this.referencedClassOrEnumUTEntities.find(r => r.reference?.toString() === property.typeClassReference?.toString());
            if (correspondingReferencedEntity) {
                const isClassUT: boolean = !!correspondingReferencedEntity.classUTEntity;
                propertyEntity.declaration = isClassUT ? correspondingReferencedEntity.classUTEntity.declaration : correspondingReferencedEntity.enumUTEntity.declaration;
            } else {
                const declarationEntity = new DeclarationEntity();
                declarationEntity.primitiveType = property.primitiveType;
                if (property.name === 'yyy') {
                    console.log(chalk.magentaBright('PROP YYYYYYY DECLARRRR'), declarationEntity);
                }
                await declarationEntity.save();
                propertyEntity.declaration = declarationEntity
            }
            await propertyEntity.save();
        }
    }

}
