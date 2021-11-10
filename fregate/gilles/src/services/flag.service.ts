import * as chalk from 'chalk';
import { MethodDeclaration, SourceFile, Statement, SyntaxKind } from 'ts-morph';
import { PROJECT } from '../constants/project.const';
import { Ast } from './ast.service';
import { CodeCoverageService } from './code-coverage.service';

export class FlagService {

    static async generateFlags(pathToAnalyze: string): Promise<void> {
        console.log(chalk.yellowBright('FLAGS GENERATION'));
        const sourceFile: SourceFile = PROJECT.getSourceFileOrThrow(pathToAnalyze);
        const copySourceFile: SourceFile = await Ast.copySourceFile(sourceFile);
        copySourceFile.addImportDeclaration({
            defaultImport: "{ FlagService }",
            moduleSpecifier: "../../../services/flag.service",
        });
        const methodDeclarations: MethodDeclaration[] = copySourceFile.getDescendantsOfKind(SyntaxKind.MethodDeclaration);
        for (const methodDeclaration of methodDeclarations) {
            this.generateFlagsByMethod(methodDeclaration);
        }
        await copySourceFile.save();
    }


    private static generateFlagsByMethod(methodDeclaration: MethodDeclaration): void {
        // methodDeclaration.insertStatements()
        let indexBlock = 0;
        methodDeclaration.getDescendantsOfKind(SyntaxKind.Block).forEach(block => {
            const blockStatements: Statement[] = block.getStatements();
            for (let indexStatement = blockStatements.length - 1; indexStatement >= 0; indexStatement--) {
                block.insertStatements(indexStatement, `FlagService.flag('${indexBlock}-${indexStatement}');`);
            }
            indexBlock++;
        })
    }


    static flag(id: string): void {
        console.log(`FLAG of statement ${id}`);
        CodeCoverageService.coveredStatements.push(id);
    }

}
