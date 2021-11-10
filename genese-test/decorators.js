#!/usr/bin/env node
"use strict";

exports.__esModule = true;
var chalk = require('chalk');
var main = require("./src/main-decorators");

try {
    console.log(chalk.yellowBright("WELCOME TO GENESE TEST DECORATORS"));
    console.log();
    // -------------------------------------   GENESE COMPLEXITY   ------------------------------------------
    var mainProcess = new main.MainDecorators();
    mainProcess.start();

}
catch (err) {
    console.error(chalk.red("Error in conversion process : " + err.stack));
}
