import { Component, OnInit } from '@angular/core';
import { Genese, GeneseService } from 'genese-angular';
import { ArrayOfArraysOfStrings } from '../models/arrayOfArraysOfStrings.model';
import { ArrayOfStrings } from '../models/arrayOfStrings.model';
import { ArrayOfArraysOfBooks } from '../models/arrayOfArraysOfBooks.model';
import { Book } from '../models/book.model';


@Component({
    selector: 'app-get-array',
    templateUrl: './get-array.component.html',
    styleUrls: ['./get-array.component.scss']
})
export class GetArrayComponent implements OnInit {

    public arrayOfArraysOfBooksGenese: Genese<ArrayOfArraysOfBooks>;
    public arrayOfArraysOfStringsGenese: Genese<ArrayOfArraysOfStrings>;
    public arrayOfStringsGenese: Genese<ArrayOfStrings>;
    public model = {
        gnArrayResponse: [[new Book()]],
        genese: {
            path: '/array-response/array-of-arrays-of-books'
        }
    };

    // --------------------------------------------------
    //                     CONSTRUCTOR
    // --------------------------------------------------

    constructor(
        private geneseService: GeneseService,
    ) {
        this.arrayOfArraysOfBooksGenese = geneseService.getGeneseInstance(ArrayOfArraysOfBooks);
        this.arrayOfArraysOfStringsGenese = geneseService.getGeneseInstance(ArrayOfArraysOfStrings);
        this.arrayOfStringsGenese = geneseService.getGeneseInstance(ArrayOfStrings);
    }

    ngOnInit(): void {
        this.getArrayOfArraysOfBooks();
        this.getArrayOfStrings();
        this.getArrayOfArraysOfStrings();
    }



    /**
     * Get array of strings
     */
    getArrayOfStrings(): void {
        this.arrayOfArraysOfStringsGenese.getArray().subscribe((data: any) => {
            console.log('%c getArrayOfStrings data', 'font-weight: bold; color: blue;', data);
        });
    }



    /**
     * Get array of arrays of strings
     */
    getArrayOfArraysOfStrings(): void {
        this.arrayOfArraysOfStringsGenese.getArray().subscribe((data: any) => {
            console.log('%c getArrayOfArraysOfStrings data', 'font-weight: bold; color: blue;', data);
        });
    }



    /**
     * Get array of arrays of books
     */
    getArrayOfArraysOfBooks(): void {
        this.arrayOfArraysOfBooksGenese.getArray().subscribe((data: Book[][]) => {
            console.log('%c getArrayOfArraysOfBooks data', 'font-weight: bold; color: blue;', data);
        });
    }
}
