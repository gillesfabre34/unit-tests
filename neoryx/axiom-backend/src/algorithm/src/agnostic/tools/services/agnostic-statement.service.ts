import { StatementKind } from '../enums/statement-kind.enum';

export abstract class AgnosticStatementService {

    abstract kind(statement: any): StatementKind;
    abstract getText(statement: any): string;
}
