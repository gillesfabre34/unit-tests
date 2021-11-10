import { Instance } from '../../capture/models/instance.model';

export class DynamicSystemState {

    instances: Instance[] = [];


    get instanceIds(): number[] {
        return this.instances.map(e => e.id);
    }


    addInstance(instance: Instance): void {
        this.instances.push(instance);
    }
}
