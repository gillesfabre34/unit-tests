import { MessageService } from './message.service';
import * as chalk from 'chalk';

export class HelloService {

    messageService = new MessageService();
    userName = '';

    constructor(userName: string) {
        this.userName = userName;
    }


    sayHello(name: string, age: number, tall: number): string {
        if (!this.userName) {
            return 'Hello ! Your name is undefined.';
        }
        let heOrShe = '';
        let hesOrHer = '';
        if (name === 'Léo') {
            hesOrHer = `He's`;
            heOrShe = `He`;
        } else if (name === 'Léa') {
            hesOrHer = `Her`;
            heOrShe = `She`;
        } else {
            hesOrHer = `He/her`;
            heOrShe = `He/she`;
        }
        let oldOrYoung = '';
        if (age > 20 && age < 70) {
            oldOrYoung = `young`;
        } else if (age < 20) {
            oldOrYoung = `very young`;
        } else {
            oldOrYoung = `old`;
        }
        let tallOrSmall = '';
        if (tall > 190) {
            tallOrSmall = `very tall`;
        } else if (tall < 170) {
            tallOrSmall = `small`;
        } else {
            tallOrSmall = `tall`;
        }
        let message = this.messageService.composeMessage(name, heOrShe, hesOrHer, oldOrYoung, tallOrSmall);
        const zzz = message.toString();
        chalk.yellowBright('ZZZ')
        return this.getFinalMessage(message);
    }


    getFinalMessage(message: string): string {
        return `Hello ${this.userName} ! ${message} Goodbye !`;
    }

}
