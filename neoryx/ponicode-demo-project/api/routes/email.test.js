const rewire = require("rewire")
const email = rewire("./email")
const isMelValid = email.__get__("isMelValid")

// @ponicode
describe("email.isEmailValid", () => {
    test("0", () => {
        let result = email.isEmailValid("pollymay@gmail.com")
        expect(result).toBe(true)
    })

    test("1", () => {
        let result = email.isEmailValid("jean-jacques-gauthier@orange.fr")
        expect(result).toBe(true)
    })

    test("2", () => {
        email.isEmailValid("Hello@tuTao.de")
    })

    test("3", () => {
        let result = email.isEmailValid("bed-free@tutanota.de")
        expect(result).toBe(true)
    })
})
