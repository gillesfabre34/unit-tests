export class IfThisProperty {

    name = 'a';

    myMethod() {
        if (this.name === 'foo') {
            return 0;
        } else {
            return 1;
        }
    }
}
