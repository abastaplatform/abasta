# DOCUMENTACIÓ TÈCNICA - PROJECTE ABASTA

**Versió:** 1.0.0
**Data:** 9 de desembre de 2025
**Autors:** Equip de desenvolupament Abasta

---

## ÍNDEX

1. [Descripció del Producte](#1-descripció-del-producte)
2. [Tecnologies i Llenguatges de Programació](#2-tecnologies-i-llenguatges-de-programació)
3. [Disseny de les Pantalles (UI)](#3-disseny-de-les-pantalles-ui)
4. [Disseny de la Base de Dades](#4-disseny-de-la-base-de-dades)
5. [Documentació del Codi Font](#5-documentació-del-codi-font)
6. [Instal·lació i Configuració del Sistema](#6-instal·lació-i-configuració-del-sistema)

---

## 1. DESCRIPCIÓ DEL PRODUCTE

### 1.1. Visió General

**Abasta** és una plataforma web de gestió empresarial dissenyada per facilitar la gestió de comandes, productes, proveïdors i usuaris. L'aplicació permet a les empreses centralitzar i optimitzar les seves operacions de compra i gestió de proveïdors mitjançant una interfície intuïtiva i funcional.

### 1.2. Funcionalitats Bàsiques

#### Gestió d'Autenticació i Usuaris

- **Registre d'empreses** amb verificació per correu electrònic
- **Sistema d'autenticació** amb JWT (JSON Web Tokens)
- **Recuperació de contrasenya** mitjançant enllaços temporals
- **Gestió d'usuaris** amb rols (ADMIN, USER)
- **Multi-tenancy**: Cada empresa gestiona els seus propis recursos

#### Gestió de Proveïdors

- **Creació i edició** de proveïdors amb informació de contacte completa
- **Cerca avançada** de proveïdors amb múltiples filtres
- **Activació/desactivació** de proveïdors
- **Associació automàtica** a l'empresa autenticada

#### Gestió de Productes

- **Catàleg de productes** organitzat per proveïdor
- **Informació detallada**: nom, categoria, preu, volum, unitat
- **Càrrega d'imatges** de productes
- **Cerca bàsica i avançada** amb filtres per categoria, proveïdor, preu
- **Desactivació** de productes (soft delete)

#### Gestió de Comandes

- **Creació de comandes** amb múltiples productes (items)
- **Seguiment d'estat**: PENDING, SENT, CONFIRMED, REJECTED, COMPLETED, CANCELLED, DELETED
- **Enviament automàtic** de comandes als proveïdors per EMAIL o WhatsApp
- **Filtratge avançat** per estat, proveïdor, dates
- **Càlcul automàtic** de totals i subtotals
- **Visualització responsive** (taules en desktop, targetes en mòbil)

#### Dashboard i Reports

- **Panell de control** amb estadístiques clau:
  - Total de comandes del mes
  - Despesa total del mes
  - Comandes pendents
- **Últimes 5 comandes** del mes actual
- **Generació de reports** globals per període amb exportació a PDF
- **Visualització de dades** amb gràfics i taules

#### Funcionalitats Addicionals

- **Paginació** en tots els llistats
- **Ordenació** per diferents camps
- **Modals de confirmació** per accions crítiques
- **Alertes i notificacions** d'èxit/error
- **Responsive design** adaptat a tots els dispositius
- **Breadcrumbs** per a navegació intuïtiva
- **Autocomplete** per a selecció de proveïdors

---

## 2. TECNOLOGIES I LLENGUATGES DE PROGRAMACIÓ

### 2.1. Frontend (abasta-app)

#### Llenguatges

- **TypeScript 5.9.3**: Superset de JavaScript amb tipat estàtic
- **HTML5 i CSS3**: Estructura i estils web
- **SCSS (Sass)**: Preprocessador CSS per a estils avançats

#### Framework i Llibreries Principals

- **React 18.3.1**: Llibreria per a construcció d'interfícies d'usuari
- **React Router DOM 7.9.4**: Gestió de rutes i navegació
- **React Hook Form 7.65.0**: Gestió de formularis amb validació

#### UI Framework

- **Bootstrap 5.3.8**: Framework CSS responsive
- **React Bootstrap 2.10.10**: Components Bootstrap per a React
- **Bootstrap Icons 1.13.1**: Biblioteca d'icones

#### Build Tool i Bundler

- **Vite 7.1.7**: Build tool de nova generació, extremadament ràpid
- **@vitejs/plugin-react-swc**: Plugin de Vite amb compilador SWC (Speedy Web Compiler)

#### Qualitat de Codi

- **ESLint 9.38.0**: Linter per a JavaScript/TypeScript
- **Prettier 3.6.2**: Formatador de codi automàtic
- **TypeScript ESLint**: Integració ESLint amb TypeScript

#### Altres Dependències

- **@fontsource/poppins**: Font tipogràfica Poppins
- **react-router-hash-link**: Navegació amb hash links

### 2.2. Backend (abasta-api)

#### Llenguatge

- **Java 21**: Última versió LTS (Long Term Support) de Java

#### Framework Principal

- **Spring Boot 3.5.6**: Framework per a aplicacions Java empresarials
  - **Spring Boot Starter Web**: Creació d'aplicacions web i REST APIs
  - **Spring Boot Starter Data JPA**: Persistència de dades amb JPA/Hibernate
  - **Spring Boot Starter Security**: Seguretat i autenticació
  - **Spring Boot Starter Validation**: Validació de dades d'entrada
  - **Spring Boot Starter Mail**: Enviament de correus electrònics
  - **Spring Boot DevTools**: Eines de desenvolupament

#### ORM i Base de Dades

- **Hibernate**: Implementació JPA per a mapeig objecte-relacional
- **MySQL Connector/J**: Driver JDBC per a MySQL

#### Seguretat

- **Spring Security**: Framework de seguretat
- **JJWT 0.11.5**: Llibreria per a generació i validació de JWT
  - jjwt-api, jjwt-impl, jjwt-jackson

#### Documentació API

- **SpringDoc OpenAPI 2.8.13**: Generació automàtica de documentació Swagger/OpenAPI

#### Generació de Documents

- **OpenPDF 1.3.30**: Generació de documents PDF

#### Utilitats

- **Lombok**: Generació automàtica de codi (getters, setters, constructors, etc.)

#### Testing

- **Spring Boot Starter Test**: Testing amb JUnit, Mockito, etc.
- **Spring Security Test**: Testing de configuracions de seguretat
- **H2 Database**: Base de dades en memòria per a tests

#### Build Tool

- **Maven 3**: Gestió de dependències i construcció del projecte

### 2.3. Base de Dades (abasta-db)

#### Sistema Gestor

- **MySQL 8.0+**: Sistema de gestió de bases de dades relacional
- **Charset**: UTF8MB4 amb collation utf8mb4_unicode_ci
- **Motor**: InnoDB (transaccions ACID, integritat referencial)

### 2.4. Eines d'Implementació del Projecte

#### Control de Versions

- **Git**: Sistema de control de versions distribuït
- **GitHub**: Plataforma d'allotjament de codi

#### Entorns de Desenvolupament (IDE)

- **IntelliJ IDEA** (recomanat per al backend Java)
- **Visual Studio Code** (recomanat per al frontend)

#### Servidor d'Aplicacions

- **Apache Tomcat** (inclòs amb Spring Boot)
- **Port predeterminat**: 8084

#### Servidor Web (Producció)

- **Apache HTTP Server** amb proxy revers

#### Gestió de Paquets

- **npm**: Gestor de paquets per a Node.js (frontend)
- **Maven**: Gestor de dependències per a Java (backend)

#### Variables d'Entorn

- Configuració mitjançant fitxers `.env` (frontend)
- Variables del sistema operatiu (backend)

---

## 3. DISSENY DE LES PANTALLES (UI)

### 3.1. Paleta de Colors

L'aplicació utilitza una paleta de colors corporativa definida a `_variables.scss`:

```scss
$primary: #1c2792     // Blau corporatiu principal
$secondary: #8c00ff   // Violeta secundari
$ternary: #cc8300     // Taronja terciari
$success: #28a745     // Verd per a èxits
$info: #17a2b8        // Blau clar informatiu
$warning: #ffc107     // Groc per a advertències
$danger: #dc3545      // Vermell per a errors
$light: #f8f9fa       // Gris clar
$dark: #343a40        // Gris fosc
```

### 3.2. Tipografia

- **Font principal**: Poppins (via @fontsource/poppins)
- **Font secundària**: Inter
- **Fallback**: System fonts (Segoe UI, Roboto, Helvetica Neue, Arial, sans-serif)

### 3.3. Estructura de Pàgines

#### Pàgines Públiques (PublicLayout)

**Estructura**: Topbar + Contingut + Footer

1. **Home Page** (`/`)
   - **HeroSection**: Secció hero amb crida a l'acció
   - **BenefitsSection**: Beneficis de la plataforma amb targetes
   - **HowItWorksSection**: Com funciona amb targetes explicatives
   - **DoToSection**: Què pots fer amb Abasta
   - **WhatsAbastaSection**: Què és Abasta
   - **Carousel**: Testimonials en carrusel
   - **FinalCTA**: Crida final a l'acció

2. **Login** (`/login`)
   - Formulari d'autenticació amb email i contrasenya
   - Enllaç a registre i recuperació de contrasenya
   - Validació en temps real

3. **Register** (`/register`)
   - Formulari de registre d'empresa
   - Camps: Nom empresa, CIF, Email, Telèfon, Adreça, CP, Ciutat
   - Creació d'usuari administrador
   - Validació completa de camps

4. **Recover Password** (`/recover`)
   - Formulari per sol·licitar restabliment de contrasenya
   - Enviament d'enllaç temporal per email

5. **Reset Password** (`/reset-password`)
   - Formulari per establir nova contrasenya
   - Validació de token temporal

6. **Verify Email** (`/verify-email`)
   - Pàgina de verificació de correu electrònic
   - Confirmació automàtica amb token

7. **Pàgines legals**
   - Privacy (`/privacy`): Política de privacitat
   - Terms (`/terms`): Termes i condicions
   - Cookies (`/cookies`): Política de cookies
   - Accessibility (`/accessibility`): Declaració d'accessibilitat

#### Pàgines Privades (PrivateLayout)

**Estructura**: Leftbar (sidebar) + Contingut principal

1. **Dashboard** (`/dashboard`)
   - **Targetes d'estadístiques**:
     - Total comandes del mes
     - Despesa del mes (€)
     - Comandes pendents
   - **Taula de comandes recents** (5 últimes del mes)
   - **Vista responsive**: Taula en desktop, targetes en mòbil
   - **Botó d'acció**: Nova comanda

2. **Suppliers** (`/suppliers`)
   - **SupplierList**: Llistat de proveïdors amb cerca i filtres
   - **SupplierManager** (modes: create, edit, detail):
     - Formulari complet amb tots els camps
     - Validació amb react-hook-form
     - Camps: Nom, Persona contacte, Email, Telèfon, Adreça, Notes

3. **Products** (`/products`)
   - **ProductList**: Catàleg de productes amb cerca avançada
   - **ProductManager** (modes: create, edit, detail):
     - Formulari de producte
     - Selecció de proveïdor amb autocomplete
     - Càrrega d'imatge
     - Camps: Nom, Categoria, Descripció, Preu, Volum, Unitat

4. **Orders** (`/orders`)
   - **OrderList**: Llistat de comandes amb filtres avançats
     - Filtres: Estat, proveïdor, dates
     - Paginació
     - Ordenació per columnes
   - **OrderCreate**: Creació de comanda
     - Selecció de proveïdor
     - Afegir múltiples productes (items)
     - Càlcul automàtic de totals
   - **OrderManager** (modes: edit, detail):
     - Edició de comanda existent
     - Enviament al proveïdor (EMAIL/WhatsApp)
     - Canvi d'estat

5. **Reports** (`/reports`)
   - Estadístiques i informes (placeholder)

6. **Company** (`/company`)
   - **CompanyConfigForm**: Configuració de l'empresa
   - Actualització de dades corporatives

7. **Users** (`/users`)
   - **UserList**: Gestió d'usuaris de l'empresa
   - **UserManager** (modes: create, edit, detail):
     - Formulari d'usuari
     - Assignació de rol (ADMIN/USER)
     - Activació/desactivació

### 3.4. Components Reutilitzables

#### Components Comuns

1. **Topbar/Navbar**: Barra de navegació superior
   - Logo
   - Menú de navegació
   - Botó de login/registre

2. **Leftbar**: Menú lateral (sidebar) per a zona privada
   - Enllaços a totes les seccions privades
   - Icones amb Bootstrap Icons
   - Responsive (col·lapsable en mòbil)

3. **Footer**: Peu de pàgina
   - Enllaços legals
   - Informació de contacte
   - Copyright

4. **PageHeader**: Capçalera de pàgina
   - Títol de la pàgina
   - Breadcrumbs opcionals

5. **Breadcrumb**: Ruta de navegació
   - Indica la ubicació actual
   - Enllaços clicables

6. **Button**: Botó personalitzat
   - Variants: primary, secondary, danger, success
   - Loading state

7. **Alert**: Component d'alertes
   - Variants: success, danger, warning, info
   - Tancable

8. **Pagination**: Component de paginació
   - Navegació per pàgines
   - Informació de resultats

9. **FormCard**: Targeta per a formularis
   - Estil consistent
   - Capçalera i cos

10. **ConfirmModal**: Modal de confirmació
    - Confirmació d'accions crítiques
    - Personalitzable

11. **DeleteModal**: Modal d'eliminació
    - Confirmació específica per a eliminacions

12. **SupplierAutocomplete**: Autocomplete de proveïdors
    - Cerca en temps real
    - Selecció intuïtiva

#### Components de Pàgina d'Inici

- **BenefitCard**: Targeta de benefici
- **HowItWorksCard**: Targeta explicativa
- **DoToCard**: Targeta de funcionalitat
- **Carousel**: Carrusel de testimonials
- **CarouselCard**: Targeta de testimoni

### 3.5. Responsive Design

L'aplicació està completament adaptada a diferents mides de pantalla:

- **Desktop** (≥ 768px):
  - Taules per a llistats
  - Layout a múltiples columnes
  - Sidebar visible

- **Mòbil** (< 768px):
  - Targetes per a llistats
  - Layout a una columna
  - Sidebar col·lapsable
  - Botons i formularis adaptats

### 3.6. Sistemes d'Espaiats

```scss
$spacers: (
  0: 0,
  1: 4px,
  2: 8px,
  3: 16px,
  4: 24px,
  5: 32px,
  6: 48px,
  7: 80px
)
```

### 3.7. Border Radius

```scss
$border-radius-sm: 4px
$border-radius: 8px
$border-radius-lg: 12px
```

---

## 4. DISSENY DE LA BASE DE DADES

### 4.1. Informació General

- **Sistema Gestor**: MySQL 8.0+
- **Nom de la BD**: `abasta_db`
- **Charset**: `utf8mb4`
- **Collation**: `utf8mb4_unicode_ci`
- **Motor**: InnoDB
- **Estratègia JPA**: `spring.jpa.hibernate.ddl-auto=none` (esquema creat manualment)

### 4.2. Diagrama Entitat-Relació (ERD)

```Estructura
┌──────────────┐
│   COMPANIES  │
└──────┬───────┘
       │
       ├─────────────────┐
       │                 │
       │1               1│
       │                 │
   N┌──┴─────┐      ┌───┴─────┐
    │ USERS  │      │SUPPLIERS│
    └────────┘      └─┬───────┘
                      │1
                      │
                     N│
                  ┌───┴────┐
                  │PRODUCTS│
                  └─┬──────┘
                    │1
                    │
                   N│
              ┌─────┴──────┐
              │ORDER_ITEMS │
              └─────┬──────┘
                    │N
                    │
                   1│
              ┌─────┴──┐
              │ ORDERS │
              └────────┘
```

### 4.3. Taules de la Base de Dades

#### 4.3.1. COMPANIES

Emmagatzema informació de les empreses clients de la plataforma.

| Camp | Tipus | Restriccions | Descripció |
|------|-------|--------------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador intern |
| `uuid` | VARCHAR(255) | UNIQUE, NOT NULL | Identificador universal |
| `name` | VARCHAR(255) | NOT NULL | Nom de l'empresa |
| `tax_id` | VARCHAR(50) | UNIQUE, NOT NULL | NIF/CIF |
| `email` | VARCHAR(255) | | Email de contacte |
| `phone` | VARCHAR(50) | | Telèfon |
| `address` | TEXT | | Adreça física |
| `city` | VARCHAR(100) | | Ciutat |
| `postal_code` | VARCHAR(20) | | Codi postal |
| `status` | ENUM | DEFAULT 'PENDING' | ACTIVE, INACTIVE, PENDING |
| `created_at` | TIMESTAMP | NOT NULL | Data de creació |
| `updated_at` | TIMESTAMP | NOT NULL | Última modificació |

**Índexs**: `idx_status`, `idx_created_at`

#### 4.3.2. USERS

Usuaris associats a les empreses.

| Camp | Tipus | Restriccions | Descripció |
|------|-------|--------------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador intern |
| `uuid` | VARCHAR(255) | UNIQUE, NOT NULL | Identificador universal |
| `company_id` | BIGINT | FK, NOT NULL | Referència a COMPANIES |
| `email` | VARCHAR(255) | UNIQUE, NOT NULL | Email únic |
| `password` | VARCHAR(255) | NOT NULL | Hash BCrypt |
| `first_name` | VARCHAR(100) | NOT NULL | Nom |
| `last_name` | VARCHAR(100) | NOT NULL | Cognoms |
| `role` | ENUM | DEFAULT 'USER' | ADMIN, USER |
| `phone` | VARCHAR(50) | | Telèfon |
| `is_active` | BOOLEAN | DEFAULT TRUE | Compte actiu |
| `is_deleted` | BOOLEAN | DEFAULT FALSE | Compte eliminat (soft delete) |
| `email_verified` | BOOLEAN | DEFAULT FALSE | Email verificat |
| `email_verification_token` | VARCHAR(255) | | Token de verificació |
| `email_verification_expires` | TIMESTAMP | | Expiració token |
| `password_reset_token` | VARCHAR(255) | | Token reset |
| `password_reset_expires` | TIMESTAMP | | Expiració reset |
| `last_login` | TIMESTAMP | | Últim login |
| `created_at` | TIMESTAMP | NOT NULL | Data creació |
| `updated_at` | TIMESTAMP | NOT NULL | Última modificació |

**Índexs**: `idx_company_id`, `idx_email`, `idx_company_email`, `idx_role`, `idx_email_verification_token`, `idx_password_reset_token`

**Foreign Key**: `company_id` → `companies(id)` ON DELETE CASCADE

#### 4.3.3. SUPPLIERS

Proveïdors de les empreses.

| Camp | Tipus | Restriccions | Descripció |
|------|-------|--------------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador intern |
| `uuid` | VARCHAR(255) | UNIQUE, NOT NULL | Identificador universal |
| `company_id` | BIGINT | FK, NOT NULL | Referència a COMPANIES |
| `name` | VARCHAR(255) | NOT NULL | Nom del proveïdor |
| `contact_name` | VARCHAR(255) | | Persona de contacte |
| `email` | VARCHAR(255) | | Email |
| `phone` | VARCHAR(50) | | Telèfon |
| `address` | TEXT | | Adreça |
| `notes` | TEXT | | Observacions |
| `is_active` | BOOLEAN | DEFAULT TRUE | Actiu |
| `created_at` | TIMESTAMP | NOT NULL | Data creació |
| `updated_at` | TIMESTAMP | NOT NULL | Última modificació |

**Índexs**: `idx_company_id`, `idx_company_active`, `idx_name`

**Foreign Key**: `company_id` → `companies(id)`

#### 4.3.4. PRODUCTS

Catàleg de productes per proveïdor.

| Camp | Tipus | Restriccions | Descripció |
|------|-------|--------------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador intern |
| `uuid` | VARCHAR(255) | UNIQUE, NOT NULL | Identificador universal |
| `supplier_id` | BIGINT | FK, NOT NULL | Referència a SUPPLIERS |
| `category` | VARCHAR(255) | | Categoria |
| `name` | VARCHAR(255) | NOT NULL | Nom producte |
| `description` | TEXT | | Descripció |
| `price` | DECIMAL(10,2) | NOT NULL | Preu |
| `volume` | DECIMAL(10,2) | | Volum |
| `unit` | VARCHAR(50) | | Unitat (kg, L, unitats, etc.) |
| `image_url` | VARCHAR(500) | | URL imatge |
| `is_active` | BOOLEAN | DEFAULT TRUE | Actiu |
| `created_at` | TIMESTAMP | NOT NULL | Data creació |
| `updated_at` | TIMESTAMP | NOT NULL | Última modificació |

**Índexs**: `idx_supplier_id`

**Foreign Key**: `supplier_id` → `suppliers(id)`

#### 4.3.5. ORDERS

Comandes a proveïdors.

| Camp | Tipus | Restriccions | Descripció |
|------|-------|--------------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador intern |
| `uuid` | VARCHAR(255) | UNIQUE, NOT NULL | Identificador universal |
| `company_id` | BIGINT | FK, NOT NULL | Referència a COMPANIES |
| `supplier_id` | BIGINT | FK, NOT NULL | Referència a SUPPLIERS |
| `user_id` | BIGINT | FK, NOT NULL | Usuari que crea |
| `name` | VARCHAR(255) | NOT NULL | Nom comanda |
| `status` | ENUM | DEFAULT 'PENDING' | PENDING, SENT, CONFIRMED, REJECTED, COMPLETED, CANCELLED, DELETED |
| `total_amount` | DECIMAL(10,2) | DEFAULT 0 | Total comanda |
| `notes` | TEXT | | Observacions |
| `delivery_date` | DATE | | Data d'entrega |
| `notification_method` | ENUM | | EMAIL, WHATSAPP, BOTH |
| `created_at` | TIMESTAMP | NOT NULL | Data creació |
| `updated_at` | TIMESTAMP | NOT NULL | Última modificació |

**Índexs**: `idx_company_id`, `idx_supplier_id`, `idx_user_id`, `idx_status`, `idx_created_at`, `idx_company_status_date`, `idx_supplier_status`

**Foreign Keys**:

- `company_id` → `companies(id)`
- `supplier_id` → `suppliers(id)`
- `user_id` → `users(id)`

#### 4.3.6. ORDER_ITEMS

Línies de productes dins de les comandes.

| Camp | Tipus | Restriccions | Descripció |
|------|-------|--------------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador intern |
| `uuid` | VARCHAR(255) | UNIQUE, NOT NULL | Identificador universal |
| `order_id` | BIGINT | FK, NOT NULL | Referència a ORDERS |
| `product_id` | BIGINT | FK, NOT NULL | Referència a PRODUCTS |
| `quantity` | DECIMAL(10,2) | NOT NULL | Quantitat |
| `unit_price` | DECIMAL(10,2) | NOT NULL | Preu unitari |
| `subtotal` | DECIMAL(10,2) | NOT NULL | Subtotal (quantity × unit_price) |
| `notes` | TEXT | | Observacions |
| `created_at` | TIMESTAMP | NOT NULL | Data creació |

**Índexs**: `idx_order_id`, `idx_product_id`

**Foreign Keys**:

- `order_id` → `orders(id)`
- `product_id` → `products(id)`

### 4.4. Relacions entre Taules

| Origen | Destinació | Cardinalitat | Tipus | Cascade |
|--------|-----------|--------------|-------|---------|
| COMPANIES | USERS | 1:N | ManyToOne | DELETE CASCADE |
| COMPANIES | SUPPLIERS | 1:N | ManyToOne | - |
| COMPANIES | ORDERS | 1:N | ManyToOne | - |
| SUPPLIERS | PRODUCTS | 1:N | ManyToOne | - |
| USERS | ORDERS | 1:N | ManyToOne | - |
| ORDERS | ORDER_ITEMS | 1:N | OneToMany | CASCADE, orphanRemoval |
| PRODUCTS | ORDER_ITEMS | 1:N | ManyToOne | - |

### 4.5. Característiques Especials

#### Identificadors

- **ID numèric (BIGINT)**: Clau primària interna
- **UUID (VARCHAR)**: Identificador universal per a ús extern (API/frontend)
- Generació automàtica de UUID amb `@PrePersist` si no existeix

#### Timestamps d'Auditoria

- **created_at**: Assignat automàticament a la creació
- **updated_at**: Actualitzat automàticament a cada modificació

#### Soft Delete

- **USERS**: Camp `is_deleted` per marcar usuaris eliminats sense esborrar dades
- **ORDERS**: Estat `DELETED` per comandes cancel·lades
- **PRODUCTS**: Camp `is_active` per desactivar productes

#### Enumeracions

**CompanyStatus**:

- `ACTIVE`: Empresa activa
- `INACTIVE`: Empresa desactivada
- `PENDING`: Pendent de verificació

**UserRole**:

- `ADMIN`: Administrador amb tots els permisos
- `USER`: Usuari estàndard amb permisos limitats

**OrderStatus**:

- `PENDING`: Comanda creada (borrador)
- `SENT`: Comanda enviada al proveïdor
- `CONFIRMED`: Comanda confirmada
- `REJECTED`: Comanda rebutjada
- `COMPLETED`: Comanda completada
- `CANCELLED`: Comanda cancel·lada
- `DELETED`: Comanda eliminada

**NotificationMethod**:

- `EMAIL`: Enviament per correu
- `WHATSAPP`: Enviament per WhatsApp
- `BOTH`: Ambdós mètodes

---

## 5. DOCUMENTACIÓ DEL CODI FONT

### 5.1. Estructura del Projecte

```Estructura del Projecte
abasta/
├── abasta-api/              # Backend (Spring Boot)
├── abasta-app/              # Frontend (React + TypeScript)
└── abasta-db/               # Scripts de base de dades
```

### 5.2. Frontend (abasta-app)

#### 5.2.1. Estructura de Carpetes

```Estructura de Carpetes
abasta-app/
├── src/
│   ├── App.tsx                      # Entrada principal
│   ├── main.tsx                     # ReactDOM render
│   ├── components/                  # Components React
│   │   ├── auth/                   # Components d'autenticació
│   │   │   ├── LoginForm/
│   │   │   ├── RegisterForm/
│   │   │   ├── VerifyEmail/
│   │   │   ├── RecoverPasswordForm/
│   │   │   ├── ResetPasswordForm/
│   │   │   └── CompanyConfigForm/
│   │   ├── common/                 # Components reutilitzables
│   │   │   ├── Navbar/
│   │   │   ├── Leftbar/
│   │   │   ├── Footer/
│   │   │   ├── Button/
│   │   │   ├── Alert/
│   │   │   ├── Pagination/
│   │   │   ├── ConfirmModal/
│   │   │   ├── DeleteModal/
│   │   │   ├── Breadcrumb/
│   │   │   ├── PageHeader/
│   │   │   ├── SupplierAutocomplete/
│   │   │   ├── FormCard/
│   │   │   └── ScrollToTop/
│   │   ├── dashboard/              # Panell de control
│   │   │   └── Dashboard.tsx
│   │   ├── orders/                 # Gestió de comandes
│   │   │   ├── OrderList/
│   │   │   ├── OrderCreate/
│   │   │   ├── OrderManager/
│   │   │   └── SendOrderModal/
│   │   ├── products/               # Gestió de productes
│   │   │   ├── ProductList/
│   │   │   └── ProductManager/
│   │   ├── suppliers/              # Gestió de proveïdors
│   │   │   ├── SupplierList/
│   │   │   └── SupplierManager/
│   │   └── users/                  # Gestió d'usuaris
│   │       ├── UserList/
│   │       └── UserManager/
│   ├── pages/                      # Pàgines estàtiques
│   │   └── Home/
│   │       ├── HomePage.tsx
│   │       ├── Privacy/
│   │       ├── Terms/
│   │       ├── Cookies/
│   │       ├── Accessibility/
│   │       └── sections/
│   ├── layouts/                    # Layouts
│   │   ├── PublicLayout.tsx
│   │   └── PrivateLayout.tsx
│   ├── routes/                     # Configuració de rutes
│   │   └── AppRoutes.tsx
│   ├── services/                   # Serveis HTTP
│   │   ├── api.ts                 # Client HTTP base
│   │   ├── authService.ts
│   │   ├── productService.ts
│   │   ├── orderService.ts
│   │   ├── supplierService.ts
│   │   ├── userService.ts
│   │   ├── company.api.ts
│   │   └── dasboardService.ts
│   ├── context/                    # Context API
│   │   ├── AuthContext.ts
│   │   ├── AuthProvider.tsx
│   │   └── useAuth.ts
│   ├── hooks/                      # Custom hooks
│   │   ├── useLoginForm.ts
│   │   ├── useRegisterForm.ts
│   │   ├── useRecoverPasswordForm.ts
│   │   ├── useResetPasswordForm.ts
│   │   ├── useOrderService.ts
│   │   ├── useOrderCreate.ts
│   │   ├── useSendOrder.ts
│   │   ├── useProductForm.ts
│   │   ├── useSupplierForm.ts
│   │   ├── useUserForm.ts
│   │   ├── useCompanyConfigForm.ts
│   │   └── useDashboard.ts
│   ├── types/                      # Tipus TypeScript
│   │   ├── auth.types.ts
│   │   ├── user.types.ts
│   │   ├── company.types.ts
│   │   ├── order.types.ts
│   │   ├── product.types.ts
│   │   ├── supplier.types.ts
│   │   ├── dashboard.types.ts
│   │   └── common.types.ts
│   ├── utils/                      # Utilitats
│   │   ├── product.utils.ts
│   │   └── supplier.utils.ts
│   ├── config/                     # Configuració
│   │   └── env.ts
│   └── assets/                     # Recursos estàtics
│       ├── styles/
│       │   ├── main.scss
│       │   ├── _variables.scss
│       │   └── _forms.scss
│       └── images/
├── public/                         # Fitxers públics
├── .env.development               # Variables d'entorn (dev)
├── .env.production                # Variables d'entorn (prod)
├── .env.example                   # Exemple de variables
├── package.json                   # Dependències npm
├── tsconfig.json                  # Configuració TypeScript
├── vite.config.ts                 # Configuració Vite
├── .eslintrc.json                 # Configuració ESLint
└── .prettierrc                    # Configuració Prettier
```

#### 5.2.2. Patrons i Arquitectura

**Arquitectura de Components**:

- **Presentational Components**: Components de UI purs (common/)
- **Container Components**: Components amb lògica de negoci (orders/, products/, etc.)
- **Page Components**: Components de pàgina (pages/)

**Gestió d'Estat**:

- **Context API**: Autenticació global (AuthContext)
- **Custom Hooks**: Estat local i lògica reutilitzable
- **React Hook Form**: Gestió d'estat de formularis

**Comunicació amb Backend**:

- **ApiService**: Client HTTP centralitzat amb fetch API
- **Services**: Serveis específics per a cada recurs (authService, orderService, etc.)
- **Interceptors**: Injecció automàtica de token JWT, gestió d'errors 401

**Flux de Dades**:

```Flux de Dades
UI Component → Custom Hook → Service → ApiService → Backend API
                   ↓
              Local State
```

#### 5.2.3. Fitxers Clau

| Fitxer | Descripció |
|--------|-----------|
| `src/App.tsx` | Punt d'entrada, configura BrowserRouter i AuthProvider |
| `src/routes/AppRoutes.tsx` | Definició de totes les rutes públiques i privades |
| `src/context/AuthProvider.tsx` | Proveïdor de context d'autenticació |
| `src/services/api.ts` | Client HTTP base amb gestió d'errors i autenticació |
| `src/hooks/useOrderService.ts` | Hook per a gestió de comandes |
| `src/types/order.types.ts` | Interfícies TypeScript per a comandes |

### 5.3. Backend (abasta-api)

#### 5.3.1. Estructura de Carpetes

```Estructura de Carpetes
abasta-api/
├── src/
│   ├── main/
│   │   ├── java/cat/abasta_back_end/
│   │   │   ├── AbastaBackEndApplication.java    # Punt d'entrada
│   │   │   ├── config/                          # Configuració
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── SwaggerConfig.java
│   │   │   │   └── WebConfig.java
│   │   │   ├── controllers/                     # Controladors REST
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── CompanyController.java
│   │   │   │   ├── OrderController.java
│   │   │   │   ├── ProductController.java
│   │   │   │   ├── ReportController.java
│   │   │   │   ├── SupplierController.java
│   │   │   │   └── UserController.java
│   │   │   ├── dto/                            # Data Transfer Objects
│   │   │   │   ├── ApiResponseDTO.java
│   │   │   │   ├── LoginRequestDTO.java
│   │   │   │   ├── LoginResponseDTO.java
│   │   │   │   ├── UserRequestDTO.java
│   │   │   │   ├── UserResponseDTO.java
│   │   │   │   ├── OrderRequestDTO.java
│   │   │   │   ├── OrderResponseDTO.java
│   │   │   │   ├── PagedResponseDTO.java
│   │   │   │   └── ...
│   │   │   ├── entities/                       # Entitats JPA
│   │   │   │   ├── User.java
│   │   │   │   ├── Company.java
│   │   │   │   ├── Product.java
│   │   │   │   ├── Supplier.java
│   │   │   │   ├── Order.java
│   │   │   │   └── OrderItem.java
│   │   │   ├── exceptions/                     # Excepcions personalitzades
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── DuplicateResourceException.java
│   │   │   │   └── BadRequestException.java
│   │   │   ├── repositories/                   # Repositoris JPA
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── CompanyRepository.java
│   │   │   │   ├── ProductRepository.java
│   │   │   │   ├── SupplierRepository.java
│   │   │   │   ├── OrderRepository.java
│   │   │   │   ├── OrderItemRepository.java
│   │   │   │   └── OrderSpecifications.java
│   │   │   ├── security/                       # Seguretat
│   │   │   │   ├── JwtUtil.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── SecurityConfig.java
│   │   │   └── services/                       # Serveis de negoci
│   │   │       ├── UserService.java
│   │   │       ├── CompanyService.java
│   │   │       ├── ProductService.java
│   │   │       ├── SupplierService.java
│   │   │       ├── OrderService.java
│   │   │       ├── ReportService.java
│   │   │       ├── EmailService.java
│   │   │       ├── NotificationService.java
│   │   │       └── impl/
│   │   │           ├── UserServiceImpl.java
│   │   │           ├── CompanyServiceImpl.java
│   │   │           ├── ProductServiceImpl.java
│   │   │           ├── SupplierServiceImpl.java
│   │   │           ├── OrderServiceImpl.java
│   │   │           ├── ReportServiceImpl.java
│   │   │           ├── EmailServiceImpl.java
│   │   │           └── NotificationServiceImpl.java
│   │   └── resources/
│   │       ├── application.properties          # Configuració principal
│   │       └── images/                         # Imatges pujades
│   └── test/
│       ├── java/cat/abasta_back_end/
│       └── resources/
│           └── test-schema.sql                 # Esquema per a tests
├── pom.xml                                     # Configuració Maven
└── .mvn/                                       # Maven wrapper
```

#### 5.3.2. Patrons i Arquitectura

**Arquitectura en Capes**:

1. **Controller Layer**: Endpoints REST, validació d'entrada
2. **Service Layer**: Lògica de negoci, transformacions DTO-Entity
3. **Repository Layer**: Accés a dades amb JPA

**Patrons Implementats**:

- **MVC REST**: Model-View-Controller per a APIs REST
- **DTO Pattern**: Separació entre objectes de transferència i entitats
- **Service/Repository Pattern**: Separació de responsabilitats
- **Dependency Injection**: Spring IoC container
- **Singleton**: Beans Spring (@Service, @Repository, @Component)
- **Builder**: Lombok @Builder per a construcció d'objectes

**Seguretat**:

- **JWT (JSON Web Tokens)**: Autenticació stateless
- **BCrypt**: Hash de contrasenyes
- **Spring Security**: Framework de seguretat
- **CORS**: Configuració per a cross-origin requests
- **Multi-tenancy**: Aïllament de dades per empresa

**Flux de Petició**:

```Flux de Petició
HTTP Request → JwtAuthenticationFilter → Controller → Service → Repository → Database
                     ↓                        ↓           ↓
              SecurityContext              Validation  Business Logic
```

#### 5.3.3. Endpoints Principals

##### AuthController (`/api/auth`)

Gestió d'autenticació i recuperació de comptes.

| Endpoint | Mètode | Descripció | Body | Resposta |
|----------|--------|-----------|------|----------|
| `/login` | POST | Autenticació amb email/contrasenya | LoginRequestDTO | LoginResponseDTO (token JWT + user info) |
| `/forgot-password` | POST | Sol·licitud de recuperació de contrasenya | PasswordResetRequestDTO | Missatge confirmació |
| `/reset-password` | POST | Restablir contrasenya amb token | PasswordResetDTO | Missatge confirmació |
| `/verify-email` | POST | Verificar email de registre | EmailVerificationDTO | Missatge confirmació |
| `/resend-verification` | POST | Reenviar correu de verificació | PasswordResetRequestDTO | Missatge confirmació |

##### CompanyController (`/api/companies`)

Gestió d'empreses registrades.

| Endpoint | Mètode | Descripció | Body | Resposta |
|----------|--------|-----------|------|----------|
| `/register` | POST | Registre públic d'empresa + admin | CompanyRegistrationDTO | CompanyResponseDTO + missatge verificació |
| `/` | GET | Obtenir empresa autenticada | - | CompanyResponseDTO |
| `/` | PUT | Actualitzar dades empresa | CompanyRequestDTO | CompanyResponseDTO actualitzat |

##### UserController (`/api/users`)

Gestió integral d'usuaris amb CRUD complet i cerca avançada.

| Endpoint | Mètode | Descripció | Paràmetres/Body | Resposta |
|----------|--------|-----------|----------------|----------|
| `/` | GET | Llistar usuaris de l'empresa (paginat) | page, size, sortBy, sortDir | PagedResponseDTO\<UserResponseDTO\> |
| `/{uuid}` | GET | Obtenir usuari per UUID | uuid (path) | UserResponseDTO |
| `/search` | GET | Cerca bàsica per text (email, nom, cognoms, telèfon) | searchText, page, size, sortBy, sortDir | PagedResponseDTO\<UserResponseDTO\> |
| `/filter` | GET | Cerca avançada amb filtres múltiples | email, firstName, lastName, phone, page, size, sortBy, sortDir | PagedResponseDTO\<UserResponseDTO\> |
| `/` | POST | Crear nou usuari | UserRegistrationDTO | UserResponseDTO (HTTP 201) |
| `/{uuid}` | PUT | Actualitzar usuari existent | uuid (path), UserRequestDTO | UserResponseDTO |
| `/{uuid}/status` | PATCH | Canviar estat actiu/inactiu | uuid (path), isActive (param) | UserResponseDTO |
| `/{uuid}/change-password` | PATCH | Canviar contrasenya | uuid (path), PasswordChangeDTO | Missatge confirmació |
| `/{uuid}` | DELETE | Eliminar usuari (soft delete) | uuid (path) | Missatge confirmació |

##### SupplierController (`/api/suppliers`)

Gestió de proveïdors amb cerca avançada.

| Endpoint | Mètode | Descripció | Paràmetres/Body | Resposta |
|----------|--------|-----------|----------------|----------|
| `/` | GET | Llistar proveïdors de l'empresa (paginat) | page, size, sortBy, sortDir | PagedResponseDTO\<SupplierResponseDTO\> |
| `/{uuid}` | GET | Obtenir proveïdor per UUID | uuid (path) | SupplierResponseDTO |
| `/search` | GET | Cerca bàsica per text (nom, contacte, email, telèfon, adreça) | searchText, page, size, sortBy, sortDir | PagedResponseDTO\<SupplierResponseDTO\> |
| `/filter` | GET | Cerca avançada amb filtres múltiples | name, contactName, email, phone, address, page, size, sortBy, sortDir | PagedResponseDTO\<SupplierResponseDTO\> |
| `/` | POST | Crear nou proveïdor | SupplierRequestDTO | SupplierResponseDTO (HTTP 201) |
| `/{uuid}` | PUT | Actualitzar proveïdor existent | uuid (path), SupplierRequestDTO | SupplierResponseDTO |
| `/{uuid}/status` | PATCH | Activar/desactivar proveïdor | uuid (path), isActive (param) | SupplierResponseDTO |

##### ProductController (`/api/products`)

Gestió de productes amb càrrega d'imatges.

| Endpoint | Mètode | Descripció | Paràmetres/Body | Resposta |
|----------|--------|-----------|----------------|----------|
| `/` | GET | Llistar productes de l'empresa (paginat) | page, size, sortBy, sortDir | PagedResponseDTO\<ProductResponseDTO\> |
| `/{uuid}` | GET | Obtenir producte per UUID | uuid (path) | ProductResponseDTO |
| `/search` | GET | Cerca bàsica de productes | searchText, page, size, sortBy, sortDir | PagedResponseDTO\<ProductResponseDTO\> |
| `/filter` | GET | Cerca avançada amb filtres múltiples | supplierUuid, name, category, price, isActive, createdAfter, createdBefore, page, size, sortBy, sortDir | PagedResponseDTO\<ProductResponseDTO\> |
| `/create` | POST | Crear nou producte | ProductRequestDTO | ProductResponseDTO (HTTP 201) |
| `/{uuid}` | PUT | Actualitzar producte existent | uuid (path), ProductRequestDTO | ProductResponseDTO |
| `/deactivate/{uuid}` | PATCH | Desactivar producte (soft delete) | uuid (path) | ProductResponseDTO |
| `/upload/{productUuid}` | POST | Pujar imatge a producte existent | productUuid (path), image (multipart) | URL de la imatge |
| `/upload-temp` | POST | Pujar imatge temporal (sense producte) | image (multipart) | URL de la imatge |

##### OrderController (`/api/orders`)

Gestió de comandes amb enviament a proveïdors.

| Endpoint | Mètode | Descripció | Paràmetres/Body | Resposta |
|----------|--------|-----------|----------------|----------|
| `/filter` o `/list` | GET | Llistar/filtrar comandes amb cerca avançada | supplierUuid, name, notes, status, searchText, createdAfter, createdBefore, page, size, sortBy, sortDir | PagedResponseDTO\<OrderResponseDTO\> |
| `/{uuid}` | GET | Obtenir comanda per UUID | uuid (path) | OrderResponseDTO |
| `/create` | POST | Crear nova comanda amb items | OrderRequestDTO (inclou OrderItemDTO[]) | OrderResponseDTO (HTTP 201) |
| `/update/{uuid}` | PUT | Actualitzar comanda existent | uuid (path), OrderRequestDTO | OrderResponseDTO |
| `/{uuid}/send` | POST | Enviar comanda al proveïdor (EMAIL/WhatsApp) | uuid (path) | OrderResponseDTO (estat SENT) |
| `/delete/{uuid}` | PATCH | Eliminar comanda (canvi a CANCELLED) | uuid (path) | OrderResponseDTO |

##### ReportController (`/api/reports`)

Generació de reports i estadístiques.

| Endpoint | Mètode | Descripció | Paràmetres | Resposta |
|----------|--------|-----------|-----------|----------|
| `/dashboard` | GET | Dades del dashboard (últim mes) | - | DashboardResponseDTO (totalComandes, despesaComandes, comandesPendents) |
| `/global` | GET | Report global per període personalitzat | startDate, endDate (YYYY-MM-DD o ISO DateTime) | ReportGlobalResponseDTO |
| `/global/pdf` | GET | Report global en format PDF | startDate, endDate | byte[] (PDF file) |

#### 5.3.4. Fitxers Clau

| Fitxer | Descripció |
|--------|-----------|
| `pom.xml` | Configuració de dependències Maven |
| `AbastaBackEndApplication.java` | Punt d'entrada de Spring Boot |
| `SecurityConfig.java` | Configuració de seguretat i JWT |
| `JwtUtil.java` | Utilitats per a generació i validació de JWT |
| `GlobalExceptionHandler.java` | Gestió centralitzada d'errors |
| `ApiResponseDTO.java` | Resposta estàndard de l'API |
| `application.properties` | Configuració de l'aplicació |

### 5.4. Base de Dades (abasta-db)

#### 5.4.1. Scripts SQL

```Scripts SQL
abasta-db/
├── abasta_db_schema.sql          # Esquema complet de producció
└── readme.md                      # Documentació
```

**Fitxer de referència**: `abasta_db_schema.sql`

Aquest script conté:

- Creació de la base de dades
- Definició de totes les taules
- Índexs per a optimització
- Foreign keys i constraints
- Configuració de charset i collation

---

## 6. INSTAL·LACIÓ I CONFIGURACIÓ DEL SISTEMA

### 6.1. Requisits Previs

#### Sistema Operatiu

- **Windows** 10/11, **Linux** (Ubuntu 20.04+), o **macOS** 11+

#### Software Necessari

**Frontend**:

- **Node.js** 22.20.0 o superior
- **npm** (inclòs amb Node.js)

**Backend**:

- **Java JDK** 21 (LTS)
- **Maven** 3.8+ (o utilitzar el wrapper inclòs)

**Base de Dades**:

- **MySQL** 8.0 o superior

**Opcional**:

- **Git** per a control de versions
- **IntelliJ IDEA** o **Eclipse** (IDE per a Java)
- **Visual Studio Code** (editor per a frontend)

### 6.2. Instal·lació de la Base de Dades

#### Pas 1: Instal·lar MySQL

**Windows**:

1. Descarregar MySQL Installer des de <https://dev.mysql.com/downloads/installer/>
2. Executar l'instal·lador i seguir les instruccions
3. Configurar contrasenya per a l'usuari `root`

**Linux (Ubuntu/Debian)**:

```bash
sudo apt update
sudo apt install mysql-server
sudo mysql_secure_installation
```

**macOS** (amb Homebrew):

```bash
brew install mysql
brew services start mysql
mysql_secure_installation
```

#### Pas 2: Crear la Base de Dades

1. Connectar a MySQL:

```bash
mysql -u root -p
```

2. Crear usuari per a l'aplicació:

```sql
CREATE USER 'abasta_user'@'localhost' IDENTIFIED BY 'password_segur';
GRANT ALL PRIVILEGES ON abasta_db.* TO 'abasta_user'@'localhost';
FLUSH PRIVILEGES;
```

3. Executar l'script de creació:

```bash
mysql -u abasta_user -p < abasta-db/abasta_db_schema.sql
```

O des de MySQL:

```sql
SOURCE /ruta/a/abasta-db/abasta_db_schema.sql;
```

### 6.3. Instal·lació del Backend

#### Pas 1: Instal·lar Java JDK 21

**Windows**:

1. Descarregar des de <https://www.oracle.com/java/technologies/downloads/#java21>
2. Executar l'instal·lador
3. Configurar variable d'entorn `JAVA_HOME`

**Linux (Ubuntu)**:

```bash
sudo apt update
sudo apt install openjdk-21-jdk
```

**macOS** (amb Homebrew):

```bash
brew install openjdk@21
```

Verificar instal·lació:

```bash
java -version
```

#### Pas 2: Configurar Variables d'Entorn

Crear variables d'entorn al sistema:

**Windows** (PowerShell com a administrador):

```powershell
[System.Environment]::SetEnvironmentVariable('DB_USER_ABASTA', 'abasta_user', 'Machine')
[System.Environment]::SetEnvironmentVariable('DB_PASS_ABASTA', 'password_segur', 'Machine')
[System.Environment]::SetEnvironmentVariable('MAIL_USER_ABASTA', 'correu@gmail.com', 'Machine')
[System.Environment]::SetEnvironmentVariable('MAIL_PASS_ABASTA', 'password_correu', 'Machine')
```

**Linux/macOS** (afegir a `~/.bashrc` o `~/.zshrc`):

```bash
export DB_USER_ABASTA=abasta_user
export DB_PASS_ABASTA=password_segur
export MAIL_USER_ABASTA=correu@gmail.com
export MAIL_PASS_ABASTA=password_correu
```

Recarregar configuració:

```bash
source ~/.bashrc  # o source ~/.zshrc
```

#### Pas 3: Configurar application.properties

Editar `abasta-api/src/main/resources/application.properties`:

```properties
# Base de dades
spring.datasource.url=jdbc:mysql://localhost:3306/abasta_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=${DB_USER_ABASTA}
spring.datasource.password=${DB_PASS_ABASTA}

# Port del servidor
server.port=8084

# URL del frontend
app.frontend.url=http://localhost:5173
```

#### Pas 4: Compilar i Executar el Backend

**Amb Maven Wrapper** (recomanat):

```bash
cd abasta-api
./mvnw clean install
./mvnw spring-boot:run
```

**Windows**:

```bash
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

**Amb Maven instal·lat**:

```bash
mvn clean install
mvn spring-boot:run
```

El backend estarà disponible a: **<http://localhost:8084>**

Swagger UI: **<http://localhost:8084/swagger-ui.html>**

### 6.4. Instal·lació del Frontend

#### Pas 1: Instal·lar Node.js

**Windows**:

1. Descarregar des de <https://nodejs.org/>
2. Executar l'instal·lador (versió LTS 22.20.0+)

**Linux (Ubuntu)**:

```bash
curl -fsSL https://deb.nodesource.com/setup_22.x | sudo -E bash -
sudo apt-get install -y nodejs
```

**macOS** (amb Homebrew):

```bash
brew install node@22
```

Verificar instal·lació:

```bash
node -v
npm -v
```

#### Pas 2: Instal·lar Dependències

```bash
cd abasta-app
npm install
```

#### Pas 3: Configurar Variables d'Entorn

Crear fitxer `.env.development`:

```env
VITE_API_URL=http://localhost:8084/api
VITE_ENV=development
VITE_APP_NAME=Abasta (Local)
VITE_APP_VERSION=1.0.0-dev
VITE_SESSION_TIMEOUT=3600000
VITE_DEBUG=true
```

Per a **producció**, crear `.env.production`:

```env
VITE_API_URL=https://api.abasta.cat/api
VITE_ENV=production
VITE_APP_NAME=Abasta
VITE_APP_VERSION=1.0.0
VITE_SESSION_TIMEOUT=3600000
```

#### Pas 4: Executar el Frontend

**Mode desenvolupament**:

```bash
npm run dev
```

El frontend estarà disponible a: **<http://localhost:5173>**

**Compilar per a producció**:

```bash
npm run build
```

Els fitxers compilats es generaran a la carpeta `dist/`.

**Previsualitzar versió de producció**:

```bash
npm run preview
```

### 6.5. Configuració de Correu Electrònic (Gmail)

Per a l'enviament de correus (verificació, reset de contrasenya, comandes):

#### Pas 1: Configurar Compte Gmail

1. Anar a <https://myaccount.google.com/security>
2. Activar **Verificació en 2 passos**
3. Crear una **Contrasenya d'aplicació**:
   - Anar a <https://myaccount.google.com/apppasswords>
   - Seleccionar "Correu" i "Altres"
   - Copiar la contrasenya generada (16 caràcters)

#### Pas 2: Configurar Variables d'Entorn

```bash
MAIL_USER_ABASTA=el_teu_correu@gmail.com
MAIL_PASS_ABASTA=contrasenya_aplicacio_generada
```

### 6.6. Scripts Disponibles

#### Frontend (abasta-app)

| Script | Comanda | Descripció |
|--------|---------|-----------|
| Desenvolupament | `npm run dev` | Inicia servidor de desenvolupament |
| Build | `npm run build` | Compila per a producció |
| Preview | `npm run preview` | Previsualitza versió de producció |
| Lint | `npm run lint` | Executa ESLint |
| Lint Fix | `npm run lint:fix` | Corregeix errors de ESLint |
| Format | `npm run format` | Formata el codi amb Prettier |
| Format Check | `npm run format:check` | Verifica format del codi |

#### Backend (abasta-api)

| Script | Comanda | Descripció |
|--------|---------|-----------|
| Compilar | `./mvnw clean install` | Compila el projecte |
| Executar | `./mvnw spring-boot:run` | Executa l'aplicació |
| Tests | `./mvnw test` | Executa els tests |
| Package | `./mvnw package` | Crea un JAR executable |

### 6.7. Desplegament en Producció

#### Backend

1. **Compilar el JAR**:

```bash
cd abasta-api
./mvnw clean package -DskipTests
```

2. **Executar el JAR**:

```bash
java -jar target/abasta-back-end-0.0.1-SNAPSHOT.jar
```

3. **Configurar com a servei** (Linux amb systemd):

Crear fitxer `/etc/systemd/system/abasta-api.service`:

```ini
[Unit]
Description=Abasta API
After=syslog.target

[Service]
User=abasta
ExecStart=/usr/bin/java -jar /opt/abasta/abasta-back-end.jar
SuccessExitStatus=143
Environment="DB_USER_ABASTA=abasta_user"
Environment="DB_PASS_ABASTA=password_segur"
Environment="MAIL_USER_ABASTA=correu@gmail.com"
Environment="MAIL_PASS_ABASTA=password_correu"

[Install]
WantedBy=multi-user.target
```

Activar el servei:

```bash
sudo systemctl enable abasta-api
sudo systemctl start abasta-api
```

#### Frontend

1. **Compilar**:

```bash
cd abasta-app
npm run build
```

2. **Desplegar amb Apache**:

Configuració d'Apache (`/etc/apache2/sites-available/abasta.conf`):

```apache
<VirtualHost *:80>
    ServerName abasta.cat
    DocumentRoot /var/www/abasta

    <Directory /var/www/abasta>
        Options -Indexes +FollowSymLinks
        AllowOverride All
        Require all granted

        # Redirecció per a React Router
        FallbackResource /index.html
    </Directory>

    # Proxy per a l'API
    ProxyPass /api http://localhost:8084/api
    ProxyPassReverse /api http://localhost:8084/api
</VirtualHost>
```

3. **Copiar fitxers compilats**:

```bash
sudo cp -r dist/* /var/www/abasta/
```

4. **Activar el site**:

```bash
sudo a2ensite abasta
sudo systemctl reload apache2
```

### 6.8. Verificació de la Instal·lació

#### Backend

1. Verificar que el servidor està actiu:

```bash
curl http://localhost:8084/api/health
```

2. Accedir a Swagger UI:

```URL
http://localhost:8084/swagger-ui.html
```

#### Frontend

1. Obrir el navegador:

```URL
http://localhost:5173
```

2. Verificar que es poden veure:
   - Pàgina d'inici
   - Formulari de login
   - Formulari de registre

#### Base de Dades

1. Verificar connexió:

```bash
mysql -u abasta_user -p abasta_db
```

2. Comprovar taules:

```sql
SHOW TABLES;
```

Hauria de mostrar: `companies`, `users`, `suppliers`, `products`, `orders`, `order_items`

### 6.9. Solució de Problemes Comuns

#### Backend no arrenca

**Error**: `Access denied for user 'abasta_user'@'localhost'`

- **Solució**: Verificar credencials de MySQL i variables d'entorn

**Error**: `Port 8084 is already in use`

- **Solució**: Canviar el port a `application.properties` o aturar el procés que ocupa el port

#### Frontend no es connecta al Backend

**Error**: `Failed to fetch` o errors CORS

- **Solució**: Verificar que `VITE_API_URL` està correctament configurat i que el backend permet CORS des de l'origen del frontend

#### Errors de compilació del Frontend

**Error**: `Module not found`

- **Solució**: Executar `npm install` de nou

**Error**: TypeScript errors

- **Solució**: Verificar que les versions de TypeScript i @types són compatibles

### 6.10. Configuració de Desenvolupament

#### Recomanacions d'IDE

**Visual Studio Code** (Frontend):

- Extensions recomanades:
  - ESLint
  - Prettier
  - TypeScript and JavaScript Language Features
  - Vite
  - Auto Rename Tag

**IntelliJ IDEA** (Backend):

- Plugins recomanats:
  - Lombok
  - Spring Boot
  - Database Navigator

#### Configuració de Git Hooks (Opcional)

Crear fitxer `.husky/pre-commit`:

```bash
#!/bin/sh
cd abasta-app
npm run lint:fix
npm run format
```

---

## APÈNDIX

### A. Enllaços Útils

- **Repositori del projecte**: [GitHub](https://github.com/abastaplatform/abasta)
- **Documentació Spring Boot**: <https://spring.io/projects/spring-boot>
- **Documentació React**: <https://react.dev/>
- **Documentació TypeScript**: <https://www.typescriptlang.org/>
- **Documentació Vite**: <https://vitejs.dev/>
- **Documentació Bootstrap**: <https://getbootstrap.com/>
- **Documentació MySQL**: <https://dev.mysql.com/doc/>

### B. Contacte i Suport

- **Email**: <abasta.platform@gmail.com>
- **Issue Tracker**: GitHub Issues

### C. Llicència

Aquest projecte és propietat de Abasta Platform. Tots els drets reservats.

---

**FI DE LA DOCUMENTACIÓ TÈCNICA**

*Última actualització: 9 de desembre de 2025*
