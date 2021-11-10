import { MethodStatement } from '../models/path-statement.model';
import { deepClone, getFilename, isSameObject, percent } from './tools.service';
import { ClassDeclaration, MethodDeclaration, SourceFile, SyntaxKind } from 'ts-morph';
import { PROJECT } from '../constants/project.const';
import { PathService } from './path.service';
import * as chalk from 'chalk';
import { IoService } from './io.service';
import cloneDeep from 'clone-deep-circular-references';
import { Outputs } from '../interfaces/outputs.interface';

export class CodeCoverageService {

    static coveredStatements: string[] = [];

    static async execute(pathFile: string) {
        const pathCopyFile = this.getCopyFilePath(pathFile);
        const file = await import(pathCopyFile);
        const sourceFile: SourceFile = PROJECT.getSourceFile(pathFile);
        const classDeclaration: ClassDeclaration = sourceFile.getFirstDescendantByKind(SyntaxKind.ClassDeclaration);
        const unit: any = new file[classDeclaration.getName()]();
        for (const methodDeclaration of classDeclaration.getDescendantsOfKind(SyntaxKind.MethodDeclaration)) {
            const thisBeforeExecution = deepClone(unit);
            const outputs = this.executeMethod(unit, methodDeclaration);
            this.expects(thisBeforeExecution, outputs);
        }
    }


    private static executeMethod(thisBeforeExecution: any, methodDeclaration: MethodDeclaration): Outputs {
        const methodName = methodDeclaration.getName();
        const copyUnit = cloneDeep(thisBeforeExecution);
        const result = copyUnit[methodName]('Fregate');
        const methodStatements: MethodStatement[] = PathService.getMethodStatements(methodDeclaration);
        console.log(chalk.yellowBright('\nCODE COVERAGE'));
        console.log(chalk.blueBright('\nCOVERED STATEMENTS'), this.coveredStatements)
        const notCoveredStatements: string[] = methodStatements.map(m => m.id).filter(id => !this.coveredStatements.includes(id));
        console.log(chalk.redBright('NOT COVERED STATEMENTS'), notCoveredStatements);
        console.log(`\nTOTAL COVERAGE : ${percent(this.coveredStatements.length, methodStatements.length)} %`);
        IoService.log();
        return { thisAfterExecution: copyUnit, returnedValue: result };
    }


    private static expects(thisBeforeExecution: any, outputs: Outputs ): void {
        console.log(chalk.yellowBright('EXPECTS\n'));
        if (!isSameObject(thisBeforeExecution, outputs.thisAfterExecution)) {
            console.log(chalk.blueBright('Expect "this" was updated :'));
            console.log(`expect(this).toEqual :`);
            console.log(outputs.thisAfterExecution);
        }
        if (outputs.returnedValue) {
            console.log(chalk.blueBright('Expect to return value :'));
            console.log(outputs.returnedValue);
        }
    }


    private static getCopyFilePath(pathFile: string): string {
        const fileName = getFilename(pathFile);
        let pathWithoutFileName = pathFile.slice(0, -fileName.length);
        return `${pathWithoutFileName}copy-${fileName}`;
    }

}
