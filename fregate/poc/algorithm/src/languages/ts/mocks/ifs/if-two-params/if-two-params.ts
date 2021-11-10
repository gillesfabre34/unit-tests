export class IfTwoParams {

    myMethod(a: number, text: string): number {
        if (a > 1) {
            if (text.length > 2) {
                return 1;
            }
            return 0;
        } else {
            if (text.length > 3) {
                return 3;
            }
            return 2;
        }
    }
}
