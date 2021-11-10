import * as chalk from 'chalk';
import { HttpException } from '@nestjs/common';

export function throwHttpException(message = 'Http exception', errorStatus: number, value: any = ''): any {
    console.log(chalk.redBright(message), value);
    throw new HttpException(message, errorStatus);
}

export function throwCustom(message = 'Error encountered', value: any = ''): any {
    console.log(chalk.redBright(message), value);
    throw Error(message);
}
