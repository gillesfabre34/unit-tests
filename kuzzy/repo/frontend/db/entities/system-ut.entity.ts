import { BaseEntity, Column, Entity, OneToMany, PrimaryGeneratedColumn } from "typeorm";
import { FileUTEntity } from './file-ut.entity';
import { FilePathEntity } from './file-path.entity';

@Entity()
export class SystemUTEntity extends BaseEntity {

    @PrimaryGeneratedColumn()
    id: number;

    @Column('text')
    name: string;

    @OneToMany(() => FileUTEntity, fileUT => fileUT.systemUT)
    fileUTs: FileUTEntity[];

    @OneToMany(() => FilePathEntity, fileUT => fileUT.systemUT)
    filePaths: FilePathEntity[];

    constructor(name: string) {
        super();
        this.name = name;
    }

}
