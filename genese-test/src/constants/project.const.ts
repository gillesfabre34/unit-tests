import { Project } from 'ts-morph';
import { PATH_MOCK } from '../mocks/paths.mock';

let project = new Project();
project.addSourceFileAtPath(PATH_MOCK);

export const PROJECT = project;
