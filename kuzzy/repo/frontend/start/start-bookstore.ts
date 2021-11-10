import { Main } from './main';

const algoPath = `${process.cwd()}`;

async function startBookstore() {
    const projectPath = `${algoPath}/sut/bookstore`;
    await new Main().startFrontend(projectPath, algoPath, 'bookstore');
}

startBookstore();
