import * as chalk from 'chalk';

export class FakeClass {

    message: any;
    externalService: { helloMethod: any, doSomething: any };

    myMethod(name) {
        try {
            this.message = 'resultHelloMethod';
            return 666;
        }
        catch(err) {
            console.log(chalk.yellowBright('Impossible to calculate the result of the mocked function "myMethod". Please complete the corresponding test.'));
            return undefined;
        }
    }
}
