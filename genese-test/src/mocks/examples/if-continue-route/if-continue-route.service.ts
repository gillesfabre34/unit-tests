import { IO } from '../../../services/decorators.service';

export class IfContinueRoute {

    @IO
    myMethod(text: string): string {
        if (text) {
            text = text + 'b';
        }
        return text;
    }
}
