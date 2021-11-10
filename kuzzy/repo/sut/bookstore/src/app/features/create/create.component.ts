import { Component, OnInit } from '@angular/core';
import { Book } from '../models/book.model';
import { Genese, GeneseService } from 'genese-angular';


@Component({
    selector: 'app-create',
    templateUrl: './create.component.html',
    styleUrls: ['./create.component.scss']
})
export class CreateComponent implements OnInit {

    // --------------------------------------------------
    //                     PROPERTIES
    // --------------------------------------------------

    public book: Book = {};
    public booksGenese: Genese<Book>;
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


    }


    create() {
        console.log('%c create this.book ', 'font-weight: bold; color: fuchsia;', this.book);
        this.booksGenese.create(this.book).subscribe((newBook: Book) => {
            console.log('%c GeneseAbstract create newBook ', 'font-weight: bold; color: fuchsia;', newBook);
        });
    }

}
