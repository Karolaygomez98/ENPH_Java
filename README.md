# Software para la ENPH - Cuadernillo 2

Proyecto Integrador, Universidad de Investigacion y Desarrollo (UDI), Ingenieria de Sistemas, Bucaramanga.

Aplicacion de escritorio en Java 17 (Swing) con base de datos Oracle 10g para registrar el Cuadernillo 2 (gastos diarios) de la Encuesta Nacional de Presupuestos de los Hogares (ENPH).

## Autores
- Karolay Daniela Gomez Pedraza
- Diego Andres Fuentes Lizcano
- Edwin Andres Arias Pineda

Docente: Jorge Giovany Castellanos Valderrama.

## Arquitectura
Arquitectura en capas con patron DAO. Solo `conexion.ConexionBD` abre conexiones a Oracle.

| Paquete | Funcion |
|---|---|
| `ui` | Ventanas y paneles Swing. No contienen SQL. |
| `servicios` | Reglas de negocio y validaciones. |
| `dao` | Acceso a datos con JDBC (PreparedStatement / CallableStatement). |
| `modelos` | Clases de datos. |
| `conexion` | `ConexionBD`, unico punto de conexion. |

Flujo: ui -> servicios -> dao -> ConexionBD -> Oracle.

## Roles
Encuestador, Supervisor y Critico-Digitador.

## Requisitos
- JDK 17
- Driver JDBC de Oracle (ojdbc), agregado al proyecto o al classpath (no se incluye en el repositorio)
- Base de datos Oracle con las tablas, procedimientos, funciones, vista y secuencias del proyecto

## Configuracion de la conexion
1. Copie `config.example.properties` como `config.properties` en la carpeta desde donde se ejecuta la aplicacion.
2. Escriba la URL, el usuario y la clave de Oracle.

Tambien se pueden usar las variables de entorno `DB_URL`, `DB_USER` y `DB_PASS`. El archivo `config.properties` real no se sube al repositorio.

## Compilar y ejecutar
```
javac -cp ojdbc.jar -d build $(find src -name "*.java")
java -cp build:ojdbc.jar ui.Main
```
En Windows use `;` en lugar de `:` en el classpath. En NetBeans: abrir el proyecto y ejecutar.
