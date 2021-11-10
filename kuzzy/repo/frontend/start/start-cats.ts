import { Main } from './main';

const algoPath = `${process.cwd()}`;

async function startBookstore() {
    const projectPath = `${algoPath}/sut/cats`;
    await new Main().startFrontend(projectPath, algoPath, 'cats');
}

startBookstore();
