export class FlagStatement {

    className: string = undefined;
    filePath: string = undefined;
    id: number = undefined;
    methodName: string = undefined;
    spiedObject: string = undefined;

    constructor(id: number = undefined, methodName: string = undefined, className: string = undefined, filePath: string = undefined, spiedObject: string = undefined) {
        this.id = id;
        this.methodName = methodName;
        this.className = className;
        this.filePath = filePath;
        this.spiedObject = spiedObject;
    }

}
