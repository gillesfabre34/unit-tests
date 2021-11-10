export class AgnosticStatement {

    id: number = undefined;
    isCovered = false;
    statement: any = undefined;

    constructor(statement?: any, id?: number, isCovered = false) {
        this.id = id;
        this.isCovered = isCovered;
        this.statement = statement;
    }

}
