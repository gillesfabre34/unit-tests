import { ClassEnum } from '../../flag/models/class-enum.model';
import { KeyValueEntity } from '../entities/key-value.entity';
import { ValueEntity } from '../entities/value.entity';
import { ClassEnumService } from '../../flag/services/class-enum.service';
import { EnumUtEntityService } from './enum-ut-entity.service';
import { MockDependency } from '../../capture/models/mock-dependency.model';
import { MockEntityService } from './mock-entity.service';
import { MockEntity } from '../entities/mock.entity';
import * as chalk from 'chalk';
import { isPrimitive, PrimitiveType } from '../../../shared/utils/primitives.util';
import { prettify } from '../../../shared/utils/prettify.util';

export class KeyValueEntityService {

    static async create(key: string, value: any, classEnum?: ClassEnum): Promise<KeyValueEntity> {
        if (isPrimitive(value) && !classEnum) {
            return this.createPrimitiveValueEntity(key, prettify(value));
        } else if (this.returnedValueIsEnum(value, classEnum)) {
            return await this.createEnumKeyValueEntity(key, value, classEnum);
        } else if (value instanceof MockDependency) {
            return await this.createMockDependencyKeyValueEntity(key, value);
        } else if (Array.isArray(value)) { // TODO: check if this case may only happens when it is a classEnum array
            return await this.createEntitiesArray(key, value, classEnum);
        } else if (value === null) {
            return new KeyValueEntity(key, value, [new ValueEntity('kuzzy_null')]);
        } else if (typeof value === 'object') {
            return this.createObjectValueEntity(key, value);
        } else {
            console.log(chalk.redBright('WARNING : UNKNOWN KEY VALUE CASE'), key, value);
            return undefined;
        }
    }


    private static async createPrimitiveValueEntity(key: string, value: string): Promise<KeyValueEntity> {
        const keyValueEntity = new KeyValueEntity(key, false, [new ValueEntity(value)]);
        keyValueEntity.primitiveType = typeof value as PrimitiveType;
        return await keyValueEntity.save();
    }


    private static returnedValueIsEnum(value: any, classEnum: ClassEnum): boolean {
        return !Array.isArray(value) && !!classEnum;
    }


    private static async createEnumKeyValueEntity(key: string, value: any, classEnum: ClassEnum): Promise<KeyValueEntity> {
        const equalValue: string = this.getEqualValue(value, classEnum);
        const keyValueEntity: KeyValueEntity = await this.createPrimitiveValueEntity(key, equalValue);
        keyValueEntity.declaration = await EnumUtEntityService.findEnumUTDeclaration(classEnum);
        return await keyValueEntity.save();
    }


    private static getEqualValue(value: any, classEnum?: ClassEnum): string {
        if (classEnum) {
            return ClassEnumService.getEnumValueAsString(classEnum, value);
        } else {
            return prettify(value);
        }
    }


    private static async createEntitiesArray(key: string, values: any[], classEnum: ClassEnum): Promise<KeyValueEntity> {
        return !!classEnum ? await this.createEnumsArrayEntity(key, values, classEnum) : await this.createAnyEntitiesArray(key, values);
    }


    private static async createEnumsArrayEntity(key: string, values: any[], classEnum: ClassEnum): Promise<KeyValueEntity> {
        const valueEntities: ValueEntity[] = [];
        for (const value of values) {
            valueEntities.push(new ValueEntity(this.getEqualValue(value, classEnum)));
        }
        const keyValueEntity: KeyValueEntity = new KeyValueEntity(key, true, valueEntities);
        keyValueEntity.declaration = await EnumUtEntityService.findEnumUTDeclaration(classEnum);
        keyValueEntity.isArray = true;
        return await keyValueEntity.save();
    }


    private static async createAnyEntitiesArray(key: string, values: any[]): Promise<KeyValueEntity> {
        const prettified: string = prettify(values);
        const prettifiedWithoutDuplicateBrackets: string = prettified.slice(1, -1);
        const valueEntity = new ValueEntity(prettifiedWithoutDuplicateBrackets);
        const keyValueEntity: KeyValueEntity = new KeyValueEntity(key, true, [valueEntity]);
        keyValueEntity.isArray = true;
        return await keyValueEntity.save();
    }


    private static async createMockDependencyKeyValueEntity(key: string, value: MockDependency): Promise<KeyValueEntity> {
        const keyValueEntity = new KeyValueEntity(key, false, [new ValueEntity(value.mockName)]);
        keyValueEntity.declaration = await MockEntityService.findDeclarationEntity(value.mockFilePath);
        if (!keyValueEntity.declaration) {
            const mockEntity: MockEntity = await MockEntityService.save(value.mock);
            keyValueEntity.declaration = mockEntity.declaration;
        }
        return await keyValueEntity.save();
    }


    private static async createObjectValueEntity(key: string, value: string): Promise<KeyValueEntity> {
        const keyValueEntity = new KeyValueEntity(key, false, [new ValueEntity(prettify(value))]);
        keyValueEntity.primitiveType = typeof value as PrimitiveType;
        return await keyValueEntity.save();
    }

}
