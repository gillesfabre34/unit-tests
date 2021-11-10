
export class ItAssertion {

    private _core = '';
    private _expects: string[] = [];
    private _spies: string[] = [];



    // ---------------------------------------------------------------------------------
    //                                Getters and setters
    // ---------------------------------------------------------------------------------


    get core(): string {
        return this._core;
    }


    set core(text: string) {
        this._core = text;
    }


    get expects(): string[] {
        return this._expects;
    }


    set expects(expects: string[]) {
        this._expects = expects;
    }



    get spies(): string[] {
        return this._spies;
    }


    set spies(spies: string[]) {
        this._spies = spies;
    }

}
