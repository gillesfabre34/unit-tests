import { Color } from '../../core/enums/colors.enum';

export class CoverageColor {

    text: string = undefined;
    color: Color;

    constructor(text?: string, color?: Color) {
        this.text = text;
        this.color = color;
    }
}
