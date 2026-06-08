# DBS-Praktikum_dbprak19
Datenbanksysteme Praktikum, Universität Leipzig, SoSe26. 

Um dieses Projekt ausführen zu können, muss man folgende Schritte ausführen: 

1. PostgreSQL installieren. (Standard Port 5432)

2. Datenbank erstellen mit: psql -U postgres -c "CREATE DATABASE mediastore;" 
                            psql -U postgres -d mediastore -f Aufgabe1a_Schema.sql
                            
3. Config-properties anpassen:   db.url =j dbc:postgresql://localhost:5432/mediastore
                                 db.user = postgres
                                 db.password = dein_passwort
4. Main Loader ausführen.

5. Ergebnisse sollten folgende sein:
## Ergebnisse

```
=== Import Zusammenfassung ===
NULL-Fehler:              2
Constraint-Fehler:        2105
Validierungsfehler:       3
-----------------------------
Gesamt abgelehnt: 2110
Gesamt geladen:   9675
```
