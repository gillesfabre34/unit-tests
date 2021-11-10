import { BaseEntity, Entity, JoinColumn, OneToOne, PrimaryGeneratedColumn } from 'typeorm';
import { EnumUTEntity } from './enum-ut.entity';
import { ClassUTEntity } from './class-ut.entity';
import { MockEntity } from './mock.entity';
import { ImportDefault } from '../../write/models/import-default.model';
import { PropertyEntity } from './property.entity';
import { getModuleSpecifier } from '../../utils/file-system.util';

@Entity()
export class DeclarationEntity extends BaseEntity {

    @PrimaryGeneratedColumn()
    id: number;

    @OneToOne(() => ClassUTEntity, classUT => classUT.declaration, { cascade: true, nullable: true })
    @JoinColumn()
    classUT: ClassUTEntity;

    @OneToOne(() => EnumUTEntity, enumUT => enumUT.declaration, { cascade: true, nullable: true })
    @JoinColumn()
    enumUT: EnumUTEntity;

    @OneToOne(() => MockEntity, mock => mock.declaration, { cascade: true, nullable: true })
    @JoinColumn()
    mock: MockEntity;

    @OneToOne(() => PropertyEntity, property => property.declaration)
    property: PropertyEntity;


    get elementDeclared(): ClassUTEntity | EnumUTEntity | MockEntity {
        return this.classUT ?? this.enumUT ?? this.mock;
    }


    get identifier(): string {
        return this.elementDeclared?.name;
    }


    get importDefault(): ImportDefault {
        return new ImportDefault(this.identifier, this.moduleSpecifier);
    }


    get moduleSpecifier(): string {
        return getModuleSpecifier(this.elementDeclared?.path);
    }
}
