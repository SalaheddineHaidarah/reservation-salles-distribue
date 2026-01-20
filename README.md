# Système Distribué de Réservation de Salles de Réunion

## Description du projet
Ce projet est un système distribué permettant la gestion et la réservation de salles de réunion. Il propose plusieurs interfaces d'accès (REST, SOAP, RMI, Sockets TCP) et offre des notifications en temps réel lors des réservations ou annulations. Les données sont stockées dans une base MySQL.

## Technologies utilisées
- **REST (JAX-RS/Jersey)** : API web pour clients modernes
- **SOAP (JAX-WS)** : Web service compatible entreprises
- **RMI** : Appels distants Java natifs
- **Sockets TCP** : Notifications temps réel et clients légers
- **Swing** : Interfaces graphiques admin et utilisateur
- **MySQL** : Stockage des salles, réservations, utilisateurs

## Lancer le projet

### 1. Préparer la base MySQL
Créez la base et les tables :
```sql
CREATE DATABASE reservation_salles;
USE reservation_salles;

CREATE TABLE salles (
  id INT AUTO_INCREMENT PRIMARY KEY,
  type VARCHAR(50),
  capacite INT,
  equipements VARCHAR(255)
);

CREATE TABLE reservations (
  id INT AUTO_INCREMENT PRIMARY KEY,
  utilisateur VARCHAR(100),
  salle_id INT,
  date_reservation DATE,
  heure_debut TIME,
  heure_fin TIME
);

CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) UNIQUE,
  password VARCHAR(255),
  role VARCHAR(50)
);
```

### 2. Lancer les serveurs
Dans des terminaux séparés, à la racine du projet :

- **Serveur REST** :
  ```
mvn -q exec:java -Dexec.mainClass="ma.ensias.salles.ws.rest.RestServer"
  ```
- **Serveur SOAP** :
  ```
mvn -q exec:java -Dexec.mainClass="ma.ensias.salles.ws.soap.SoapServer"
  ```
- **Serveur RMI** :
  ```
mvn -q exec:java -Dexec.mainClass="ma.ensias.salles.rmi.RmiServer"
  ```
- **Serveur de notifications (TCP)** :
  ```
mvn -q exec:java -Dexec.mainClass="ma.ensias.salles.socket.rt.NotificationServerMain"
  ```

## Exemples de requêtes REST
- **Vérifier la disponibilité d'une salle** :
  ```
GET http://localhost:8081/api/salles/disponible?id=1&date=2026-01-20&debut=10:00&fin=11:00
  ```
- **Créer une réservation** :
  ```
POST http://localhost:8081/api/reservations
Content-Type: application/json
{
  "utilisateur": "alice",
  "salleId": 1,
  "date": "2026-01-20",
  "heureDebut": "10:00",
  "heureFin": "11:00"
}
  ```
- **Lister les réservations d'un utilisateur** :
  ```
GET http://localhost:8081/api/reservations/user/alice
  ```

## Notifications temps réel
Lorsqu'une réservation ou une annulation est effectuée, un message est envoyé à tous les clients connectés via Sockets TCP (port 9100). Exemple de message :
```
NEW_RESERVATION salle=1 date=2026-01-20 10:00-11:00 user=alice
```

## Démo (5 étapes pour la présentation)
1. **Connexion d'un client notification** : Lancer `NotificationClient` pour écouter les notifications.
2. **Création d'une réservation via l'interface utilisateur ou REST** : Montrer la réservation et la notification reçue en temps réel.
3. **Consultation des réservations** : Utiliser l'interface ou une requête REST pour afficher les réservations d'un utilisateur.
4. **Ajout/Suppression de salle via l'interface admin** : Montrer l'effet sur la liste des salles.
5. **Annulation d'une réservation** : Montrer la notification d'annulation reçue par les clients connectés.

---
Projet réalisé à l'ENSIAS — Architecture des Systèmes Distribués.
