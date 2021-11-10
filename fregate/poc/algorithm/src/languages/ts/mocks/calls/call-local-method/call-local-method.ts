export class CallLocalMethod {

    myMethod(): number {
        const result = this.methodToCall();
        return result;
    }


    methodToCall(): number {
        return 1;
    }
}
