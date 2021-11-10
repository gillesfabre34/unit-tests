
export class CallLocalStaticMethod {


    static myMethod(a: number): boolean {
        return false;
        // TODO: Fix SpyOn
        // return this.isGreaterThanOne(a);
    }


    static isGreaterThanOne(a: number): boolean {
        if (a > 1) {
            return true;
        }
        return false;
    }
}
