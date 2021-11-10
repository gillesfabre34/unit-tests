import { GLOBAL } from '../../init/const/global.const';

export class Instance {

    concreteObject: any = undefined;
    constructorArguments: any[] = [];
    fileUTPath: string = undefined;
    id: number = undefined;
    interfaces: any[] = []; // TODO : add idea of interfaces with out of system

    constructor(fileUTPath: string, concreteObject: any, constructorArguments: any[] = []) {
        this.concreteObject = concreteObject;
        this.fileUTPath = fileUTPath;
        this.constructorArguments = constructorArguments;
        this.setId();
    }


    get className(): string {
        return this.concreteObject?.constructor.name;
    }


    setId(): void {
        const instanceIds: number[] = GLOBAL.dynamicAppState.instanceIds ?? [];
        this.id = instanceIds.length > 0 ? Math.max(...instanceIds) + 1 : 0;
    }

}
