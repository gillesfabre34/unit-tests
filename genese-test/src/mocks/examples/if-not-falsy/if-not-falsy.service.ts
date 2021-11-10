import { IO } from '../../../services/decorators.service';

export class IfNotFalsyService {

    @IO
    myMethod(text: string): number {
        if (text) {
            return 0;
        } else {
            return 1;
        }
    }
}
