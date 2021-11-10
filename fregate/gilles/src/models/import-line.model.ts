export class ImportLine {

    private _elementsToImport: string[];
    private _moduleName: string;


    // ---------------------------------------------------------------------------------
    //                                Getters and setters
    // ---------------------------------------------------------------------------------


    get code(): string {
        return `import { MyService } from './my.service';`;
    }


    get elementsToImport(): string[] {
        return this._elementsToImport;
    }


    set elementsToImport(elementsToImport: string[]) {
        this._elementsToImport = elementsToImport;
    }


    get moduleName(): string {
        return this._moduleName;
    }


    set moduleName(moduleName: string) {
        this._moduleName = moduleName;
    }



    // ---------------------------------------------------------------------------------
    //                                  Other methods
    // ---------------------------------------------------------------------------------



}
