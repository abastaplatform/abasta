# Documentació de l'API - APP d’Abasta Frontend

Aquest document descriu l'estructura i l'ús dels serveis d'API que utilitza l'aplicació frontend d'Abasta. Inclou informació sobre els endpoints, mètodes HTTP, autenticació i gestió d'errors.

## Estructura General

Les peticions a l'API es realitzen a través de la capa de serveis que es troba a `/src/services`. Aquesta capa encapsula la comunicació amb el backend, centralitzant la gestió dels tokens, headers i errors comuns.

### Principals serveis

- **authService.ts**
  - `login(credentials: LoginData): Promise<User>`
  - `register(data: RegisterData): Promise<User>`
  - `logout(): void`
  - `validateToken(token: string): Promise<boolean>`
  
- **userService.ts**
  - `getUserProfile(userId: string): Promise<User>`
  - `updateUserProfile(userId: string, data: Partial<User>): Promise<User>`
  - `deleteUser(userId: string): Promise<void>`

### Funcionament de les peticions

- Totes les peticions es fan amb la funció centralitzada `api.ts` que utilitza `fetch`.
- Aquesta funció afegeix automàticament el token d'autenticació, si existeix, als headers.
- Les respostes es validen i es tracten els errors d'autenticació, redirigint a login si cal.

### Headers utilitzats

- `Authorization: Bearer <token>` — per a autenticació.
- `Content-Type: application/json` — per a peticions que envien dades JSON.

### Gestió d'errors

- Errors 401 i 403: redirecció a la pàgina de login.
- Errors 500: mostra un missatge d'error genèric a l'usuari.
- Altres errors es manegen específicament segons el servei.
