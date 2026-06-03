Generá un proyecto de ejemplo con **Vaadin + Spring Boot (Java 17+)** que gestione personas, siguiendo estas especificaciones:

## Dominio
- Entidad `Persona` con los campos: `dni` (String, clave primaria), `nombre` (String) y `edad` (int).

## Arquitectura por capas (Service Layer)
- **Capa de entidad:** clase `Persona` anotada con JPA (`@Entity`, `@Id`, `@Column`), mapeada a la base de datos.
- **Capa de modelo de vista (DTO):** clase `PersonaView` separada de la entidad, usada únicamente en la UI. Incluí un `PersonaMapper` (o métodos de conversión) para transformar entre `Persona` (entidad) y `PersonaView` (DTO) en ambos sentidos.
- **Capa de repositorio:** interfaz `PersonaRepository extends JpaRepository<Persona, String>`.
- **Capa de servicio:** interfaz `PersonaService` y su implementación `PersonaServiceImpl` anotada con `@Service`, que contenga la lógica de negocio (CRUD) y trabaje con DTOs hacia la vista y entidades hacia el repositorio.

## Capa de presentación (Vaadin)
- Una vista `PersonaListView` (anotada con `@Route`) con un `Grid<PersonaView>` para listar personas y un formulario (`FormLayout`) con campos para alta/edición y botones de guardar/eliminar. La vista debe consumir solo `PersonaService` y trabajar con `PersonaView`, nunca con la entidad directamente.

## Persistencia (SQL Server local con autenticación de Windows)
- Configurá `application.properties` para conectarse a **SQL Server** en el host `DEV` usando **autenticación integrada de Windows** (sin usuario ni contraseña en el archivo):
  - `spring.datasource.url=jdbc:sqlserver://DEV:1433;databaseName=PersonasDB;integratedSecurity=true;authenticationScheme=NativeAuthentication;encrypt=true;trustServerCertificate=true`
  - Driver: `com.microsoft.sqlserver.jdbc.SQLServerDriver`
  - **No incluyas** `spring.datasource.username` ni `spring.datasource.password`, ya que la conexión usa la identidad del usuario Windows actual.
  - `spring.jpa.hibernate.ddl-auto=update`
  - Dialecto de Hibernate para SQL Server.
- Incluí en el `pom.xml` las dependencias: `vaadin-spring-boot-starter`, `spring-boot-starter-data-jpa` y el driver `mssql-jdbc`.
- Agregá un comentario en el `application.properties` (y en el README) aclarando que la autenticación de Windows requiere la **DLL nativa** `mssql-jdbc_auth-<versión>-x64.dll`, que debe coincidir con la versión del driver `mssql-jdbc` y estar disponible en el `PATH` del sistema o indicada con `-Djava.library.path` al ejecutar la aplicación.

## Pruebas unitarias
- Usá **JUnit 5** y **Mockito** (dependencia `spring-boot-starter-test`).
- **Tests del servicio (`PersonaServiceImplTest`):** mockeá el `PersonaRepository` con `@Mock` e inyectalo con `@InjectMocks`. Cubrí todos los métodos del CRUD: crear, listar todos, buscar por dni, actualizar y eliminar. Verificá tanto los casos exitosos como los bordes (por ejemplo, buscar un dni inexistente). Validá que las conversiones entidad↔DTO se apliquen correctamente y usá `verify(...)` para confirmar las interacciones con el repositorio.
- **Tests del mapper (`PersonaMapperTest`):** validá la conversión en ambos sentidos (`Persona` → `PersonaView` y viceversa), incluyendo el manejo de valores nulos.
- Seguí el patrón **Arrange-Act-Assert** y usá nombres de método descriptivos (por ejemplo, `crearPersona_deberiaGuardarYDevolverDTO`).
- Las pruebas del servicio deben ser **unitarias puras**, sin levantar el contexto de Spring ni conectarse a la base de datos (así no dependen de la autenticación de Windows).

## Salida esperada
Mostrá el árbol de paquetes propuesto (`entity`, `dto`, `repository`, `service`, `mapper`, `ui` y el espejo bajo `src/test/java`), el `pom.xml` completo y todas las clases (de producción y de test) con su código completo y comentado.