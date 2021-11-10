import { AgnosticSutClass } from '../models/agnostic-sut-class.model';

export abstract class AgnosticSutClassService<T> {

    sutClass: AgnosticSutClass<T> = undefined;

    abstract generate(sutClass: AgnosticSutClass<T>): AgnosticSutClassService<T>;

}
