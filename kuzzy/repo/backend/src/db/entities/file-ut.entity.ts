import { BaseEntity, Column, Entity, ManyToOne, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { SystemUTEntity } from './system-ut.entity';
import { ClassUTEntity } from './class-ut.entity';
import { FunctionUTEntity } from './function-ut.entity';
import { EnumUTEntity } from './enum-ut.entity';
import { db } from '../const/db.const';

@Entity()
export class FileUTEntity extends BaseEntity {

    @PrimaryGeneratedColumn()
    id: number;

    @OneToMany(() => ClassUTEntity, classUT => classUT.fileUT, { cascade: true })
    classUTs: ClassUTEntity[];

    @OneToMany(() => EnumUTEntity, enumUT => enumUT.fileUT, { cascade: true })
    enumUTs: EnumUTEntity[];

    @OneToMany(() => FunctionUTEntity, functionUT => functionUT.fileUT, { cascade: true })
    functionUTs: FunctionUTEntity[];

    @Column('text')
    path: string;

    @ManyToOne(() => SystemUTEntity, app => app.fileUTs, { cascade: true, onDelete: 'CASCADE' })
    systemUT: SystemUTEntity;

    constructor(path: string, systemUTEntity: SystemUTEntity) {
        super();
        this.path = path;
        this.systemUT = systemUTEntity;
    }


    // async save(): Promise<FileUTEntity> {
    //     return await db.connection.getRepository(FileUTEntity).save(this);
    // }


    // get fileUT(): FileUT {
    //     const fileUT = new FileUT(GLOBAL.project.getSourceFile(this.path));
    //     fileUT.statementUTs = this.statementUTs;
    //     return fileUT;
    // }
    //
    //
    // get statementUTs(): StatementUT[] {
    //     const classStatementUTs: StatementUT[] = ClassUtEntityService.getStatementUTs(this.classUTs, this.path);
    //     const functionsStatementUTs: StatementUT[] = FunctionUtEntityService.getStatementUTs(this.functionUTs, this.path);
    //     return mergeWithoutDuplicates(classStatementUTs, functionsStatementUTs);
    // }
}
