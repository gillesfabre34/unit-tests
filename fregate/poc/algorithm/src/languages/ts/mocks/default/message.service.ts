export class MessageService {

    composeMessage(name: string, heOrShe: string, hesOrHer: string, oldOrYoung: string, tallOrSmall: string) {
        return `${hesOrHer} name is ${name}. ${heOrShe} is ${oldOrYoung} and ${tallOrSmall}.`;
    }

}

