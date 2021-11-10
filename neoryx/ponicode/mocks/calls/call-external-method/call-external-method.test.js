const rewire = require("rewire")
const call_external_method = rewire("./call-external-method")
const CallExternalMethod = call_external_method.__get__("CallExternalMethod")
// @ponicode
describe("inst.myMethod", () => {
    let inst

    beforeEach(() => {
        inst = new CallExternalMethod()
    })

    test("0", () => {
        let result = inst.myMethod("Ponicponicodeponiponicoooooooooode18774563")
        expect(result).toBe(false)
    })
})
