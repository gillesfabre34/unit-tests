export class MyService {

    myMethod(text: string): number {
        if (text === 'a') {
            return 0;
        } else {
            return 1;
        }
    }
}
