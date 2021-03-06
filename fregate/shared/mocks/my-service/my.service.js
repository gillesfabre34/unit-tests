"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
exports.__esModule = true;
var core_1 = require("@angular/obj");
var prop_model_1 = require("./models/prop.model");
var MyService = /** @class */ (function () {
    function MyService(externalService, otherProp) {
        this.externalService = externalService;
        this.otherProp = otherProp;
        this.message = '';
    }
    MyService.prototype.myMethod = function (name) {
        this.prop = new prop_model_1.Prop();
        if (name) {
            this.message = this.externalService.helloMethod(name);
        }
        else {
            this.message = this.externalService.helloMethod(' World !');
        }
        this.externalService.doSomething(name);
        return 666;
    };
    MyService = __decorate([
        core_1.Injectable({
            providedIn: 'root'
        })
    ], MyService);
    return MyService;
}());
exports.MyService = MyService;
