import { Param } from './params.model';

export class BeforeEach {

    private _constructorParams: Param[] = [];
    private _mock: string;



    // ---------------------------------------------------------------------------------
    //                                Getters and setters
    // ---------------------------------------------------------------------------------


    get constructorParams(): Param[] {
        return this._constructorParams;
    }


    set constructorParams(constructorParams: Param[]) {
        this._constructorParams = constructorParams;
    }


    get constructorParamsCode(): string {
        let code = '';
        for (const param of this.constructorParams) {
            code = `${code}${param},`;
        }
        return code.slice(0, -1);
    }


    get mock(): string {
        return this._mock;
    }


    set mock(mock: string) {
        this._mock = mock;
    }

}

