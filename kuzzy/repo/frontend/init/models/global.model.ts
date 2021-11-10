import { Mock } from '../../capture/models/mock.model';
import { Project, SourceFile } from 'ts-morph';
import { DynamicSystemState } from '../../system/models/dynamic-app-state.model';
import { SystemState } from '../../system/models/system-state.model';
import { Story } from '../../system/models/story.model';
import 'reflect-metadata';
import { SystemUT } from '../../system/models/system-ut.model';
import { Enumerable } from '../../flag/models/enumerable.model';
import { SystemUTEntity } from '../../db/entities/system-ut.entity';
import * as chalk from 'chalk';
import { lastElement } from '../../../shared/utils/arrays.util';
import { TestCaseDto } from '../../../dtos/test-cases/test-case.dto';


export class Global {

    algoPath: string = undefined;
    apiUrl: string = undefined;
    configFilePath: string = undefined;
    dynamicAppState: DynamicSystemState = undefined;
    enumerables: Enumerable[] = [];
    entryPoint: string = undefined;
    fileUtsToFlag: SourceFile[] = [];
    flaggedProject: Project = undefined;
    kuzzyPath: string = undefined;
    mocks: Mock[] = [];
    project: Project = undefined;
    projectPath: string = undefined;
    start: number = undefined;
    story: Story = undefined;
    systemUT: SystemUT = undefined;
    systemUTEntity: SystemUTEntity = undefined;
    testCasesDto: TestCaseDto[] = [];


    get lastSystemState(): SystemState {
        return lastElement(this.story.systemStates)
    }

    addMock(mock: Mock): void {
        this.mocks.push(mock);
    }

    logDuration(message: string): void {
        console.log(chalk.blueBright(`${message} : TIME `), Date.now() - this.start);
    }

}
