import {
    BaseEntity,
    Column,
    Entity,
    JoinColumn,
    ManyToOne,
    OneToMany,
    OneToOne,
    PrimaryGeneratedColumn
} from 'typeorm';
import { KeyValueEntity } from './key-value.entity';
import { FileUTEntity } from './file-ut.entity';
import { DeclarationEntity } from './declaration.entity';

@Entity()
export class EnumUTEntity extends BaseEntity {

    @PrimaryGeneratedColumn()
    id: number;

    @OneToOne(() => DeclarationEntity, declaration => declaration.enumUT)
    declaration: DeclarationEntity;

    @ManyToOne(() => FileUTEntity, fileUT => fileUT.enumUTs, { onDelete: 'CASCADE' })
    @JoinColumn()
    fileUT: FileUTEntity;

    @OneToMany(() => KeyValueEntity, keyValue => keyValue.enumUT)
    keyValues: KeyValueEntity[];

    @Column('text')
    name: string;


    constructor(fileUTEntity: FileUTEntity, name: string) {
        super();
        this.fileUT = fileUTEntity;
        this.name = name;
    }


    // get path(): string {
    //     return this.fileUT?.path;
    // }


    // get importDefault(): ImportDefault {
    //     return new ImportDefault(this.name, getModuleSpecifier(this.path));
    // }
}
