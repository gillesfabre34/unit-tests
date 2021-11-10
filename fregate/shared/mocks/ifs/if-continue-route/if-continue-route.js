"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.IfContinueRoute = void 0;
class IfContinueRoute {
    myMethod(text) {
        if (text) {
            text = text + 'b';
        }
        return text;
    }
}
exports.IfContinueRoute = IfContinueRoute;
