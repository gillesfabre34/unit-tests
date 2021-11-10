import { Mock } from '../../capture/models/mock.model';
import { MockEntity } from '../entities/mock.entity';
import { ClassUtEntityService } from './class-ut-entity.service';
import { originalPath } from '../../utils/kuzzy-folder.util';
import { db } from '../../init/const/db.const';
import { GLOBAL } from '../../init/const/global.const';
import { DeclarationEntity } from '../entities/declaration.entity';
import { ParameterEntityService } from './parameter-entity.service';
import { PostTestCaseDtoMock } from '../../../dtos/test-cases/test-case.dto';

export class MockEntityService {

    static save(mock: Mock): PostTestCaseDtoMock {
        let postTestCaseDtoMock: PostTestCaseDtoMock = this.existingRecord(mock.name);
        if (postTestCaseDtoMock) {
            return postTestCaseDtoMock;
        } else {
            postTestCaseDtoMock = {
                classId: mock.classId,
                code: mock.code,
                // constructorArguments: ParameterEntityService.createParameterEntities(mock.constructorArguments),
                mockFilePath: originalPath(mock.fileUTPath),
                // mocksDependencies: this.createMockDependenciesEntities(mock)
            };
            return postTestCaseDtoMock;
        }
    }


    private static existingRecord(mockName: string): PostTestCaseDtoMock {
        return undefined;
        // return await MockEntity.findOne({ name: mockName });
    }

    // static async save(mock: Mock): Promise<MockEntity> {
    //     let mockEntity: MockEntity = await this.existingRecord(mock.name);
    //     if (mockEntity) {
    //         return mockEntity;
    //     } else {
    //         mockEntity = new MockEntity(mock.mockFilePath, mock.name, mock.code);
    //         mockEntity.mocksDependencies = await this.createMockDependenciesEntities(mock);
    //         mockEntity.classUT = await ClassUtEntityService.findClassUTEntity(mock.className, originalPath(mock.fileUTPath));
    //         mockEntity.constructorArguments = await ParameterEntityService.createParameterEntities(mock.constructorArguments);
    //         mock.entity = await mockEntity.save();
    //         const declarationEntity = new DeclarationEntity();
    //         declarationEntity.mock = mock.entity;
    //         mock.entity.declaration = await declarationEntity.save();
    //         return mock.entity;
    //     }
    // }


    // private static async existingRecord(mockName: string): Promise<MockEntity> {
    //     return await MockEntity.findOne({ name: mockName });
    // }


    // private static async createMockDependenciesEntities(mock: Mock): Promise<MockEntity[]> {
    //     const entities: MockEntity[] = [];
    //     for (const mockDependency of mock.mocksDependencies) {
    //         entities.push(await this.save(mockDependency.mock));
    //     }
    //     return entities;
    // }



    static async findDeclarationEntity(mockFilePath: string): Promise<DeclarationEntity> {
        return await db.connection.getRepository(DeclarationEntity)
            .createQueryBuilder('declaration')
            .leftJoinAndSelect('declaration.mock', 'mock')
            .leftJoinAndSelect('mock.classUT', 'classUT')
            .leftJoinAndSelect('classUT.fileUT', 'fileUT')
            .leftJoinAndSelect('fileUT.systemUT', 'systemUT')
            .where('systemUT.name = :name', { name: GLOBAL.systemUT.name })
            .andWhere('mock.name = :path', { path: mockFilePath })
            .getOne();
    }


    // static async save(mock: Mock): Promise<MockEntity> {
    //     let mockEntity: MockEntity = await this.existingRecord(mock.name);
    //     if (mockEntity) {
    //         return mockEntity;
    //     } else {
    //         mockEntity = new MockEntity(mock.mockFilePath, mock.name, mock.code);
    //         mockEntity.mocksDependencies = await this.createMockDependenciesEntities(mock);
    //         mockEntity.classUT = await ClassUtEntityService.findClassUTEntity(mock.className, originalPath(mock.fileUTPath));
    //         mockEntity.constructorArguments = await ParameterEntityService.createParameterEntities(mock.constructorArguments);
    //         mock.entity = await mockEntity.save();
    //         const declarationEntity = new DeclarationEntity();
    //         declarationEntity.mock = mock.entity;
    //         mock.entity.declaration = await declarationEntity.save();
    //         return mock.entity;
    //     }
    // }
    //
    //
    // private static async existingRecord(mockName: string): Promise<MockEntity> {
    //     return await MockEntity.findOne({ name: mockName });
    // }
    //
    //
    // private static async createMockDependenciesEntities(mock: Mock): Promise<MockEntity[]> {
    //     const entities: MockEntity[] = [];
    //     for (const mockDependency of mock.mocksDependencies) {
    //         entities.push(await this.save(mockDependency.mock));
    //     }
    //     return entities;
    // }
    //
    //
    //
    // static async findDeclarationEntity(mockFilePath: string): Promise<DeclarationEntity> {
    //     return await db.connection.getRepository(DeclarationEntity)
    //         .createQueryBuilder('declaration')
    //         .leftJoinAndSelect('declaration.mock', 'mock')
    //         .leftJoinAndSelect('mock.classUT', 'classUT')
    //         .leftJoinAndSelect('classUT.fileUT', 'fileUT')
    //         .leftJoinAndSelect('fileUT.systemUT', 'systemUT')
    //         .where('systemUT.name = :name', { name: GLOBAL.systemUT.name })
    //         .andWhere('mock.name = :path', { path: mockFilePath })
    //         .getOne();
    // }

}
