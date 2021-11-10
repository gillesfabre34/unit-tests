export class IfThisProperty {

    name: string;

    myMethod(): number {
        if (this.name === 'foo') {
            return 0;
        } else {
            return 1;
        }
    }
}
