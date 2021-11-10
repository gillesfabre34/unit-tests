import { AgnosticSutClass } from '../models/agnostic-sut-class.model';

export abstract class AgnosticSutClassService {

    sutClass: AgnosticSutClass = undefined;

    abstract generate(sutClass: AgnosticSutClass): AgnosticSutClassService;

}
