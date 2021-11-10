import axios from 'axios';
import { throwCustom } from '../../../shared/utils/errors.util';
import { GLOBAL } from '../../init/const/global.const';
import * as chalk from 'chalk';
import { Mapper } from '@genese/mapper';

export class SystemUtDataService {


    static async putAndReturnId(name: string): Promise<number> {
        try {
            const response = await axios.put(`${GLOBAL.apiUrl}/system-ut`, {
                name: name
            });
            return await Mapper.create(Number, response?.data);
        } catch(err) {
            return throwCustom(`Error saving SystemUT ${err.response?.data}`, err);
        }
    }


    static async dropSystemUT(systemUT: string): Promise<void> {
        await axios.delete(`${GLOBAL.apiUrl}/system-ut/${systemUT}`);
    }

}
