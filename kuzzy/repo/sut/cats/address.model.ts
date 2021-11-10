export class Address {

    public street: string = undefined;
    public streetNumber: number = undefined;
    public town: string = undefined;

    constructor(street: string = undefined, streetNumber: number = undefined, town: string = undefined) {
        this.street = street;
        this.streetNumber = streetNumber;
        this.town = town;
    }

}
