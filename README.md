# Software para la ENPH – Cuadernillo 2

Proyecto Integrador (Ingeniería de Sistemas, UDI). Software de apoyo al diligenciamiento del **Cuadernillo 2 (gastos diarios del hogar)** de la Encuesta Nacional de Presupuestos de los Hogares (ENPH) del DANE.

Autora: Karolay Daniela Gómez Pedraza

## Componentes

| Componente | Tecnología | Descripción |
|---|---|---|
| Interfaz web (cliente) | HTML, CSS, JavaScript | Inicio de sesión, hogares asignados, capítulos A, B, C, D y D1, consultas y reportes. Guarda automáticamente en el navegador. |
| Backend (servidor) | Java 21, JDBC | Capas `conexion`, `modelos`, `dao` y `servicios`. Implementa el CRUD de `gasto_diario`. |
| Base de datos | Oracle Database 10g Express | Tablas `encuestadores`, `hogares` y `gasto_diario` (script `database.sql`). |

## Estructura

```
index.html        Interfaz web (inicio de sesión)
css/  js/  img/   Estilos, lógica y recursos de la interfaz
src/              Backend en Java
  conexion/       ConexionBD (conexión JDBC a Oracle)
  modelos/        GastoDiario
  dao/            GastoDiarioDAO (crear, leer, actualizar, eliminar)
  servicios/      GastoServicio (validaciones y lógica de negocio)
  Main.java       Prueba de conexión y CRUD
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

Ejecutar `Main`. Comprueba la conexión y realiza un CRUD sobre `gasto_diario`: crea un gasto, lo consulta, lo actualiza y lo elimina, mostrando el resultado de cada paso.

## Estado del proyecto (segundo avance)

- Interfaz web funcional (el guardado actual es local, en el navegador).
- Backend Java conectado a Oracle con CRUD de `gasto_diario`.
- Pendiente: integrar la interfaz web con el backend mediante peticiones HTTP y completar las demás tablas del modelo relacional.
