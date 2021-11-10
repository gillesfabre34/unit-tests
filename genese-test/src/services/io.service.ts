import { IoClass } from '../models/io-class.model';
import * as chalk from 'chalk';

export class IoService {

    private static instance: IoService = undefined;
    static ioClasses: IoClass[] = [];


    private constructor() {
    }


    addIo(): void {

    }


    static log(ioClass: IoClass): void {
        console.log(chalk.yellowBright(`IoClass ${ioClass.className}`));
        console.log(chalk.blueBright(`Methods`));
        for (const method of ioClass.methods) {
            console.log(method.ios);
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
