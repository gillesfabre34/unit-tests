import { ItService } from '../services/it.service';
import { ItAssertion } from './it-assertion.model';

export class It {

    private _assertion: ItAssertion = new ItAssertion();
    private _expectation = '';



    // ---------------------------------------------------------------------------------
    //                                Getters and setters
    // ---------------------------------------------------------------------------------


    get assertion(): ItAssertion {
        return this._assertion;
    }


    set assertion(itAssertion: ItAssertion) {
        this._assertion = itAssertion;
    }


    get code(): string {
        return ItService.getCodeFromItObject(this);
    }


    get expectation(): string {
        return this._expectation;
    }


    set expectation(title: string) {
        this._expectation = title;
    }


}
