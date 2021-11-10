import { Executable } from '../models/executable.model';


const executableIfExpressionStatement = new Executable();
executableIfExpressionStatement.methodName = `helloMethod`;
executableIfExpressionStatement.methodBody = `            this.message = 'resultHelloMethod';\n`;
executableIfExpressionStatement.properties = ['message: any;'];
executableIfExpressionStatement.parameters = ['name'];

export const EXECUTABLE_IF_EXPRESSION_MOCK = executableIfExpressionStatement;

const executableIf = new Executable();
executableIf.methodName = `helloMethod`;
executableIf.methodBody = `            this.message = 'resultHelloMethod';
            return 666;\n`;
executableIf.properties = ['message: any;'];
executableIf.parameters = ['name'];

export const EXECUTABLE_IF_MOCK = executableIf;
