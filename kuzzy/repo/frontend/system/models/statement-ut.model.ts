import { StatementUTEntity } from '../../db/entities/statement-ut.entity';

export class StatementUT {

    className: string = undefined;
    entity: StatementUTEntity = undefined;
    filePath: string = undefined;
    id: number = undefined;
    isCovered: boolean = undefined;
    isParsed: boolean = undefined;
    methodOrFunctionName: string = undefined;
    statementIndex: number = undefined;

    // TODO : statements out of functions and methods
    constructor(className: string, methodOrFunctionName: string, statementId: number, filePath: string) {
        this.className = className;
        this.methodOrFunctionName = methodOrFunctionName;
        this.statementIndex = statementId;
        this.filePath = filePath;
    }

    cover(): void {
        this.isCovered = true;
        this.isParsed = false;
    }

    parse(): void {
        if (this.isParsed === undefined) {
            this.isParsed = true;
        }
    }
}
