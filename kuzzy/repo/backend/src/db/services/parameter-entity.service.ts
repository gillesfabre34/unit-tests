import { ParameterEntity } from '../entities/parameter.entity';
import { KeyValueEntityService } from './key-value-entity.service';
import { MockEntityService } from './mock-entity.service';
import * as chalk from 'chalk';

export class ParameterEntityService {

    // TODO: parameters which are enums
    // static async createParameterEntities(parameterValues: any[] = []): Promise<ParameterEntity[]> {
    //     const parameterEntities: ParameterEntity[] = [];
    //     for (let i = 0; i < parameterValues.length; i++) {
    //         parameterEntities.push(await this.createParameterEntity(parameterValues[i], i))
    //     }
    //     return parameterEntities;
    // }
    //
    //
    // // TODO : parameters as ClassEnums
    // private static async createParameterEntity(parameterValue: any, index: number): Promise<ParameterEntity> {
    //     const parameterEntity = new ParameterEntity(index);
    //     const instanceId: number = getInstanceId(parameterValue);
    //     if (instanceId) {
    //         const mock: Mock = getMockInStoryWithInstanceId(instanceId);
    //         const declarationEntity = await MockEntityService.findDeclarationEntity(mock.mockFilePath);
    //         parameterEntity.keyValue = await KeyValueEntityService.create(`param_${index}`, mock.associatedMockDependency, undefined);
    //         parameterEntity.keyValue.declaration = declarationEntity;
    //     } else {
    //         parameterEntity.keyValue = await KeyValueEntityService.create(`param_${index}`, parameterValue, undefined);
    //     }
    //     return await parameterEntity.putAndReturnId();
    // }
}
