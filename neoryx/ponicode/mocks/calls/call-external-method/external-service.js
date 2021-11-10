#!/usr/bin/env node
"use strict";

exports.__esModule = true;
class ExternalService {

    isGreaterThanOne(a) {
        if (a > 1) {
            return true;
        }
        return false;
    }

}
module.exports = ExternalService

// const zzz = new ExternalService();
// console.log("ZZZZ", zzz.myMethod(1))
