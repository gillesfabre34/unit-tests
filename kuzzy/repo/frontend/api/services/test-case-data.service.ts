import { TestCaseDto } from '../../../dtos/test-cases/test-case.dto';
import axios from 'axios';
import { GLOBAL } from '../../init/const/global.const';
import * as chalk from 'chalk';
import { throwCustom } from '../../../shared/utils/errors.util';
import { Mapper } from '@genese/mapper';

export class TestCaseDataService {

    static async findTestCases(): Promise<TestCaseDto[]> {
        try {
            const response = await axios.get(`${GLOBAL.apiUrl}/test-case`);
            return Mapper.create([TestCaseDto], response.data);
            // return response.data as TestCaseDto[];
        } catch(err) {
            console.log(chalk.red('Error getting test cases from database'), err.response?.data);
            return [];
        }
    }


    static async postTestCase(testCaseDto: TestCaseDto): Promise<void> {
        try {
            await axios.post(`${GLOBAL.apiUrl}/test-case`, {
                testCaseDto: testCaseDto
            });
        } catch(err) {
            throwCustom('Error getting test cases from database', err.response?.data);
        }
    }

}
