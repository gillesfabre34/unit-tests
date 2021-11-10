import { Language } from 'algorithm/src/agnostic/init/enums/language.enum';
import { Report } from 'algorithm/src/agnostic/reports/core/models/report.model';
import Algorithm from '../algorithm/src/main'

export class AlgorithmService {

    private algorithm = new Algorithm();

    async generateUnitTestReport(fileOrDirPathToTest: string, sut: string, language: Language): Promise<Report> {
        return this.algorithm.start(fileOrDirPathToTest, sut, language);
    }
}
