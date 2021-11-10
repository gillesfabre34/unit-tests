import { FileUTEntity } from '../entities/file-ut.entity';
import { SystemUTEntity } from '../entities/system-ut.entity';
import { SourceFile } from 'ts-morph';
import { ClassUtEntityService } from './class-ut-entity.service';
import { EnumUtEntityService } from './enum-ut-entity.service';
import { db } from '../../init/const/db.const';
import { TestFileService } from '../../write/services/test-file.service';
import { kuzzyPath } from '../../utils/kuzzy-folder.util';
import { flat } from '../../../shared/utils/arrays.util';

export class FileUtEntityService {


    static async saveFileUTs(systemUTEntity: SystemUTEntity, sourceFiles: SourceFile[]): Promise<void> {
        for (const sourceFile of sourceFiles) {
            await this.save(sourceFile, systemUTEntity);
        }
    }


    static async save(sourceFile: SourceFile, systemUTEntity: SystemUTEntity): Promise<void> {
        const path: string = sourceFile.getFilePath();
        let fileUTEntity: FileUTEntity = await FileUTEntity.findOne({path: path});
        if (!fileUTEntity) {
            fileUTEntity = new FileUTEntity(path, systemUTEntity);
        }
        await fileUTEntity.save();
        await ClassUtEntityService.saveClassUTEntities(fileUTEntity, sourceFile);
        await EnumUtEntityService.saveEnumUTEntities(fileUTEntity, sourceFile);
    }


    static async deleteFileUTEntities(fileUTPaths: string[]): Promise<string[]> {
        let filesToRemove: string[] = [];
        if (fileUTPaths?.length > 0) {
            // let kuzzyCloneFiles: string[] = fileUTPaths.map(f => kuzzyClonePath(f));
            let kuzzyFlagFiles: string[] = fileUTPaths.map(f => kuzzyPath(f));
            // filesToRemove.push(...kuzzyCloneFiles);
            filesToRemove.push(...kuzzyFlagFiles);
            for (const filePath of fileUTPaths) {
                filesToRemove.push(...await this.findBoundFileEntityPaths(filePath));
            }
            await db.connection.createQueryBuilder()
                .delete()
                .from(FileUTEntity)
                .where('path IN (:...paths)', {paths: fileUTPaths})
                .execute();
        }
        return filesToRemove;
    }


    private static async findBoundFileEntityPaths(fileUTPath: string): Promise<string[]> {
        const boundFileEntities: FileUTEntity = await this.findBoundFileEntities(fileUTPath);
        const paths: string[] = flat(boundFileEntities?.classUTs?.map(c => c.mocks?.map(m => kuzzyPath(m.path))));
        // const paths: string[] = flat(boundFileEntities?.classUTs?.map(c => c.mocks?.map(m => kuzzyClonePath(m.path))));
        return paths.concat(TestFileService.testFilePath(fileUTPath));
    }


    private static async findBoundFileEntities(fileUTPath: string): Promise<FileUTEntity> {
        return await db.connection.getRepository(FileUTEntity)
            .createQueryBuilder('fileUT')
            .leftJoinAndSelect('fileUT.classUTs', 'classUT')
            .leftJoinAndSelect('classUT.mocks', 'mocks')
            .leftJoinAndSelect('classUT.methodUTs', 'methodUT')
            .leftJoinAndSelect('methodUT.statementUTs', 'methodUTStatementUT')
            .leftJoinAndSelect('methodUTStatementUT.testCase', 'methodUTStatementUTTestCase')
            .leftJoinAndSelect('fileUT.functionUTs', 'functionUT')
            .leftJoinAndSelect('functionUT.statementUTs', 'functionUTStatementUT')
            .leftJoinAndSelect('functionUTStatementUT.testCase', 'functionUTStatementUTTestCase')
            .where('fileUT.path = :path', { path: fileUTPath })
            .getOne();
    }
}
