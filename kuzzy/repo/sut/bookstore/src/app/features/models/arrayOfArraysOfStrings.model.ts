import { ArrayResponse, GeneseModelEnvironment } from 'genese-angular';


export class ArrayOfArraysOfStrings implements ArrayResponse {

    public gnArrayResponse ?= [['']];
    public genese?: GeneseModelEnvironment = {
        path: '/array-response/array-of-arrays-of-strings'
    };
}
