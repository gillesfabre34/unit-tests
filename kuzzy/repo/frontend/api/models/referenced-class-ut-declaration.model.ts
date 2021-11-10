import { ClassDeclaration, EnumDeclaration } from 'ts-morph';
import { PostFileUTDtoClassUT } from '../../../dtos/file-ut/post-file-uts.dto';


export class ReferencedDeclaration {

    classDeclaration: ClassDeclaration = undefined;
    enumDeclaration: EnumDeclaration = undefined;
    postFileUTDtoClassUT: PostFileUTDtoClassUT = undefined;
    reference: number = undefined;

    constructor(declaration: ClassDeclaration | EnumDeclaration, postFileUTDtoClassUT: PostFileUTDtoClassUT, reference: number) {
        if (declaration instanceof ClassDeclaration) {
            this.classDeclaration = declaration;
        } else {
            this.enumDeclaration = declaration;
        }
        this.postFileUTDtoClassUT = postFileUTDtoClassUT;
        this.reference = reference;
    }


    get declaration(): ClassDeclaration | EnumDeclaration {
        return this.classDeclaration ?? this.enumDeclaration;
    }


    get filePath(): string {
        return this.declaration.getSourceFile().getFilePath();
    }
}
