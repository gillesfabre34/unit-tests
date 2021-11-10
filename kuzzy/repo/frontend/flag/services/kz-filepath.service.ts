import { CallExpression, ClassDeclaration, SourceFile, VariableDeclarationKind } from 'ts-morph';

export abstract class KzFilePathService {


    static addKzFilePathArgument(callExpression: CallExpression): void {
        if (!this.alreadyAddedKzFilePathArgument(callExpression)) {
            callExpression?.insertArgument(0, 'kz_filePath');
        }
    }


    private static alreadyAddedKzFilePathArgument(callExpression: CallExpression): boolean {
        return callExpression.getArguments()?.[0]?.getText() === 'kz_filePath';
    }


    static addConstants(sourceFile: SourceFile): void {
        sourceFile.insertVariableStatement(0, {
            declarations: [{
                name: 'kz_filePath',
                initializer: `'${sourceFile.getFilePath()}'`
            }],
            declarationKind: VariableDeclarationKind.Const
        });
    }


    static setClassDecorator(classDeclaration: ClassDeclaration): void {
        classDeclaration.insertDecorator(0, {
            name: 'CreateInstance',
            arguments: [`kz_filePath`],
            typeArguments: ['string']
        });
    }
}
