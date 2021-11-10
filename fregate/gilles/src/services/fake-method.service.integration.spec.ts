import { FakeMethodService } from './fake-method.service';
import { PATH_MOCK_IF } from '../mocks/paths-statements.mock';

describe('FAKE METHOD INTEGRATION SERVICE', () => {

    describe('getResult', () => {
        it('should return 666', () => {
            const result = FakeMethodService.getResult(PATH_MOCK_IF, ['a']);
            expect(result).toEqual('666');
        });
    })

})
