import { BaseEntity, createConnection, getConnectionOptions } from 'typeorm';
import { db } from '../src/db/const/db.const';
import { NestFactory } from '@nestjs/core';
import { AppModule } from '../src/app.module';
import * as chalk from 'chalk';
import { SystemUTEntity } from '../src/db/entities/system-ut.entity';
import { TConstructor } from '../../shared/types/constructor.type';
import { StatementUTEntity } from '../src/db/entities/statement-ut.entity';
import { throwCustom } from '../../shared/utils/errors.util';
import { FileUTEntity } from '../src/db/entities/file-ut.entity';
import { ClassUTEntity } from '../src/db/entities/class-ut.entity';
import { MethodUTEntity } from '../src/db/entities/method-ut.entity';
import { MockEntity } from '../src/db/entities/mock.entity';
import { ParameterEntity } from '../src/db/entities/parameter.entity';
import { EnumUTEntity } from '../src/db/entities/enum-ut.entity';
import { DeclarationEntity } from '../src/db/entities/declaration.entity';
import { KeyValueEntity } from '../src/db/entities/key-value.entity';
import { TestCaseEntity } from '../src/db/entities/test-case.entity';
import { ValueEntity } from '../src/db/entities/value.entity';
import { PropertyEntity } from '../src/db/entities/property.entity';
import { DbLoggerService } from '../../frontend/db/services/db-logger.service';

console.log('Launch backend...');

export function startBackend(clear: boolean) {
    getConnectionOptions().then(connectionOptions => {
        createConnection(Object.assign(connectionOptions, {
            logger: new DbLoggerService()
        })).then(async connection => {
            try {
                db.connect(connection);
                if (clear) {
                    await dropSystemUT();
                }
                await bootstrap();
                console.log(chalk.yellowBright('Launched backend'));
            } catch (err) {
                console.error(chalk.redBright("Error in process : "), err.message);
            }
        })
    });
}


// -----------------------------------------   Clear DB   -----------------------------------------


export async function dropSystemUT(): Promise<void> {
    try {
        await deleteAll(StatementUTEntity);
        await deleteAll(ParameterEntity);
        await deleteAll(PropertyEntity);
        await deleteAll(ValueEntity);
        await deleteAll(TestCaseEntity);
        await deleteAll(KeyValueEntity);
        await deleteAll(DeclarationEntity);
        await deleteAll(EnumUTEntity);
        await deleteAll(MockEntity);
        await deleteAll(MethodUTEntity);
        await deleteAll(ClassUTEntity);
        await deleteAll(FileUTEntity);
        await deleteAll(SystemUTEntity);
        console.log(chalk.yellowBright('DATABASE CLEARED'));
    } catch (err) {
        throwCustom('Error clearing database', err);
    }
}


async function deleteAll(entity: TConstructor<BaseEntity>): Promise<void> {
    await db.connection.createQueryBuilder().delete().from(entity).execute();
}


// ------------------------------------   Launch NestJs app   -----------------------------------------


async function bootstrap() {
    const app = await NestFactory.create(AppModule);
    await app.listen(3001);
}
