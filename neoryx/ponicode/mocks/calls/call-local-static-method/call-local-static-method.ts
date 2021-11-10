
export class CallLocalStaticMethod {


    static myMethod(a: number): boolean {
        return this.isGreaterThanOne(a);
    }


    static isGreaterThanOne(a: number): boolean {
        if (a > 1) {
            return true;
        }
        return false;
    }
}
