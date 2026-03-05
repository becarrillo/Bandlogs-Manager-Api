## Hello! This is Bandlogs-Manager-Api

# Description
Personal project. This is Java based server side musical application for Bandlogs Manager client-side app which is an API that performs management of musical bands
and guests who as well can create other bands by themselves, but with more specific functionalities like events agenda (calendar with info) and songs' chord transposition.
Finished at october 18th / 2025. ® Brando Elí Carrillo Pérez 

# Scope
-> Users signup
-> Users authentication (JWT implementation)
-> Creation and retrieving of events by date (not unique constraint by date) 🕟
-> Harmony info edition musical-wise
-> Automatic push notifications of user events on the platform via WhatsApp

# Technologies
Spring Boot 3.4.2 version - JDK 17 version
JWT dependency version: 0.12.1
database: Oracle PLSQL

# Endpoints
-> Signup:  POST "/api/v1/usuarios/registro" HTTP/1.1
-> Login:   POST "/api/v1/auth/login" HTTP/1.1      fields: nickname, password
-> Get user role: GET "/api/v1/auth/usuario-rol" HTTP/1.1    header: "Authorization"
-> Logout: DELETE "/api/v1/auth/logout"  HTTP/1.1
-> Get user by its id: GET "/api/v1/usuarios/{userId}" HTTP/1.1  path variable: int userId
-> Get user by its nickname: GET "/api/v1/usuarios" HTTP/1.1     params:  {'nombre-de-usuario'}  (String)
-> List all users: GET "/api/v1/usuarios" HTTP/1.1
-> Get users by nickname containing: GET "/api/v1/usuarios" HTTP/1.1    params:  {'nombre-de-usuario'}  (String)
-> Delete user: DELETE "/api/v1/usuarios/{userId}/eliminar" HTTP/1.1
-> Delete user: PUT "/api/v1/usuarios/{userId}/modificar" HTTP/1.1
-> Get band by its id: GET "/api/v1/bandas/{bandId}" HTTP/1.1     path variable:  short bandId
-> Get band by its name: GET "/api/v1/bandas" HTTP/1.1    params:  {'nombre'}  (String)
-> List all bands:  GET "/api/v1/bandas" HTTP/1.1
-> List bands by its director: GET "/api/v1/bandas/por-director" HTTP/1.1    params:  {'nombre-de-usuario'}  (String)
-> List bands by one member: GET "/api/v1/bandas/por-miembro" HTTP/1.1    params:  {'nombre-de-usuario'}  (String)
-> Save/Create band: POST "/api/v1/bandas/agregar" HTTP/1.1
-> Patch one event to band: PATCH "/api/v1/bandas/{bandId}/eventos/agregar"  HTTP/1.1       path variable:  short bandId
-> Patch one member to band: PATCH "/api/v1/bandas/{bandId}/usuarios/agregar"  HTTP/1.1       path variable:  short bandId
-> Update one band: PUT "/api/v1/bandas/{bandId}/modificar" HTTP/1.1        path variable:  short bandId
-> Delete one band: DELETE "/api/v1/bandas/{bandId}/eliminar" HTTP/1.1       path variable:  short bandId
-> Get event by its id: GET "/api/v1/eventos/{eventId}" HTTP/1.1        path variable: String eventId
-> List all events:  GET "/api/v1/eventos" HTTP/1.1
-> List events by date: GET "/api/v1/eventos" HTTP/1.1         params: {'fecha'}  (String)
-> Update one event: PUT "/api/v1/eventos/{eventId}/modificar" HTTP/1.1       path variable:  String eventId
-> Delete one event: DELETE "/api/v1/eventos/{eventId}/eliminar" HTTP/1.1       path variable:  String eventId
-> Get song by its id: GET "/api/v1/repertorio/{songId}" HTTP/1.1     path variable:  int songId
-> List all songs: GET "/api/v1/repertorio" HTTP/1.1
-> To transport song harmony: GET "/api/v1/repertorio/{songId}/transportar" HTTP/1.1     path variable:  int songId
