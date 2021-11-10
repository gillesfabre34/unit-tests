import { ReportService } from './report.service';


describe('ReportService', () => {
    let reportService = new ReportService();

    beforeEach(() => {
    });

    describe('generateReport', () => {
        it(`
            Should`, () => {

            const result = reportService.generateReport();

            expect(result).toEqual(undefined);
        });


    });

    describe('createMainStats', () => {
        it(`
            Should`, () => {

            const result = reportService.createMainStats();

            expect(result).toEqual(undefined);
        });


    });

    describe('createStatsArray', () => {
        it(`
            Should`, () => {

            const result = reportService.createStatsArray();

            expect(result).toEqual(undefined);
        });


    });

    describe('getRowsStats', () => {
        it(`
            Should`, () => {

            const result = reportService.getRowsStats(undefined, 0);

            expect(result).toEqual(undefined);
        });


    });

    describe('writeReport', () => {
        it(`
            Should`, () => {

            const result = reportService.writeReport();

            expect(result).toEqual(undefined);
        });


    });

    describe('registerPartial', () => {
        it(`
            Should`, () => {

            const result = reportService.registerPartial(``, ``);

            expect(result).toEqual(undefined);
        });


    });


});

