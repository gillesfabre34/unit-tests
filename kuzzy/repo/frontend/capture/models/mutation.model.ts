export class Mutation {

    key: string = undefined;
    finalValue: any = undefined;
    initialValue: any = undefined;

    constructor(key?: string, initialValue?: any, finalValue?: any) {
        this.key = key;
        this.initialValue = initialValue;
        this.finalValue = finalValue;
    }
}
