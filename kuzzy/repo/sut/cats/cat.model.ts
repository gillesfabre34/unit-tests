import { Meaow } from './meaow.enum';
import * as chalk from 'chalk';
import { Mood } from './mood.enum';
import { IAnimal } from './iAnimal.interface';

export class Cat implements IAnimal {

    age: number = undefined;
    hungry: boolean = false;
    isHealthy: boolean = true;
    mood: Mood = undefined;
    name: string = undefined;
    weight: number = undefined;

    constructor(age: number = undefined, name: string = undefined, mood: Mood = Mood.SAD) {
        this.age = age;
        this.mood = mood;
        this.name = name;
    }


    get numberOfLegs(): number {
        return 4;
    }


    getWeight(): number {
        return this.weight;
    }


    meaow(): Meaow {
        let meaow: Meaow;
        switch (this.mood) {
            case Mood.ANGRY:
                meaow = Meaow.ANGRY;
                break;
            case Mood.HAPPY:
                meaow = Meaow.HAPPY;
                break;
            case Mood.SAD:
                meaow = Meaow.SAD;
                break;
            default:
                meaow = Meaow.SAD;
        }
        console.log(chalk.blueBright(meaow));
        return meaow;
    }


    async eatFood(food: 'croquettes' | 'swill'): Promise<Meaow> {
        if (this.hungry) {
            console.log(chalk.redBright('IS HUNGRY : '), this.name);
            return food === 'croquettes' ? Meaow.HAPPY : Meaow.SAD;
        }
        if (food === 'croquettes') {
            this.mood = Mood.HAPPY;
            return Meaow.HAPPY;
        } else {
            this.mood = Mood.ANGRY;
            return Meaow.ANGRY;
        }
    }
}

