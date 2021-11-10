import { ClassEnum } from '../../flag/models/class-enum.model';
import { ValueEntity } from '../entities/value.entity';
import { ClassEnumService } from '../../flag/services/class-enum.service';
import { EnumUtEntityService } from './enum-ut-entity.service';
import { MockDependency } from '../../capture/models/mock-dependency.model';
import { MockEntityService } from './mock-entity.service';
import { MockEntity } from '../entities/mock.entity';
import * as chalk from 'chalk';
import { isPrimitive, PrimitiveType } from '../../../shared/utils/primitives.util';
import { prettify } from '../../../shared/utils/prettify.util';
import { PostTestCaseDtoKeyValue } from '../../../dtos/test-cases/test-case.dto';

export class KeyValueDataService {

    static create(key: string, value: any, classEnum?: ClassEnum): PostTestCaseDtoKeyValue {
        if (isPrimitive(value) && !classEnum) {
            return this.createPrimitiveValueEntity(key, prettify(value));
        } else if (this.returnedValueIsEnum(value, classEnum)) {
            return this.createEnumPostTestCaseDtoKeyValue(key, value, classEnum);
        } else if (value instanceof MockDependency) {
            return this.createMockDependencyPostTestCaseDtoKeyValue(key, value);
        } else if (Array.isArray(value)) { // TODO: check if this case may only happens when it is a classEnum array
            return this.createEntitiesArray(key, value, classEnum);
        } else if (value === null) {
            return {
                key: key,
                isArray: false,
                values: ['kuzzy_null']
            };
        } else if (typeof value === 'object') {
            return this.createObjectValueEntity(key, value);
        } else {
            console.log(chalk.redBright('WARNING : UNKNOWN KEY VALUE CASE'), key, value);
            return undefined;
        }
    }


    private static createPrimitiveValueEntity(key: string, value: string): PostTestCaseDtoKeyValue {
        const postTestCaseDtoKeyValue: PostTestCaseDtoKeyValue = {
            key: key,
            isArray: false,
            primitiveType: typeof value,
            values: [value]
        }
        return postTestCaseDtoKeyValue;
    }


    private static returnedValueIsEnum(value: any, classEnum: ClassEnum): boolean {
        return !Array.isArray(value) && !!classEnum;
    }


    private static createEnumPostTestCaseDtoKeyValue(key: string, value: any, classEnum: ClassEnum): PostTestCaseDtoKeyValue {
        const equalValue: string = this.getEqualValue(value, classEnum);
        const keyValueEntity: PostTestCaseDtoKeyValue = this.createPrimitiveValueEntity(key, equalValue);
        keyValueEntity.declaration = EnumUtEntityService.findEnumUTDeclaration(classEnum);
        return keyValueEntity.save();
    }


    private static getEqualValue(value: any, classEnum?: ClassEnum): string {
        if (classEnum) {
            return ClassEnumService.getEnumValueAsString(classEnum, value);
        } else {
            return prettify(value);
        }
    }


    private static createEntitiesArray(key: string, values: any[], classEnum: ClassEnum): PostTestCaseDtoKeyValue {
        return !!classEnum ? this.createEnumsArrayEntity(key, values, classEnum) : this.createAnyEntitiesArray(key, values);
    }


    private static createEnumsArrayEntity(key: string, values: any[], classEnum: ClassEnum): PostTestCaseDtoKeyValue {
        const valueEntities: ValueEntity[] = [];
        for (const value of values) {
            valueEntities.push(new ValueEntity(this.getEqualValue(value, classEnum)));
        }
        const keyValueEntity: PostTestCaseDtoKeyValue = new PostTestCaseDtoKeyValue(key, true, valueEntities);
        keyValueEntity.declaration = EnumUtEntityService.findEnumUTDeclaration(classEnum);
        keyValueEntity.isArray = true;
        return keyValueEntity.save();
    }


    private static createAnyEntitiesArray(key: string, values: any[]): PostTestCaseDtoKeyValue {
        const prettified: string = prettify(values);
        const prettifiedWithoutDuplicateBrackets: string = prettified.slice(1, -1);
        const valueEntity = new ValueEntity(prettifiedWithoutDuplicateBrackets);
        const keyValueEntity: PostTestCaseDtoKeyValue = new PostTestCaseDtoKeyValue(key, true, [valueEntity]);
        keyValueEntity.isArray = true;
        return keyValueEntity.save();
    }


    private static createMockDependencyPostTestCaseDtoKeyValue(key: string, value: MockDependency): PostTestCaseDtoKeyValue {
        const keyValueEntity = new PostTestCaseDtoKeyValue(key, false, [new ValueEntity(value.mockName)]);
        keyValueEntity.declaration = MockEntityService.findDeclarationEntity(value.mockFilePath);
        if (!keyValueEntity.declaration) {
            const mockEntity: MockEntity = MockEntityService.save(value.mock);
            keyValueEntity.declaration = mockEntity.declaration;
        }
        return keyValueEntity.save();
    }


    private static createObjectValueEntity(key: string, value: string): PostTestCaseDtoKeyValue {
        const keyValueEntity = new PostTestCaseDtoKeyValue(key, false, [new ValueEntity(prettify(value))]);
        keyValueEntity.primitiveType = typeof value as PrimitiveType;
        return keyValueEntity.save();
    }

}
