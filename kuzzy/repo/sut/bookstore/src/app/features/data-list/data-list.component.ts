import { AfterViewInit, Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { Genese, GeneseService, GetAllResponse } from 'genese-angular';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Book } from '../models/book.model';
import { tap } from 'rxjs/operators';
import { homeEnv } from '../homeEnv';


@Component({
    selector: 'app-data-list',
    templateUrl: './data-list.component.html',
    styleUrls: ['./data-list.component.scss']
})
export class DataListComponent implements AfterViewInit, OnChanges, OnInit {

    // --------------------------------------------------
    //                     PROPERTIES
    // --------------------------------------------------

    @Input() data: any[] = [];
    @Output() delete: EventEmitter<string> = new EventEmitter<any>();
    @Output() update: EventEmitter<string> = new EventEmitter<any>();

    public booksGenese: Genese<Book>;
    public dataSource = new MatTableDataSource<Book>();
    public displayedColumns: string[] = [];
    public emptyList = true;
    public pageIndex = 0;
    public pageSize = 5;

    @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;

    // --------------------------------------------------
    //                     CONSTRUCTOR
    // --------------------------------------------------

    constructor(
        private geneseService: GeneseService,
    ) {
        this.booksGenese = geneseService.getGeneseInstance(Book);
    }

    /**
     * Component initialization
     */
    ngOnInit(): void {
        this.paginator.pageIndex = this.pageIndex;
        this.paginator.pageSize = this.pageSize;
        this.getAll();
    }


    ngOnChanges(changes: SimpleChanges): void {
        if (changes.data) {
            this.getAll();
        }
    }

    /**
     * Component after initialization
     */
    ngAfterViewInit(): void {
        this.paginator.page
            .pipe(
                tap(() => this.getAllWithPagination())
            )
            .subscribe();
    }


    deleteElement(id: string): void {
        this.delete.emit(id);
    }


    updateElement(id: string): void {
        this.update.emit(id);
    }



    /**
     * Get all the app with pagination
     */
    getAll(): void {
        this.displayedColumns = ['id', 'author', 'title', 'description', 'actions'];
        if (Array.isArray(this.data)) {
            this.displayMatTableDataSource({results: this.data, totalResults: this.data.length});
        }
    }

    /**
     * Get all the app with pagination
     */
    getAllWithPagination(): void {
        this.displayedColumns = ['id', 'author', 'title', 'description', 'actions'];
        this.booksGenese
            .getAllWithPagination(
                homeEnv.path,
                {
                    pageIndex: this.paginator.pageIndex,
                    pageSize: this.paginator.pageSize
                })
            .subscribe((response: {results: Book[], totalResults: number}) => {
                console.log('%c getAllWithPagination response ', 'font-weight: bold; color: orange;', response);
                this.displayMatTableDataSource(response);
            });
    }



    /**
     * Display the app list in a MatTable with pagination
     * @param data
     */
    displayMatTableDataSource(data: GetAllResponse<Book>) {
        this.dataSource = data && Array.isArray(data.results) ? new MatTableDataSource(data.results) : new MatTableDataSource([]);
        this.paginator.length = data && data.totalResults ? data.totalResults : 0;
        this.emptyList = this.paginator.length === 0;
    }


}
