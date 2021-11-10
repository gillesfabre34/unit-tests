import * as chalk from 'chalk';
import { GlobalService } from './services/global.service';
import { createFileAsync, getFilename } from './services/tools.service';
import { PROJECT } from './constants/project.const';
import { IfNotFalsy } from './mocks/examples/ifs/if-not-falsy/if-not-falsy';
import { IoService } from './services/io.service';
import { IfContinueRoute } from './mocks/examples/ifs/if-continue-route/if-continue-route';

export class MainDecorators {

    /**
     * Starts the main process
     */
    async start(): Promise<any> {
        let service = new IfNotFalsy();
        service.myMethod('ZZZ');
        let other = new IfContinueRoute();
        other.myMethod('a');
        console.log(chalk.magentaBright('IO RESULTS\n'));
        for (const ioClass of IoService.ioClasses) {
            ioClass.log();
        }
        console.log();
        console.log(chalk.greenBright('UNIT TESTS GENERATED SUCCESSFULLY WITH DECORATORS'));
    }

}
