import { FunctionDeclaration, MethodDeclaration } from 'ts-morph';
import { MethodUTEntity } from '../entities/method-ut.entity';
import { FunctionUTEntity } from '../entities/function-ut.entity';
import { StatementUTEntity } from '../entities/statement-ut.entity';
import { StatementUT } from '../../system/models/statement-ut.model';
import { db } from '../../init/const/db.const';

export class StatementUtEntityService {

    static createStatementUTEntities(entity: MethodUTEntity | FunctionUTEntity, declaration: FunctionDeclaration | MethodDeclaration): StatementUTEntity[] {
        let index = 0;
        let statementUTEntities: StatementUTEntity[] = [];
        for (const statement of declaration.getDescendantStatements()) {
            statementUTEntities.push(new StatementUTEntity(index, entity, entity.isMethod));
            index++;
        }
        return statementUTEntities;
    }

    static async findStatementEntity(statementUT: StatementUT): Promise<StatementUTEntity> {
        return db.connection.getRepository(StatementUTEntity)
            .createQueryBuilder('statementUT')
            .leftJoinAndSelect('statementUT.methodUT', 'methodUT')
            .leftJoinAndSelect('methodUT.classUT', 'classUT')
            .leftJoinAndSelect('classUT.fileUT', 'fileUT')
            .where('statementUT.index = :index', { index: statementUT.statementIndex })
            .andWhere(`methodUT.name = '${statementUT.methodOrFunctionName}'`)
            .andWhere('classUT.name = :name', { name: statementUT.className })
            .andWhere('fileUT.path = :path', { path: statementUT.filePath })
            .getOne();
    }


    static getStatementUTs(statementUTEntities: StatementUTEntity[], className: string, methodOrFunctionName: string, filePath: string): StatementUT[] {
        const statementUTs: StatementUT[] = [];
        for (const statementUTEntity of statementUTEntities) {
            statementUTs.push(this.createStatementUT(statementUTEntity, className, methodOrFunctionName, filePath));
        }
        return statementUTs;
    }


    private static createStatementUT(statementUTEntity: StatementUTEntity, className: string, methodOrFunctionName: string, filePath: string): StatementUT {
        const statementUT = new StatementUT(className, methodOrFunctionName, statementUTEntity.index, filePath);
        statementUT.isCovered = statementUTEntity.isCovered;
        return statementUT;
    }
}
