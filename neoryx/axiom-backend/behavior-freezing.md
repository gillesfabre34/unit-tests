# Behavior freezing

## Definition

The Behavior Freezing is the action to "freeze" the behavior of a method, a file or a project to protect it against the involuntary changes of its behavior. It increases the security of the app by decreasing the probability of bugs than if he used the classic testing method. 

In this article, the approach of the behavior freezing will be limited to a method consisting to increase the number of tests comparatively to adding only one test for each branch. Our goal will be to explain how this augmentation will increase the security of the app and how to solve the problem of the readability of the test files.

## Reinforce the security by adding optional tests

### Constraint Boundaries

#### Definition

A Constraint Boundary is a limit value which is changing the value of a predicate.

**Example :**

The Constraint Boundaries of the method below are 3 and 8.

```
inInterval(a: number): number {
    if (a > 3 && a < 8) {
        return 1;
    } else {
        return 2;
    }
}
```   


### Control points

#### Definition

A Control Point is a test case which is essential to use in the eyes of the developer. The Control Points usually include some constraint boundaries but are not limited to them.


**Example :**

Let's assume that we want to test the method above. The developer needs only two values to cover all the statements (the `if` and `else` paths). For example, he could choose the boundary `3` and a value strictly included in the interval, like 5. That's engough to cover all the statements, but the developer could want to add a test case corresponding to the values higher than 8, like 10, for example. 

At the end, he will have 3 control points : `3`, `5` and `10`.

#### Behavior freezing of control points

At first, we must remind that the fact of adding tests will never be able to completely freeze the behavior of a method or a project. This behavior will be frozen ***only*** for the test case values, even if we add a test for each branch. In the example above, we can only say that the method will be protected against every involuntary changes for the values 3, 5 and 10. No more.

**Example :**

Assuming that a second developer will modify the `inInterval` method like this :

```
inInterval(a: number): number {
    if (a > 3 && a < 7) {
        return 1;
    } else {
        return 2;
    }
}
```   

The behavior of the method changed (the value `7` will now return `2` instead of `1`), but no test crashed. If this changement was involuntary, that means that the second developer introduced a bug and that no test will tell it to him.

 Of course, in this example, the changement is so evident that we could say that it can't be involuntary, and that the developer was sure to not break something. In reality, this is not true : the developer can make this changement thinking that it doesn't change anything but in reality will produce a hidden bug somewhere else.
 
 **Example :**
 
 Assuming that elsewhere in the project, there is this other file : 
 
 ```
import { inInterval } from 'is-in-interval.ts';

doSomething(a: number) {
    // do some stuff
    const b: number = inInterval(a);
    // do some other stuff using the const 'b'
    return something;
}
```

Assuming now that the `doSomething()` method is also perfectly tested, for exemple with the values `3`, `5` and `10`.

When the developer replaced the value `8` by `7`, he changed the behavior of the methods `isInterval()` **AND** `doSomething()`, and no one test crashed, even for the `doSomething()` method.

That means that the developer changed the behavior of a method in another file without knowing it, which is a big risk of introducing a new bug.

### Freezing force

As we saw above, a test case is able to freeze the behavior of the app on one point, no somewhere else. That means that if we add other tests, we will reinforce the security of the app by giving the guarantee to freeze the behavior in other points. Of course, this reasoning has some limits. We can't add an infinity of tests. In contrast, we can add some points on strategic postions to decrease significantly the probability of adding bugs.

We call the actual practice to add only one test case for each branch a **"freezing of force 1"**.

#### Boundaries testing (freezing force 2)

A common practice to create tests as relevant as possible is to do "boundaries testing". The idea is to add tests cases "just before" and "just after" the boundaries to be sure that even a small changement of them will make the test crash.

**Example :**

In the example above, the boundaries were `3` and `8`, and the control points chosen by the developer were `3`, `5` and `10`. Instead of choosing these values, we could choose something like `2.99`, `3.01`, `7,99` and `8.01`. With these choices, when the second developer will replace `8` by `7`, the test case `7.99` will crash and will prevent an involuntary behavior changement. The security of the app is reinforced.

By adding the boundaries test cases, we say that we use behavior freezing of force 2.

#### Distant testing (freezing force 3)

With boundaries testing, the limits are secured, but that doesn't mean that everything is under control.

**Example :**

Assuming that the future developer will modify the `inInterval()` method by adding a new path :

```
inInterval(a: number): number {
    if (a > 3 && a < 8) {
        return 1;
    } else if (a > 15) {
        return 3;
    } else {
        return 2;
    }
}
```   

In this case, even with the boundaries testing, no test will crash, and a bug could be introduced. To avoid this problem, we can add tests "as distant as possible" from the boundaries. 

For example, if we added the test cases `-1 000 000 000`, `5.5` and `1 000 000 000`, the test for the value `1 000 000 000` shall crash and inform the developer of the changement of behavior.

With boundaries testing and distant testing, we say that we use a freezing of force 3.

#### Higher freezing forces

From a test suite of a given freezing force, we can construct new test cases which will each time increase the security of the app. Each force level will decrease the probability of bugs than the previous one.

### Ghost tests

#### Problematic

In classic testing, the test files must be easy to read and to maintain. So, if we add a lot of other tests, the files will be more long to write, to read and to maintain. That's why we need some tools to avoid these secondary effects.

#### Test generator

At first, we need a test generator which would be able to create automatically as many tests as we want, depending only on the freezing force that we want to apply. With this test generator, the developer will not waste his time for test writing.

#### Readability

To avoid the problem of readability, we could display by default only the tests of control points (force 1) or around boundaries (force 2) and hide the others. The developer shall be able to see all of them in a separated file if he really wants to see all of them.  

The hidden tests are called **"ghost tests"**.

#### Maintainability

About maintainability, if the developer wants to change the behavior of a method, he will need to modify the corresponding tests. If the tests are plentiful, it will be long and fastidious. That's why the unit test generator must be able to modify automatically the tests created previously.

#### Performances

Running all the tests of a huge project can be long. So, if we multiply the number of tests by 2 or 3, the time needed to execute tests will increase in proportion.

That's why the developer must be able to run only the tests of the force he wants. For example, during the development, he could launch the tests of force 1, for a commit the tests of force 2, for a merge those of force 3 and finally the tests of force 10 when he will deliver the app to the client in production.


