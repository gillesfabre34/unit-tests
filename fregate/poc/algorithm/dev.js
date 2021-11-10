#!/usr/bin/env node
"use strict";


exports.__esModule = true;
var chalk = require('chalk');
var languages = require("./src/agnostic/init/enums/language.enum");
var main = require("./src/main");

try {
    console.log(chalk.yellowBright("WELCOME TO NEORYX UNIT TEST GENERATION"));
    console.log();
    var mainProcess = new main.Main();
    const filePath = __dirname + '/src/languages/ts/mocks/ifs/if-object-equality';
    mainProcess.start(filePath, languages.Language.TS);

}
catch (err) {
    console.error(chalk.red("Error in process : " + err.stack));
}
