import * as chalk from 'chalk';
import axios from 'axios';
import { ClassDeclaration, EnumDeclaration, FunctionDeclaration, PropertyDeclaration, SourceFile } from 'ts-morph';
import {
    PostFileUTDto,
    PostFileUTDtoCallerUT,
    PostFileUTDtoClassUT,
    PostFileUTDtoEnumUT,
    PostFileUTDtoProperty,
    PostFileUTsDto
} from '../../../dtos/file-ut/post-file-uts.dto';
import { GLOBAL } from '../../init/const/global.const';
import { PrimitiveType, primitiveTypes } from '../../../shared/utils/primitives.util';
import { getImportDefaultsInsideProject } from '../../../shared/utils/ast-imports.util';
import { ImportDefault } from '../../write/models/import-default.model';
import { ReferencedDeclaration } from '../models/referenced-class-ut-declaration.model';
import { ShouldBeSavedService } from './should-be-saved.service';
import { ClassOrEnumDeclaration } from '../types/class-or-enum-declaration.type';
import { removeExtension } from '../../utils/file-system.util';
import { Mapper } from '@genese/mapper';

export class FileUTDataService {

    static async deleteFileUts(fileUTPaths: string[]): Promise<string> {
        try {
            const response = await axios.delete(`${GLOBAL.apiUrl}/file-ut/paths`, {
                data: {
                    fileUTPaths: fileUTPaths
                }
            });
            const numberOfDeletedFiles: string = await Mapper.create(String, response.data);
            console.log(chalk.cyanBright('Number of deleted files : '), numberOfDeletedFiles);
            return response.data;
        } catch(err) {
            console.log(chalk.red('Error deleting FileUTs'), err.response?.data);
            return err.response?.data;
        }
    }


    static async findFileUTPaths(): Promise<string[]> {
        try {
            const response: any = await axios.get(`${GLOBAL.apiUrl}/file-ut/paths`);
            return await Mapper.create('string[]', response?.data);
        } catch(err) {
            console.log(chalk.red('Error getting fileUT paths'), err.response?.data);
            return [];
        }
    }


    // --------------------------------------------------------------------------------------------
    // -------------------------------------   Post FileUTs   -------------------------------------
    // -----   During the init proc, synchronizes data in database and current project files   ----
    // --------------------------------------------------------------------------------------------

    /**
     * References which will be used to link property types to the corresponding class or enum
     */
    static reference = 0;
    static referencedDeclarations: ReferencedDeclaration[] = [];


    /**
     * Posts fileUTs in database
     * @param sourceFiles
     */
    static async postFileUTDtos(sourceFiles: SourceFile[]): Promise<number> {
        try {
            const postFileUTsDto: PostFileUTsDto = this.formatToPostFileUTsDto(sourceFiles);
            const response = await axios.post(`${GLOBAL.apiUrl}/file-ut`, {
                fileUTs: postFileUTsDto.fileUTs,
                systemId: GLOBAL.systemUT.id
            });
            return await Mapper.create(Number, response.data);
        } catch(err) {
            console.log(chalk.red('Error posting FileUTs'), err.response?.data);
            return err.response?.data;
        }
    }


    /**
     * Formats sourceFiles in PostFileUTsDto format
     * @param sourceFiles
     * @private
     */
    private static formatToPostFileUTsDto(sourceFiles: SourceFile[]): PostFileUTsDto {
        const postFileUTDtos: PostFileUTsDto = {
            fileUTs: []
        };
        for (const sourceFile of ShouldBeSavedService.shouldBeSaved(sourceFiles)) {
            postFileUTDtos.fileUTs.push(this.formatToPostFileUTDto(sourceFile));
        }
        this.formatToPostFileUTDtoProperties();
        return postFileUTDtos;
    }


    /**
     * Formats sourceFile in PostFileUTsDto format
     * @param sourceFile
     * @private
     */
    private static formatToPostFileUTDto(sourceFile: SourceFile): PostFileUTDto {
        let  postFileUTDto: PostFileUTDto = {
            classUTs: [],
            enumUTs: [],
            functionUTs: []
        };
        postFileUTDto = this.formatClassesOrEnumsUTDto(postFileUTDto, sourceFile);
        for (const functionDeclaration of sourceFile.getFunctions()) {
            postFileUTDto.functionUTs.push(this.formatToPostFileUTDtoFunctionUT(functionDeclaration));
        }
        postFileUTDto.path = sourceFile.getFilePath();
        return postFileUTDto;
    }


    /**
     * Formats the classes and enums in PostFileUTsDto format
     * @param postFileUTDto
     * @param sourceFile
     * @private
     */
    private static formatClassesOrEnumsUTDto(postFileUTDto: PostFileUTDto, sourceFile: SourceFile): PostFileUTDto {
        for (const classDeclaration of sourceFile.getClasses()) {
            this.formatClassOrEnumDto(postFileUTDto, classDeclaration);
        }
        for (const enumDeclaration of sourceFile.getEnums()) {
            this.formatClassOrEnumDto(postFileUTDto, enumDeclaration);
        }
        return postFileUTDto;
    }


    /**
     * Formats one class or one enum in PostFileUTsDto format
     * Creates a new reference for it, which will be used later to link property types to the corresponding class or enum
     * @param postFileUTDto
     * @param declaration
     * @private
     */
    private static formatClassOrEnumDto(postFileUTDto: PostFileUTDto, declaration: ClassOrEnumDeclaration): void {
        let postFileUTDtoClassOrEnumUT: PostFileUTDtoClassUT | PostFileUTDtoEnumUT;
        if (declaration instanceof ClassDeclaration) {
            postFileUTDtoClassOrEnumUT = this.formatToPostFileUTDtoClassUT(declaration);
            postFileUTDto.classUTs.push(postFileUTDtoClassOrEnumUT);
        } else {
            postFileUTDtoClassOrEnumUT = this.formatToPostFileUTDtoEnumUT(declaration);
            postFileUTDto.enumUTs.push(postFileUTDtoClassOrEnumUT);
        }
        this.referencedDeclarations.push(new ReferencedDeclaration(declaration, postFileUTDtoClassOrEnumUT, this.reference));
        this.reference++;
    }


    /**
     * Formats one class in PostFileUTsDto format
     * @param classDeclaration
     * @private
     */
    private static formatToPostFileUTDtoClassUT(classDeclaration: ClassDeclaration): PostFileUTDtoClassUT {
        const postFileUTDtoClassUT: PostFileUTDtoClassUT = {
            methodUTs: []
        };
        for (const methodDeclaration of classDeclaration.getMethods()) {
            postFileUTDtoClassUT.methodUTs.push({
                name: methodDeclaration.getName(),
                numberOfStatementUTs: methodDeclaration.getDescendantStatements().length
            });
        }
        postFileUTDtoClassUT.name = classDeclaration.getName();
        postFileUTDtoClassUT.reference = this.reference;
        return postFileUTDtoClassUT;
    }


    /**
     * Formats one enum in PostFileUTsDto format
     * @param enumDeclaration
     * @private
     */
    private static formatToPostFileUTDtoEnumUT(enumDeclaration: EnumDeclaration): PostFileUTDtoEnumUT {
        const postFileUTDtoEnumUT: PostFileUTDtoEnumUT = {
            keyValues: []
        };
        postFileUTDtoEnumUT.name = enumDeclaration.getName();
        for (const member of enumDeclaration.getMembers()) {
            postFileUTDtoEnumUT.keyValues.push({
                key: member.getName(),
                value: member.getValue().toString()
            })
            postFileUTDtoEnumUT.primitiveType = typeof member.getValue() as 'string' | 'number';
        }
        postFileUTDtoEnumUT.reference = this.reference;
        return postFileUTDtoEnumUT;
    }


    /**
     * Formats one function in PostFileUTsDto format
     * @param functionDeclaration
     * @private
     */
    private static formatToPostFileUTDtoFunctionUT(functionDeclaration: FunctionDeclaration): PostFileUTDtoCallerUT {
        const postFileUTDtoFunctionUT = new PostFileUTDtoCallerUT();
        postFileUTDtoFunctionUT.name = functionDeclaration.getName();
        postFileUTDtoFunctionUT.numberOfStatementUTs = functionDeclaration.getDescendantStatements().length;
        return postFileUTDtoFunctionUT;
    }


    // ---------------------------------   Post FileUTs properties   -------------------------------------


    /**
     * Formats properties in PostFileUTsDto format
     * @private
     */
    private static formatToPostFileUTDtoProperties(): void {
        const referencedClassDeclarations: ReferencedDeclaration[] = this.referencedDeclarations.filter(r => !!r.classDeclaration);
        for (const referencedClassDeclaration of referencedClassDeclarations) {
            this.addPropertiesToClassUTDto(referencedClassDeclaration);
        }
    }


    /**
     * Adds properties to PosFileUTDto.
     * @param referencedClassDeclaration
     * @private
     */
    private static addPropertiesToClassUTDto(referencedClassDeclaration: ReferencedDeclaration): void {
        referencedClassDeclaration.postFileUTDtoClassUT.properties = [];
        const classDeclaration: ClassDeclaration = referencedClassDeclaration.declaration as ClassDeclaration;
        const importDefaultsInsideProject: ImportDefault[] = getImportDefaultsInsideProject(classDeclaration.getSourceFile());
        for (const propertyDto of classDeclaration.getProperties()) {
            const postFileUTDtoProperty: PostFileUTDtoProperty = this.addPropertyToClassUTDto(propertyDto, importDefaultsInsideProject);
            referencedClassDeclaration.postFileUTDtoClassUT.properties.push(postFileUTDtoProperty);
        }
    }


    /**
     * Adds property to PosFileUTDto and links it to the (eventual) corresponding type class or enum
     * @private
     * @param propertyDto
     * @param importDefaultsInsideProject
     */
    private static addPropertyToClassUTDto(propertyDto: PropertyDeclaration, importDefaultsInsideProject: ImportDefault[]): PostFileUTDtoProperty {
        const postFileUTDtoProperty = new PostFileUTDtoProperty();
        postFileUTDtoProperty.name = propertyDto.getName();
        const apparentType: string = propertyDto.getType().getApparentType().getText().toLowerCase();
        const apparentTypeImportDeclarationPath: string = this.getApparentTypeImportDeclarationPath(apparentType);
        if (primitiveTypes.includes(apparentType)) {
            this.addPrimitiveType(postFileUTDtoProperty, apparentType);
        } else if (apparentTypeImportDeclarationPath) {
            this.addTypeClassReference(postFileUTDtoProperty, apparentTypeImportDeclarationPath, importDefaultsInsideProject);
        }
        else {
            this.addUnknownType(postFileUTDtoProperty);
        }
        return postFileUTDtoProperty;
    }


    /**
     * Returns the path of the import of a property with its apparent type
     * @param apparentType
     * @private
     */
    private static getApparentTypeImportDeclarationPath(apparentType: string): string {
        return /^import\("(.*)"/.exec(apparentType)?.[1];
    }


    /**
     * Adds the property "primitiveType" for primitive properties
     * @param postFileUTDtoProperty
     * @param apparentType
     * @private
     */
    private static addPrimitiveType(postFileUTDtoProperty: PostFileUTDtoProperty, apparentType: string): void {
        postFileUTDtoProperty.primitiveType = apparentType as PrimitiveType;
    }


    /**
     * Adds the type class or enum reference to the property which has a type declared inside the project
     * @param postFileUTDtoProperty
     * @param apparentTypeImportDeclarationPath
     * @param importDefaultsInsideProject
     * @private
     */
    private static addTypeClassReference(postFileUTDtoProperty: PostFileUTDtoProperty, apparentTypeImportDeclarationPath: string, importDefaultsInsideProject: ImportDefault[]): void {
        const importDefault: ImportDefault = importDefaultsInsideProject.find(i => removeExtension(i.declarationPath?.toLowerCase()) === apparentTypeImportDeclarationPath);
        const referenceClass: ReferencedDeclaration = this.referencedDeclarations.find(r => r.filePath?.toLowerCase() === importDefault?.declarationPath?.toLowerCase());
        postFileUTDtoProperty.typeClassReference = referenceClass?.reference;
    }


    /**
     * Adds 'unknown' to propertyEntity fields when the type was not found (like for literal objects)
     * @param postFileUTDtoProperty
     * @private
     */
    private static addUnknownType(postFileUTDtoProperty: PostFileUTDtoProperty): void {
        postFileUTDtoProperty.primitiveType = 'unknown';
        postFileUTDtoProperty.typeClassReference = 'unknown';
    }
}
