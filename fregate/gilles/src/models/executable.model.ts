import { ExecutableService } from '../services/executable.service';

export class Executable {

    methodBody = '';
    methodName = '';
    parameters: string[] = [];
    properties: string[] = [];


    get code(): string {
        return ExecutableService.getCode(this);
    }

}
