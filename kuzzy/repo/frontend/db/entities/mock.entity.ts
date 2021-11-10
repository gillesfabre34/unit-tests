import {
    BaseEntity,
    Column,
    Entity,
    JoinColumn,
    JoinTable,
    ManyToMany,
    ManyToOne,
    OneToMany,
    OneToOne,
    PrimaryGeneratedColumn
} from "typeorm";
import { ClassUTEntity } from './class-ut.entity';
import { TestCaseEntity } from './test-case.entity';
import { DeclarationEntity } from './declaration.entity';
import { ParameterEntity } from './parameter.entity';
import { ImportDefault } from '../../write/models/import-default.model';
import { getModuleSpecifier } from '../../utils/file-system.util';

@Entity()
export class MockEntity extends BaseEntity {

    @PrimaryGeneratedColumn()
    id: number;

    @ManyToOne(() => ClassUTEntity, classUT => classUT.mocks, { onDelete: 'CASCADE' })
    classUT: ClassUTEntity;

    @Column('text')
    code: string;

    @OneToMany(() => ParameterEntity, parameter => parameter.mock, { cascade: true })
    @JoinColumn()
    constructorArguments: ParameterEntity[];

    @OneToOne(() => DeclarationEntity, declaration => declaration.mock)
    declaration: DeclarationEntity;

    @ManyToMany(() => MockEntity, { onDelete: 'CASCADE' })
    @JoinTable()
    mocksDependencies: MockEntity[];

    @Column('text')
    name: string;

    @Column('text')
    path: string;

    @OneToMany(() => TestCaseEntity, testCase => testCase.mock)
    @JoinTable()
    testCases: TestCaseEntity[];


    constructor(mockFilePath: string, name: string, code: string) {
        super();
        this.path = mockFilePath;
        this.name = name;
        this.code = code;
    }


    get className(): string {
        return this.classUT?.name;
    }


    get fileUTPath(): string {
        return this.classUT?.fileUT?.path;
    }


    get importDefaultMockFile(): ImportDefault {
        return new ImportDefault(this.name, getModuleSpecifier(this.path));
    }


    get importDefaultClassUT(): ImportDefault {
        return new ImportDefault(this.className, getModuleSpecifier(this.fileUTPath));
    }

}
