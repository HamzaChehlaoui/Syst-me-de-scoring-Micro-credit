# 🏦 Système de Scoring Crédit - Microfinance Maroc

<div align="center">

![Java](https://img.shields.io/badge/Java-8+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JDBC](https://img.shields.io/badge/JDBC-Database-blue?style=for-the-badge&logo=oracle&logoColor=white)
![Status](https://img.shields.io/badge/Status-In%20Development-yellow?style=for-the-badge)

**Système automatisé d'évaluation du risque crédit pour le secteur de la microfinance marocaine**

[Fonctionnalités](#-fonctionnalités) • [Architecture](#-architecture) • [Installation](#-installation) • [Utilisation](#-utilisation) • [Documentation](#-documentation)

</div>

---

## 📋 Table des Matières

- [Contexte du Projet](#-contexte-du-projet)
- [Objectifs](#-objectifs)
- [Fonctionnalités](#-fonctionnalités)
- [Architecture](#-architecture)
- [Technologies Utilisées](#-technologies-utilisées)
- [Modèle de Données](#-modèle-de-données)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Utilisation](#-utilisation)
- [Algorithme de Scoring](#-algorithme-de-scoring)
- [Modules](#-modules)
- [Design Patterns](#-design-patterns)
- [Contribution](#-contribution)
- [Auteur](#-auteur)

---

## 🎯 Contexte du Projet

Le secteur de la microfinance marocaine fait face à des défis majeurs dans l'évaluation du risque crédit :

- ❌ **Processus manuels** lents et inefficaces
- ❌ **Subjectivité des décisions** manque d'objectivité
- ❌ **Exclusion de profils solvables** par manque d'outils adaptés
- ❌ **Absence de traçabilité** et d'historisation

### 💡 La Solution

Un système de scoring automatisé développé en **Java pur** qui combine :

- ✅ Algorithmes de scoring intelligents basés sur 5 composants métier
- ✅ Moteur de décision automatique et objectif
- ✅ Historisation complète pour audit et conformité
- ✅ Réduction des risques tout en améliorant l'accès au financement

---

## 🎯 Objectifs

```
┌─────────────────────────────────────────────────────────────┐
│  🎯  Automatiser l'évaluation du risque crédit             │
│  📊  Objectiver les décisions d'octroi                     │
│  🔍  Tracer et auditer toutes les opérations               │
│  💰  Optimiser la capacité d'emprunt                       │
│  📈  Améliorer le taux d'approbation de profils solvables  │
└─────────────────────────────────────────────────────────────┘
```

---

## ⚡ Fonctionnalités

### 📌 MODULE 1 : Gestion des Clients

- ➕ **Créer** un nouveau client (Employé ou Professionnel)
- ✏️ **Modifier** les informations d'un client
- 👁️ **Consulter** le profil complet d'un client
- 🗑️ **Supprimer** un client
- 📋 **Lister** tous les clients avec pagination

### 📊 MODULE 2 : Calcul de Score

#### Composants du Scoring (100 points)

| Composant | Poids | Description |
|-----------|-------|-------------|
| 🏢 **Stabilité Professionnelle** | 30% | Type de contrat, ancienneté, secteur |
| 💰 **Capacité Financière** | 30% | Revenus, ratio d'endettement, épargne |
| 📜 **Historique de Paiement** | 25% | Incidents, régularité, impayés |
| 🤝 **Relation Client** | 10% | Ancienneté, fidélité, produits détenus |
| 🏠 **Patrimoine** | 5% | Investissements, placements, biens |

#### Capacité d'Emprunt

```java
Nouveau Client (Score ≥ 70) : Montant max = 4x salaire mensuel
Client Existant (60-80)      : Montant max = 7x salaire mensuel  
Client Existant (> 80)       : Montant max = 10x salaire mensuel
```

### 📅 MODULE 3 : Gestion de l'Historique de Paiement

- 📆 **Génération automatique** des échéances après validation du crédit
- 🏷️ **Classification intelligente** des paiements :
    - ✅ **Payé à temps** : 0 jour de retard
    - ⚠️ **En retard** : 5-30 jours (Pénalité : -5 points)
    - ❌ **Impayé** : 31+ jours (Pénalité : -15 points)
- 🎁 **Système de bonus** : +10 points pour historique sans incident
- 📊 **Mise à jour dynamique** du score selon l'historique

### 🤖 MODULE 4 : Moteur de Décision Automatique

```
Score ≥ 80        →  ✅ ACCORD IMMÉDIAT
Score 60-79       →  📋 ÉTUDE MANUELLE  
Score < 60        →  ❌ REFUS AUTOMATIQUE
```

### 📈 MODULE 5 : Analytics & Recherches Avancées

#### 🔍 Recherches Personnalisées

1. **Clients Éligibles Crédit Immobilier**
    - Âge : 25-50 ans
    - Revenus : > 4000 DH/mois
    - Type emploi : CDI uniquement
    - Score : > 70
    - Situation familiale : Marié

2. **Clients à Risque (Top 10)**
    - Score < 60
    - Incidents récents (< 6 mois)
    - Triés par score décroissant

3. **Campagne Marketing Crédit Consommation**
    - Score : 65-85
    - Revenus : 4000-8000 DH
    - Âge : 28-45 ans
    - Pas de crédit en cours

#### 📊 Statistiques & Répartitions

- **Tri multi-critères** : Score, Revenus, Ancienneté
- **Répartition par type d'emploi** avec moyennes et taux d'approbation
- **Tableaux de bord** analytiques

---

## 🏗️ Architecture

```
📦 credit-scoring-system
├── 📂 src
│   ├── 📂 ui
│   │   ├── 📄 AnalytucsView.java
│   │   └── 📄 ClientView.java
│   │   ├── 📄 CreaditView.java
│   │   └── 📄 DemoView.java
│   │   ├── 📄 MainApp.java
│   │   └── 📄 MenuView.java
│   │   ├── 📄 PaymentView.java
│   │
│   ├── 📂 service (Couche Métier)
│   │   ├── 📄 ClientService.java
│   │   ├── 📄 ScoringService.java
│   │   ├── 📄 CreditService.java
│   │   ├── 📄 EcheanceService.java
│   │   └── 📄 AnalyticsService.java
│   │
│   ├── 📂 repository (Couche Données)
│   │   ├── 📄 AuditRepository.java
│   │   ├── 📄 ClientRepository.java
│   │   ├── 📄 CreditRepository.java
│   │   ├── 📄 EcheanceRepository.java
│   │   └── 📄 IncidentRepository.java
│   │
│   ├── 📂 model (Entités)
│   │   ├── 📄 Personne.java (abstract)
│   │   ├── 📄 AuditRecord.java
│   │   ├── 📄 Employe.java
│   │   ├── 📄 Professionnel.java
│   │   ├── 📄 Credit.java
│   │   ├── 📄 Echeance.java
│   │   └── 📄 Incident.java
│   │
│   ├── 📂 enums
│   │   ├── 📄 TypeContrat.java
│   │   ├── 📄 Secteur.java
│   │   ├── 📄 PaymentStatus.java
│   │   ├── 📄 DecisionType.java
│   │   ├── 📄 IncidentType.java
│   │
│   ├── 📂 util
│   │   ├── 📄 DatabaseConnection.java (Singleton)
│   │   ├── 📄 ValidationUtil.java
│   │   └── 📄 DateUtil.java
│   │
│
├── 📂 resources
│   ├── 📄 database.sql
│
│
├── 📄 README.md

```

---

## 🛠️ Technologies Utilisées

### Core Technologies

| Technologie        | Version | Utilisation |
|--------------------|---------|-------------|
| ☕ **Java**         | 8+      | Langage principal |
| 🗄️ **JDBC**       | 4.2+    | Connexion base de données |
| 🐘 **MySql**       | 14+     | Base de données |
| 📅 **Java Time API** | Built-in | Gestion des dates |

### Concepts Java Avancés

```java
✅ Streams API         : Traitement de données en flux
✅ Collections         : List, Set, Map
✅ HashMap             : Stockage clé-valeur performant
✅ Optional            : Gestion élégante des valeurs nulles
✅ Enums               : Types énumérés pour la sécurité
✅ Lambda Expressions  : Programmation fonctionnelle
✅ Method References   : Syntaxe concise
```

---

## 📊 Modèle de Données

### 🧑 Hiérarchie des Classes

```
         Personne (abstract)
              |
    +---------+---------+
    |                   |
Employe          Professionnel
```


## 🚀 Installation

### Prérequis

- ☕ **JDK 8 ** ou supérieur
- 🐘 **MySql** 
- 💻 **IDE** recommandé : IntelliJ IDEA

### Étapes d'Installation

#### 1️⃣ Cloner le Projet

```bash
git clone https:https://github.com/HamzaChehlaoui/Syst-me-de-scoring-Micro-credit.git
```



## 🧮 Algorithme de Scoring

### Composants Détaillés

#### 1️⃣ Stabilité Professionnelle (30 points)

```java
Type de Contrat:
- CDI Public           : 30 points
- CDI Grande Entreprise: 25 points
- CDI PME             : 20 points
- CDD                 : 10 points
- Freelance           : 5 points

Ancienneté:
- > 5 ans  : Bonus +5
- 2-5 ans  : Bonus +3
- < 2 ans  : Bonus +0
```

#### 2️⃣ Capacité Financière (30 points)

```java
Revenu Mensuel:
- > 10000 DH : 30 points
- 7000-10000 : 25 points
- 4000-7000  : 20 points
- < 4000     : 10 points

Ratio Endettement:
- < 30%  : Bonus +5
- 30-40% : Bonus +0
- > 40%  : Malus -5

Épargne/Placements:
- Présence : Bonus +5
```

#### 3️⃣ Historique de Paiement (25 points)

```java
Sans Incident       : 25 points + Bonus 10
Incidents Mineurs   : 15 points
Incidents Multiples : 5 points
Impayés Non Réglés  : 0 points
```

#### 4️⃣ Relation Client (10 points)

```java
Ancienneté Relation:
- > 5 ans  : 10 points
- 3-5 ans  : 7 points
- 1-3 ans  : 5 points
- < 1 an   : 2 points
```

#### 5️⃣ Patrimoine (5 points)

```java
Investissement > 50000 : 5 points
Investissement > 20000 : 3 points
Investissement présent : 1 point
```

---

## 🎨 Design Patterns

### 🔹 Singleton Pattern

```java
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    
    private DatabaseConnection() {
        // Initialisation connexion
    }
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
}
```

<div align="center">

### ⭐ Si ce projet vous aide, n'hésitez pas à lui donner une étoile !

**Made with ❤️ by Hamza Chehlaoui • © 2025**

</div>