export class MainStats {

    bugs = 0;
    codeCoverage = '';
    duration = '';
    tests = 0;

    constructor(bugs = 0, codeCoverage = '', duration = '', tests = 0) {
        this.bugs = bugs;
        this.codeCoverage = codeCoverage;
        this.duration = duration;
        this.tests = tests;
    }

}
