# 🌤️ Meteala

**Meteala** est une mini-application météo hybride, dynamique et ultra-légère. Elle combine un backend robuste en Scala hébergé dans le Cloud (Fly.io) avec une interface frontend fluide générée dynamiquement via l'application native **Raccourcis (Shortcuts)** d'iOS.

L'interface utilise les principes du **Glassmorphism** et s'adapte en temps réel (couleurs d'arrière-plan) en fonction des conditions météorologiques actuelles.

---

## ✨ Fonctionnalités

* **🌡️ Données en temps réel :** Température actuelle, prévision à +1h, minimales et maximales du jour.
* **📍 Localisation dynamique :** Utilisation des coordonnées GPS de l'iPhone pour récupérer la météo exacte.
* **🎨 Design adaptatif (Glassmorphism) :** L'arrière-plan de l'interface change de couleur dynamiquement grâce à un gradient CSS calculé par le serveur selon le code météo (soleil, pluie, orage, etc.).
* **⚡ Interface sans friction :** Affichage instantané via le moteur WebKit natif d'iOS (fonction "Coup d'œil" / Quick Look), sans nécessiter l'installation d'une application tierce.
* **☁️ Disponible 24/7 :** Backend conteneurisé (Docker) et déployé sur le web.

---

## 🏗️ Architecture

Le projet est divisé en deux parties distinctes communiquant via une API REST publique :

### 1. Backend (Scala / Docker / Fly.io)
Un serveur HTTP léger développé en Scala qui fait office de proxy et de formateur de données.
* **Stack :** `http4s` (Ember Server), `sttp` (requêtes HTTP), `circe` (parsing JSON).
* **Source de données :** API Open-Meteo.
* **Rôle :** Intercepte la latitude/longitude, requête l'API météo, isole les données utiles, génère le gradient CSS correspondant à la condition, et renvoie un JSON épuré.
* **Déploiement :** Conteneurisé via un `Dockerfile` et hébergé sur Fly.io.

### 2. Frontend (iOS Shortcuts + HTML/CSS)
Un script visuel exécuté nativement sur l'iPhone.
* **Stack :** Raccourcis iOS, HTML5, CSS3.
* **Rôle :** Récupère la position GPS locale, interroge l'API publique Scala, injecte les variables du JSON retourné dans un template HTML embarquant l'image en Base64 pour contourner les restrictions Sandbox, et affiche le rendu final.

---

## 🚀 Installation & Configuration

### Tourner l'application localement
* [SBT](https://www.scala-sbt.org/) installé sur la machine hébergeant le serveur.
* Un iPhone avec l'application native **Raccourcis**.
* Le serveur et l'iPhone doivent être sur le **même réseau local** (Wi-Fi ou Partage de connexion).
* Clonez ce dépôt :
   ```bash
   git clone https://github.com/AntoineLeriston/meteala.git
   cd meteala

### Déploiement Cloud (Fly.io)
Cette méthode permet d'utiliser l'application partout dans le monde via la 4G/5G.
Si le serveur que j'ai mis en place sur Fly.io est encore fonctionnel, je peux vous partager le raccourci et vous pouvez vous servir directement de l'application.

-> [Raccourci iOS](https://www.icloud.com/shortcuts/1653b35e0bb54dd4a1f95d35394d439e)
