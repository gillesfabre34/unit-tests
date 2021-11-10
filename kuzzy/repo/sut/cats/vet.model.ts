import { Person } from './person.model';
import { IAnimal } from './iAnimal.interface';
import { OnInit } from '@angular/core';

// export class Vet implements OnInit {
export class Vet extends Person implements OnInit {

    clinicName: string = undefined;

    constructor(firstName: string, lastName: string, clinicName: string) {
        super(firstName, lastName);
        this.clinicName = clinicName;
    }

    heal(animal: IAnimal): IAnimal {
        animal.isHealthy = true;
        return animal;
    }

    ngOnInit() {
        console.log('NgOnIt method');
    }

}
