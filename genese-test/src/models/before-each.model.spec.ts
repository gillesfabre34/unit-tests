import { BeforeEach } from './before-each.model';

describe('BEFORE EACH', () => {
    let before;

    beforeEach(() => {
        before = new BeforeEach();
    })

    describe('GET PARAMS CODE', () => {

        it('should return BEFORE_EACH_MOCK', () => {
            before.constructorParams = [undefined];
            const result = before.constructorParamsCode;
            expect(result).toEqual(`undefined`);
        });
    })

})
