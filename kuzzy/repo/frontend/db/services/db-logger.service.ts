import { Logger, QueryRunner } from 'typeorm';
import * as chalk from 'chalk';

export class DbLoggerService implements Logger {

    log(level: "log" | "info" | "warn", message: any, queryRunner?: QueryRunner): any {
    }

    logMigration(message: string, queryRunner?: QueryRunner): any {
    }

    logQuery(query: string, parameters?: any[], queryRunner?: QueryRunner): any {
        if (query.includes('zzz')) {
            console.log(chalk.yellowBright('QUERY'), query);
            console.log(chalk.yellowBright('PARAMETERS'), parameters);
        }
    }

    logQueryError(error: string | Error, query: string, parameters?: any[], queryRunner?: QueryRunner): any {
        console.log(chalk.redBright('QUERY'), query);
        console.log(chalk.redBright('PARAMETERS'), parameters);
    }

    logQuerySlow(time: number, query: string, parameters?: any[], queryRunner?: QueryRunner): any {
    }

    logSchemaBuild(message: string, queryRunner?: QueryRunner): any {
    }



}
