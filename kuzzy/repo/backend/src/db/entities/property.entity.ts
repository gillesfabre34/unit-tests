import { BaseEntity, Column, Entity, JoinColumn, ManyToOne, OneToOne, PrimaryGeneratedColumn } from "typeorm";
import { DeclarationEntity } from './declaration.entity';
import { ClassUTEntity } from './class-ut.entity';

@Entity()
export class PropertyEntity extends BaseEntity {

    @PrimaryGeneratedColumn()
    id: number;

    @ManyToOne(() => DeclarationEntity, declaration => declaration.properties, { cascade: true, nullable: true, onDelete: 'CASCADE', onUpdate: 'CASCADE' })
    declaration: DeclarationEntity;

    @ManyToOne(() => ClassUTEntity, classUT => classUT.properties, { onDelete: 'CASCADE', onUpdate: 'CASCADE' })
    classUT: ClassUTEntity;

    @Column('text')
    name: string;

    constructor(name: string, classUT: ClassUTEntity) {
        super();
        this.name = name;
        this.classUT = classUT;
    }

}
