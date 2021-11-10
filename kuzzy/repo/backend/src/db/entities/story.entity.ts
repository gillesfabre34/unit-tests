import { BaseEntity, Column, Entity, PrimaryGeneratedColumn } from "typeorm";

@Entity()
export class StoryEntity extends BaseEntity {

    @PrimaryGeneratedColumn()
    id: number;

    @Column("int")
    date: number;


}
