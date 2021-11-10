# Mocking or not mocking, that is the question

Dans quels cas devrait-on mocker les valeurs de retour des appels à des fonctions extérieures à la classe que l'on souhaite tester ? Tout le temps, parfois, jamais ?

Pour clarifier le propos, j'entends par "test unitaire" un test s'appliquant à une unité correspondant à une classe donnée. 

Le problème est le suivant : **lorsqu'une méthode fait appel à une autre méthode externe à la classe étudiée et que cette dernière est correctement testée, peut-on avoir confiance en sa valeur de retour ?**

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

Supposons maintenant que l'on se dise que si la méthode `isGreaterThanOne()` est correctement testée, alors nous avons la garantie qu'elle nous renverra bien `true` si a est supérieur à 1 et `false` sinon. Du coup, inutile de mocker sa valeur de retour, et on peut se contenter du test suivant :

```ts
import { CallExternalMethod } from './call-external-method';
     
 describe('CallExternalMethod', () => {
     let service;
 
     beforeEach(() => {
         service = new CallExternalMethod();
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

OK, notre méthode est correctement testée (je n'ai pas ajouté les `spy` pour ne pas alourdir le propos).

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
Cette méthode est-elle correctement testée ? Oui. Cela nous garantit-il que sa valeur de retour sera la bonne pour pouvoir l'utiliser dans le test unitaire de notre première méthode myMethod ? Non.

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

En conclusion, **on ne peut jamais se fier aux valeurs de retour d'une méthode externe, y compris si elle est parfaitement testée**, y compris s'il s'agit d'une fonction pure et déterministe. A mon sens, l'idée de se baser sur la confiance en les valeurs de retour des fonctions externes pose problème en raison de sa tendance à faire croire que nos tests sont solides alors qu'ils ne le sont pas.

Un test unitaire ne garantit **jamais** qu'une fonction se comporte comme on l'entend, mais **seulement** qu'elle renvoie les valeurs voulues pour les valeurs testées. Tout le reste n'est qu'illusion.

Cela signifie-t-il qu'il faille mocker systématiquement toutes les valeurs de retour des appels de méthodes externes ? Sans doute pas : à mon sens, il faut faire deux choses : des tests sans illusion et des tests pseudo-aléatoires.

## Les tests sans illusion

Ici, l'idée est de réaliser des tests qui ne vérifient que ce que l'on est certains de pouvoir parfaitement vérifier, c'est-à-dire le comportement de ce que fait **réellement** la méthode testée et non de ce qu'elle est **censée faire**. Autrement dit, de tester ce que l'on maitrise parfaitement et de ne pas se faire d'illusions sur le reste, c'est-à-dire sur l'exactitude des valeurs de retour des méthodes externes.

**Seuls les tests sans illusion peuvent apporter la certitude de quelque chose**. Certes, ils ne permettent toujours pas de garantir que la fonction testée renverra bien la valeur attendue : par contre, **ils garantissent que la fonction fait ce qu'elle est censée faire**, c'est-à-dire faire elle-même diverses opérations et appeler des méthodes externes et en attendre (ou pas) des valeurs de retour. Face à l'impossibilité de garantir ces valeurs de retour, **il est nécessaire de mocker systématiquement les appels externes**.

 ## Les tests pseudo-aléatoires
 
 Garantir que la fonction fait ce qu'elle est censée faire, sans illusions sur le monde extérieur, c'est bien : cela garantit vraiment quelque chose. Par contre, c'est un peu limité : on aimerait quand-même que notre test puisse crasher si jamais la méthode externe renvoie une valeur inattendue. Pour cela, il faut **enlever les mocks** des valeurs de retour des méthodes externes : ce n'est que de cette manière que l'on peut espérer repérer certains bugs.
 
 Dans l'exemple précédent, si on avait rajouté un test de `myMethod()` avec la valeur -2, il aurait crashé, puisque la fonction incorrectement codée `isGreaterThanOne()` aurait renvoyé `true` alors que l'on s'attendait à `false`. Le souci, c'est que l'on ne pouvait pas savoir ***a priori*** que -2 était une valeur utile à tester.
 
 Les tests avec 0 et 2 étaient corrects, car ils permettaient de passer par les deux chemins et de garantir que la méthode `myMethod()` se comportait correctement avec ces deux inputs précis. Mais ajouter un troisième test avec la valeur -2 aurait mécaniquement amélioré la sécurité du test en diminuant le nombre de "trous dans la raquette". 
 
 D'une manière générale, plus on testera de valeurs, plus les tests seront solides. Bien évidemment, étant donné que le nombre de valeurs possibles est infini, il est impossible de les tester toutes. Par contre, plus on fera de tests, plus les garanties seront fortes. Reste à savoir combien de tests serait-il raisonnable de faire et quels sont les critères permettant de faire les tests les plus judicieux possibles.
 
 Ajouter beaucoup de tests poserait-il problème ? Oui. Ou plutôt, oui dans le contexte actuel. **Oui, avant Fregate...**
 
 Cela poserait problème, car un développeur humain ne peut pas consacrer plus de temps qu'il ne le fait déjà à écrire des tests. Par contre, on peut imaginer générer automatiquement une foule de tests que le développeur n'aurait qu'à valider.
 
 On aurait encore un souci, car cela diminuerait grandement la lisibilité des fichiers de tests (du fait de leur longueur). En outre, cela augmenterait le coût de leur maintenance, puisqu'à chaque refacto il faudrait modifier une quantité considérable de tests.
 
Alors, quelle solution imaginer pour améliorer la solidité des tests (c'est-à-dire la probabilité qu'ils repèrent des erreurs) tout en évitant de surcharger de travail le développeur ?

**C'est à Fregate d'y répondre. C'est donc à nous de nous creuser la tête.** Je ne prétends pas avoir la réponse à cette question, mais je pense que nous devrions pouvoir trouver quelques pistes intéressantes d'ici la fin du POC. En voici quelques-unes :

## Calcul automatisé des valeurs de retour

Un long débat traverse les soutes de Frégate concernant la nécessité ou l'utilité de calculer automatiquement les valeurs de retour des méthodes à tester (`myMethod()` dans le cas précédent) ou des méthodes externes (`isGreaterThanOne()`). Dans un cas, comme dans l'autre, on suppose que l'on a su générer des valeurs de paramètres permettant de passer dans els différents chemins.
 
Si l'on choisit de ne pas calculer les valeurs de retour, le fichier généré serait 

```ts
import { ExternalService } from './external-service';

describe('ExternalService', () => {
    let service;

    beforeEach(() => {
        service = new ExternalService ();
    });

    describe('isGreaterThanOne', () => {

        // Tests sans illusion

        it(`should return XXX when a = 2`, () => { // TODO
            spyOn(service.externalService, 'isGreaterThanOne').and.returnValue(); // TODO
            const result = service.isGreaterThanOne(2);
            expect(service.externalService.isGreaterThanOne).toHaveBeenCalledWith(); // TODO
            expect(result) // TODO
        });
        it(`should return XXX when a = 0`, () => { // TODO
            spyOn(service.externalService, 'isGreaterThanOne').and.returnValue(); // TODO
            const result = service.isGreaterThanOne(0);
            expect(service.externalService.isGreaterThanOne).toHaveBeenCalledWith(); // TODO
            expect(result) // TODO
        });

        // Tests pseudo-aléatoires

        it(`should return XXX when a = 2`, () => { // TODO
            spyOn(service.externalService, 'isGreaterThanOne');
            const result = service.isGreaterThanOne(2);
            expect(service.externalService.isGreaterThanOne).toHaveBeenCalled();
            expect(result) // TODO
        });
        it(`should return XXX when a = 0`, () => { // TODO
            spyOn(service.externalService, 'isGreaterThanOne');
            const result = service.isGreaterThanOne(0);
            expect(service.externalService.isGreaterThanOne).toHaveBeenCalled();
            expect(result) // TODO
        });
    });
});

```  

Si l'on choisit de calculer les valeurs de retour, le fichier généré serait 

```ts
import { ExternalService } from './external-service';

describe('ExternalService', () => {
    let service;

    beforeEach(() => {
        service = new ExternalService ();
    });

    describe('isGreaterThanOne', () => {

        // Tests sans illusion

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

        // Tests pseudo-aléatoires

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

```  

Dans le premier cas, on demande au développeur d'enlever tous les TODO en calculant lui-même les valeurs de retour attendues en fonction des paramètres que nous lui aurons indiqué. 

Dans l'autre, on demande au développeur de regarder les tests que nous avons écrits et de vérifier que la valeur de retour que nous avons écrite est bien celle à laquelle il s'attendait.

Dans les deux cas, le développeur garde son libre arbitre et vérifie réellement que les valeurs retournées sont bien les bonnes. Il n'y a donc pas de perte de qualité du test, dans un cas comme dans l'autre. 

Les avantages de renvoyer les tests avec valeur de retour sont les suivants :

* Il n'y a pas besoin de remplacer les 12 TODO un par un et de compléter chaque ligne. Un simple coup d'oeil pour vérifier que les valeurs de retour sont les bonnes suffit. 
* On peut imaginer proposer au développeur toute une série de tests avec valeurs de retour parmi lesquels il n'aurait qu'à piocher

Le second point est important, car **en proposant toute une série de tests avec valeurs de retour, le développeur peut s'apercevoir que la fonction ne renvoie pas ce qu'il attendait sur un paramètre qu'il n'aurait pas pensé à tester.**

Le développeur a donc **une probabilité beaucoup plus forte de coder une méthode qui fasse réellement ce à quoi il s'attend.**

##  Les tests aux limites

Vous avez vu que le paragraphe précédent s'intitulait "les tests pseudo-aléatoires", et pourtant vous n'avez rien dit jusqu'ici alors que je n'ai jamais expliqué pourquoi je les appelais "pseudo-aléatoires". Être plus attentif tu dois, jeune développeur !

En réalité, quand on choisit une valeur à tester, on ne le fait pas de manière complètement aléatoire : pour `myMethod()`, on a choisi 0 et 2 car c'étaient des chiffres ronds qui "entouraient" 1. On a donc fait ce choix de manière non pas aléatoire mais pseudo-aléatoire. Mais était-ce le meilleur choix possible ?

On a vu plus haut que si l'on avait choisi -2 au lieu de 0, on se serait aperçus que la méthode externe ne renvoyait pas la valeur attendue, et on aurait pu faire la correction. Cela signifie-t-il que -2 aurait été un meilleur choix que 0 ?

Non, car si le développeur de la méthode externe s'était trompé non pas en écrivant `if (a * a > 1)` mais en écrivant `if (a > -1)`, alors c'est le choix du test avec la valeur 0 qui aurait permis de détecter l'erreur. Du coup, valait-il mieux choisir 0, -2, autre chose ou peu importe ?

C'est là qu'intervient la notion de test aux limites que je trouve décrite assez simplement [ici](https://latavernedutesteur.fr/2017/11/03/les-tests-aux-limites/). L'idée est relativement simple: en choisissant des valeurs très proches de la limite (c'est-à-dire de 1 dans notre cas), on améliore la qualité du test. Mais est-ce vraiment vrai de vrai ?

D'un point de vue mathématique, la réponse est non : il existe autant de fonctions qui mettent en échec nos tests en prenant des valeurs aux limites qu'en les prenant autrement. Par exemple, même en choisissant 0,999999 et 1,0000001 comme valeurs de test de la méthode `isGreaterThanOne()`, cela n'aurait rien changé au fait que la valeur -2 passe au travers des mailles du filet : si `isGreaterThanOne()` est codée avec `if (a * a > 1)` elle renverra quand même une valeur inattendue (true) lorsqu'elle reçoit la valeur -2, alors que notre test est "aux limites".

Cependant, nous vivons dans un monde imparfait, c'est-à-dire extérieur aux mathématiques. Dans ce monde-là, on peut considérer que le développeur peut se tromper avec une probabilité plus ou moins forte suivant le type d'erreur possible. Par exemple, il est moins probable qu'un développeur se trompe en changeant radicalement **la nature de la fonction** (ou du if dans le cas présent).

Dans l'exemple précédent, il est peu probable qu'un développeur puisse imaginer que sa méthode ne va pas changer de comportement en remplaçant une inégalité de degré 1 par une autre de degré 2 (même s'il n'est pas très fort en maths). Vous me direz qu'il ne serait pas moins stupide de remplacer benoîtement `if (a > 1)` par `if (a > 2)` sans se douter de rien, mais vous voyez l'idée.

Dans l'exemple de [la caverne du testeur](https://latavernedutesteur.fr/2017/11/03/les-tests-aux-limites/), les paramètres représentent le poids des pièces jointes et leur nombre : difficile de se tromper en utilisant une équation de degré deux. Voici ce qu'il écrit :

 > Les tests aux limites ont un intérêt principal : Ils vérifient les points faibles de l’application, c'est-à-dire les points qui ont le plus de chances d’être en échec.
 > En effet, dans les tests aux limites donnent plus d’informations que les tests aléatoires. Dans le cas des tests aux limites on peut facilement dessiner la limite voulue par les spécifications alors que les limites sont beaucoup plus floues avec les tests aléatoires.

Comme on vient de le voir, il a raison, mais seulement dans la mesure où la fonction testée **ne change pas de nature**. Reste à préciser ce que signifie la "nature" d'une fonction.

