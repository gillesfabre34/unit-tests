import { HistoricReport } from '../models/historic-report.model';
import { Historic } from '../models/historic.model';
import { JsonHistoricService } from './json-historic.service';
import { HistoricService } from './historic.service';
import * as chalk from 'chalk';
import { MethodCoverage } from '../models/method-coverage.model';
import { HistoricRow } from '../models/historic-row.model';
import { ArchiveStats } from '../models/archive-stats.model';
import { getFilename } from '../../../tools/utils/file-system.util';
import { CoverageColor } from '../models/coverage-color.model';
import { Color } from '../../core/enums/colors.enum';
import { percentage } from '../../../tools/utils/numbers.util';
import * as eol from "eol";
import * as fs from 'fs-extra';
import * as path from "path";
import { ReportService } from '../../core/services/report.service';
import { GLOBALS } from '../../../init/constants/globals.const';
import { FullDate } from '../models/full-date.model';

export class HistoricReportService {

    static async generateReport(date?: number, numberOfArchives: number = 10): Promise<HistoricReport> {
        const historic: Historic = await JsonHistoricService.getHistoricFromJsonFile();
        let latestArchiveStats: ArchiveStats = HistoricService.getArchiveStats(historic, date);
        const previousArchiveStats: ArchiveStats[] = HistoricService.getListOfPreviousArchiveStats(historic, latestArchiveStats, numberOfArchives);
        const historicReport: HistoricReport = this.getHistoricReport(latestArchiveStats, previousArchiveStats);
        const template:string = eol.auto(fs.readFileSync(path.join(process.cwd(), `/src/algorithm/src/agnostic/reports/historic/templates/historic.handlebars`), 'utf-8'));
        await ReportService.writeReport(`${GLOBALS.projectPath}/neoryx/reports/historic.html`, template, historicReport);
        return historicReport;
    }


    private static getHistoricReport(latestArchiveStats: ArchiveStats, previousArchivesStats: ArchiveStats[]): HistoricReport {
        const latestMethodsCoverage: MethodCoverage[] = this.getMethodsCoverage(latestArchiveStats);
        const historicRows: HistoricRow[] = [];
        const fullDates: FullDate[] = this.getTableHeader(latestArchiveStats, previousArchivesStats);
        const columns: CoverageColor[][] = this.getColumns(latestMethodsCoverage, latestArchiveStats, previousArchivesStats);
        let isFirstColumn = true;
        for (const column of columns) {
            for (let row = 0; row< column.length; row++) {
                if (isFirstColumn) {
                    const filePath: string = latestArchiveStats.methodsArchives[row].methodCoverage.filePath;
                    const methodName: string = latestArchiveStats.methodsArchives[row].methodCoverage.name;
                    historicRows[row] = new HistoricRow(getFilename(filePath), methodName, [column[row]]);
                } else {
                    historicRows[row].coveragesColor.push(column[row]);
                }
            }
            isFirstColumn = false;
        }
        const globalCoverage: CoverageColor[] = this.getGlobalCoverage(latestArchiveStats, previousArchivesStats);
        return new HistoricReport(historicRows, fullDates, globalCoverage);
    }


    private static getTableHeader(latestArchiveStats: ArchiveStats, previousArchiveStats: ArchiveStats[]): FullDate[] {
        const fullDates: FullDate[] = [new FullDate(this.formatDate(latestArchiveStats.date), this.formatTime(latestArchiveStats.date))];
        for (const archiveStats of previousArchiveStats) {
            fullDates.push(new FullDate(this.formatDate(archiveStats.date), this.formatTime(archiveStats.date)));
        }
        return fullDates;
    }


    private static getColumns(latestMethodsCoverage: MethodCoverage[], latestArchiveStats: ArchiveStats, previousArchivesStats: ArchiveStats[]): CoverageColor[][] {
        const firstColumn: CoverageColor[] = this.setColumn(latestMethodsCoverage);
        const columns: CoverageColor[][] = [firstColumn];
        for (const previousArchiveStats of previousArchivesStats) {
            columns.push(this.setOtherColumn(latestArchiveStats, previousArchiveStats.methodsArchives.map(m => m.methodCoverage)));
        }
        return columns;
    }


    private static setColumn(methodsCoverage: MethodCoverage[] = []): CoverageColor[] {
        const coveragesColor: CoverageColor[] = [];
        for (const methodCoverage of methodsCoverage) {
            coveragesColor.push(this.getCoverageColor(methodCoverage));
        }
        return coveragesColor;
    }


    private static setOtherColumn(latestArchiveStats: ArchiveStats, methodsCoverage: MethodCoverage[] = []): CoverageColor[] {
        const coveragesColor: CoverageColor[] = [];
        for (const methodCoverage of methodsCoverage) {
            if (this.findMethodCoverage(methodCoverage, latestArchiveStats)) {
                coveragesColor.push(this.getCoverageColor(methodCoverage));
            }
        }
        return coveragesColor;
    }


    private static getGlobalCoverage(latestArchiveStats: ArchiveStats, previousArchivesStats: ArchiveStats[]): CoverageColor[] {
        const globalCoverage: CoverageColor[] = [this.getGlobalCoverageColor(latestArchiveStats, previousArchivesStats?.[0])];
        for (let i = 0; i < previousArchivesStats.length; i ++) {
            globalCoverage.push(this.getGlobalCoverageColor(previousArchivesStats[i], previousArchivesStats[i + 1]));
        }
        return globalCoverage;
    }


    private static getGlobalCoverageColor(archiveStats: ArchiveStats, previousArchiveStats: ArchiveStats): CoverageColor {
        const text: string = this.getCodeCoverageText(archiveStats.coveredStatements, archiveStats.totalStatements);
        const codeCoverage: number = archiveStats.sutCoverage;
        const firstPreviousCodeCoverage: number = previousArchiveStats ? previousArchiveStats.sutCoverage : 0;
        return new CoverageColor(text, this.getColor(codeCoverage, firstPreviousCodeCoverage));
    }


    private static getMethodsCoverage(archiveStats: ArchiveStats): MethodCoverage[] {
        const methodsCoverage: MethodCoverage[] = [];
        for (const methodArchive of archiveStats.methodsArchives) {
            const methodCoverage: MethodCoverage = methodArchive.methodCoverage;
            methodCoverage.date = methodCoverage.date || archiveStats.date;
            methodsCoverage.push(methodCoverage);
        }
        return methodsCoverage;
    }


    private static getCoverageColor(methodCoverage: MethodCoverage, archiveStats?: ArchiveStats): CoverageColor {
        const previousMethodCoverage: MethodCoverage = this.findMethodCoverage(methodCoverage, archiveStats);
        if (!previousMethodCoverage) {
            return new CoverageColor(this.getCodeCoverageText(methodCoverage.numberOfCoveredStatements, methodCoverage.totalStatements), Color.NORMAL);
        }
        return new CoverageColor(this.getCodeCoverageText(methodCoverage.numberOfCoveredStatements, methodCoverage.totalStatements), this.getColor(methodCoverage.codeCoverage, previousMethodCoverage.codeCoverage));
    }


    private static findMethodCoverage(methodCoverage: MethodCoverage, archiveStats: ArchiveStats): MethodCoverage {
        return archiveStats?.methodsArchives?.map(m => m.methodCoverage).find(m => m.isSameAs(methodCoverage));
    }


    private static getColor(codeCoverage: number, previousCodeCoverage: number): Color {
        if (codeCoverage > previousCodeCoverage) {
            return Color.GOOD;
        } else if (codeCoverage === previousCodeCoverage) {
            return Color.NORMAL;
        }
        return Color.ERROR;
    }


    private static getCodeCoverageText(numberOfCoveredStatements: number, totalStatements: number): string {
        return `${numberOfCoveredStatements} / ${totalStatements} (${percentage(numberOfCoveredStatements, totalStatements)} %)`;
    }


    private static formatDate(date: number): string {
        return new Date(date).toISOString().slice(0, 10);
    }


    private static formatTime(date: number): string {
        return new Date(date).toISOString().slice(11, 19);
    }


}
