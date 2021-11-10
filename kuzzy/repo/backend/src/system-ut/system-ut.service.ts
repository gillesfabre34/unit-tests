import { Injectable } from '@nestjs/common';
import { SystemUTEntity } from '../db/entities/system-ut.entity';
import * as chalk from 'chalk';

@Injectable()
export class SystemUtService {

    // async getSystemUt(name: string): Promise<any> {
    //     return await SystemUTEntity.find({ name: name });
    // }


    async putSystemUT(name: string): Promise<number> {
        console.log(chalk.yellowBright('Put SystemUT : '), name);
        let previousSystemUTEntity: SystemUTEntity = await SystemUTEntity.findOne({name: name});
        if (!previousSystemUTEntity) {
            previousSystemUTEntity = await SystemUTEntity.save(new SystemUTEntity(name));
        }
        return previousSystemUTEntity.id;
    }
}
