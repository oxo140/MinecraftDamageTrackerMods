# Damage Tracker Mod (Beta)

> **Minecraft 1.21.1 · Fabric**

---

## Français

### Description du Mod (Beta)

Ce mod, actuellement en phase Beta, permet d'afficher **en temps réel** quel joueur subit des dégâts dans un environnement multijoueur.  
Idéal pour les défis, les survies hardcore ou les événements communautaires !

> ⚠️ **Installation** : Le mod doit être installé à la fois sur le **Serveur** et sur le **Client** pour fonctionner correctement.

### Fonctionnalité actuelle

Lorsqu'un joueur prend des dégâts, un message apparaît immédiatement dans **l'action bar** de tous les joueurs connectés :

```
⚔ <NomDuJoueur> prend des dégâts ! (<montant> ❤)
```

### 🚀 Prochaines mises à jour (Coming Soon)

| Fonctionnalité | Statut |
|---|---|
| ❤️ Synchronisation de la vie | À venir |
| 🍗 Faim partagée | À venir |
| 🦘 Sauts partagés | À venir |
| ✨ Effets de potions partagés | À venir |
| ⚙️ Menu de configuration | À venir |

---

## English

### Mod Description (Beta)

This mod is currently in Beta. It displays a **real-time** notification whenever a player takes damage in a multiplayer world.

> ⚠️ **Installation**: This mod is required on both **Server-side** and **Client-side** to function properly.

### Current Feature

When a player takes damage, a message is immediately shown in the **action bar** for every connected player:

```
⚔ <PlayerName> prend des dégâts ! (<amount> ❤)
```

*(English translation coming in a future update.)*

### 🚀 Upcoming Features (Roadmap)

| Feature | Status |
|---|---|
| ❤️ Health Sync | Coming Soon |
| 🍗 Shared Hunger | Coming Soon |
| 🦘 Shared Jumping | Coming Soon |
| ✨ Shared Potion Effects | Coming Soon |
| ⚙️ Configuration Menu | Coming Soon |

---

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for Minecraft **1.21.1**.
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) and place it in your `mods/` folder.
3. Place `damagetracker-<version>.jar` in the `mods/` folder of **both** the server and every client.
4. Launch Minecraft / start the server.

## Building from source

```bash
./gradlew build
```

The compiled JAR will be in `build/libs/`.

## Requirements

- Java 21+
- Minecraft 1.21.1
- Fabric Loader ≥ 0.15.11
- Fabric API

