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

    @ManyToOne(() => FileUTEntity, fileUT => fileUT.functionUTs, { onDelete: 'CASCADE' })
    fileUT: FileUTEntity;

    @OneToMany(() => StatementUTEntity, statementUT => statementUT.functionUT, { cascade: true })
    statementUTs: StatementUTEntity[];


    constructor(name: string, fileUT: FileUTEntity) {
        super();
        this.name = name;
        this.fileUT = fileUT;
        // TODO : change false to null
        this.isAsync = false;
        this.isVoid = false;
    }

    //
    // get isMethod(): boolean {
    //     return true;
    // }
    //
    //
    // get path(): string {
    //     return this.fileUT?.path;
    // }

}
