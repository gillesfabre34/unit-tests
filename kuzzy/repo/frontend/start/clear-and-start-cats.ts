import { Main } from './main';
import { SystemUtDataService } from '../data-services/system-ut-data.service';

const algoPath = `${process.cwd()}`;
const appName = 'cats';

async function clearAndStart() {
    await SystemUtDataService.dropSystemUT(appName);
    const projectPath = `${__dirname}/../sut/${appName}`;
    await new Main().startFrontend(projectPath, algoPath, appName);
}

clearAndStart();
