const rewire = require("rewire")
const if_and_dependent_nested_inclusion = rewire("./if-and-dependent-nested-inclusion")
const IfAndDependentNestedInclusion = if_and_dependent_nested_inclusion.__get__("IfAndDependentNestedInclusion")
// @ponicode
describe("inst.myMethod", () => {
    let inst

    beforeEach(() => {
        inst = new IfAndDependentNestedInclusion()
    })

    test("0", () => {
        let result = inst.myMethod(1.0)
        expect(result).toBe(0)
    })

    test("1", () => {
        let result = inst.myMethod(5.0)
        expect(result).toBe(1)
    })

    test("2", () => {
        let result = inst.myMethod(4.0)
        expect(result).toBe(2)
    })
})
