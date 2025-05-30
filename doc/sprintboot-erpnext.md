Pour connecter une application **Spring Boot** à **ERPNext**, tu as plusieurs options, selon ce que tu veux faire : consommer l'API REST d'ERPNext, intégrer un module personnalisé, ou interagir avec la base de données.

Voici l’approche la plus courante et recommandée : **utiliser l’API REST de ERPNext**.

---

## 🔗 Connexion via API REST (méthode recommandée)

ERPNext expose une API REST standard, que tu peux consommer depuis Spring Boot avec `RestTemplate`, `WebClient` (Reactor), ou une bibliothèque comme `Feign`.

### 1. 🔑 Authentification à l'API ERPNext

Tu as deux options pour l'authentification :

- **Token API Key**  
  Tu dois générer une API key et secret dans ERPNext (dans ton profil utilisateur).
  ```http
  Authorization: token <api_key>:<api_secret>
  ```

- **Session Cookie (login)**  
  Faire une requête POST à `/api/method/login` avec les identifiants pour obtenir une session.

---

### 2. Exemple avec `RestTemplate` (API Key)

#### Étape 1 : Créer un service Spring Boot

```java
@Service
public class ErpNextService {

    private final RestTemplate restTemplate;

    public ErpNextService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public String getItems() {
        String url = "http://localhost:8000/api/resource/Item"; // URL vers ERPNext
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token your_api_key:your_api_secret");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
            url, HttpMethod.GET, entity, String.class
        );

        return response.getBody();
    }
}
```

#### Étape 2 : Configurer un bean `RestTemplate` (optionnel)

```java
@Configuration
public class RestConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
```

---

### 3. Exemple avec WebClient (réactif)

```java
@Bean
public WebClient webClient() {
    return WebClient.builder()
            .baseUrl("http://localhost:8000")
            .defaultHeader("Authorization", "token your_api_key:your_api_secret")
            .build();
}
```

---

### 4. Tester la connexion

Depuis ton contrôleur :

```java
@RestController
public class TestController {

    private final ErpNextService erpService;

    public TestController(ErpNextService erpService) {
        this.erpService = erpService;
    }

    @GetMapping("/test-erp")
    public String testErp() {
        return erpService.getItems();
    }
}
```

---

## 🧪 Tester avec Postman

Avant de coder, teste les requêtes vers :

- `http://localhost:8000/api/method/login` (POST)
- `http://localhost:8000/api/resource/Item` (GET)

---

## ✅ Résumé

| Étape | Détail |
|------|--------|
| 1 | Génère une API Key dans ERPNext |
| 2 | Crée un appel HTTP dans Spring Boot (`RestTemplate` ou `WebClient`) |
| 3 | Ajoute les en-têtes d’authentification |
| 4 | Teste l’appel et traite la réponse JSON |

---


-----------------------------------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------------------------------

Voici un guide **professionnel** étape par étape pour créer un **login ERPNext depuis Spring Boot**, en utilisant l’API REST de ERPNext avec authentification **via session** (utilisateur/mot de passe).

---

## 🎯 Objectif
Créer une application Spring Boot qui :
- Envoie une requête POST à `/api/method/login` d’ERPNext.
- Stocke la **session** (cookie) pour réutilisation dans les requêtes suivantes.

---

## 🧱 Étapes générales

1. Créer le projet Spring Boot
2. Ajouter les dépendances
3. Créer les classes de modèle (DTOs)
4. Créer un service pour se connecter à ERPNext
5. Gérer la session (cookie)
6. Créer un contrôleur pour tester la connexion

---

## ⚙️ Étape 1 – Créer un projet Spring Boot

Tu peux utiliser [https://start.spring.io](https://start.spring.io) avec :

- **Spring Web**
- **Spring Boot DevTools**
- (optionnel) **Lombok**

---

## 🧩 Étape 2 – Ajouter les dépendances Maven

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Pour JSON si tu veux utiliser DTOs -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
</dependencies>
```

---

## 📦 Étape 3 – Créer les classes de modèle

### `LoginRequest.java`
```java
public class LoginRequest {
    private String usr;
    private String pwd;

    // getters/setters
}
```

### `LoginResponse.java`
```java
public class LoginResponse {
    private String full_name;
    private String message;
    private String home_page;

    // getters/setters
}
```

---

## 🔧 Étape 4 – Créer un service pour le login

### `ErpNextLoginService.java`
```java
@Service
public class ErpNextLoginService {

    private final RestTemplate restTemplate;

    public ErpNextLoginService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public ResponseEntity<String> login(String username, String password) {
        String url = "http://localhost:8000/api/method/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("usr", username);
        body.put("pwd", password);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        return restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    }
}
```

---

## 🧠 Étape 5 – Gérer la session (cookie)

Tu peux extraire le cookie `Set-Cookie` de la réponse pour les futures requêtes.

### `SessionManager.java`
```java
@Component
public class SessionManager {
    private String sessionCookie;

    public String getSessionCookie() {
        return sessionCookie;
    }

    public void setSessionCookie(String cookieHeader) {
        this.sessionCookie = cookieHeader;
    }
}
```

Modifie le service :

```java
public class ErpNextLoginService {

    private final RestTemplate restTemplate;
    private final SessionManager sessionManager;

    public ErpNextLoginService(RestTemplateBuilder builder, SessionManager sessionManager) {
        this.restTemplate = builder.build();
        this.sessionManager = sessionManager;
    }

    public String login(String username, String password) {
        String url = "http://localhost:8000/api/method/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("usr", username);
        body.put("pwd", password);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        // Récupérer le cookie de session
        List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (cookies != null && !cookies.isEmpty()) {
            sessionManager.setSessionCookie(cookies.get(0)); // JSESSIONID ou sid
        }

        return response.getBody();
    }
}
```

---

## 🌐 Étape 6 – Créer un contrôleur de test

### `ErpController.java`
```java
@RestController
@RequestMapping("/erp")
public class ErpController {

    private final ErpNextLoginService loginService;

    public ErpController(ErpNextLoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String response = loginService.login(request.getUsr(), request.getPwd());
        return ResponseEntity.ok(response);
    }
}
```

---

## 📦 Exemple JSON pour test (Postman ou frontend)

```json
POST http://localhost:8080/erp/login
Content-Type: application/json

{
  "usr": "administrator@example.com",
  "pwd": "admin"
}
```

---

## ✅ Résultat

- Si le login est correct, tu obtiens un JSON avec `"message": "Logged In"`.
- Le cookie `sid` est stocké dans `SessionManager`.
- Tu peux l’utiliser pour faire des appels authentifiés (avec l'en-tête `Cookie`).

---


-----------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------
Pour obtenir un **fournisseur spécifique par son nom (ID)** dans ERPNext, tu peux utiliser l’API REST avec ce format :

```
GET /api/resource/Supplier/<supplier_name>
```

En ERPNext, la "clé primaire" des documents comme Supplier est généralement le champ `name`, qui correspond souvent à un identifiant unique ou au nom du fournisseur (ex. `"Fournisseur ABC"` ou `"SUP-0001"`).

---

## 🎯 Objectif

Créer un service Spring Boot pour :

- Envoyer une requête `GET /api/resource/Supplier/{name}`
- Récupérer les détails complets d’un fournisseur

---

## ✅ Étape 1 – DTO de réponse (exemple simplifié)

### `SupplierDetailResponse.java`

```java
package com.example.erp.dto;

public class SupplierDetailResponse {
    private String name;
    private String supplier_type;
    private String supplier_group;
    private String default_currency;

    // Ajoute d'autres champs selon les besoins

    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSupplier_type() { return supplier_type; }
    public void setSupplier_type(String supplier_type) { this.supplier_type = supplier_type; }

    public String getSupplier_group() { return supplier_group; }
    public void setSupplier_group(String supplier_group) { this.supplier_group = supplier_group; }

    public String getDefault_currency() { return default_currency; }
    public void setDefault_currency(String default_currency) { this.default_currency = default_currency; }
}
```

---

## 🔧 Étape 2 – Service pour récupérer un fournisseur

### `SupplierService.java`

Ajoute cette méthode :

```java
public SupplierDetailResponse getSupplierByName(String name) {
    String url = "http://localhost:8000/api/resource/Supplier/" + UriUtils.encodePath(name, StandardCharsets.UTF_8);

    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.COOKIE, sessionManager.getSessionCookie());

    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ResponseEntity<SupplierDetailResponse> response = restTemplate.exchange(
        url,
        HttpMethod.GET,
        entity,
        SupplierDetailResponse.class
    );

    return response.getBody();
}
```

> ⚠️ N’oublie pas d’utiliser `UriUtils.encodePath()` pour gérer les noms contenant des espaces ou des caractères spéciaux.

---

## 🌐 Étape 3 – Contrôleur

### `ErpController.java`

```java
@GetMapping("/suppliers/{name}")
public ResponseEntity<?> getSupplierByName(@PathVariable String name) {
    return ResponseEntity.ok(supplierService.getSupplierByName(name));
}
```

---

## 🧪 Exemple de requête HTTP

```
GET http://localhost:8080/erp/suppliers/SUP-0001
```

ou si le nom est textuel :

```
GET http://localhost:8080/erp/suppliers/Fournisseur%20ABC
```

---

## ✅ Résultat attendu (exemple JSON)

```json
{
  "name": "Fournisseur ABC",
  "supplier_type": "Company",
  "supplier_group": "Local",
  "default_currency": "EUR"
}
```

---












Parfait ! Maintenant que tu sais te connecter à **ERPNext via Spring Boot**, voyons comment créer un **service pour récupérer la liste des fournisseurs (Supplier)** en utilisant la **session** obtenue précédemment.

---

## 🎯 Objectif

Créer un service Spring Boot qui :

1. Utilise le cookie de session ERPNext (authentification).
2. Fait une requête GET vers l’endpoint `/api/resource/Supplier`.
3. Récupère et retourne la liste des fournisseurs.

---

## 🧱 Structure des fichiers

```
src/
 └── main/
     └── java/
         └── com.example.erp/
             ├── controller/
             │    └── ErpController.java
             ├── dto/
             │    └── SupplierResponse.java
             ├── service/
             │    ├── ErpNextLoginService.java
             │    └── SupplierService.java
             └── session/
                  └── SessionManager.java
```

---

## ✅ Étape 1 – DTO de réponse (simplifié)

### `SupplierResponse.java`

```java
package com.example.erp.dto;

import java.util.List;

public class SupplierResponse {
    private List<Supplier> data;

    public List<Supplier> getData() {
        return data;
    }

    public void setData(List<Supplier> data) {
        this.data = data;
    }

    public static class Supplier {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
```

---

## 🔧 Étape 2 – Service pour appeler l’API ERPNext

### `SupplierService.java`

```java
package com.example.erp.service;

import com.example.erp.dto.SupplierResponse;
import com.example.erp.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SupplierService {

    private final RestTemplate restTemplate;
    private final SessionManager sessionManager;

    @Autowired
    public SupplierService(RestTemplate restTemplate, SessionManager sessionManager) {
        this.restTemplate = restTemplate;
        this.sessionManager = sessionManager;
    }

    public SupplierResponse getSuppliers() {
        String url = "http://localhost:8000/api/resource/Supplier";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, sessionManager.getSessionCookie()); // Utilise le cookie de session

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<SupplierResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                SupplierResponse.class
        );

        return response.getBody();
    }
}
```

---

## 🌐 Étape 3 – Ajout d’un endpoint REST

### `ErpController.java` (ajoute cette méthode)

```java
@Autowired
private SupplierService supplierService;

@GetMapping("/suppliers")
public ResponseEntity<?> getSuppliers() {
    return ResponseEntity.ok(supplierService.getSuppliers());
}
```

---

## ⚙️ Étape 4 – Configuration du `RestTemplate` (si ce n’est pas fait)

```java
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
```

---

## 🧪 Test

Appelle :

```
GET http://localhost:8080/erp/suppliers
```

📝 *Assure-toi de t’être connecté au préalable via `/erp/login` pour que le cookie de session soit stocké.*

---

## ✅ Résultat attendu (exemple JSON)

```json
{
  "data": [
    { "name": "Supplier 1" },
    { "name": "Supplier 2" },
    ...
  ]
}
```

---

Souhaites-tu que je t’aide à filtrer les fournisseurs (ex. par nom ou actif) ou à paginer les résultats ?