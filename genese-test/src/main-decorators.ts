import * as chalk from 'chalk';
import { ClassSpecService } from './services/class-spec.service';
import { createFileAsync, getFilename } from './services/tools.service';
import { PROJECT } from './constants/project.const';
import { IfNotFalsyService } from './mocks/examples/if-not-falsy/if-not-falsy.service';
import { IoService } from './services/io.service';
import { IfContinueRoute } from './mocks/examples/if-continue-route/if-continue-route.service';

export class MainDecorators {

    /**
     * Starts the main process
     */
    async start(): Promise<any> {
        let service = new IfNotFalsyService();
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
