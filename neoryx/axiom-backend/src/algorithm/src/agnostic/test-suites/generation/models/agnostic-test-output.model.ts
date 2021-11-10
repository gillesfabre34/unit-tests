import { SpyMethod } from '../../../write/models/spy-method.model';

export class AgnosticTestOutput {

    returnedValue: any = undefined;
    spies: SpyMethod[] = [];
    thisValue: any = undefined;

}
