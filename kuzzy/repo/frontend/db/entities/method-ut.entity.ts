import { BaseEntity, Column, Entity, JoinColumn, ManyToOne, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { ClassUTEntity } from './class-ut.entity';
import { StatementUTEntity } from './statement-ut.entity';

@Entity()
export class MethodUTEntity extends BaseEntity {

    @PrimaryGeneratedColumn()
    id: number;

    @ManyToOne(() => ClassUTEntity, classUT => classUT.methodUTs, { onDelete: 'CASCADE' })
    @JoinColumn()
    classUT: ClassUTEntity;

    @Column('boolean')
    isAsync: boolean;

    @Column('boolean')
    isVoid: boolean;

    @Column('text')
    name: string;

    @OneToMany(() => StatementUTEntity, statementUT => statementUT.methodUT, { cascade: true })
    statementUTs: StatementUTEntity[];

    constructor(name: string, classUT: ClassUTEntity, isAsync: boolean, isVoid: boolean) {
        super();
        this.name = name;
        this.classUT = classUT;
        this.isAsync = isAsync;
        this.isVoid = isVoid;
    }


    get isMethod(): boolean {
        return true;
    }


    get path(): string {
        return this.classUT?.path;
    }

}
