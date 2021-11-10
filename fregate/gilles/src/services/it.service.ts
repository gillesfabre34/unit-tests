import { Describe } from '../models/describe.model';
import { throwError } from './tools.service';
import { It } from '../models/it.model';
import { Path } from '../models/path.model';
import { PathService } from './path.service';
import { tabs } from '../mocks/tabs.mock';
import { StatementsService } from './statements.service';
import { ItAssertion } from '../models/it-assertion.model';
import {
    IT_NAME_A_ASSERTION,
    IT_NAME_A_ASSERTION_CODE,
    IT_NAME_UNDEFINED_ASSERTION_CODE
} from '../mocks/it-assertion.mock';

export class ItService {


    static generateIts(describe: Describe): It[] {
        if (!describe) {
            throwError('No Describe for It generation');
        }
        const paths: Path[] = PathService.getDescribePaths(describe);
        let its: It[] = [];
        for (const path of paths) {
            its.push(StatementsService.generateIt(path))
        }
        return its;
    }


    static joinItsText(its: It[]): string {
        let itsCode = '';
        for (const it of its) {
            itsCode = `${itsCode}${this.getCodeFromItObject(it)}`;
        }
        return itsCode;
    }


    static getCodeFromItObject(it: It): string {
        const declaration: string = this.getItDeclaration(it);
        return `${declaration}${this.getAssertionCode(it.assertion)}${tabs(2)}});\n`;
    }


    private static getItDeclaration(it: It): string {
        return `${tabs(2)}it('${it.expectation}', () => {\n`;
    }


    static getAssertionCode(itAssertion: ItAssertion): string {
        let code = `${tabs(3)}${itAssertion.spies.join(`${tabs(3)}`)}`;
        code = `${code}${tabs(3)}${itAssertion.core}`;
        code = `${code}${tabs(3)}${itAssertion.expects.join(`${tabs(3)}`)}`;
        return code;
    }

}
