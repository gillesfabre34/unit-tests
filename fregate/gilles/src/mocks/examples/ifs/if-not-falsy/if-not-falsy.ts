import { IO } from '../../../../services/decorators.service';

export class IfNotFalsy {

    @IO
    myMethod(text: string): number {
        console.log('myMethod ', text)
        if (text) {
            return 0;
        } else {
            return 1;
        }
    }
}
