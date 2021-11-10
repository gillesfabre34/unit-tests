# Airbus RETEX Back

# Architecture : 
Selon les spécificités du projet, une architecture en couche a été choisie. Car l’application comporte principalement des CRUD.
Voici la liste des modules :

- retex-controller : module d’exposition des end-points 
- retex-business : module métier
- retex-impl : modules d’implementation
- retex-model : module des models de la base de données











































# Stratégie de test :
- tests unitaires sur la couche business
- tests d’intégration sur une base de donnée H2

# Gitflow:
Pour le nommage des branches : fetaure/<n°jira>_<libelle>
Pour les merges request : ne pas hésiter a decouper ses merges request en plusieurs cohérentes afin de limiter les relecture de code en nombre de fichiers a relire. Par exemple : faire une MR pour le cote base de données et les repository, une MR pour le business et les endpoints.

# Relecture de code : 
les relectures de code seront collectives de préférence afin que de partager les bonnes pratiques et les bonnes astuces de tous, et le lead dev validera en fin les MR pour merge.

# Sonar :
Chaque merge request lancera un job jenkins pour sonar. L’objectif est 80 % de couverture de code par les tests, un niveau de fiabilité au moins à B. 
Le code doit être commenté intelligemment sur les points complexes, ou ayant nécessité un choix technique particulier,
La javadoc est indispensable.

# Authentification : 
Afin de facilité les vérifications des droits utilisateurs, langages et autres , il a été décidé de mettre en place un cache des utilisateurs. 

# Traçabilité :

2 stratégies sont en discussion : 
-event sourcing (avec Axon) : avantages stockage plus léger, possibilité d’asychrone / inconvenients : effort de montée en compétence et mise en place a chaque service

-Hibernate Envers / JPA :  avantages : transparent pour les dev , bon outils de requêtage, facile a mettre en place / inconvénients : toutes les tables sont doublées et le volumes de données et plus conséquent

# TMC : 
Des campagnes de tmc sont prévues avec Gatling sur des environnements dédiés.

# Transaction :
la stratégie de la coupe AOP au niveau business a été retenue afin que l’intégralité des actions métier soient dans la transaction en cas de rollback.

# Ségrégation des données :
Chaque donnée doit être porteuse de son entité (pays) ,afin de permettre un filtrage de données. En effet seul les personnels de la France peuvent avoir accès a toutes les données sans restrictions. Les autres utilisateurs sont rattachés a une entité(pays) et ne voient que les données de leur entité (pays).
Chaque accès aux données doit donc intégrer ce filtre,


# Multi langue :
L’application est multi-langue, il faut donc prévoir une structure de donnée permettant de gérer toutes les traductions, sachant que les langues seront ajoutées au fil de l’eau. Au départ seules l’anglais et le français sont prévues. Il faut que le système puissent donc gérer l’ajout de langue facilement ; cf conception de Base de données.

Pour les fichier properties, il faut donc prévoir une fichier de traduction pour chaque langue supportée.
Il est prévue de stocker une langue par défaut sur l’utilisateur, et dans un second temps de pouvoir sélection la langue dans l’IHM de l’application.

# Conception Base de données :
La base de données est MariaDB. Liquibase est utilisé dans la back pour la gestion des scripts de base de données.
La modélisation reste classique sauf pour certain cas nécessitant une modélisation plus souple : Damage, Gamme, Inspection. 
La modélisation choisie pour ces 3 entités est celle : entity-attributes-values. Les avantages de cette modélisation est le fait de pouvoir ajouter des attributs facilement, le multi-langue est parfaitement intégré, la ségrégation de données aussi.


# swagger
http://serverName:port/swagger-ui.html#/

par ex : http://localhost:8080/swagger-ui.html#/

