import TestController from '../controllers/test.controller';
import { Router } from 'express';
import Route from '../interfaces/routes.interface';

class TestRoute implements Route {
    public path = '/test';
    public router = Router();
    public testController = new TestController();

    constructor() {
        this.initializeRoutes();
    }

    private initializeRoutes() {
        this.router.get(`${this.path}`, this.testController.testFileOrFolder);
        this.router.get(`${this.path}/tree`, this.testController.getTree);
    }
}

export default TestRoute;
