import { BaseEntity, Column, Entity, JoinColumn, ManyToOne, OneToOne, PrimaryGeneratedColumn } from 'typeorm';
import { KeyValueEntity } from './key-value.entity';
import { MockEntity } from './mock.entity';
import { TestCaseEntity } from './test-case.entity';

@Entity()
export class ParameterEntity extends BaseEntity {

    @PrimaryGeneratedColumn()
    id: number;

    @Column('int')
    index: number;

    @OneToOne(() => KeyValueEntity, { cascade: true })
    @JoinColumn()
    keyValue: KeyValueEntity;

    @ManyToOne(() => MockEntity, { onDelete: 'CASCADE' })
    mock: MockEntity;

    @ManyToOne(() => TestCaseEntity, { onDelete: 'CASCADE' })
    testCase: TestCaseEntity;

    constructor(index: number) {
        super();
        this.index = index;
    }

}
