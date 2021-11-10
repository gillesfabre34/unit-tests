import { BaseEntity, Column, Entity, ManyToOne, OneToMany, OneToOne, PrimaryGeneratedColumn } from "typeorm";
import { FileUTEntity } from './file-ut.entity';
import { MethodUTEntity } from './method-ut.entity';
import { MockEntity } from './mock.entity';
import { DeclarationEntity } from './declaration.entity';
import { PropertyEntity } from './property.entity';

@Entity()
export class ClassUTEntity extends BaseEntity {

    @PrimaryGeneratedColumn()
    id: number;

    @OneToOne(() => DeclarationEntity, declaration => declaration.classUT)
    declaration: DeclarationEntity;

    @ManyToOne(() => FileUTEntity, fileUT => fileUT.classUTs, { onDelete: 'CASCADE' })
    fileUT: FileUTEntity;

    @OneToMany(() => MethodUTEntity, methodUT => methodUT.classUT, { cascade: true })
    methodUTs: MethodUTEntity[];

    @OneToMany(() => MockEntity, mock => mock.classUT, { cascade: true })
    mocks: MockEntity[];

    @Column('text')
    name: string;

    @OneToMany(() => PropertyEntity, property => property.classUT, { cascade: true })
    properties: PropertyEntity[];

    constructor(name: string, fileUT: FileUTEntity) {
        super();
        this.name = name;
        this.fileUT = fileUT;
    }


    get path(): string {
        return 'zzz';
        // return this.fileUT?.path;
    }

}
