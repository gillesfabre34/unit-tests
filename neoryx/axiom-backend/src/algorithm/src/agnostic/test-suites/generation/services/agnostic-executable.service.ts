import { TestSuite } from '../../../../languages/ts/test-suites/generation/models/test-suite.model';
import * as chalk from 'chalk';
import { AgnosticTestSuite } from '../models/agnostic-test-suite.model';
import { PropertyValue } from '../models/property-value.model';
import { SpiesService } from '../../../init/services/spies.service';
import { GLOBALS } from '../../../init/constants/globals.const';
import { Bug } from '../../../reports/core/bugs/models/bug.model';
import { AgnosticSutMethod } from '../../sut/models/agnostic-sut-method.model';
import { BugType } from '../../../reports/core/bugs/enums/bug-type.enum';

export abstract class AgnosticExecutableService {

    spiesService?: SpiesService = undefined;
    testSuite?: AgnosticTestSuite = undefined;
    abstract async executeMethod(mock: any, methodDeclaration: any): Promise<void>;
    abstract async getNewObject(sutMethod: AgnosticSutMethod): Promise<any>;


    constructor(testSuite: TestSuite) {
        if(!testSuite?.sutMethod?.sutClass) {
            console.log(chalk.red('ERROR: no SutMethod in this TestSuite '), testSuite);
        }
        if(!testSuite?.sutMethod?.agnosticMethod?.methodDeclaration) {
            console.log(chalk.red('ERROR: no methodDeclaration found for this TestSuite '));
        }
        this.testSuite = testSuite;
    }


    get sutMethod(): AgnosticSutMethod {
        if (!this.testSuite?.sutMethod) {
            GLOBALS.stats.report.addBug(new Bug(BugType.METHOD_NOT_FOUND_AT_EXECUTION));
            throw Error(BugType.METHOD_NOT_FOUND_AT_EXECUTION);
        }
        return this.testSuite.sutMethod;
    }


    async executeAndReturnUpdatedTestSuite(): Promise<TestSuite> {
        try {
            if (!this.sutMethod?.agnosticMethod?.methodDeclaration) {
                console.log(chalk.redBright(`No methodDeclaration for '${this.sutMethod.name}'`))
                throw Error(`No methodDeclaration for '${this.sutMethod.name}'`);
            }
            const instance: any = await this.getInstance();
            await this.executeMethod(instance, this.sutMethod.agnosticMethod.methodDeclaration);
            return this.testSuite;
        } catch (err) {
            throw Error(err);
        }
    }


    async getInstance(): Promise<any> {
        const newObject: any = await this.getNewObject(this.sutMethod);
        return this.addMockProperties(newObject, this.testSuite.instanceProperties);
    }


    addMockProperties(newObject: any, testSuiteInstance: PropertyValue[]): any {
        if (!this.testSuite.instanceProperties) {
            return newObject;
        }
        for (const propertyValue of testSuiteInstance) {
            newObject[propertyValue.name] = propertyValue.value;
        }
        return newObject;
    }

}
