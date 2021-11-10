import { BaseEntity, Column, Entity, JoinColumn, ManyToOne, OneToOne, PrimaryGeneratedColumn } from "typeorm";
import { DeclarationEntity } from './declaration.entity';
import { ClassUTEntity } from './class-ut.entity';

@Entity()
export class PropertyEntity extends BaseEntity {

    @PrimaryGeneratedColumn()
    id: number;

    @OneToOne(() => DeclarationEntity, declaration => declaration.property, { cascade: true, nullable: true })
    @JoinColumn()
    declaration: DeclarationEntity;

    @ManyToOne(() => ClassUTEntity, classUT => classUT.properties)
    classUT: ClassUTEntity;

    @Column('text')
    name: string;

    constructor(name: string, classUT: ClassUTEntity) {
        super();
        this.name = name;
        this.classUT = classUT;
    }

}
