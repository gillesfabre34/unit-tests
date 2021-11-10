import { Body, Controller, Get, Post } from '@nestjs/common';
import { TestCaseService } from './test-case.service';
import { GetTestCasesDto } from '../../../dtos/test-cases/get-test-cases.dto';
import { GeneseMapper } from 'genese-mapper';
import * as chalk from 'chalk';
import { PostTestCaseDto } from '../../../dtos/test-cases/post-test-case.dto';

@Controller('test-case')
export class TestCaseController {

    constructor(private testCaseService: TestCaseService) { }

    @Get()
    async getTestCases(): Promise<GetTestCasesDto> {
        const getTestCasesDto: GetTestCasesDto = {
            testCases: []
        };
        return getTestCasesDto;
    }


    @Post()
    async postTestCases(@Body() body: PostTestCaseDto): Promise<void> {
        const geneseMapper = new GeneseMapper(PostTestCaseDto);
        const postTestCaseDto: PostTestCaseDto = geneseMapper.map(body);
        console.log(chalk.blueBright('TestCase to save : '), postTestCaseDto);
        // console.log(chalk.blueBright('TestCase to save : '), body);
    }

}
