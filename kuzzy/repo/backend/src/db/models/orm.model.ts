import { Connection, EntityManager } from 'typeorm';

export class Orm {

    connection: Connection = undefined;
    manager: EntityManager = undefined;

    connect(connection: Connection) {
        this.connection = connection;
        this.manager = connection.manager;
    }

}
