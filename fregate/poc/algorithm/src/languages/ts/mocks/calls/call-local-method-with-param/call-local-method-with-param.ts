export class CallLocalMethodWithParam {

    myMethod(a: number): number {
        const result = this.methodToCall(a + 1);
        return result;
    }


    methodToCall(a: number): number {
        return a * 2;
    }
}
