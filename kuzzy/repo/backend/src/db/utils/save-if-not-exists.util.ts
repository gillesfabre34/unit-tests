import { BaseEntity } from 'typeorm';
import { db } from '../const/db.const';
import { TConstructor } from '../../../../shared/types/constructor.type';
import { FindConditions } from 'typeorm/find-options/FindConditions';
import * as chalk from 'chalk';

export async function saveIfNotExists<T extends BaseEntity>(entity: T, conditions: FindConditions<T>): Promise<T> {
    // console.log(chalk.blueBright('CONDITIONSSSSS'), conditions);
    let dbEntity: T = await db.connection.getRepository(entity.constructor).findOne(conditions) as T;
    // console.log(chalk.blueBright('CONDITIONSSSSS dbEntity'), dbEntity);
    // console.log(chalk.blueBright('CONDITIONSSSSS entity'), entity);
    return dbEntity ?? await entity.save();
}

// export async function saveIfNotExists<T extends BaseEntity>(entityType: TConstructor<T>, conditions: FindConditions<T>, constructorParametersOrEntity: ConstructorParameters<TConstructor<T>> | T): Promise<T> {
//     let entity: T = await db.connection.getRepository(entityType).findOne(conditions);
//     if (entity) {
//         return entity;
//     } else if (Array.isArray(constructorParametersOrEntity)) {
//         return await new entityType(constructorParametersOrEntity).save();
//     } else {
//         return constructorParametersOrEntity.save();
//     }
// }
