import { AgnosticFlagsService } from '../../../../agnostic/init/services/agnostic-flags.service';
import {
    Block,
    CallExpression,
    ClassDeclaration,
    FunctionDeclaration,
    MethodDeclaration,
    MethodDeclarationStructure,
    Project,
    SourceFile,
    Statement,
    SyntaxKind
} from 'ts-morph';
import { GLOBALS } from '../../../../agnostic/init/constants/globals.const';
import { SpiesService } from '../../../../agnostic/init/services/spies.service';
import { FlagStatement } from '../../../../agnostic/init/models/flag-statement.model';
import { AgnosticMethod } from '../../../../agnostic/test-suites/sut/models/agnostic-method.model';
import { Method } from '../../test-suites/sut/models/method.model';
import { AgnosticSutFile } from '../../../../agnostic/test-suites/sut/models/agnostic-sut-file.model';
import { AgnosticSutClass } from '../../../../agnostic/test-suites/sut/models/agnostic-sut-class.model';
import { AgnosticSutMethod } from '../../../../agnostic/test-suites/sut/models/agnostic-sut-method.model';
import { getExtension, neoryxFlagPath } from '../../../../agnostic/tools/utils/file-system.util';
import * as chalk from 'chalk';
import { AgnosticBranchesService } from '../../../../agnostic/init/services/agnostic-branches.service';
import { BranchesService } from './branches.service';
import { AgnosticSourceFileService } from '../../../../agnostic/tools/services/agnostic-sourcefile.service';
import { SourceFileService } from '../../tools/services/sourcefile.service';
import { copySync } from 'fs-extra';

export class FlagsService extends AgnosticFlagsService {


    createFlaggedProject(): Project {
        copySync(GLOBALS.configFilePath, neoryxFlagPath(GLOBALS.configFilePath));
        return new Project({
            tsConfigFilePath: neoryxFlagPath(GLOBALS.configFilePath)
        });
    }


    addToProject(filePath: string, project: Project): void {
        project.addSourceFileAtPath(filePath);
    }


    isAbstractMethod(methodDeclaration: MethodDeclaration): boolean {
        return methodDeclaration.isAbstract();
    }


    isFileToFlag(filePath: string): boolean {
        return ['ts'].includes(getExtension(filePath));
        // TODO: fix bug with js files with the #! at the beginning of the file which can't be flagged
        // return ['ts', 'js'].includes(getExtension(filePath));
    }


    getSourceFile(filePath: string, project: Project): SourceFile {
        return project.getSourceFile(filePath);
    }


    getSourceFiles(project: Project): SourceFile[] {
        return project.getSourceFiles();
    }


    getPath(sourceFile: SourceFile): string {
        return sourceFile.getFilePath();
    }


    getFlaggedMethod(className: string, methodName: string, flaggedSourceFile: SourceFile): AgnosticMethod {
        const classDeclaration: ClassDeclaration = flaggedSourceFile.getDescendantsOfKind(SyntaxKind.ClassDeclaration).find(c => c.getName() === className);
        if (!classDeclaration) {
            return undefined;
        }
        const methodDeclaration: MethodDeclaration = classDeclaration.getDescendantsOfKind(SyntaxKind.MethodDeclaration).find(m => m.getName() === methodName);
        return new Method(methodDeclaration);
    }


    getClassDeclarations(sourceFile: SourceFile): ClassDeclaration[] {
        return sourceFile.getClasses();
    }


    getFunctionDeclarations(sourceFile: SourceFile): FunctionDeclaration[] {
        return sourceFile.getFunctions();
    }


    getMethodDeclarations(classDeclaration: ClassDeclaration): MethodDeclaration[] {
        return classDeclaration.getMethods();
    }


    getDescendantStatements(methodDeclaration: any): any[] {
        return methodDeclaration.getDescendantStatements();
    }


    async setFlagToStatement(methodDeclaration: MethodDeclaration, statement: Statement): Promise<void> {
        if (this.isNotAStatement(statement)) {
            return undefined;
        }
        const parent: Block = statement.getFirstAncestorByKind(SyntaxKind.Block);
        const indexStatement: number = statement.getChildIndex();
        const index = methodDeclaration.getDescendantStatements().findIndex(s => s.getPos() === statement.getPos());
        const callExpressions: CallExpression[] = statement.getDescendantsOfKind(SyntaxKind.CallExpression);
        if (indexStatement < parent.getStatements().length) {
            if(callExpressions.length > 0) {
                // TODO : check case of multiple CallExpression in one Statement
                const firstCallExpression: CallExpression = callExpressions[0];
                const spiedObject: string = SpiesService.getSpiedObject(firstCallExpression);
                if (spiedObject) {
                    parent.insertStatements(indexStatement, `FlagsService.flag(new FlagStatement(${index / 2}, '${methodDeclaration.getName()}', '${methodDeclaration.getFirstAncestorByKind(SyntaxKind.ClassDeclaration).getName()}', '${methodDeclaration.getSourceFile().getFilePath()}', '${spiedObject}'));`);
                    return;
                }
            }
            parent.insertStatements(indexStatement, `FlagsService.flag(new FlagStatement(${index / 2}, '${methodDeclaration.getName()}', '${methodDeclaration.getFirstAncestorByKind(SyntaxKind.ClassDeclaration).getName()}', '${methodDeclaration.getSourceFile().getFilePath()}'));`);
        }
    }


    isNotAStatement(statement: Statement): boolean {
        return statement.getKind() === SyntaxKind.BinaryExpression;
    }


    static flag(flagStatement: FlagStatement): void {
        const sutFile: AgnosticSutFile = GLOBALS.mainSutFolder.sutFiles.find(f => neoryxFlagPath(f.path) === flagStatement.filePath);
        if (sutFile) {
            const sutClass: AgnosticSutClass = sutFile.sutClasses.find(c => c.name === flagStatement.className);
            const sutMethod: AgnosticSutMethod = sutClass.sutMethods.find(m => m.name === flagStatement.methodName);
            sutMethod.agnosticMethod.flagStatement(flagStatement.id);
            const spiesService = SpiesService.getInstance();
            spiesService.spiedObject = flagStatement.spiedObject;
        }
    }


    addImportDeclarations(sourceFile: SourceFile): void {
        sourceFile.addImportDeclarations([
            {
                defaultImport: "{ Wrapper }",
                moduleSpecifier: `${GLOBALS.algoPath}/src/algorithm/src/languages/ts/init/services/wrapper-decorator.service`
            },
            {
                defaultImport: "{ FlagsService }",
                moduleSpecifier: `${GLOBALS.algoPath}/src/algorithm/src/languages/ts/init/services/flags.service`
            },
            {
                defaultImport: "{ FlagStatement }",
                moduleSpecifier: `${GLOBALS.algoPath}/src/algorithm/src/agnostic/init/models/flag-statement.model`
            }
        ]);
    }


    async saveSourceFile(sourceFile: SourceFile): Promise<void> {
        await sourceFile.save();
    }


    async wrapMethod(methodDeclaration: MethodDeclaration): Promise<void> {
        methodDeclaration.addDecorator({
            name: 'Wrapper',
            arguments: [`'${methodDeclaration?.getSourceFile()?.getFilePath()}'`],
            typeArguments: ['string']
        })
    }


    duplicateStaticMethods(classDeclaration: ClassDeclaration): void {
        for (const methodDeclaration of classDeclaration.getMethods()) {
            if (methodDeclaration.isStatic()) {
                const structure: MethodDeclarationStructure = methodDeclaration.getStructure() as MethodDeclarationStructure;
                structure.isStatic = false;
                classDeclaration.insertMethod(0, structure);
            }
        }
    }


    newBranchesService(): AgnosticBranchesService {
        return new BranchesService();
    }


    getSourceFilePathOfMethod(methodDeclaration: MethodDeclaration): string {
        return methodDeclaration?.getSourceFile()?.getFilePath();
    }


    newSourceFileService(): AgnosticSourceFileService {
        return new SourceFileService();
    }
}
