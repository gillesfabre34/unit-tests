export const FAKE_CLASS_IF_A = `import * as chalk from 'chalk';

export class FakeClass {

    message: any;

    helloMethod(name) {
        try {
            this.message = 'resultHelloMethod';
            return 666;
        }
        catch(err) {
            console.log(chalk.yellowBright('Impossible to calculate the result of the mocked function "helloMethod". Please complete the corresponding test.'));
            return undefined;
        }
    }
}
`;
