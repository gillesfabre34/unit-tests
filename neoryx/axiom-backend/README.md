# NEORYX


## TODO

### Features

* *Add wrappers (DONE)* 
* Change instance values in "its"
* Add tests for instance updates
* *Abstract classes (DONE)*
* Abstract methods
* *Static methods (DONE)*
* Array parameters (for-let-const.ts)
* Parse ALL the paths (if-continue-route.ts, if-without-else.ts)
* Infinity (if-first-degree-equation-crash.ts)
* Create report showing missing tests or paths not covered
* Add the case "if not falsy" (if-not-falsy.ts)
* Create relevant tests for external methods calls with different paths inside them (call-external-method.ts)
* Async methods
* Typed parameters
* Global vars modification (init.service.ts)
* AI for usual parameters names (name, path, ...) (init.service.ts)

### Bug fixes

* *Remove ";" (one-line-and-return.ts) (DONE)*
* *Remove "with" (one-line-and-return.ts) (DONE)*
* *Remove empty "beforeEach" (one-line-and-return.ts) (DONE)*
* *Missing title (if-and-dependent.ts) (DONE)*
* Use indexes choices for parameters without constraints (if-and-dependent-nested-calls.ts)
* Decimals choices (if-and-dependent-nested-intersection.ts, if-fifth-degree-equation.ts, if-second-degree-equation.ts)
* Object equality (if-object-equality.ts)
* Instance property (if-this-property.ts)
* String length (if-two-params.ts)
* Typed objects (if-type-object-in-param.ts)
* Error calculating number of statements (sut-file.service.ts)
* What should we do when the method returns new Something() ? (sut-file.service.ts)
* SpyOn static methods (call-local-static-method.ts)

### Analyse of the results

| Files                                 | Crash | Tests OK | Tests to modify | Tests to add | Comments |
| ---                                   | ---   | ---      | ---             | ---          | ---      | 
| call-external-method                  |       | 1        |                 | 1            | Should "understand" the behaviour of the external method |
| -> external.service                   |       | 2        |                 |              |          |
| call-external-method-inside-if        |       | 2        |                 |             |         |
| call-external-method-with-new-keyword |       | 1        |                 |              | Unable to add spies when the initializer is inside the current method         |
| call-external-static-method           |       | 1        |                 |              |          |
| call-local-method                     |       | 2        |                 |              |          |
| call-local-method-with-param          |       | 2        |                 |              |          |
| call-local-static-method              |       | 3        |                 |              |          |
| hello.service                         |       | 9        |                 |              | One test irrelevant         |
| message.service                       |       |          | 1               |              | Test irrelevant         |
| if-and-dependent                      |       | 2        |                 |              |          |
| if-and-dependent-nested-calls         |       | 2        |                 | 1            | Missing one path for value between 4 and 5. Algorithm not enough powerful        |
| -> external.service                   |       | 4        |                 |              |          |
| if-and-dependent-nested-inclusion     |       | 2        |                 |              |          |
| if-and-dependent-nested-intersection  |       | 3        |                 |              |          |
| if-and-independent                    |       | 2        |                 |              |          |
| if-continue-route                     |       | 2        |                 |              |          |
| if-fifth-degree-equation              |       | 1        |                 | 1            | Impossible to solve this kind of equality         |
| if-first-degree-equation              |       | 2        |                 |              | It would not be possible for a solution more "complex" (decimal, irrational, ...)         |
| if-first-degree-equation-crash        |       | 2        |                 |              |          |
| if-no-solution                        |       | 1        |                 | 1            | Not really a test to add, but a warning telling that a path is impossible to parse         |
| if-not-falsy                          |       | 2        |                 |              |          |
| if-not-falsy-static                   |       | 2        |                 |              |          |
| if-not-falsy-without-types            |       | 1        | 1               |              | Should understand that a parameter called "text" is not a number. Should choose undefined instead of 0. Miss the case "not undefined'         |
| if-object-equality                    |       | 1        |                 |              | Caution : one branch can't be used         |
| if-object-in-param                    |       | 2        |                 |              |          |
| if-or                                 |       | 2        |                 |              |          |
| if-second-degree-equation             |       | 1        |                 | 1            | Miss one value         |
| if-string-equality                    |       | 2        |                 |              | Titles could have better style (empty string)        |
| if-ternary                            |       | 2        |                 |              |          |
| if-this-property                      |       | 1        |                 | 1            | TODO: change the instance value in the different Its         |
| if-two-params                         |       | 3        |                 | 1            | Miss one case         |
| if-typed-object-in-param              |       | 1        |                 | 1            | Tests for typed objects are not implemented         |
| if-without-else                       |       | 2        |                 |              |          |
| for-let-const                         |       | 2        |                 |              | Error in one title (empty array)         |
| for-let-const-if                      |       | 2        |                 |              |          |
| for-let-i                             |       | 1        |                 |              |          |
| for-return-array                      |       | 2        |                 |              | Error in titles         |
| one-line-and-return                   |       | 1        |                 |              |          |
| return.service                        |       | 1        |                 |              |          |
| return-with-param                     |       | 1        |                 |              |          |
| TOTAL                                 |       | 78 (88 %)| 3 (3 %)         | 8 (9 %)      |          |
| |       |          |                 |              |          |
| |       |          |                 |              |          |
| |       |          |                 |              |          |
| |       |          |                 |              |          |
| |       |          |                 |              |          |
| |       |          |                 |              |          |
| |       |          |                 |              |          |
| |       |          |                 |              |          |
