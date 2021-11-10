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
import { neoryxPath } from '../../../../agnostic/tools/utils/file-system.util';

export class FlagsService extends AgnosticFlagsService {


    isAbstractMethod(methodDeclaration: MethodDeclaration): boolean {
        return methodDeclaration.isAbstract();
    }


    getFlaggedProject(): Project {
        return new Project({
            tsConfigFilePath: GLOBALS.flaggedConfigPath
        });
    }


    getSourceFile(filePath: string, project: Project): SourceFile {
        return project.getSourceFile(filePath);
    }


    getSourceFiles(project: Project): SourceFile[] {
        return project.getSourceFiles();
    }


    addToProject(filePath: string, project: Project): void {
        project.addSourceFileAtPath(filePath);
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
                parent.insertStatements(indexStatement, `FlagsService.flag(new FlagStatement(${index / 2}, '${methodDeclaration.getName()}', '${methodDeclaration.getFirstAncestorByKind(SyntaxKind.ClassDeclaration).getName()}', '${methodDeclaration.getSourceFile().getFilePath()}', '${SpiesService.getSpiedObject(firstCallExpression)}'));`);
            } else {
                parent.insertStatements(indexStatement, `FlagsService.flag(new FlagStatement(${index / 2}, '${methodDeclaration.getName()}', '${methodDeclaration.getFirstAncestorByKind(SyntaxKind.ClassDeclaration).getName()}', '${methodDeclaration.getSourceFile().getFilePath()}'));`);
            }
        }
    }


    isNotAStatement(statement: Statement): boolean {
        return statement.getKind() === SyntaxKind.BinaryExpression;
    }


    static flag(flagStatement: FlagStatement): void {
        const sutFile: AgnosticSutFile = GLOBALS.mainSutFolder.sutFiles.find(f => neoryxPath(f.path) === flagStatement.filePath);
        if (sutFile) {
            const sutClass: AgnosticSutClass<any> = sutFile.sutClasses.find(c => c.name === flagStatement.className);
            const sutMethod: AgnosticSutMethod<any> = sutClass.sutMethods.find(m => m.name === flagStatement.methodName);
            sutMethod.agnosticMethod.flagStatement(flagStatement.id);
            const spiesService = SpiesService.getInstance();
            spiesService.spiedObject = flagStatement.spiedObject;
        }
    }


    addImportDeclarations(sourceFile: SourceFile): void {
        sourceFile.addImportDeclarations([
            {
                defaultImport: "{ Wrapper }",
                moduleSpecifier: `${GLOBALS.appRoot}/src/languages/ts/init/services/wrapper-decorator.service`
            },
            {
                defaultImport: "{ FlagsService }",
                moduleSpecifier: `${GLOBALS.appRoot}/src/languages/ts/init/services/flags.service`
            },
            {
                defaultImport: "{ FlagStatement }",
                moduleSpecifier: `${GLOBALS.appRoot}/src/agnostic/init/models/flag-statement.model`
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

}
