import { BaseEntity, Column, Entity, JoinColumn, ManyToOne, PrimaryGeneratedColumn } from "typeorm";
import { SystemUTEntity } from './system-ut.entity';

@Entity()
export class FilePathEntity extends BaseEntity {

    @PrimaryGeneratedColumn()
    id: number;

    @ManyToOne(() => SystemUTEntity, systemUT => systemUT.filePaths)
    @JoinColumn()
    systemUT: SystemUTEntity;

    @Column('text')
    path: string;

    constructor(path: string, systemUT: SystemUTEntity) {
        super();
        this.path = path;
        this.systemUT = systemUT;
    }

}
