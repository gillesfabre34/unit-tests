import { BaseEntity, Column, Entity, ManyToOne, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { FileUTEntity } from './file-ut.entity';
import { StatementUTEntity } from './statement-ut.entity';

@Entity()
export class FunctionUTEntity extends BaseEntity {

    @PrimaryGeneratedColumn()
    id: number;

    @Column('boolean')
    isAsync: boolean;

    @Column('boolean')
    isVoid: boolean;

    @Column('text')
    name: string;

    @ManyToOne(() => FileUTEntity, fileUT => fileUT.functionUTs)
    fileUT: FileUTEntity;

    @OneToMany(() => StatementUTEntity, statementUT => statementUT.functionUT, { cascade: true })
    statementUTs: StatementUTEntity[];


    constructor(isAsync: boolean, isVoid: boolean) {
        super();
        this.isAsync = isAsync;
        this.isVoid = isVoid;
    }


    get isMethod(): boolean {
        return true;
    }


    get path(): string {
        return this.fileUT?.path;
    }

}
