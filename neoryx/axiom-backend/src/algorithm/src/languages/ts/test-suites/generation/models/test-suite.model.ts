import { AgnosticTestSuite } from '../../../../../agnostic/test-suites/generation/models/agnostic-test-suite.model';
import { SutMethod } from '../../sut/models/sut-method.model';
import { Parameter } from '../../../../../agnostic/test-suites/generation/models/parameter.model';
import { PropertyValue } from '../../../../../agnostic/test-suites/generation/models/property-value.model';

export class TestSuite extends AgnosticTestSuite {

    constructor(sutMethod?: SutMethod, parameters: Parameter[] = [], instance?: PropertyValue[]) {
        super(sutMethod, parameters, instance);
    }

}
