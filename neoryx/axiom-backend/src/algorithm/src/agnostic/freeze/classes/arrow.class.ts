import { State } from './state.class';

export class Arrow {

    id: string;
    instance: any = undefined;
    frozenMethodId: string = undefined;
    newState: State = undefined;
    oldState: State = undefined;
    parameters: any[] = [];
    returnedValue: any = undefined;

}
