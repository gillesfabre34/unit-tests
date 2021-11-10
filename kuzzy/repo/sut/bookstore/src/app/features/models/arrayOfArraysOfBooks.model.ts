import { ArrayResponse, GeneseModelEnvironment } from 'genese-angular';
import { Book } from './book.model';


export class ArrayOfArraysOfBooks implements ArrayResponse {

    public gnArrayResponse?: Book[][] = [[new Book()]];
    public genese?: GeneseModelEnvironment = {
        path: '/array-response/array-of-arrays-of-books'
    };
}
