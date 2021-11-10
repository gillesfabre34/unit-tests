import { Cat } from './cat.model';
import { Address } from './address.model';
import { Meaow } from './meaow.enum';
import { Mood } from './mood.enum';

export class Person {

    public address: Address = undefined;
    public busy: boolean = undefined;
    public cats: Cat[] = [];
    public firstName: string = undefined;
    public isHappy: boolean;
    public lastName: string = undefined;
    public socialNumber: string = undefined;

    constructor(firstName: string, lastName: string, cats: Cat[] = [], address: Address = undefined, isHappy = undefined, socialNumber: string = undefined) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cats = cats;
        this.address = address;
        this.isHappy = isHappy;
        this.socialNumber = socialNumber;
    }


    addCat(cat: Cat): void {
        this.cats.push(cat);
    }


    adopt(cat: Cat): Person {
        if (cat) {
            // this.cats.push(cat);
            this.addCat(cat);
            this.isHappy = true;
            cat.mood = Mood.HAPPY;
        } else {
            this.isHappy = false;
        }
        return this;
    }


    async giveFood(food: 'croquettes' | 'swill'): Promise<Meaow[]> {
        const meaows: Meaow[] = [];
        for (const cat of this.cats) {
            const meaow = await cat.eatFood(food);
            meaows.push(meaow);
        }
        return meaows;
    }

}


