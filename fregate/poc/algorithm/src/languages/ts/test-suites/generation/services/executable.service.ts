import { AgnosticExecutableService } from '../../../../../agnostic/test-suites/generation/services/agnostic-executable.service';
import { SutMethod } from '../../sut/models/sut-method.model';
import { ClassDeclaration, ConstructorDeclaration, MethodDeclaration, SourceFile } from 'ts-morph';
import * as chalk from 'chalk';
import { SpiesService } from '../../../../../agnostic/init/services/spies.service';
import { MethodStatsService } from '../../../../../agnostic/reports/dashboard/services/method-stats.service';
import { Bug } from '../../../../../agnostic/reports/core/bugs/models/bug.model';
import { BugType } from '../../../../../agnostic/reports/core/bugs/enums/bug-type.enum';
import { getFilename } from '../../../../../agnostic/tools/utils/file-system.util';

export class ExecutableService<T> extends AgnosticExecutableService<T> {


    async getNewObject(sutMethod: SutMethod<T>): Promise<any> {
        if (!sutMethod?.sutFile?.flaggedSourceFile) {
            console.log(chalk.red('ERROR: flagged SourceFile not found for SutFile '), sutMethod?.sutFile);
        }
        if (!sutMethod?.sutClass?.classDeclaration) {
            console.log(chalk.red('ERROR: ClassDeclaration not found.'));
        }
        const flaggedSourceFile: SourceFile = sutMethod.sutFile.flaggedSourceFile;
        const flaggedSourceFilePath: string = flaggedSourceFile.getFilePath();
        const flaggedFile: any = await require(flaggedSourceFilePath);
        const className: string = sutMethod.sutClass.name;
        const flaggedClass: ClassDeclaration = this.getClassDeclaration(flaggedSourceFile, className);
        const params: undefined[] = this.getConstructorParameters(this.getNumberOfConstructorParameters(flaggedClass));
        return new flaggedFile[className](...params);
    }


    private getClassDeclaration(flaggedSourceFile: SourceFile, originalClassName: string): ClassDeclaration {
        const classDeclaration: ClassDeclaration = flaggedSourceFile.getClasses().find(c => c.getName() === originalClassName);
        if (!classDeclaration) {
            console.log(chalk.red('ERROR: classDeclaration not found in flagged SourceFile.'));
        }
        return classDeclaration;
    }


    private getNumberOfConstructorParameters(classDeclaration: ClassDeclaration): number {
        const constructorDeclarations: ConstructorDeclaration[] = classDeclaration.getConstructors();
        let minNbOfParams: number = undefined;
        for (const constructorDeclaration of constructorDeclarations) {
            const nbOfParams: number = constructorDeclaration.getParameters().length;
            minNbOfParams = (!minNbOfParams || minNbOfParams < nbOfParams) ? nbOfParams : minNbOfParams;
        }
        return minNbOfParams;
    }


    private getConstructorParameters(numberOfParameters: number): undefined[] {
        const params: undefined[] = [];
        for (let i = 0; i < numberOfParameters; i++) {
            params.push(undefined);
        }
        return params;
    }


    async executeMethod(instance: any): Promise<void> {
        try {
            const methodDeclaration: MethodDeclaration = this.sutMethod.agnosticMethod.methodDeclaration;
            const methodName: string = methodDeclaration.getName();
            this.spyOnService = SpiesService.getInstance();
            this.spyOnService.clear();
            this.testSuite.output.returnedValue = await instance[methodName](...this.testSuite.parameters.map(p => p?.value));
            this.testSuite.output.spies = this.spyOnService.spies;
        } catch (err) {
            const message = `Error executing ${this.sutMethod?.name} : no test written for ${this.sutMethod?.agnosticMethod?.methodDeclaration?.getName()} in ${getFilename(this.sutMethod?.sutFile?.path)}`;
            console.log(chalk.red(message));
            MethodStatsService.addBug(new Bug(BugType.EXECUTION_ERROR), this.sutMethod?.sutFile?.path, this.sutMethod?.name, this.sutMethod?.sutClass?.name, this.sutMethod?.isFunction);
        }
    }

}
