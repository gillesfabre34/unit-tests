import { BaseEntity, Column, Entity, ManyToOne, PrimaryGeneratedColumn } from "typeorm";
import { KeyValueEntity } from './key-value.entity';

@Entity()
export class ValueEntity extends BaseEntity {

    @PrimaryGeneratedColumn()
    id: number;

    @Column('text')
    text: string;

    @ManyToOne(() => KeyValueEntity, keyValue => keyValue.values, { onDelete: 'CASCADE' })
    keyValue: KeyValueEntity;

    constructor(text: string, keyValueEntity: KeyValueEntity) {
        super();
        this.text = text;
        this.keyValue = keyValueEntity;
    }

}
