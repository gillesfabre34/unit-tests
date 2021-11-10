import { ClassDeclaration } from 'ts-morph';
import { ClassUTEntity } from '../entities/class-ut.entity';
import { db } from '../../init/const/db.const';
import { MethodUTEntity } from '../entities/method-ut.entity';
import { StatementUtEntityService } from './statement-ut-entity.service';
import { StatementUT } from '../../system/models/statement-ut.model';
import { isVoidMethod } from '../../../shared/utils/ast-statements.util';

export class MethodUtEntityService {

    static async saveEach(classUTEntity: ClassUTEntity, classDeclaration: ClassDeclaration): Promise<void> {
        for (const methodDeclaration of classDeclaration.getMethods()) {
            const methodName: string = methodDeclaration.getName();
            let methodUTEntity: MethodUTEntity = await this.existingMethod(classUTEntity, methodName);
            if (!methodUTEntity) {
                methodUTEntity = new MethodUTEntity(methodName, classUTEntity, methodDeclaration.isAsync(), isVoidMethod(methodDeclaration));
                methodUTEntity.statementUTs = StatementUtEntityService.createStatementUTEntities(methodUTEntity, methodDeclaration);
            }
            await methodUTEntity.save();
        }
    }


    private static async existingMethod(classUTEntity: ClassUTEntity, methodName: string): Promise<MethodUTEntity> {
        return await db.connection.getRepository(MethodUTEntity)
            .createQueryBuilder('method')
            .innerJoinAndSelect('method.classUT', 'classUT')
            .where(`method.name = '${methodName}' AND classUT.id = ${classUTEntity.id}`)
            .getOne();
    }


    static getStatementUTs(methodUTEntities: MethodUTEntity[], className: string, filePath: string): StatementUT[] {
        const statementUTs: StatementUT[] = [];
        for (const methodUTEntity of methodUTEntities) {
            statementUTs.push(...StatementUtEntityService.getStatementUTs(methodUTEntity.statementUTs, className, methodUTEntity.name, filePath));
        }
        return statementUTs;
    }

}
