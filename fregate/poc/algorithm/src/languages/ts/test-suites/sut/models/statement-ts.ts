import { AgnosticStatement } from '../../../../../agnostic/test-suites/sut/models/agnostic-statement.model';
import { Statement } from 'ts-morph';

export class StatementTs extends AgnosticStatement {

    constructor(statement?: Statement, id?: number, isCovered = false) {
        super(statement, id, isCovered);
    }
}
