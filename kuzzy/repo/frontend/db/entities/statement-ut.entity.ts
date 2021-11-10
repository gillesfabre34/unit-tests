import { BaseEntity, Column, Entity, JoinColumn, ManyToOne, PrimaryGeneratedColumn } from "typeorm";
import { MethodUTEntity } from './method-ut.entity';
import { FunctionUTEntity } from './function-ut.entity';
import { TestCaseEntity } from './test-case.entity';

@Entity()
export class StatementUTEntity extends BaseEntity {

    @PrimaryGeneratedColumn()
    id: number;

    @ManyToOne(() => FunctionUTEntity, functionUT => functionUT.statementUTs, { onDelete: 'CASCADE' })
    functionUT: FunctionUTEntity;

    @Column('int')
    index: number;

    @Column('boolean', { default: false })
    isCovered: boolean;

    @ManyToOne(() => MethodUTEntity, methodUT => methodUT.statementUTs, { onDelete: 'CASCADE' })
    methodUT: MethodUTEntity;

    @ManyToOne(() => TestCaseEntity, testCase => testCase.statementUTs, { onDelete: 'CASCADE' })
    @JoinColumn()
    testCase: TestCaseEntity;

    constructor(index: number, entity: FunctionUTEntity | MethodUTEntity, isMethod: boolean) {
        super();
        this.index = index;
        this.isCovered = false;
        if (isMethod) {
            this.methodUT = entity as MethodUTEntity;
        } else {
            this.functionUT = entity as FunctionUTEntity;
        }
    }


    get path(): string {
        return this.methodUT?.path ?? this.functionUT?.path;
    }


    get methodOrFunctionName(): string {
        return this.methodUT?.name ?? this.functionUT?.name;
    }

}
