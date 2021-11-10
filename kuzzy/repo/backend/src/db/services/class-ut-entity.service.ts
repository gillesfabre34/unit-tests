import { FileUTEntity } from '../entities/file-ut.entity';
import { SourceFile } from 'ts-morph';
import { ClassUTEntity } from '../entities/class-ut.entity';
import { MethodUtEntityService } from './method-ut-entity.service';

export class ClassUtEntityService {

    // static async saveClassUTEntities(fileUTEntity: FileUTEntity, sourceFile: SourceFile): Promise<void> {
    //     for (const declaration of sourceFile.getClasses()) {
    //         const className: string = declaration.getName();
    //         let classUTEntity: ClassUTEntity = await this.existingClass(fileUTEntity, className);
    //         if (!classUTEntity) {
    //             classUTEntity = await this.putAndReturnId(fileUTEntity, className);
    //         }
    //         await MethodUtEntityService.saveEach(classUTEntity, declaration);
    //     }
    // }


    // private static async existingClass(fileUTEntity: FileUTEntity, className: string): Promise<ClassUTEntity> {
    //     return await db.connection.getRepository(ClassUTEntity)
    //         .createQueryBuilder('classUT')
    //         .having('classUT.name = :name', { name: className })
    //         .innerJoinAndSelect('classUT.fileUT', 'fileUT')
    //         .where(`fileUT.id = ${fileUTEntity.id}`)
    //         .getOne();
    // }


    static async save(fileUTEntity: FileUTEntity, className: string): Promise<ClassUTEntity> {
        return await new ClassUTEntity(className, fileUTEntity).save();
    }


    // static getStatementUTs(classUTEntities: ClassUTEntity[], filePath: string): StatementUT[] {
    //     const statementUTs: StatementUT[] = [];
    //     for (const classUTEntity of classUTEntities) {
    //         statementUTs.push(...MethodUtEntityService.getStatementUTs(classUTEntity.methodUTs, classUTEntity.name, filePath));
    //     }
    //     return statementUTs;
    // }
    //
    //
    // static async findClassUTEntity(className: string, filePath: string): Promise<ClassUTEntity> {
    //     return await db.connection.getRepository(ClassUTEntity)
    //         .createQueryBuilder('classUT')
    //         .leftJoinAndSelect('classUT.fileUT', 'fileUT')
    //         .leftJoinAndSelect('fileUT.systemUT', 'systemUT')
    //         .where(`classUT.name = '${className}'`)
    //         .andWhere('fileUT.path = :path', { path: filePath})
    //         .andWhere('systemUT.name = :name', { name: GLOBAL.appName })
    //         .getOne()
    // }

}
