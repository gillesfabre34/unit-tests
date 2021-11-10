import { AgnosticSutStatement } from '../../../../../agnostic/test-suites/sut/models/agnostic-sut-statement.model';
import { Statement } from 'ts-morph';

export class SutStatement extends AgnosticSutStatement {

    constructor(statement?: Statement, id?: number, isCovered = false) {
        super(statement, id, isCovered);
    }
}
