import { StatementUT } from '../../system/models/statement-ut.model';
import * as chalk from 'chalk';
import { getFileUT, getStatementUT } from '../../../shared/utils/system-ut.util';

export class CoverageService {

    static cover(fileUTPath: string): void {
        for (const statementUT of getFileUT(fileUTPath).statementUTs.filter(s => s.isParsed)) {
            statementUT.cover();
        }
    }


    // TODO : Check case for functions, with className undefined
    static parse(statementId: number, methodName: string, className: string, fileUTPath: string): void {
        const statementUT: StatementUT = getStatementUT(statementId, methodName, className, fileUTPath);
        if (statementUT && !statementUT.isCovered && !statementUT.isParsed) {
            statementUT.parse();
        }
    }


    static hasParsedStatements(fileUTPath: string): boolean {
        return getFileUT(fileUTPath).parsedStatements.length > 0;
    }

}
