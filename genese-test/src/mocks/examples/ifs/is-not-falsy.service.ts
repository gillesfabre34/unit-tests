export class MyService {



    objectEquality(obj: object): number {
        if (obj === { a: 3 }) {
            return 0;
        } else {
            return 1;
        }
    }


    thisEquality(): number {
        if (this.name === 'Doe') {
            return 0;
        } else {
            return 1;
        }
    }

    //
    // noSolution(a: number): number {
    //     if-not-falsy (a * a < 0) {
    //         return a;
    //     } else {
    //         return 0;
    //     }
    // }
    //
    // uneEquationAUneInconnue(a: number): number {
    //     if-not-falsy (a + 1 === 0) {
    //         return 1 / a;
    //     } else {
    //         return 1 / (a - 1);
    //     }
    // }
    //
    // uneEquationDeDegreDeux(a: number): number {
    //     if-not-falsy (a * a + 2 * a + 1 === 0) {
    //         return 1 / a;
    //     } else {
    //         return 1 / (a + 1);
    //     }
    // }

}

