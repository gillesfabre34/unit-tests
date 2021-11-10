export class IfContinueRoute {

    myMethod(text: string): string {
        if (text) {
            text = text + 'b';
        }
        return text;
    }
}
