# Mocking or not mocking, that is the question

Dans quels cas devrait-on mocker les valeurs de retour des appels à des fonctions extérieures à la classe que l'on souhaite tester ? Tout le temps, parfois, jamais ?

Pour clarifier le propos, j'entends par "test unitaire" un test s'appliquant à une unité correspondant à une classe donnée. 

Le problème est le suivant : **lorsqu'une méthode fait appel à une méthode externe à la classe étudiée et que cette dernière est correctement testée, peut-on avoir confiance en sa valeur de retour ?**

A mon sens, **la réponse est non**. Ce n'est que mon avis et ce qui suit n'est qu'un raisonnement parmi d'autres, destiné à être analysé et éventuellement contredit. En tout cas, il est censé susciter le débat.

Prenons un exemple très simple de méthode censée renvoyer 1 quand son paramètre a est supérieur à 1 et retourner 0 sinon : 

```ts
import ExternalService from 'external.service';

class MyService {

    externalService = new ExternalService();

    myMethod(a: number): number {
        if (this.externalService.isGreaterThanOne(a)) {
            return 1;
        } else {
            return 0;
        }
    }
}
``` 

Supposons maintenant que l'on se dise que si la méthode `isGreaterThanOne()` est correctement testée, alors nous avons la garantie qu'elle renverra `true` si a est supérieur à 1 et `false` sinon. Du coup, il serait effectivement inutile de mocker sa valeur de retour, et on pourrait se contenter du test suivant :

```ts
import { MyService } from 'my.service';
     
 describe('MyService', () => {
     let service;
 
     beforeEach(() => {
         service = new MyService();
     });
 
     describe('myMethod', () => {
         it(`should return 1 when a = 2`, () => {
             const result = service.myMethod(2);
             expect(result).toEqual(1);
         });
         it(`should return 0 when a = 0`, () => {
             const result = service.myMethod(0);
             expect(result).toEqual(0);
         });
     });
 });
```

OK, notre méthode est correctement testée (je n'ai pas ajouté les éventuels `spy` pour ne pas alourdir le propos).

Continuons avec la méthode externe :

```ts
export class ExternalService {
    
    isGreaterThanOne(a: number): boolean {
        if (a > 1) {
            return true;
        }
        return false;
    }
    
}
```

et son test unitaire :

```ts
import { ExternalService } from './external-service';

describe('ExternalService', () => {
    let service;

    beforeEach(() => {
        service = new ExternalService ();
    });

    describe('isGreaterThanOne', () => {
        it(`should return true when a = 2`, () => {
            const result = service.isGreaterThanOne(2);
            expect(result).toBeTrue();
        });
        it(`should return false when a = 0`, () => {
            const result = service.isGreaterThanOne(0);
            expect(result).toBeFalse();
        });
    });
});

```
Cette méthode est-elle correctement testée ? Oui. Cela nous garantit-il que sa valeur de retour sera la bonne et que pouvons nous fier là-dessus pour l'utiliser sans crainte dans le test unitaire de notre première méthode `myMethod()` ? Non.

**Ce n'est pas parce qu'un test est bien fait qu'il garantit que la méthode va réellement se comporter comme on l'entend**. A mon sens, **aucun** test unitaire, aussi bon soit-il, ne garantira jamais qu'une méthode se comporte comme on le souhaite.

**Exemple :**

Imaginons que le développeur de la méthode externe se soit trompé (lui ou un développeur futur qui modifiera la méthode et changera son comportement, de manière volontaire ou non).

Par exemple, imaginons qu'il ait écrit ça : (c'est un exemple très facile où l'on voit rapidement l'erreur, mais cela serait la même chose dans un cas plus complexe)

```ts
export class ExternalService {
    
    isGreaterThanOne(a: number): boolean {
        if (a * a > 1) {
            return true;
        }
        return false;
    }
    
}
```

Supposons également que les tests unitaires de cette méthode soient les mêmes que ci-dessus. Ces derniers seraient toujours corrects, car la méthode retournerait toujours `true` pour `a = 2` et `false` pour `a = 0`;

Dans ce cas, les deux méthodes `myMethod()` et `isGreaterThanOne()` seraient toujours parfaitement testées, mais aucun de leurs tests ne crasherait, y compris ceux de `myMethod()`, alors même qu'aucune des deux méthodes n'a le comportement attendu. Par exemple, pour `a = -2`, `myMethod()` renverrait 1 au lieu de 0.

En conclusion, **on ne peut jamais se fier aux valeurs de retour d'une méthode externe, y compris si elle est parfaitement testée**, y compris s'il s'agit d'une fonction pure et déterministe. A mon sens, l'idée de se baser sur la confiance en les valeurs de retour de fonctions externes pose problème en raison de sa tendance à faire croire que nos tests sont solides alors qu'ils ne le sont pas.

Un test unitaire ne garantit **jamais** qu'une fonction se comporte comme on l'entend, mais **seulement** qu'elle renvoie les valeurs voulues pour les valeurs testées. Tout le reste n'est qu'illusion.

Cela signifie-t-il qu'il faille mocker systématiquement toutes les valeurs de retour des appels de méthodes externes ? Sans doute pas : à mon sens, il faut faire deux choses : des tests sans illusion et des tests pseudo-aléatoires.

## Les tests sans illusion

Ici, l'idée est de réaliser des tests qui ne vérifient que ce que l'on est certains de pouvoir parfaitement vérifier, c'est-à-dire le comportement de ce que fait **réellement** la méthode testée et non de ce qu'elle est **censée faire**. Autrement dit, de tester ce que l'on maitrise parfaitement et de ne pas se faire d'illusions sur le reste, c'est-à-dire sur l'exactitude des valeurs de retour des méthodes externes.

**Seuls les tests sans illusion peuvent apporter la certitude de quelque chose**. Certes, ils ne permettent pas de garantir que la fonction testée renverra bien la valeur attendue : par contre, **ils garantissent que la fonction fait ce qu'elle est censée faire**, c'est-à-dire réaliser elle-même diverses opérations et appeler des méthodes externes et en attendre (ou pas) des valeurs de retour. Face à l'impossibilité de garantir ces valeurs de retour, **il est nécessaire de mocker systématiquement les appels externes**.

 ## Les tests pseudo-aléatoires
 
 Garantir que la fonction fait ce qu'elle est censée faire, sans illusions sur le monde extérieur, c'est bien : cela garantit vraiment quelque chose. Par contre, c'est un peu limité : on aimerait quand-même que notre test puisse crasher si jamais la méthode externe renvoie une valeur inattendue. Pour cela, il faut **enlever les mocks** des valeurs de retour des méthodes externes : ce n'est que de cette manière que l'on peut espérer repérer certains bugs.
 
 Dans l'exemple précédent, si on avait rajouté un test de `myMethod()` avec la valeur -2, il aurait crashé, puisque la fonction incorrectement codée `isGreaterThanOne()` aurait renvoyé `true` alors que l'on s'attendait à `false`. Le souci, c'est que l'on ne pouvait pas savoir ***a priori*** que -2 était une valeur utile à tester.
 
 Les tests avec 0 et 2 étaient corrects, car ils permettaient de passer par les deux chemins et de garantir que la méthode `myMethod()` se comportait correctement avec ces deux inputs précis. Mais ajouter un troisième test avec la valeur -2 aurait mécaniquement amélioré la sécurité du test en diminuant le nombre de "trous dans la raquette". 
 
 D'une manière générale, plus on testera de valeurs, plus les tests seront solides. Bien évidemment, le nombre de valeurs possibles étant infini, il est impossible de les tester toutes. Par contre, plus on fera de tests, plus les garanties seront fortes. Reste à savoir combien de tests il serait raisonnable de faire et quels seraient les critères permettant de les choisir le plus judicieusement possible.
 
 Ajouter beaucoup de tests poserait-il problème ? Oui. Ou plutôt, oui dans le contexte actuel. **Oui, avant Fregate...**
 
 Cela poserait problème, car un développeur humain ne peut pas consacrer plus de temps qu'il ne le fait déjà à écrire des tests. Par contre, on peut imaginer générer automatiquement une foule de tests que le développeur n'aurait qu'à valider.
 
 On aurait encore un souci, car cela diminuerait grandement la lisibilité des fichiers de tests (du fait de leur longueur). En outre, cela augmenterait le coût de leur maintenance, puisqu'à chaque refacto il faudrait modifier une quantité considérable de tests.
 
Alors, quelle solution imaginer pour améliorer la solidité des tests (c'est-à-dire la probabilité qu'ils repèrent des erreurs) tout en évitant de surcharger de travail le développeur ?

**C'est à Fregate d'y répondre. C'est donc à nous de nous creuser la tête.** Je ne prétends pas avoir la réponse à cette question, mais je pense que nous devrions pouvoir trouver quelques pistes intéressantes d'ici la fin du POC.


##  Les tests aux limites

Vous avez vu que le paragraphe précédent s'intitulait "les tests pseudo-aléatoires" et pourtant vous n'avez rien dit, alors même que je n'ai jamais expliqué pourquoi je les appelais "pseudo-aléatoires". Être plus attentif tu dois, jeune entrepreneur !

En réalité, quand on choisit une valeur à tester, on ne le fait pas de manière complètement aléatoire : pour `myMethod()`, on a choisi 0 et 2 car c'étaient des chiffres ronds qui "entouraient" 1. On a donc fait ce choix de manière non pas aléatoire mais pseudo-aléatoire. Mais était-ce le meilleur choix possible ?

On a vu plus haut que si l'on avait choisi -2 au lieu de 0, on se serait aperçus que la mauvaise méthode externe ne renvoyait pas la valeur attendue, et on aurait pu faire la correction. Cela signifie-t-il que -2 aurait été un meilleur choix que 0 ?

Non, car si le développeur de la méthode externe s'était trompé non pas en écrivant `if (a * a > 1)` mais en écrivant `if (a > -1)`, alors c'est le choix du test avec la valeur 0 qui aurait permis de trouver l'erreur. Du coup, valait-il mieux choisir 0, -2, autre chose ou peu importe ?

C'est là qu'intervient la notion de **test aux limites** que je trouve décrite assez simplement [ici](https://latavernedutesteur.fr/2017/11/03/les-tests-aux-limites/). L'idée est la suivante : en choisissant des valeurs très proches de la limite (c'est-à-dire de 1 dans notre cas), on améliore la qualité du test. Mais est-ce vraiment le cas, et si oui, comment déterminer ces limites de manière algorithmique ?

Pour le savoir, il te faudra prendre patience, jeune entrepreneur, et attendre l'article suivant... ou l'écrire toi-même !

En attendant, on termine cet article avec le clou du spectacle, la polémique du moment que chacun attend avec gourmandise : le calcul des valeurs de retour ! 


## Calcul automatisé des valeurs de retour

Un long débat traverse les soutes de Frégate concernant la nécessité ou l'utilité de calculer automatiquement les valeurs de retour des méthodes que nous souhaitons tester (c'est-à-dire `myMethod()` dans le cas précédent) ou des méthodes externes (comme `isGreaterThanOne()`). Dans un cas comme dans l'autre, on part de l'hypothèse que l'on a su générer des valeurs de paramètres permettant de passer dans les différents chemins, ce qui n'est pas rien.
 
Si l'on choisit de ne pas calculer les valeurs de retour, le fichier généré serait 

```ts
import { MyService } from 'my.service';
     
 describe('MyService', () => {
     let service;
 
     beforeEach(() => {
         service = new MyService();
     });
 
     describe('myMethod', () => {

        describe('no illusion tests', () => {

            beforeEach(() => {
                service.externalService = {
                    isGreaterThanOne: () => {}
                }
            });

            it(`should return XXX when a = 2`, () => { // TODO
                spyOn(service.externalService, 'isGreaterThanOne').and.returnValue(XXX); // TODO
                const result = service.isGreaterThanOne(2);
                expect(service.externalService.isGreaterThanOne).toHaveBeenCalledWith(2);
                expect(result).XXX; // TODO
            });
            it(`should return XXX when a = 0`, () => { // TODO
                spyOn(service.externalService, 'isGreaterThanOne').and.returnValue(XXX); // TODO
                const result = service.isGreaterThanOne(0);
                expect(service.externalService.isGreaterThanOne).toHaveBeenCalledWith(0);
                expect(result).XXX; // TODO
            });
        })


        describe('pseudo-random tests', () => {
            it(`should return XXX when a = 2`, () => { // TODO
                spyOn(service.externalService, 'isGreaterThanOne');
                const result = service.isGreaterThanOne(2);
                expect(service.externalService.isGreaterThanOne).toHaveBeenCalledWith(2);
                expect(result).XXX; // TODO
            });
            it(`should return XXX when a = 0`, () => { // TODO
                spyOn(service.externalService, 'isGreaterThanOne');
                const result = service.isGreaterThanOne(0);
                expect(service.externalService.isGreaterThanOne).toHaveBeenCalledWith(0);
                expect(result).XXX; // TODO
            });
        });
    });
});

```  

D'un autre côté, si l'on choisit de calculer les valeurs de retour, le fichier généré serait 

```ts
import { MyService } from 'my.service';
     
 describe('MyService', () => {
     let service;
 
     beforeEach(() => {
     	service = new MyService();
     });
 
     describe('myMethod', () => {

        describe('no illusion tests', () => {

            beforeEach(() => {
                service.externalService = {
                    isGreaterThanOne: () => {}
                }
            });

            it(`should return 1 when a = 2`, () => {
                spyOn(service.externalService, 'isGreaterThanOne').and.returnValue(true);
                const result = service.isGreaterThanOne(2);
                expect(service.externalService.isGreaterThanOne).toHaveBeenCalledWith(2);
                expect(result).toBeTrue();
            });
            it(`should return 0 when a = 0`, () => {
                spyOn(service.externalService, 'isGreaterThanOne').and.returnValue(false);
                const result = service.isGreaterThanOne(0);
                expect(service.externalService.isGreaterThanOne).toHaveBeenCalledWith(0);
                expect(result).toBeFalse();
            });
        })


        describe('pseudo-random tests', () => {
            it(`should return 1 when a = 2`, () => {
                spyOn(service.externalService, 'isGreaterThanOne');
                const result = service.isGreaterThanOne(2);
                expect(service.externalService.isGreaterThanOne).toHaveBeenCalledWith(2);
                expect(result).toBeTrue();
            });
            it(`should return 0 when a = 0`, () => {
                spyOn(service.externalService, 'isGreaterThanOne');
                const result = service.isGreaterThanOne(0);
                expect(service.externalService.isGreaterThanOne).toHaveBeenCalledWith(0);
                expect(result).toBeFalse();
            });
        });
    });
});

```  

Dans le premier cas, on demande au développeur d'**enlever tous les TODO en calculant lui-même les valeurs de retour attendues** en fonction des paramètres que nous lui aurons indiqués. 

Dans l'autre, on demande au développeur de **regarder les tests que nous avons générés** et de **vérifier que la valeur de retour est bien celle à laquelle il s'attendait**.

Dans les deux cas, le développeur garde son libre arbitre et **vérifie réellement que les valeurs retournées sont les bonnes**. Il n'y a donc pas de perte de qualité du test, dans un cas comme dans l'autre, et il s'agit bien d'un test unitaire qui permet au développeur de s'assurer que la fonction qu'il est en train de coder a le comportement attendu. 

Les avantages de générer les valeurs de retour sont les suivants :

* Il n'y a pas besoin de remplacer les TODO un par un et de compléter chaque ligne. Il suffit d'un simple coup d'oeil pour vérifier que les valeurs de retour sont les bonnes. 
* Plutôt que de générer le nombre minimal de tests, on peut en proposer beaucoup plus au développeur. Il n'aurait alors qu'à choisir ceux qui l'intéressent (éventuellement via une interface graphique facile à lire)

Ce second point est important, car en proposant toute une série de tests avec valeurs de retour, le développeur peut s'apercevoir que la fonction ne renvoie pas ce qu'il attendait **pour un paramètre qu'il n'aurait pas pensé à tester.**

Il a donc **une probabilité beaucoup plus forte de coder une méthode qui fasse réellement ce à quoi il s'attend.**

Il y a probablement beaucoup d'autres idées intéressantes qui pourraient surgir à partir du moment où l'on imagine savoir trouver les valeurs de retour. Je ne sais pas lesquelles, mais la piste me semble prometteuse. 

C'est pourquoi je la soumets à nouveau à débat.
