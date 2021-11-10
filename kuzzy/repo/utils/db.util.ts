import { BaseEntity, createConnection, getConnectionOptions } from 'typeorm';
import { DbLoggerService } from '../backend/src/db/services/db-logger.service';
import { db } from '../backend/src/db/const/db.const';
import { StatementUTEntity } from '../backend/src/db/entities/statement-ut.entity';
import { ParameterEntity } from '../backend/src/db/entities/parameter.entity';
import { PropertyEntity } from '../backend/src/db/entities/property.entity';
import { ValueEntity } from '../backend/src/db/entities/value.entity';
import { TestCaseEntity } from '../backend/src/db/entities/test-case.entity';
import { KeyValueEntity } from '../backend/src/db/entities/key-value.entity';
import { DeclarationEntity } from '../backend/src/db/entities/declaration.entity';
import { EnumUTEntity } from '../backend/src/db/entities/enum-ut.entity';
import { MockEntity } from '../backend/src/db/entities/mock.entity';
import { MethodUTEntity } from '../backend/src/db/entities/method-ut.entity';
import { ClassUTEntity } from '../backend/src/db/entities/class-ut.entity';
import { SystemUTEntity } from '../backend/src/db/entities/system-ut.entity';
import { FileUTEntity } from '../backend/src/db/entities/file-ut.entity';
import * as chalk from 'chalk';
import { TConstructor } from '../shared/types/constructor.type';

// -----------------------------------------   Clear DB   -----------------------------------------

// TODO : DELETE FILE







getConnectionOptions().then(connectionOptions => {
    createConnection(Object.assign(connectionOptions, {
        logger: new DbLoggerService()
    })).then(async connection => {
        try {
            db.connect(connection);
            await deleteAll(StatementUTEntity);
            await deleteAll(ParameterEntity);
            await deleteAll(PropertyEntity);
            await deleteAll(ValueEntity);
            await deleteAll(TestCaseEntity);
            await deleteAll(KeyValueEntity);
            await deleteAll(DeclarationEntity);
            await deleteAll(EnumUTEntity);
            await deleteAll(ParameterEntity);
            await deleteAll(MockEntity);
            await deleteAll(MethodUTEntity);
            await deleteAll(ClassUTEntity);
            await deleteAll(FileUTEntity);
            await deleteAll(SystemUTEntity);
            console.log(chalk.yellowBright('DATABASE CLEARED'));
            await db.connection.close();
        } catch (err) {
            console.error(chalk.red("Error in process : " + err.stack));
        }

    })
});


export async function deleteAll(entity: TConstructor<BaseEntity>): Promise<void> {
    await db.connection.createQueryBuilder().delete().from(entity).execute();
}

