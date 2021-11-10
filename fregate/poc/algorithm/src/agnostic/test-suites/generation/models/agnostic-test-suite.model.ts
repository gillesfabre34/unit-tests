import { Parameter } from './parameter.model';
import { AgnosticTestOutput } from './agnostic-test-output.model';
import { PropertyValue } from './property-value.model';
import { AgnosticSutMethod } from '../../sut/models/agnostic-sut-method.model';
import { AgnosticSutClass } from '../../sut/models/agnostic-sut-class.model';
import { AgnosticSutFile } from '../../sut/models/agnostic-sut-file.model';

export abstract class AgnosticTestSuite<T> {

    coveredStatements: number[] = [];
    instanceProperties: PropertyValue[] = [];
    output: AgnosticTestOutput = new AgnosticTestOutput();
    parameters: PropertyValue[] = [];
    sutMethod: AgnosticSutMethod<T> = undefined;

    protected constructor(sutMethod?: AgnosticSutMethod<T>, parameters: Parameter[] = [], instanceProperties: PropertyValue[] = []) {
        this.instanceProperties = instanceProperties;
        this.parameters = parameters;
        this.sutMethod = sutMethod;
    }


    get sutClass(): AgnosticSutClass<T> {
        return this.sutMethod.sutClass;
    }


    get sutFile(): AgnosticSutFile {
        return this.sutClass.sutFile;
    }

}
