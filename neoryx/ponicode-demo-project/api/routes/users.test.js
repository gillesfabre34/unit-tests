const users = require("./users")
// @ponicode
describe("users.filterUsers", () => {
    test("0", () => {
        let result = users.filterUsers(1, 18)
        expect(result).toEqual([{ name: "Jean-Jacques", age: 16, address: { city: "Paris", road: "Rue de Rivoli" }, email: "jean-jacques-gauthier@orange.fr" }, { name: "Polly", age: 8, address: { city: "New York", road: "Madison Avenue" }, email: "pollymay@gmail.com" }, { name: "Barbara", age: 18, address: { city: "Paris", road: "Boulevard Magenta" }, email: "_barbie_girl@yahoo.fr" }])
    })

    test("1", () => {
        let result = users.filterUsers(0, 15)
        expect(result).toEqual([{ name: "Polly", age: 8, address: { city: "New York", road: "Madison Avenue" }, email: "pollymay@gmail.com" }])
    })
})
