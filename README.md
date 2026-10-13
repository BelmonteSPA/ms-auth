# Belmonte SpA - Microservicio de Autenticación (ms-auth)

Maneja el registro, el login y los roles de los usuarios. Al iniciar sesión entrega un token JWT que la app envía
en cada petición. Para registrar un pasajero crea su ficha en `ms-pasajeros` (OpenFeign).

## Roles

| Rol | Quién es | Qué hace en el sistema |
|---|---|---|
| `ADMIN` | Administrador / recepcionista | Gestiona usuarios, ve solicitudes y asigna conductores |
| `PASAJERO` | Solicitante | Crea solicitudes de traslado |
| `CONDUCTOR` | Conductor | Consulta sus viajes, los inicia y los finaliza |

## Tecnologías

- Java 25 / Spring Boot 4.0.6
- Spring Security + JWT (jjwt, firma HS256) + contraseñas con BCrypt
- MySQL (Laragon) + Spring Data JPA, migraciones con Flyway
- Spring Cloud OpenFeign y cliente Eureka (desactivado por ahora)

## Puerto

`http://localhost:8081`

## Endpoints

### Públicos (`/api/v1/auth`)

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/register` | Registra un solicitante: crea la cuenta (rol `PASAJERO`) y su ficha en ms-pasajeros. Devuelve el token (409 si el email ya existe) |
| POST | `/login` | Inicia sesión y devuelve el token (401 si las credenciales son incorrectas) |

### Con token (`Authorization: Bearer <token>`)

| Método | Ruta | Rol | Descripción |
|---|---|---|---|
| GET | `/api/v1/auth/me` | Cualquiera | Datos del usuario del token |
| GET | `/api/v1/usuarios` | `ADMIN` | Lista usuarios |
| GET | `/api/v1/usuarios/{id}` | `ADMIN` | Detalle de un usuario |
| POST | `/api/v1/usuarios` | `ADMIN` | Crea un usuario de cualquier rol (conductores, otros admins) |
| PATCH | `/api/v1/usuarios/{id}/estado` | `ADMIN` | Alterna activo (1) / inactivo (0) |

Sin token o con token vencido responde 401; con un rol sin permiso responde 403.

Ejemplo de `POST /register`:

```json
{
  "nombre": "Ana Prueba",
  "email": "ana@ejemplo.test",
  "password": "Clave12345",
  "telefono": "+56911112222",
  "empresa": "Empresa Test"
}
```

Ejemplo de `POST /login`:

```json
{ "email": "admin@belmonte.test", "password": "Admin1234!" }
```

Respuesta:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "expiraEnMinutos": 480,
  "usuario": { "id": 1, "email": "admin@belmonte.test", "rol": "ADMIN", "idReferencia": null, "activo": true }
}
```

Ejemplo de `POST /api/v1/usuarios` (solo ADMIN):

```json
{
  "email": "nuevo.conductor@ejemplo.test",
  "password": "Clave12345",
  "rol": "CONDUCTOR",
  "idReferencia": 5
}
```

`idReferencia` es el id de la ficha en ms-pasajeros (`PASAJERO`) o ms-conductores (`CONDUCTOR`); es obligatorio para
esos dos roles y no aplica a `ADMIN`. La app lo usa, por ejemplo, para enviar `idPasajero` al crear un viaje.

## El token

Contiene el email, el id de usuario, el rol y el `idReferencia`, firmados con la clave `jwt.secret`. Dura 480 minutos
(`jwt.expiration-minutes`). Una cuenta desactivada no puede iniciar sesión, pero un token ya emitido sigue valiendo
hasta que expire.

La clave por defecto es solo para desarrollo. Para cambiarla, define la variable `JWT_SECRET` (mínimo 32 caracteres).
Los demás microservicios validarán el token con esa misma clave cuando se agregue el API Gateway.

## Comunicación con otros microservicios

| Servicio | URL por defecto | Variable | Endpoint usado |
|---|---|---|---|
| ms-pasajeros | `http://localhost:8082` | `MS_PASAJEROS_URL` | `POST /api/v1/pasajeros` (solo en `/register`) |

Si `ms-pasajeros` no responde, `/register` devuelve 503. El login no depende de otros servicios.

## Base de datos (MySQL con Laragon)

1. Iniciar MySQL desde Laragon (puerto 3306).
2. La base `db_auth` se crea sola al arrancar (`createDatabaseIfNotExist=true`).
3. Las tablas las crea Flyway al arrancar (`src/main/resources/db/migration`).
4. Credenciales por variables de entorno (por defecto, las de Laragon):

| Variable | Por defecto |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/db_auth?createDatabaseIfNotExist=true&serverTimezone=America/Santiago` |
| `DB_USER` | `root` |
| `DB_PASSWORD` | (vacía) |

## Ejecución local

El perfil `dev` viene activo en `application.yml`: si la tabla está vacía, carga 121 usuarios sintéticos que coinciden
con los datos de prueba de los otros servicios.

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin@belmonte.test` | `Admin1234!` | `ADMIN` |
| `pasajero1@ejemplo.test` … `pasajero100@ejemplo.test` | `Pasajero1234!` | `PASAJERO` (idReferencia 1 a 100) |
| `conductor1@ejemplo.test` … `conductor20@ejemplo.test` | `Conductor1234!` | `CONDUCTOR` (idReferencia 1 a 20) |

```bash
./mvnw spring-boot:run
```

## Nota

Si falta el wrapper de Maven (`mvnw`, carpeta `.mvn`), generarlo con `mvn wrapper:wrapper` o crearlo desde el IDE.
