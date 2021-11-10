import { AgnosticSutMethod } from './agnostic-sut-method.model';
import { AgnosticSutFile } from './agnostic-sut-file.model';
import { AgnosticTestSuite } from '../../generation/models/agnostic-test-suite.model';

export abstract class AgnosticSutClass<T> {

    classDeclaration: any = undefined;
    code: string = '';
    name: string = undefined;
    sutFile: AgnosticSutFile = undefined;
    sutMethods: AgnosticSutMethod<T>[] = [];
    abstract getClassName(): string;
    abstract getMethodDeclarations(): any[];
    abstract newSutMethod(): AgnosticSutMethod<T>;


    get testSuites(): AgnosticTestSuite<T>[] {
        return [].concat(...this.sutMethods?.map(s => s.testSuites)) ?? [];
    }


    generate(sutFile: AgnosticSutFile, classDeclaration: any): AgnosticSutClass<T> {
        this.classDeclaration = classDeclaration;
        this.sutFile = sutFile;
        this.name = this.getClassName();
        const methodDeclarations: any[] = this.getMethodDeclarations() ?? [];
        for (const methodDeclaration of methodDeclarations) {
            this.sutMethods.push(this.newSutMethod().generate(this, methodDeclaration));
        }
        return this;
    }


    async addTestSuites(): Promise<void> {
        for (const sutMethod of this.sutMethods) {
            await sutMethod.addTestSuites();
        }
    }


    addConstraints(): void {
        for (const sutMethod of this.sutMethods) {
            sutMethod.addConstraints();
        }
    }
}
