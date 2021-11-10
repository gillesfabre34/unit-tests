#!/usr/bin/env node
"use strict";

exports.__esModule = true;
var chalk = require('chalk');
var main = require("./src/main");

try {
    console.log(chalk.yellowBright("WELCOME TO FREGATE UNIT TEST GENERATION"));
    console.log();
    // -------------------------------------   GENESE COMPLEXITY   ------------------------------------------
    var mainProcess = new main.Main();
    const pathToAnalyze = __dirname + '/src/mocks/examples/my-service/my.service.ts';
    mainProcess.start(pathToAnalyze);

}
catch (err) {
    console.error(chalk.red("Error in conversion process : " + err.stack));
}
