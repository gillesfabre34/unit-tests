import { BugType } from '../enums/bug-type.enum';

export class Bug {

    bugType: BugType = undefined;
    message: string = undefined;
    value: any = undefined;

    constructor(bugType?: BugType, message?: string, value?: any) {
        this.message = message;
        this.bugType = bugType;
        this.value = value;
    }


    isTheSameThan(bug: Bug): boolean {
        return this.bugType === bug.bugType && this.message === bug.message && this.value === bug.value;
    }

}
