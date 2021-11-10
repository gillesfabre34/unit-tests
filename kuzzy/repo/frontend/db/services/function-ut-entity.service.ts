import { StatementUtEntityService } from './statement-ut-entity.service';
import { StatementUT } from '../../system/models/statement-ut.model';
import { FunctionUTEntity } from '../entities/function-ut.entity';

export class FunctionUtEntityService {


    static getStatementUTs(functionUTEntities: FunctionUTEntity[], filePath: string): StatementUT[] {
        const statementUTs: StatementUT[] = [];
        for (const functionUTEntity of functionUTEntities) {
            statementUTs.push(...StatementUtEntityService.getStatementUTs(functionUTEntity.statementUTs, undefined, functionUTEntity.name, filePath));
        }
        return statementUTs;
    }

}
