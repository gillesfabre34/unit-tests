import { Body, Controller, Delete, HttpException, Param, Put } from '@nestjs/common';
import { SystemUtService } from './system-ut.service';
import * as chalk from 'chalk';
import { PutSystemUtDto } from '../../../dtos/system-ut/put-system-ut.dto';
import { throwHttpException } from '../../../shared/utils/errors.util';
import { dropSystemUT } from '../../start/main-nest';
import { Mapper } from '@genese/mapper';

@Controller('system-ut')
export class SystemUtController {

    constructor(private systemUtService: SystemUtService) { }

    @Put()
    async putSystemUT(@Body() body: any): Promise<number> {
        console.log(chalk.blueBright('Put SystemUT : '), body);
        const putSystemUTDto: PutSystemUtDto = await Mapper.create(PutSystemUtDto, body);
        console.log(chalk.magentaBright('Put SystemUT : '), putSystemUTDto);
        if (!body?.name) {
            throw new HttpException('System name is undefined', 400);
        }
        return await this.systemUtService.putSystemUT(body.name);
    }


    @Delete(':name')
    async deleteSystemUT(@Param('name') name: any): Promise<string> {
        try {
            console.log(chalk.yellowBright('Will drop SystemUT : '), name);
            // TODO : drop only corresponding SystemUT
            await dropSystemUT();
            console.log(chalk.yellowBright('SystemUT dropped'));
            return 'SystemUT dropped';
        } catch (err) {
            return throwHttpException('Error: SystemUT not dropped.', err);
        }
    }

}
