export class IfWithoutElse {

    myMethod(text: string): number {
        if (text) {
            return 0;
        }
        return 1;
    }
}
