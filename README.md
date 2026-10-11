# Software para la ENPH – Cuadernillo 2

Proyecto Integrador (Ingeniería de Sistemas, UDI). Software de apoyo al diligenciamiento del **Cuadernillo 2 (gastos diarios del hogar)** de la Encuesta Nacional de Presupuestos de los Hogares (ENPH) del DANE.

Grupo: Diego Andrés Fuentes Lizcano, Edwin Andrés Arias Pineda y Karolay Daniela Gómez Pedraza

Desarrollo del código: Karolay Daniela Gómez Pedraza

## Componentes

| Componente | Tecnología | Descripción |
|---|---|---|
| Interfaz web (cliente) | HTML, CSS, JavaScript | Inicio de sesión, hogares asignados, capítulos A, B, C, D y D1, consultas y reportes. Guarda en el navegador y, con el servidor Java encendido, también envía los gastos diarios (capítulo D) a Oracle (`js/oracle.js`). |
| Backend (servidor) | Java 21, JDBC | Capas `conexion`, `modelos`, `dao` y `servicios`. Implementa el CRUD de `gasto_diario`, `hogares` y `encuestadores`. `servidor/ServidorHTTP` expone rutas HTTP locales para que la interfaz web guarde los gastos en Oracle. |
| Base de datos | Oracle Database 10g Express | Tablas `encuestadores`, `hogares` y `gasto_diario` (script `database.sql`). |

## Estructura

```
index.html        Interfaz web (inicio de sesión)
css/  js/  img/   Estilos, lógica y recursos de la interfaz
  js/app.js       Lógica de la interfaz (guarda en el navegador)
  js/oracle.js    Envía los gastos diarios al servidor Java para guardarlos en Oracle
src/              Backend en Java
  conexion/       ConexionBD (conexión JDBC a Oracle)
  modelos/        Encuestador, Hogar, GastoDiario
  dao/            EncuestadorDAO, HogarDAO, GastoDiarioDAO (crear, leer, actualizar, eliminar)
  servicios/      EncuestadorServicio, HogarServicio, GastoServicio (validaciones y lógica de negocio)
  servidor/       ServidorHTTP (rutas locales que conectan la interfaz web con Oracle)
  Main.java       Prueba de conexión y CRUD de las tres tablas
database.sql      Script de creación de la base de datos
.env.example      Ejemplo de variables de entorno
```

## Requisitos

- JDK 21
- Oracle Database 10g Express Edition (con Application Express)
- Controlador `ojdbc14.jar` (no se incluye en el repositorio; copiarlo en `lib/` y agregarlo a *Referenced Libraries* en VS Code)

## Base de datos

1. Entrar a APEX (`http://127.0.0.1:8080/apex`) y crear un usuario `ENPH` con los privilegios CONNECT y RESOURCE.
2. Entrar con ese usuario y ejecutar las instrucciones de `database.sql` en *SQL → Comandos SQL*, una por una.
3. Verificar con `SELECT table_name FROM user_tables;` (deben aparecer `ENCUESTADORES`, `HOGARES` y `GASTO_DIARIO`).

## Configuración de la conexión

`ConexionBD` lee estas variables de entorno (no guardar contraseñas reales en el repositorio):

| Variable | Valor por defecto |
|---|---|
| `DB_URL` | `jdbc:oracle:thin:@localhost:1521:xe` |
| `DB_USER` | `ENPH` |
| `DB_PASS` | *(definir en el equipo local)* |

## Ejecución de la prueba del backend

Ejecutar `Main`. Comprueba la conexión y realiza el CRUD (crear, consultar, actualizar y eliminar) sobre `gasto_diario`, `hogares` y `encuestadores`, mostrando el resultado de cada paso en la consola.

## Ejecución de la aplicación completa (interfaz web + Oracle)

1. Dejar Oracle activo y creadas las tablas (sección *Base de datos*).
2. Definir la contraseña de Oracle (`DB_PASS`) en el equipo, como se indica arriba.
3. Ejecutar `src/servidor/ServidorHTTP.java`. En la consola debe aparecer *Servidor ENPH activo en http://127.0.0.1:8081* y *Conexion con Oracle: OK*. Dejar esa ventana abierta.
4. Abrir `index.html` en el navegador (usuario de prueba precargado en el formulario de inicio de sesión).
5. Entrar a un hogar, abrir **Gastos diarios** y registrar un gasto. En la esquina inferior izquierda aparece *Guardado en Oracle (ID …)*.
6. Verificar en APEX (*SQL → Comandos SQL*): `SELECT * FROM gasto_diario;`

Al editar o eliminar un gasto en la tabla de la página, el cambio también se aplica en Oracle. Si el servidor Java está apagado, la interfaz sigue funcionando y guarda solo en el navegador.

Rutas del servidor (`127.0.0.1:8081`): `GET /api/salud`, `GET /api/gastos?hogar=ID`, `POST /api/gastos/crear`, `POST /api/gastos/actualizar`, `POST /api/gastos/eliminar`.

## Estado del proyecto (segundo avance)

- Interfaz web funcional. Los gastos diarios (capítulo D) se guardan, editan y eliminan también en Oracle mediante el servidor Java; el resto de capítulos (A, B, C y D1) se guarda por ahora en el navegador.
- Backend Java conectado a Oracle con CRUD de `gasto_diario`, `hogares` y `encuestadores`.
- Pendiente: integrar con Oracle los demás capítulos (visitas, características del gasto y comidas fuera del hogar) y completar las demás tablas del modelo relacional.
