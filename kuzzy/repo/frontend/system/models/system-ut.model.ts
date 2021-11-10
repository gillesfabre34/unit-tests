import { FileUT } from './file-ut.model';

export class SystemUT {

    filesUts: FileUT[] = [];
    id: number = undefined;
    name: string = undefined;

    constructor(name: string) {
        this.name = name;
    }
}
