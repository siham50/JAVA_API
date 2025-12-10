# API Java — Gestion Multi-SGBD 

## Objectif
Développer une API Java unifiée pour interagir avec plusieurs SGBD (MySQL, PostgreSQL, SQL Server, Oracle), en masquant
les différences entre les systèmes et en simplifiant l’accès aux bases de données.

## Présentation:
Ce projet est une API Java modulaire permettant la connexion et l'exécution de requêtes SQL sur différents systèmes de gestion 
de bases de données (MySQL, PostgreSQL, Oracle, SQL Server). L'API fournit une interface unifiée DatabaseManager et des implémentations
spécifiques pour chaque SGBD, tout en gérant la configuration via un fichier de propriétés et en générant
un .jar exécutable. Des tests automatisés assurent la fiabilité du comportement de l’API sur chaque système cible.

## Structure du projet
src/
├── main/
│   ├── java/
│   │   └── ma/ensa/
│   │       ├── db/
│   │       │   ├── DatabaseManager.java
│   │       │   ├── MySQLManager.java
│   │       │   ├── OracleManager.java
│   │       │   ├── PostgreSQLManager.java
│   │       │   ├── SQLServerManager.java
│   │       ├── util/
│   │       │   └── DBConfigLoader.java
│   │       └── Main.java
│   └── resources/
│       └── db.properties
├── test/
│   ├── java/ma/ensa/db/
│   │   ├── ManagerTest.java
│   │   └── UtilisateurTest.java
│   └── resources/
│       └── utilisateurs.csv

## Fonctionnalités
- Interface `DatabaseManager` générique pour abstraction de la couche SGBD.
- Implémentations concrètes pour :
    - `MySQLManager`
    - `PostgreSQLManager`
    - `SQLServerManager`
    - `OracleManager`
- Configuration dynamique via `DBConfigLoader` + `db.properties`.
- Gestion robuste des erreurs avec messages clairs.
- Exécution de requêtes `SELECT`, `INSERT`, `UPDATE`, `DELETE`.
- Gestion des résultats des requêtes sous forme de List<Map<String, Object>>, facilitant la réutilisation et l'affichage dynamique.
- Utilisation de Lombok (`@Getter`) pour simplifier la classe de config.
- **Tests automatisés via `ManagerTest` (CRUD)** et **tests paramétrés via `UtilisateurTest` (CSV)**.
- Création d’un `.jar` exécutable avec toutes les dépendances.



##  Configuration du fichier `db.properties`
Exemple :
db.type=mysql

mysql.url=jdbc:mysql://localhost:3306/mysqltest
mysql.username=root
mysql.password=

postgresql.url=jdbc:postgresql://localhost:5432/postgreTest
postgresql.username=postgres
postgresql.password=

sqlserver.url=jdbc:sqlserver://localhost:1433;databaseName=sqlserverTest;encrypt=false
sqlserver.username=sa
sqlserver.password=

oracle.url=jdbc:oracle:thin:@//localhost:1521/XEPDB1
oracle.username=baseTest
oracle.password=


### Génération du fichier .jar avec la commande
mvn clean compile assembly:single
Cela produit ce fichier:
target/api_tp9-1.0-SNAPSHOT-jar-with-dependencies.jar


### Exécution de l’API.jar (exécution de main dans la classe principale (ma.ensa.Main) définie dans le pom.xml)
java -jar target/api_tp9-1.0-SNAPSHOT-jar-with-dependencies.jar
---> execution que  des requêtes dans main et non pas celles dans ManagerTest et UtilisateursTest
Le fichier db.properties à l'intérieur du .jar doit obligatoirement contenir :db.type=mysql (ou postgresql, oracle, sqlserver)
Cette valeur est à modifier manuellement avant chaque test du .jar.


### Lancer les tests
1. Changer manuellement db.type dans src/main/resources/db.properties :
   mvn test

2. Utiliser une propriété système -DdbType= (si db.properties contient db.type=${dbType}) :
   mvn test -DdbType=mysql
   mvn test -DdbType=postgresql
   mvn test -DdbType=oracle
   mvn test -DdbType=sqlserver

3. Exécuter un seul test :
mvn test -Dtest=ManagerTest -DdbType=mysql
mvn test -Dtest=UtilisateurTest -DdbType=mysql

----> après lancement des tests on peut exécuter aussi main mais manuellement(les requêtes 
qui y sont inclus ne s'exécutent pas lors des tests, que celles dans ManagerTest et UtilisateursTest qui s'executent!)



### Tests inclus
-ManagerTest.java:
Test de la connexion
Requêtes INSERT, SELECT, UPDATE, DELETE
Vérification de la structure des résultats
Fermeture correcte des connexions

-UtilisateurTest.java
Test paramétré avec @CsvFileSource
Chargement de données depuis utilisateurs.csv
Insertion ligne par ligne dans la table employes

-utilisateurs.csv: 
prenom,age,salaire,sexe,email
Siham,30,40000,F,siham@example.com
User2,28,35000,M,user2@example.com
User3,32,42000,F,user3@example.com
...

### Dépendances Maven

Dépendance:	                              Version:	          Utilisation:
mysql-connector-java	                  8.0.33	          JDBC MySQL
postgresql	                              42.6.0	          JDBC PostgreSQL
ojdbc11	                                  21.5.0.0	          JDBC Oracle
mssql-jdbc	                              12.4.1.jre11	      JDBC SQL Server
lombok	                                  1.18.30	          Réduction du code (getters...)
junit-jupiter	                          5.9.2	              Tests unitaires

#### Installation manuelle du pilote Oracle
Le pilote Oracle (ojdbc11) n’est pas disponible directement sur Maven Central. Il doit être installé manuellement :

mvn install:install-file \
-Dfile=ojdbc11.jar \
-DgroupId=com.oracle.database.jdbc \
-DartifactId=ojdbc11 \
-Dversion=21.5.0.0 \
-Dpackaging=jar

Placer le fichier ojdbc11.jar (téléchargé depuis le site Oracle) dans un répertoire accessible.




### Remarques
Le .jar n’exécute que Main.java, pas les tests JUnit.

Les tests (ManagerTest, UtilisateurTest) ne s’exécutent que via Maven.

db.properties dans le .jar doit avoir un db.type explicite (mysql, oracle, etc.).

Pour les tests flexibles, on peut utiliser soit db.type=${dbType} ou un db.type explicite (mysql, oracle, etc.) 
dans le src/main/resources.




### Prérequis
Avant d'exécuter ce projet, assurez-vous d'avoir :
-Java JDK 17 installé
-Apache Maven 3.6+ installé
-Accès à au moins un des SGBD suivants : 
MySQL (testé avec XAMPP ou MariaDB)
PostgreSQL 
SQL Server
Oracle Database Express Edition (XE)
-Compte utilisateur valide pour chaque base de données
-Drivers JDBC fonctionnels (inclus dans le projet sauf Oracle)
-Pilote Oracle installé manuellement (ojdbc11.jar) si Oracle est utilisé
-IDE Java (ex: IntelliJ IDEA, Eclipse) ou terminal avec Maven configuré
