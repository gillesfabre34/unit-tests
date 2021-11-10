const rewire = require("rewire")
const if_without = rewire("./if-without.else")
const myMethod = if_without.__get__("myMethod")

const Animal = if_without.__get__("Animal")
// @ponicode
describe("inst.myMethod", () => {
    let inst

    beforeEach(() => {
        inst = new Animal()
    })

    test("0", () => {
        let result = inst.myMethod("Hello, world!")
        expect(result).toBe(2)
    })
})
