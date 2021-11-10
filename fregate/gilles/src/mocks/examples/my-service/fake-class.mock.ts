import * as chalk from 'chalk';

export class FakeClass {

    message;

    fakeMethod(name) {
        try {
            this.message = 'returnHelloMethod';
            return 666;
        }
        catch(err) {
            console.log(chalk.yellowBright('Impossible to calculate the result of the mocked function "helloMethod". Please complete the corresponding test.'));
            return undefined;
        }
    }
}

