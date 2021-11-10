import { BaseEntity, Column, Entity, JoinColumn, ManyToOne, OneToOne, PrimaryGeneratedColumn } from "typeorm";
import { ValueEntity } from './value.entity';
import { MockEntity } from './mock.entity';

@Entity()
export class MutationEntity extends BaseEntity {

    @PrimaryGeneratedColumn()
    id: number;

    @Column('text')
    key: string;

    @ManyToOne(() => MockEntity)
    mock: MockEntity;

    @OneToOne(() => ValueEntity, { cascade: true })
    @JoinColumn()
    value: ValueEntity;

    constructor(key: string, valueEntity: ValueEntity) {
        super();
        this.key = key;
        this.value = valueEntity;
    }
}
