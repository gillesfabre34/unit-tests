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

            it(`should return true when a = 2`, () => {
                spyOn(externalService, 'isGreaterThanOne').and.returnValue(true);
                const result = service.myMethod(2);
                expect(externalService.isGreaterThanOne).toHaveBeenCalledWith(2);
                expect(result).toBeTrue();
            });
        })


        describe('pseudo-random tests', () => {
            it(`should return true when a = 2`, async () => {
                const importExternalService = await import('./external-service');
                const externalService = new importExternalService.ExternalService();
                spyOn(externalService, 'isGreaterThanOne');
                const result = service.myMethod(2);
                expect(externalService.isGreaterThanOne).toHaveBeenCalledWith(2);
                expect(result).toBeTrue();
            });
        });
    });
});
