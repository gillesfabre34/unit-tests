import { CoverageColor } from './coverage-color.model';

export class HistoricRow {

    coveragesColor: CoverageColor[] = [];
    fileName: string = undefined;
    methodName: string = undefined;

    constructor(fileName?: string, methodName?: string, coveragesColor?: CoverageColor[]) {
        this.fileName = fileName;
        this.methodName = methodName;
        this.coveragesColor = coveragesColor;
    }

}
