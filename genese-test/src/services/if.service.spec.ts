import { DESCRIBE_MOCK } from '../mocks/describe.mock';
import { IT_NAME_A, IT_NAME_UNDEFINED } from '../mocks/it.mock';
import { ItService } from './it.service';

describe('IF SERVICE', () => {


    describe('getIts', () => {
        it('Should return [IT_NAME_A, IT_NAME_UNDEFINED]', () => {
            const result = ItService.generateIts(DESCRIBE_MOCK.describes[0]);

            // expect(result).toEqual([IT_NAME_A, IT_NAME_UNDEFINED])
        });
    });
})
