import { GLOBAL } from '../../init/const/global.const';
import { MockFile } from '../models/mock-file.model';
import { MockEntity } from '../../db/entities/mock.entity';
import { db } from '../../init/const/db.const';
import { writeFile } from '../../../shared/utils/file-system.util';

export class MockFileService {

    static async writeMockFiles(): Promise<void> {
        const mockEntities: MockEntity[] = await this.findMockEntities();
        const mockFiles: MockFile[] = this.createMockFiles(mockEntities);
        this.setCode(mockFiles);
        await this.write(mockFiles);
    }


    private static async findMockEntities(): Promise<MockEntity[]> {
        return await db.connection.getRepository(MockEntity)
            .createQueryBuilder('mock')
            .leftJoinAndSelect('mock.classUT', 'classUT')
            .leftJoinAndSelect('classUT.fileUT', 'fileUT')
            .leftJoinAndSelect('fileUT.systemUT', 'systemUT')
            .leftJoinAndSelect('mock.constructorArguments', 'constructorArguments')
            .leftJoinAndSelect('mock.mocksDependencies', 'mocksDependencies')
            .leftJoinAndSelect('mocksDependencies.classUT', 'mocksDependenciesClassUT')
            .leftJoinAndSelect('mocksDependenciesClassUT.fileUT', 'mocksDependenciesFileUT')
            .leftJoinAndSelect('constructorArguments.keyValue', 'constructorArgumentsKeyValue')
            .leftJoinAndSelect('constructorArgumentsKeyValue.values', 'constructorArgumentValues')
            .leftJoinAndSelect('constructorArgumentsKeyValue.declaration', 'constructorArgumentDeclaration')
            .leftJoinAndSelect('constructorArgumentDeclaration.enumUT', 'constructorArgumentEnumUT')
            .leftJoinAndSelect('constructorArgumentDeclaration.classUT', 'constructorArgumentClassUT')
            .leftJoinAndSelect('constructorArgumentEnumUT.fileUT', 'constructorArgumentEnumUTFileUT')
            .leftJoinAndSelect('constructorArgumentClassUT.fileUT', 'constructorArgumentClassUTFileUT')
            .where('systemUT.name = :name', { name: GLOBAL.appName })
            .getMany();
    }


    private static createMockFiles(mockFileEntities: MockEntity[]): MockFile[] {
        const mockFiles: MockFile[] = [];
        for (const mockFileEntity of mockFileEntities) {
            mockFiles.push(new MockFile(mockFileEntity));
        }
        return mockFiles;
    }


    private static setCode(mockFiles: MockFile[]): void {
        for (const mockFile of mockFiles) {
            mockFile.setImportLines();
            mockFile.setCode();
        }
    }


    private static async write(mockFiles: MockFile[]): Promise<void> {
        for (const mockFile of mockFiles) {
            await writeFile(mockFile.mockFilePath, mockFile.code);
        }
    }

}
