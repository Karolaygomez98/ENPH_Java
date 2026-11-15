# Software para la ENPH - Cuadernillo 2

Proyecto Integrador, Universidad de Investigacion y Desarrollo (UDI), Ingenieria de Sistemas, Bucaramanga.

Aplicacion de escritorio en Java 17 (Swing) con base de datos Oracle 10g para registrar el Cuadernillo 2 (gastos diarios) de la Encuesta Nacional de Presupuestos de los Hogares (ENPH).

## Autores
- Karolay Daniela Gomez Pedraza
- Diego Andres Fuentes Lizcano
- Edwin Andres Arias Pineda

## Contenido del repositorio

| Elemento | Descripcion |
|---|---|
| `src/` | Codigo fuente Java (paquetes ui, servicios, dao, modelos y conexion). |
| `sql/` | Scripts de la base de datos, numerados en el orden en que se ejecutan. |
| `lib/ojdbc14.jar` | Driver JDBC de Oracle 10g. |
| `nbproject/`, `build.xml`, `manifest.mf` | Archivos del proyecto de NetBeans. |
| `config.example.properties` | Plantilla de configuracion de la conexion. |

## Arquitectura
Arquitectura cliente-servidor en capas con patron DAO. La aplicacion es un solo programa de escritorio que se conecta a Oracle por JDBC. Solo `conexion.ConexionBD` abre conexiones a Oracle.

| Paquete | Funcion |
|---|---|
| `ui` | Ventanas y paneles Swing. No contienen SQL. |
| `servicios` | Reglas de negocio y validaciones. |
| `dao` | Acceso a datos con JDBC (PreparedStatement / CallableStatement). |
| `modelos` | Clases de datos. |
| `conexion` | `ConexionBD`, unico punto de conexion. |

Flujo: ui -> servicios -> dao -> ConexionBD -> Oracle.

## Roles
Encuestador, Supervisor y Critico-Digitador. Los usuarios de la aplicacion son `kgomez` (Encuestador), `dfuentes` (Supervisor) y `earias` (Critico-Digitador). La clave se crea en el primer ingreso de cada usuario.

## Requisitos
- JDK 17 o superior
- Driver `lib/ojdbc14.jar` (incluido en el repositorio)
- Una base de datos Oracle con el esquema del proyecto (ver el siguiente apartado)

## Base de datos
Los scripts de la carpeta `sql/` se ejecutan en SQL*Plus, conectado con el usuario dueno del esquema (PROENPH en el servidor de la UDI), en este orden:

1. `02_database.sql`: crea las 17 tablas, las secuencias, los disparadores, las funciones y los procedimientos.
2. `02b_objetos_informe_solo_vista.sql`: crea o reemplaza la vista `v_gasto_unificado` (informe gerencial).
3. `03_datos_demo.sql`: carga datos de demostracion (opcional).
4. `04_consultas_evidencia.sql`: consultas para comprobar el usuario, los roles y los objetos creados.
5. `05_usuarios_roles_oracle_udi.sql`: crea los roles y usuarios de Oracle (opcional, requiere permisos de administrador; la clave se pide por pantalla).

**Atencion:** `02_database.sql` borra y vuelve a crear las tablas del proyecto. Ejecutelo solo en un esquema de pruebas o en uno que no tenga datos propios con esos nombres.

## Configuracion de la conexion
1. Copie `config.example.properties` como `config.properties` en la carpeta desde donde se ejecuta la aplicacion.
2. Escriba la URL, el usuario y la clave de Oracle.

Tambien se pueden usar las variables de entorno `DB_URL`, `DB_USER` y `DB_PASS`. El archivo `config.properties` real no se sube al repositorio. Los datos de acceso al servidor de la UDI se entregan a los docentes por separado.

## Abrir y ejecutar en NetBeans
1. Descargue o clone el repositorio.
2. En NetBeans: Archivo > Abrir proyecto, y elija la carpeta del repositorio.
3. Si NetBeans no encuentra el driver, haga clic derecho en el proyecto > Propiedades > Bibliotecas > Agregar JAR/carpeta, y seleccione `lib/ojdbc14.jar`.
4. Cree `config.properties` como se explica arriba.
5. Ejecute el proyecto (clase principal `ui.Main`).

## Compilar y ejecutar desde la consola
```
javac -cp lib/ojdbc14.jar -d build $(find src -name "*.java")
java -cp build:lib/ojdbc14.jar ui.Main
```
En Windows use `;` en lugar de `:` en el classpath.
