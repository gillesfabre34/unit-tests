import { Component, OnInit } from '@angular/core';
import { Book } from '../models/book.model';
import { Genese, GeneseService } from 'genese-angular';


@Component({
    selector: 'app-get-all',
    templateUrl: './get-all.component.html',
    styleUrls: ['./get-all.component.scss']
})
export class GetAllComponent implements OnInit {

    // --------------------------------------------------
    //                     PROPERTIES
    // --------------------------------------------------

    public booksGenese: Genese<Book>;
    public data: Book[] = [];
    public model = {
        genese: {
            path: '/books'
        }
    };

    // --------------------------------------------------
    //                     CONSTRUCTOR
    // --------------------------------------------------

    constructor(
        private geneseService: GeneseService,
    ) {
        this.booksGenese = geneseService.getGeneseInstance(Book);
    }

    ngOnInit(): void {
        this.getData();
    }


    getData(): void {
        this.booksGenese
            .getAll()
            .subscribe((response: Book[]) => {
                console.log('%c getAll response ', 'font-weight: bold; color: black;', response);
                this.data = response;
            });
    }
}
