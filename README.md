# TP1 SOA : API Etudiants (Jakarta EE 10)

Service REST en mémoire : `GET /api/etudiants`, `GET /api/etudiants/{id}`, `POST /api/etudiants`.

## Prérequis
- JDK 17 (compilation)
- Maven 3.8.6 ou plus
- Payara Server 7 Full Platform (Community). Payara 7 exige Java 21 minimum
  pour s'exécuter : le lancer avec JDK 21 ou plus (variable `AS_JAVA`).
- curl (cmd ou Git Bash)

## Construction
    mvn clean package
Résultat attendu : `BUILD SUCCESS` et `target/soa-tp1.war`.

## Déploiement (Windows)
    set AS_JAVA=C:\Program Files\Java\jdk-23
    C:\tools\payara7\bin\asadmin.bat start-domain domain1
    C:\tools\payara7\bin\asadmin.bat deploy --force=true --contextroot soa-tp1 CHEMIN\target\soa-tp1.war

## URL de test
http://localhost:8080/soa-tp1/api/etudiants

## Tests
Voir `docs/tests.md` (T1 à T5). Contrat : `docs/openapi.yaml`.

## Structure
    src/main/java/mr/univ/soa/{api,model,service}
    docs/openapi.yaml, docs/tests.md