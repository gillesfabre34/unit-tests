const rewire = require("rewire")
const call_local_method = rewire("./call-local-method")
const CallLocalMethod = call_local_method.__get__("CallLocalMethod")
// @ponicode
describe("inst.myMethod", () => {
    let inst

    beforeEach(() => {
        let inst = new CallLocalMethod()
    })

    test("0", () => {
        let result = inst.myMethod()
        expect(result).toBe(1)
    })
})

// @ponicode
describe("inst.methodToCall", () => {
    let inst

    beforeEach(() => {
        inst = new CallLocalMethod()
    })

    test("0", () => {
        let result = inst.methodToCall()
        expect(result).toBe(1)
    })
})
