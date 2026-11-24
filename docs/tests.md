# Tests TP1 : API Etudiants

URL de base : `http://localhost:8080/soa-tp1/api/etudiants`

## T1 : GET collection avant création
Requête :
    curl -i http://localhost:8080/soa-tp1/api/etudiants
- Attendu : 200, tableau vide
- Obtenu : 200 OK, `[]`
- Verdict : réussi

## T2 : POST étudiant valide
Requête :
    curl -i -X POST http://localhost:8080/soa-tp1/api/etudiants -H "Content-Type: application/json" -d "{\"nom\":\"Aminata\",\"email\":\"aminata@example.org\"}"
- Attendu : 201, Location, id
- Obtenu : 201 Created, `Location: http://localhost:8080/soa-tp1/api/etudiants/1`, `{"email":"aminata@example.org","id":1,"nom":"Aminata"}`
- Verdict : réussi

## T3 : GET étudiant créé
Requête :
    curl -i http://localhost:8080/soa-tp1/api/etudiants/1
- Attendu : 200, JSON correct
- Obtenu : 200 OK, `{"email":"aminata@example.org","id":1,"nom":"Aminata"}`
- Verdict : réussi

## T4 : GET identifiant absent
Requête :
    curl -i http://localhost:8080/soa-tp1/api/etudiants/999
- Attendu : 404, erreur JSON
- Obtenu : 404 Not Found, `{"erreur":"ETUDIANT_INTROUVABLE","message":"Identifiant inconnu"}`
- Verdict : réussi

## T5 : POST nom vide / email invalide
Requête :
    curl -i -X POST http://localhost:8080/soa-tp1/api/etudiants -H "Content-Type: application/json" -d "{\"nom\":\"\",\"email\":\"incorrect\"}"
- Attendu : 400, erreur JSON
- Obtenu : 400 Bad Request, `{"erreur":"DONNEES_INVALIDES","message":"Nom ou email invalide"}`
- Verdict : réussi