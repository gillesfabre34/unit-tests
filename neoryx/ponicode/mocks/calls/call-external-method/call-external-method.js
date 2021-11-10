#!/usr/bin/env node
"use strict";

exports.__esModule = true;
var extService = require("./external-service");

class CallExternalMethod {

    externalService = new extService()

    myMethod(a) {
        return this.externalService.isGreaterThanOne(a);
    }
}

const zzz = new CallExternalMethod();
console.log("ZZZZ", zzz.myMethod(1))
