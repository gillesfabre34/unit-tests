import { CallExternalMethodWithNewKeyword } from './call-external-method-with-new-keyword';

describe('CallExternalMethodWithNewKeyword', () => {
    let service;

    beforeEach(() => {
        service = new CallExternalMethodWithNewKeyword();
    });

    describe('myMethod', () => {

        describe('no illusion tests', () => {
            let externalService;
            beforeEach(() => {
                externalService = {
                    isGreaterThanOne: () => {}
                }
            });

            it(`should return XXX when a = 2`, () => { // TODO
                spyOn(externalService, 'isGreaterThanOne').and.returnValue(true); // TODO
                const result = service.myMethod(2);
                expect(externalService.isGreaterThanOne).toHaveBeenCalledWith(2);
                expect(result).XXX; // TODO
            });
        })


        describe('pseudo-random tests', () => {
            it(`should return XXX when a = 2`, async () => { // TODO
                const importExternalService = await import('./external-service');
                const externalService = new importExternalService.ExternalService();
                spyOn(externalService, 'isGreaterThanOne');
                const result = service.myMethod(2);
                expect(externalService.isGreaterThanOne).toHaveBeenCalledWith(2);
                expect(result).XXX; // TODO
            });
        });
    });
});
