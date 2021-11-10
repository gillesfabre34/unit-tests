import * as chalk from 'chalk';
import { MethodDeclaration, SyntaxKind } from 'ts-morph';
import { AgnosticStatementService } from '../../tools/services/agnostic-statement.service';
import { StatementKind } from '../../tools/enums/statement-kind.enum';
import { MethodStats } from '../../reports/dashboard/models/method-stats.model';
import { FileStatsService } from '../../reports/dashboard/services/file-stats.service';
import { MethodStatsService } from '../../reports/dashboard/services/method-stats.service';

export abstract class AgnosticBranchesService {

    abstract getNewTernaryAssignmentCode(beforeEqual: string, condition: string, firstCase: string, secondCase: string): string;
    abstract getNewReturnBinaryCode(code: string): string;
    abstract getReturnBinariesStatements(methodDeclaration: any): any[];
    abstract getTernariesStatements(methodDeclaration: any): any[];
    abstract newStatementService(): AgnosticStatementService;
    abstract replaceCode(methodDeclaration: any, statement: any, newCode: string): void;


    transformBranchesInStatements(methodDeclaration: MethodDeclaration): void {
        this.transformTernaries(methodDeclaration);
        this.transformReturnBinaries(methodDeclaration);
        // TODO : fix for functions
        const methodStats: MethodStats = MethodStatsService.getMethodStats(methodDeclaration?.getSourceFile()?.getFilePath(), methodDeclaration?.getName(), methodDeclaration?.getFirstAncestorByKind(SyntaxKind.ClassDeclaration)?.getName(), false, false);
        if (methodStats) {
            methodStats.totalStatements = methodDeclaration.getDescendantStatements().length;
        }
    }


    private transformTernaries(methodDeclaration: any): void {
        for (const ternary of this.getTernariesStatements(methodDeclaration)) {
            this.transformTernaryStatement(methodDeclaration, ternary);
        }
    }


    private transformTernaryStatement(methodDeclaration: any, statement: any): void {
        let newCode: string = undefined;
        switch (this.newStatementService().kind(statement)) {
            case StatementKind.ASSIGNATION:
                newCode = this.transformAssignationStatement(statement);
                break;
            default:
                return;
        }
        this.replaceCode(methodDeclaration, statement, newCode);
    }


    private transformAssignationStatement(statement: any): string {
        const text: string = this.newStatementService().getText(statement);
        if (text?.includes('=')) {
            const splitEqual: string[] = text.split('=');
            const beforeEqual: string = splitEqual[0];
            const afterEqual: string = splitEqual[1];
            if (afterEqual.includes('?') && afterEqual.includes(':')) {
                const splitBranches: string[] = afterEqual.split(/[?:]/);
                const condition: string = splitBranches[0];
                const firstCase: string = splitBranches[1];
                const secondCase: string = splitBranches[2];
                return this.getNewTernaryAssignmentCode(beforeEqual, condition, firstCase, secondCase);
            }
        }
        return text;
    }


    private transformReturnBinaries(methodDeclaration: any): void {
        for (const returnBinaryStatement of this.getReturnBinariesStatements(methodDeclaration)) {
            const newCode: string = this.transformReturnBinaryStatement(returnBinaryStatement);
            this.replaceCode(methodDeclaration, returnBinaryStatement, newCode);
        }
    }


    private transformReturnBinaryStatement(returnBinaryStatement: any): string {
        const text: string = this.newStatementService().getText(returnBinaryStatement);
        return this.getNewReturnBinaryCode(text);
    }


}
