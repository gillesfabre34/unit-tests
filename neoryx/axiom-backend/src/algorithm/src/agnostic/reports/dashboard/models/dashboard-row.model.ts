export class DashboardRow {

    bugs = '';
    color = 'black';
    codeCoverage = '';
    duration = '';
    fileName = '';
    filePath = '';
    methodName = '';
    numberOfTests = 0;
    testFilePath = '';

    constructor(bugs = '', codeCoverage = '', duration = '', fileName = '', methodName = '', numberOfTests = 0, color = 'black', filePath = '', testFilePath = '') {
        this.bugs = bugs;
        this.codeCoverage = codeCoverage;
        this.duration = duration;
        this.fileName = fileName;
        this.methodName = methodName;
        this.numberOfTests = numberOfTests;
        this.color = color;
        this.filePath = filePath;
        this.testFilePath = testFilePath;
    }

}
