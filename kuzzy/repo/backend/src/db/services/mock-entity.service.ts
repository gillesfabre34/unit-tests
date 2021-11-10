import { MockEntity } from '../entities/mock.entity';
import { ClassUtEntityService } from './class-ut-entity.service';
import { DeclarationEntity } from '../entities/declaration.entity';
import { ParameterEntityService } from './parameter-entity.service';

export class MockEntityService {

    // static async putAndReturnId(mock: Mock): Promise<MockEntity> {
    //     let mockEntity: MockEntity = await this.existingRecord(mock.name);
    //     if (mockEntity) {
    //         return mockEntity;
    //     } else {
    //         mockEntity = new MockEntity(mock.mockFilePath, mock.name, mock.code);
    //         mockEntity.mocksDependencies = await this.createMockDependenciesEntities(mock);
    //         mockEntity.classUT = await ClassUtEntityService.findClassUTEntity(mock.className, originalPath(mock.fileUTPath));
    //         mockEntity.constructorArguments = await ParameterEntityService.createParameterEntities(mock.constructorArguments);
    //         mock.entity = await mockEntity.putAndReturnId();
    //         const declarationEntity = new DeclarationEntity();
    //         declarationEntity.mock = mock.entity;
    //         mock.entity.declaration = await declarationEntity.putAndReturnId();
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
    //         entities.push(await this.putAndReturnId(mockDependency.mock));
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
    //         .where('systemUT.name = :name', { name: GLOBAL.appName })
    //         .andWhere('mock.name = :path', { path: mockFilePath })
    //         .getOne();
    // }

}
