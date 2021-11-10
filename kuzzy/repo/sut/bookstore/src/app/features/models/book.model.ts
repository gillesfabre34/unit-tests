import { GeneseModelEnvironment } from 'genese-angular';


export class Book {

    public id ?= 0;
    public title ?= '';
    public description ?= '';
    public author ?= '';
    public genese?: GeneseModelEnvironment = {
        path: '/books'
    };
}
