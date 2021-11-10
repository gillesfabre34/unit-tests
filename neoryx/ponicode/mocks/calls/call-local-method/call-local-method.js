class CallLocalMethod {

    myMethod() {
        const result = this.methodToCall();
        return result;
    }


    methodToCall() {
        return 1;
    }
}

let inst = new CallLocalMethod()

console.log('RESULT = ', inst.myMethod());
