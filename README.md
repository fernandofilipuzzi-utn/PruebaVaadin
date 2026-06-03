# PruebaVaadin — Gestión de Personas

Proyecto de ejemplo con **Vaadin 24 + Spring Boot 3 (Java 17)** que gestiona
personas siguiendo una **arquitectura por capas (Service Layer)**, con
persistencia en **SQL Server** usando **autenticación integrada de Windows**.

## Dominio

Entidad `Persona`:

| Campo    | Tipo     | Notas                |
|----------|----------|----------------------|
| `dni`    | `String` | Clave primaria       |
| `nombre` | `String` |                      |
| `edad`   | `int`    |                      |

## Arquitectura por capas

```
src/main/java/com/utn/personas
├── Application.java                 # Arranque Spring Boot
├── entity
│   └── Persona.java                 # Entidad JPA (@Entity, @Id, @Column)
├── dto
│   └── PersonaView.java             # DTO / modelo de vista (solo UI)
├── mapper
│   └── PersonaMapper.java           # Conversión entidad <-> DTO (ambos sentidos)
├── repository
│   └── PersonaRepository.java       # JpaRepository<Persona, String>
├── service
│   ├── PersonaService.java          # Interfaz de la capa de servicio
│   └── PersonaServiceImpl.java      # Implementación @Service (CRUD)
└── ui
    └── PersonaListView.java         # Vista Vaadin @Route con Grid + FormLayout

src/test/java/com/utn/personas
├── mapper
│   └── PersonaMapperTest.java       # Conversión en ambos sentidos + nulls
└── service
    └── PersonaServiceImplTest.java  # CRUD con @Mock + @InjectMocks (unitario puro)

src/main/resources
└── application.properties           # Configuración SQL Server + JPA + Vaadin
```

**Flujo de datos:** la UI (`PersonaListView`) consume solo `PersonaService` y
trabaja con `PersonaView`. El servicio traduce con `PersonaMapper`: DTOs hacia
la vista, entidades hacia el repositorio. La entidad `Persona` nunca escapa de
las capas de servicio/repositorio.

## Persistencia: SQL Server con autenticación de Windows

La conexión está configurada en
[application.properties](src/main/resources/application.properties):

```
spring.datasource.url=jdbc:sqlserver://DEV:1433;databaseName=PersonasDB;integratedSecurity=true;authenticationScheme=NativeAuthentication;encrypt=true;trustServerCertificate=true
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
```

> **No** se definen `spring.datasource.username` ni `spring.datasource.password`:
> la conexión usa la **identidad del usuario Windows** que ejecuta la aplicación
> (`integratedSecurity=true`).

### ⚠️ Requisito: DLL nativa de autenticación

La autenticación integrada de Windows necesita la **DLL nativa**:

```
mssql-jdbc_auth-12.6.1.x64.dll
```

- Su versión **debe coincidir** con la del driver `mssql-jdbc` declarado en el
  `pom.xml` (aquí **12.6.1**).
- Debe estar disponible de una de estas formas:
  - en una carpeta incluida en el **`PATH`** del sistema, **o**
  - indicada al arrancar con
    `-Djava.library.path="C:\ruta\a\la\carpeta\de\la\dll"`.
- Se descarga junto al driver desde la
  [página oficial de Microsoft](https://learn.microsoft.com/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server)
  (dentro del paquete del driver, carpeta `auth\x64`).

Además, la base de datos `PersonasDB` debe existir en la instancia SQL Server
del host `DEV` (con `ddl-auto=update` Hibernate crea/actualiza las tablas, pero
**no** la base de datos en sí).

## Cómo ejecutar

```powershell
# Modo desarrollo (la DLL debe estar en el PATH)
mvn spring-boot:run

# Indicando la ruta de la DLL nativa explícitamente
mvn spring-boot:run "-Dspring-boot.run.jvmArguments=-Djava.library.path=C:\sqljdbc_auth"
```

Luego abrir <http://localhost:8080>.

## Pruebas

```powershell
mvn test
```

- **`PersonaServiceImplTest`**: unitario **puro** (JUnit 5 + Mockito). Mockea el
  repositorio con `@Mock` e inyecta con `@InjectMocks`; **no** levanta el
  contexto de Spring ni se conecta a la base de datos, por lo que **no** depende
  de la autenticación de Windows. Cubre crear, listar, buscar (existente e
  inexistente), actualizar y eliminar, con `verify(...)` sobre el repositorio.
- **`PersonaMapperTest`**: valida la conversión `Persona ↔ PersonaView` en ambos
  sentidos, incluyendo valores `null`.

Patrón **Arrange-Act-Assert** y nombres descriptivos
(p. ej. `crearPersona_deberiaGuardarYDevolverDTO`).

## Dependencias clave (pom.xml)

- `vaadin-spring-boot-starter` (vía `vaadin-bom` 24.3.11)
- `spring-boot-starter-data-jpa`
- `mssql-jdbc` 12.6.1.jre11
- `spring-boot-starter-test` (JUnit 5 + Mockito + AssertJ)
