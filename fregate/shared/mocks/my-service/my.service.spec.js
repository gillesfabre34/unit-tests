"use strict";
exports.__esModule = true;
var example_merged_1 = require("./example-merged");
describe('MyService', function () {
    var service;
    beforeEach(function () {
        service = new example_merged_1.MyService(undefined);
        service.externalService = {
            doSomething: function () { },
            helloMethod: function () { return ''; }
        };
    });
    it('should exist', function () {
        expect(service).toBeTruthy();
    });
    it('should call doSomething()', function () {
        spyOn(service.externalService, 'doSomething');
        service.myMethod('Dark Vador');
        expect(service.externalService.doSomething).toHaveBeenCalledWith('Dark Vador');
    });
    it('should change message value', function () {
        spyOn(service.externalService, 'helloMethod').and.returnValue('returnedMessage');
        service.myMethod('Dark Vador');
        expect(service.externalService.helloMethod).toHaveBeenCalledWith('Dark Vador');
        expect(service.message).toEqual('returnedMessage');
    });
    it('should change message value without name', function () {
        spyOn(service.externalService, 'helloMethod').and.returnValue('returnedMessage');
        service.myMethod('');
        expect(service.externalService.helloMethod).toHaveBeenCalledWith(' World !');
        expect(service.message).toEqual('returnedMessage');
    });
    it('should return 666', function () {
        var result = service.myMethod('Dark Vador');
        expect(result).toEqual(666);
    });
});
