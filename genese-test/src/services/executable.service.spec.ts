import { FAKE_CLASS_IF_A } from '../mocks/fake-class-stringified.mock';
import { ExecutableService } from './executable.service';
import { EXECUTABLE_IF_MOCK } from '../mocks/executable.mock';

describe('EXECUTABLE SERVICE', () => {

    describe('getCode', () => {
        it('should return FAKE_CLASS_IF_A', () => {
            const result = ExecutableService.getCode(EXECUTABLE_IF_MOCK);
            expect(result).toEqual(FAKE_CLASS_IF_A);
        });
    })
})
