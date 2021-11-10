import { Describe } from './describe.model';
import { ClassSpecService } from '../services/class-spec.service';

export class ClassSpec {

    private _className: string = undefined;
    private _describe: Describe = undefined;
    private _imports: string = undefined;



    // ---------------------------------------------------------------------------------
    //                                Getters and setters
    // ---------------------------------------------------------------------------------


    get className(): string {
        return this._className;
    }


    set className(className: string) {
        this._className = className;
    }


    get code(): string {
        return ClassSpecService.getCode(this);
    }


    get describe(): Describe {
        return this._describe;
    }


    set describe(describe: Describe) {
        this._describe = describe;
    }


    get imports(): string {
        return this._imports;
    }


    set imports(imports: string) {
        this._imports = imports;
    }



    // ---------------------------------------------------------------------------------
    //                                  Other methods
    // ---------------------------------------------------------------------------------



}
