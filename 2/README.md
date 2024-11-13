# Assignment 2024-11-11

> Implementare il comportamento molti a molti nella classe e nel Dao del piatto.
> Implementare un progetto con Dao per la gestione di una relazione molti a molti, in particolare il dao e il relativo
> proxy andrà implementato per entrambe le entità.

## Soluzione

I DAO sono implementati come classi statiche prive di stato. Ho scelto di fondere l'interfaccia con l'implementazione,
poiché esiste una sola implementazione per ciascun DAO.

`DataSource` tenta di stabilire una connessione con PostgresQL, nome utente `postgres`, password vuota e database
`dao_exercise`. Lo schema relazionale è impostato da `DataSource.initDatabase()`.

→ `Main` [it.unical.Main](src/main/java/it/unical/Main.java)
