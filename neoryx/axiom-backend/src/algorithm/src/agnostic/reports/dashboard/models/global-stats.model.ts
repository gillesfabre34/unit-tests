import * as chalk from 'chalk';

export class GlobalStats {

    files = 0;
    functions = 0;
    methods = 0;
    statements = 0;

    constructor(files?: number, functions?: number, methods?: number, statements?: number) {
        this.files = files;
        this.functions = functions;
        this.methods = methods;
        this.statements = statements;
    }


    log(): void {
        console.log(chalk.blueBright('Files : '), this.files);
        console.log(chalk.blueBright('Functions : '), this.functions);
        console.log(chalk.blueBright('Methods : '), this.methods);
        if (this.statements > 0) {
            console.log(chalk.blueBright('Statements : '), this.statements);
        }
        console.log();
    }

}
