export class IfNotFalsyStatic {

    static myMethod(text: string): number {
        if (text) {
            return 0;
        } else {
            return 1;
        }
    }
}
