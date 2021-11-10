const rewire = require("rewire")
const if_and_dependent = rewire("./if-and-dependent")
const myMethod = if_and_dependent.__get__("myMethod")
// @ponicode
describe("myMethod", () => {
    test("0", () => {
        let result = myMethod(-1)
        expect(result).toBe(1)
    })
})
