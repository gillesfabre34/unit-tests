import { BaseEntity, Column, Entity, ManyToOne, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { SystemUTEntity } from './system-ut.entity';
import { ClassUTEntity } from './class-ut.entity';
import { FunctionUTEntity } from './function-ut.entity';
import { FileUT } from '../../system/models/file-ut.model';
import { GLOBAL } from '../../init/const/global.const';
import { StatementUT } from '../../system/models/statement-ut.model';
import { ClassUtEntityService } from '../services/class-ut-entity.service';
import { FunctionUtEntityService } from '../services/function-ut-entity.service';
import { EnumUTEntity } from './enum-ut.entity';
import { mergeWithoutDuplicates } from '../../../shared/utils/arrays.util';

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


    get fileUT(): FileUT {
        const fileUT = new FileUT(GLOBAL.project.getSourceFile(this.path));
        fileUT.statementUTs = this.statementUTs;
        return fileUT;
    }


    get statementUTs(): StatementUT[] {
        const classStatementUTs: StatementUT[] = ClassUtEntityService.getStatementUTs(this.classUTs, this.path);
        const functionsStatementUTs: StatementUT[] = FunctionUtEntityService.getStatementUTs(this.functionUTs, this.path);
        return mergeWithoutDuplicates(classStatementUTs, functionsStatementUTs);
    }
}
