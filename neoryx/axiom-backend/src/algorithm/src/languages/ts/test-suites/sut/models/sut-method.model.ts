import { ClassDeclaration, FunctionDeclaration, MethodDeclaration, ParameterDeclaration, SyntaxKind } from 'ts-morph';
import { TestSuite } from '../../generation/models/test-suite.model';
import { Method } from './method.model';
import { ExecutableService } from '../../generation/services/executable.service';
import { InputConstraintsService } from '../../../constraints/services/input-constraints.service';
import { AgnosticInputConstraintsService } from '../../../../../agnostic/constraints/services/agnostic-input-constraints.service';
import { AgnosticSutMethod } from '../../../../../agnostic/test-suites/sut/models/agnostic-sut-method.model';
import { AgnosticBranchesService } from '../../../../../agnostic/init/services/agnostic-branches.service';
import { BranchesService } from '../../../init/services/branches.service';
import * as chalk from 'chalk';

export class SutMethod extends AgnosticSutMethod   {


    isStatic(methodDeclaration: MethodDeclaration): boolean {
        return methodDeclaration.isStatic();
    }


    getFunctionName(functionDeclaration: FunctionDeclaration): string {
        return functionDeclaration.getName();
    }


    getMethodName(methodDeclaration: MethodDeclaration): string {
        return methodDeclaration.getName();
    }


    getMethodDeclarations(classDeclaration: ClassDeclaration): MethodDeclaration[] {
        return classDeclaration.getMethods();
    }


    getInputConstraintsService(): AgnosticInputConstraintsService {
        return new InputConstraintsService();
    }


    newBranchesService(): AgnosticBranchesService {
        return new BranchesService();
    }



    newMethod(methodDeclaration: MethodDeclaration, isStatic: boolean): Method {
        return new Method(methodDeclaration, isStatic);
    }


    newExecutableService(testSuite?: TestSuite): ExecutableService {
        return new ExecutableService(testSuite);
    }
}
