import * as chalk from 'chalk';
import { HistoricService } from './historic.service';
import { GLOBALS } from '../../../init/constants/globals.const';

console.log(chalk.yellowBright('TEST HISTORIC SERVICE\n'));

GLOBALS.historicPath = `${GLOBALS.appRoot}/src/agnostic/reports/historic/mocks/historic-mock.json`;

async function testGetRegressions() {
    const regressions = await HistoricService.getRegressions();
}

testGetRegressions();
