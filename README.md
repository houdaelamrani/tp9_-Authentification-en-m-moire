# 🛡️ TP Spring Security : Authentification en Mémoire (In-Memory Authentication)

> **Projet :** Portail Académique & Pédagogique Sécurisé  
> **Institution :** Faculté des Sciences et Techniques (FSTG)  
> **Technologies :** Java 17+, Spring Boot 3.3.x, Spring Security 6, Thymeleaf, Bootstrap 5, Maven  

---

## 📋 Description du Projet

Ce TP illustre la mise en œuvre des concepts fondamentaux de la sécurité applicative avec le framework **Spring Security 6** sous **Spring Boot 3** :

1. **Interception des requêtes HTTP** via une chaîne de filtres (`SecurityFilterChain`).
2. **Authentification en mémoire (`InMemoryUserDetailsManager`)** sans base de données externe pour le prototypage rapide.
3. **Chiffrement fort des identifiants** avec l'algorithme `BCryptPasswordEncoder`.
4. **Contrôle d'accès basé sur les rôles (RBAC - Role-Based Access Control)**.
5. **Personnalisation des vues** : Formulaire de connexion sur mesure (`/login`), page d'interdiction d'accès (`/access-denied` - Erreur HTTP 403), et intégration dynamique Thymeleaf / Bootstrap 5.

---

## 👥 Comptes de Test & Droits d'Accès

| Utilisateur | Mot de passe en clair | Rôles associés | Routes autorisées |
| :--- | :--- | :--- | :--- |
| **`etudiant`** | `etudiant123` | `ROLE_ETUDIANT` | `/etudiant/**`, `/` |
| **`professeur`** | `prof123` | `ROLE_ENSEIGNANT`, `ROLE_ETUDIANT` | `/enseignant/**`, `/etudiant/**`, `/` |
| **`admin`** | `admin123` | `ROLE_ADMIN`, `ROLE_ENSEIGNANT`, `ROLE_ETUDIANT` | **Toutes les routes** (`/admin/**`, `/enseignant/**`, `/etudiant/**`, `/`) |

---

## 🏗️ Structure du Projet

```text
spring-security-demo/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/ma/fstg/security/
    │   │   ├── SpringSecurityDemoApplication.java
    │   │   ├── config/
    │   │   │   └── SecurityConfig.java          # Configuration Spring Security 6 & BCrypt
    │   │   └── web/
    │   │       └── HomeController.java          # Contrôleur Spring MVC
    │   └── resources/
    │       ├── application.properties
    │       ├── static/css/style.css
    │       └── templates/
    │           ├── index.html                   # Accueil public & menu dynamique
    │           ├── login.html                   # Formulaire de connexion personnalisé
    │           ├── access-denied.html           # Page d'erreur 403 personnalisée
    │           ├── etudiant/dashboard.html      # Tableau de bord Étudiant
    │           ├── enseignant/dashboard.html    # Tableau de bord Enseignant
    │           └── admin/dashboard.html         # Console d'Administration
    └── test/
        └── java/ma/fstg/security/
            └── SpringSecurityDemoApplicationTests.java
```

---

## 🚀 Lancement de l'Application

### Prérequis
- **Java JDK 17 ou supérieur** installé.
- **Apache Maven 3.8+**.

### Démarrage
À la racine du projet, exécutez la commande :

```bash
mvn spring-boot:run
```

L'application démarre sur le port **8080** :
👉 **[http://localhost:8080/](http://localhost:8080/)**

---

## 🧪 Scénarios de Validation

1. **Accès Public** : Rendez-vous sur `http://localhost:8080/`. La page d'accueil s'affiche sans authentification préalable (`.permitAll()`).
2. **Accès Restreint** : Cliquez sur le bouton *Accéder* de l'Espace Étudiant. Spring Security intercepte la requête et redirige vers la page personnalisée `/login`.
3. **Connexion Étudiant** : Saisissez `etudiant` / `etudiant123`. Vous accédez avec succès à `/etudiant/dashboard`.
4. **Test de Violation de Rôle (403)** : Tout en étant connecté comme `etudiant`, tentez d'ouvrir manuellement l'URL `http://localhost:8080/admin/dashboard`. Vous êtes automatiquement redirigé vers la page conviviale `/access-denied` (Code HTTP 403).
5. **Connexion Administrateur** : Déconnectez-vous, puis connectez-vous avec `admin` / `admin123`. Vous avez un accès complet aux 3 espaces (Étudiant, Enseignant, Administrateur).

---

## 🔒 Principes Clés Implémentés

- **BCrypt** : Les mots de passe ne sont jamais stockés en clair en mémoire. Ils sont hachés avec un sel aléatoire.
- **Principe du Moindre Privilège** : Toute route non explicitement déclarée publique nécessite une authentification (`anyRequest().authenticated()`).
- **Protection CSRF** : Activée par défaut par Spring Security avec injection automatique du token dans les formulaires Thymeleaf via `th:action`.
