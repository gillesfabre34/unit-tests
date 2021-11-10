export class TypeReference {

    className: string = undefined;
    sourceFilePath: string = undefined;

    constructor(sourceFilePath: string = undefined, className: string = undefined) {
        this.className = className;
        this.sourceFilePath = sourceFilePath;
    }

}
