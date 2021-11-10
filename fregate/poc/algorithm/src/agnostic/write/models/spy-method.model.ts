export class SpyMethod {

    calledWith: any[] = [];
    className: string = undefined;
    filePath: string = undefined;
    methodName: string = undefined;
    returnedValues: any[] = [];
    spiedObject: string = undefined;

    private static instance: SpyMethod = undefined;

    constructor(filePath?: string, className?: string, methodName?: string, spiedObject?: string) {
        this.filePath = filePath;
        this.className = className;
        this.methodName = methodName;
        this.spiedObject = spiedObject;
    }


    /**
     * Singleton instanceProperties of this service
     */
    static getInstance(filePath: string, className: string, methodName: string): SpyMethod {
        if (!SpyMethod.instance) {
            SpyMethod.instance = new SpyMethod(filePath, className, methodName);
        }
        return SpyMethod.instance;
    }
}
