import { IfAndDependentNestedCalls } from './if-and-dependent-nested-calls';

describe('IfAndDependentNestedCalls', () => {
    let service;

    beforeEach(() => {
        service = new IfAndDependentNestedCalls();
    });

    describe('myMethod', () => {

        describe('no illusion tests', () => {

            beforeEach(() => {
                service.externalService = {
                    isLowerThanFive: () => {},
                    isHigherThanFour: () => {}
                }
            });

            it(`#000 should return 2 when a = 4`, () => {
                spyOn(service.externalService, 'isLowerThanFive').and.returnValue(true);
                spyOn(service.externalService, 'isHigherThanFour').and.returnValue(false);
                const result = service.myMethod(4);
                expect(service.externalService.isLowerThanFive).toHaveBeenCalledWith(4);
                expect(service.externalService.isHigherThanFour).toHaveBeenCalledWith(4);
                expect(result).toEqual(2);
            });
            it(`#001 should return 1 when a = 6`, () => {
                spyOn(service.externalService, 'isLowerThanFive').and.returnValue(false);
                const result = service.myMethod(6);
                expect(service.externalService.isLowerThanFive).toHaveBeenCalledWith(6);
                expect(result).toEqual(1);
            });
            it(`#01 should return 0 when a = 4.5`, () => {
                spyOn(service.externalService, 'isLowerThanFive').and.returnValue(true);
                spyOn(service.externalService, 'isHigherThanFour').and.returnValue(true);
                const result = service.myMethod(4.5);
                expect(service.externalService.isLowerThanFive).toHaveBeenCalledWith(4.5);
                expect(service.externalService.isHigherThanFour).toHaveBeenCalledWith(4.5);
                expect(result).toEqual(0);
            });

        });

        describe('pseudo-random tests', () => {

            it(`#000 should return 2 when a = 4`, () => {
                spyOn(service.externalService, 'isLowerThanFive');
                spyOn(service.externalService, 'isHigherThanFour');
                const result = service.myMethod(4);
                expect(service.externalService.isLowerThanFive).toHaveBeenCalledWith(4);
                expect(service.externalService.isHigherThanFour).toHaveBeenCalledWith(4);
                expect(result).toEqual(2);
            });
            it(`#001 should return 1 when a = 6`, () => {
                spyOn(service.externalService, 'isLowerThanFive');
                const result = service.myMethod(6);
                expect(service.externalService.isLowerThanFive).toHaveBeenCalledWith(6);
                expect(result).toEqual(1);
            });
            it(`#01 should return 0 when a = 4.5`, () => {
                spyOn(service.externalService, 'isLowerThanFive');
                spyOn(service.externalService, 'isHigherThanFour');
                const result = service.myMethod(4.5);
                expect(service.externalService.isLowerThanFive).toHaveBeenCalledWith(4.5);
                expect(service.externalService.isHigherThanFour).toHaveBeenCalledWith(4.5);
                expect(result).toEqual(0);
            });

        });
    });
});
