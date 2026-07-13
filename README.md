# 🌤️ Meteala

**Meteala** est une mini-application météo hybride, dynamique et ultra-légère. Elle combine un backend robuste en Scala avec une interface frontend fluide générée dynamiquement via l'application native **Raccourcis (Shortcuts)** d'iOS.

L'interface utilise les principes du **Glassmorphism** et s'adapte en temps réel (couleurs d'arrière-plan) en fonction des conditions météorologiques actuelles.

---

## ✨ Fonctionnalités

* **🌡️ Données en temps réel :** Température actuelle, prévision à +1h, minimales et maximales du jour.
* **📍 Localisation dynamique :** Utilisation des coordonnées GPS de l'iPhone pour récupérer la météo exacte.
* **🎨 Design adaptatif (Glassmorphism) :** L'arrière-plan de l'interface change de couleur dynamiquement grâce à un gradient CSS calculé par le serveur selon le code météo (soleil, pluie, orage, etc.).
* **⚡ Interface sans friction :** Affichage instantané via le moteur WebKit natif d'iOS (fonction "Coup d'œil" / Quick Look), sans nécessiter l'installation d'une application tierce.

---

## 🏗️ Architecture

Le projet est divisé en deux parties distinctes qui communiquent via HTTP :

### 1. Backend (Scala)
Un serveur HTTP léger développé en Scala qui fait office de proxy et de formateur de données.
* **Stack :** `http4s` (Ember Server), `sttp` (requêtes HTTP), `circe` (parsing JSON).
* **Source de données :** API Open-Meteo (Météo-France).
* **Rôle :** Intercepte la latitude/longitude, requête l'API météo, isole les données de l'heure courante, génère le gradient CSS correspondant à la condition, et renvoie un JSON épuré.

### 2. Frontend (iOS Shortcuts + HTML/CSS)
Un script visuel exécuté sur l'iPhone.
* **Stack :** Raccourcis iOS, HTML5, CSS3.
* **Rôle :** Récupère la position GPS locale, interroge le serveur Scala, injecte les variables du JSON retourné dans un template HTML, et affiche le rendu final.

---

## 🚀 Installation & Configuration

### Prérequis
* [SBT](https://www.scala-sbt.org/) installé sur la machine hébergeant le serveur.
* Un iPhone avec l'application native **Raccourcis**.
* Le serveur et l'iPhone doivent être sur le **même réseau local** (Wi-Fi ou Partage de connexion).

### Étape 1 : Lancer le serveur Scala
1. Clonez ce dépôt :
   ```bash
   git clone [https://github.com/VOTRE_NOM/meteala.git](https://github.com/VOTRE_NOM/meteala.git)
   cd meteala
