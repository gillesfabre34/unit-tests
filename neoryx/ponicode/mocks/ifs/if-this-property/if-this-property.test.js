const if_this_property = require("./if-this-property")
// @ponicode
describe("inst.myMethod", () => {
    let inst

    beforeEach(() => {
        inst = new if_this_property.IfThisProperty()
    })

    test("0", () => {
        let result = inst.myMethod()
        expect(result).toBe(0)
    })
})
