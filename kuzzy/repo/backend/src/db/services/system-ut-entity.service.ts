import { SystemUTEntity } from '../entities/system-ut.entity';
import { FileUtEntityService } from './file-ut-entity.service';
import { SourceFile } from 'ts-morph';

export class SystemUtEntityService {


    // static async putAndReturnId(sourceFiles: SourceFile[]): Promise<SystemUTEntity> {
    //     const systemUTEntity: SystemUTEntity = await this.saveSystemUTIfNotExists(GLOBAL.appName);
    //     await FileUtEntityService.saveFileUTsInDb(systemUTEntity, sourceFiles);
    //     return systemUTEntity;
    // }
    //
    //
    // static async saveSystemUTIfNotExists(name: string): Promise<SystemUTEntity> {
    //     const previousSystemUTEntity: SystemUTEntity = await SystemUTEntity.findOne({name: name});
    //     if (previousSystemUTEntity) {
    //         return await db.connection.getRepository(SystemUTEntity)
    //             .createQueryBuilder('systemUT')
    //             .leftJoinAndSelect('systemUT.fileUTs', 'fileUTs')
    //             .where('systemUT.name = :name', { name: GLOBAL.appName })
    //             .getOne();
    //     } else {
    //         return await SystemUTEntity.putAndReturnId(new SystemUTEntity(name));
    //     }
    // }
}
