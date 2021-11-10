import { Executable } from '../models/executable.model';
import { tab, tabs } from '../mocks/tabs.mock';

export class ExecutableService {


    static getCode(executable: Executable): string {
        let code = `import * as chalk from 'chalk';\n`;
        code = `${code}\n`;
        code = `${code}export class FakeClass {\n`;
        code = `${code}\n`;
        for (const property of executable.properties) {
            code = `${code}${tab}${property}\n`;
        }
        code = `${code}\n`;
        code = `${code}${tab}${executable.methodName}(`;
        for (const parameter of executable.parameters) {
            code = `${code}${parameter},`;
        }
        code = code.slice(0, -1);
        code = `${code}) {\n`;
        code = `${code}${tabs(2)}try {\n`;
        code = `${code}${executable.methodBody}`;
        code = `${code}${tabs(2)}}\n`;
        code = `${code}${tabs(2)}catch(err) {\n`;
        code = `${code}${tabs(3)}console.log(chalk.yellowBright('Impossible to calculate the result of the mocked function "${executable.methodName}". Please complete the corresponding test.'));\n`;
        code = `${code}${tabs(3)}return undefined;\n`;
        code = `${code}${tabs(2)}}\n`;
        code = `${code}${tab}}\n`;
        code = `${code}}\n`;
        return code;
    }
}
