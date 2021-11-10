import { Controller, Get, Param, Post, Body, Query, Delete } from '@nestjs/common';
import { BooksService } from './books.service';
import { CreateBookDTO } from './dto/create-book.dto';
import * as chalk from 'chalk';

@Controller('books')
export class BooksController {
    constructor(private booksService: BooksService) { }

    @Get()
    async getBooks() {
        const books = await this.booksService.getBooks();
        console.log(chalk.yellowBright('GET BOOKS'), books);
        return books;
    }

    @Get(':bookID')
    async getBook(@Param('bookID') bookID) {
        const book = await this.booksService.getBook(bookID);
        console.log(chalk.yellowBright('GET BOOK'), bookID, book);
        return book;
    }

    @Post()
    async addBook(@Body() createBookDTO: CreateBookDTO) {
        const book = await this.booksService.addBook(createBookDTO);
        console.log(chalk.yellowBright('ADD BOOK'), createBookDTO, book);
        return book;
    }

    @Delete()
    async deleteBook(@Query() query) {
        const books = await this.booksService.deleteBook(query.bookID);
        console.log(chalk.yellowBright('DELETE BOOK'), query, books);
        return books;
    }
}
