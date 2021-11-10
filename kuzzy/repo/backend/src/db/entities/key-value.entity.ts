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
import { EnumUTEntity } from './enum-ut.entity';
import { DeclarationEntity } from './declaration.entity';
import { ValueEntity } from './value.entity';
import { TestCaseEntity } from './test-case.entity';

@Entity()
export class KeyValueEntity extends BaseEntity {

    @PrimaryGeneratedColumn()
    id: number;

    @ManyToOne(() => DeclarationEntity, { onDelete: 'CASCADE' })
    @JoinColumn()
    declaration: DeclarationEntity;

    @ManyToOne(() => EnumUTEntity, enumUT => enumUT.keyValues, { onDelete: 'CASCADE' })
    enumUT: EnumUTEntity;

    @Column('boolean', { default: false })
    isArray: boolean;

    @Column('text')
    key: string;

    // @Column('text', { default: null })
    // primitiveType: PrimitiveType;

    @OneToOne(() => TestCaseEntity, testCase => testCase.returnedValue)
    testCaseReturnedValue: TestCaseEntity;

    @OneToMany(() => ValueEntity, value => value.keyValue, { cascade: true })
    @JoinColumn()
    values: ValueEntity[];


    constructor(key: string, isArray: boolean) {
        super();
        this.key = key;
        this.isArray = isArray;
    }


    // get importDefault(): ImportDefault {
    //     return this.declaration?.importDefault;
    // }


    get text(): string {
        return this.isArray ? `[${this.values.map(v => v.text).join(', ')}]` : this.values[0].text;
    }
}
