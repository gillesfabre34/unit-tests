import { IoClass } from '../models/io-class.model';
import * as chalk from 'chalk';

export class IoService {

    private static instance: IoService = undefined;
    static ioClasses: IoClass[] = [];


    private constructor() {
    }


    addIo(): void {

    }


    static log(ioClass?: IoClass): void {
        if (ioClass) {
            this.logIoClass(ioClass);
        } else {
            for (const someIoClass of this.ioClasses) {
                this.logIoClass(someIoClass);
            }
        }
    }

    private static logIoClass(ioClass?: IoClass): void {
        console.log(chalk.yellowBright(`\nMETHODS CALLED IN ${ioClass.className}\n`));
        for (const method of ioClass.methods) {
            console.log(chalk.blueBright(`${method.name} have been called with`));
            console.log(method.inputs);
            console.log(chalk.blueBright('and returned'));
            console.log(method.output);
        }
        console.log();
    }


    /**
     * Singleton instance of this service
     */
    static getInstance() {
        if (!IoService.instance) {
            IoService.instance = new IoService();
        }
        return IoService.instance;
    }
}
