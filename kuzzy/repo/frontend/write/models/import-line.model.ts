export class ImportLine {

    identifiers: string[] = [];
    moduleSpecifier: string = undefined;

    constructor(moduleSpecifier: string, identifiers: string[] = []) {
        this.moduleSpecifier = moduleSpecifier;
        this.identifiers = identifiers;
    }


    get code(): string {
        let code = `import { `;
        for (const identifier of this.identifiers) {
            code = `${code}${identifier}, `;
        }
        code = `${code.slice(0, -2)} } from '${this.moduleSpecifier}';\n`;
        return code;
    }

}
