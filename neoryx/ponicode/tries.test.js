const rewire = require("rewire")
const tries = rewire("./tries")
const doSomething = tries.__get__("doSomething")
// @ponicode
describe("doSomething", () => {
    test("0", () => {
        doSomething(3)
    })
})
