import { Language } from '../algorithm/src/agnostic/init/enums/language.enum';
import { Request, Response, NextFunction } from 'express';
import { AlgorithmService } from '../services/algorithm.service';
import * as dirtree from 'directory-tree';

class TestController {
    private algorithmService: AlgorithmService = new AlgorithmService();

    public testFileOrFolder = async (req: Request, res: Response, next: NextFunction) => {

        const path: string = req.query.path as string;
        const language: Language = req.query.language as Language;
        const sut: string = req.query.sut as string;
        try {
            const testResult = await this.algorithmService.generateUnitTestReport(path, sut, language);
            res.status(200).json({ data: testResult, message: 'testGenerated' });
        } catch (error) {
            next(error);
        }
    }

    public getTree = async (req: Request, res: Response, next: NextFunction) => {
        return res.status(200).json(dirtree('/Users/utilisateur/Documents/neoryx/axiom/axiom-backend', { extensions: /\.ts$/ }));
    }
}

export default TestController;
