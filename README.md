# Prérequis

- **Java version** : 17 (OpenJDK 17 recommandé)  
- **Maven version** : 3.8+  
- **Node.js version** : 16.x ou supérieur  
- **npm version** : 8.x ou supérieur  
- **Git** : pour cloner le projet  
- **Base de données** : MySQL 8.0 (ou MariaDB compatible)  
- **IDE recommandé** : IntelliJ IDEA / Eclipse / VSCode

# Clonage du projet

- Ouvrez un terminal et clonez le dépôt Git en tappant la commande ```git clone https://github.com/Lukas-Thai/Projet-03```

# Mise en place du Frontend

- Ouvrez un terminal et veillez être à la racine du projet
- Placez-vous dans le fichier avec la commande ```cd frontend```
- Télécharger les dépendances du frontend avec la commande ```npm ci```

# Mise en place de la base de données

- Créer une nouvelle base de données dans votre SGBD (Système de Gestion de Base de Données)
- Importez la base de données en exécutant le script situé à cet emplacement : Frontend\ressources\sql\script.sql

# Mise en place du Backend 

- Importez un projet Java à partir du fichier `Backend` avec votre IDE Java
- Ouvrez le fichier application.properties situé à cet endroit Backend\src\main\resources\application.properties
- Remplacez PORT par le port pour se connecter à votre base de données
- Remplacez DATABASE_NAME par le nom de la base de données qui correspond
- Remplacez USERNAME et PASSWORD par vos identifiants qui servent à se connecter à la base de données
- Remplacez JWTKEY par une chaine de caractère d'au moins 88 caractères
- Importez les dépendances en exécutant la commande ```mvn clean install``` dans le fichier Backend. (Si vous êtes sur Eclipse : Click droit sur le projet dans le Package Explorer > Maven > Update Projects > OK)

# Lancement du Frontend

- À partir du fichier Frontend, ouvrez l'invite de commande et tappez la commande ```npm start```
- Depuis votre navigateur, allez sur l'url générée par la commande

# Lancement du Backend

- Exécutez le projet Java depuis votre IDE avec la classe Projet3Application située le package server

# URL du Swagger

localhost:8080/swagger-ui/index.html
