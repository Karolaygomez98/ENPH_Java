-- =====================================================================
-- ENPH - Evidencia del usuario montado en el servidor de la UDI
-- Ejecutar en SQL*Plus conectada como PROENPH:
--     @04_consultas_evidencia.sql
-- El resultado queda en el archivo evidencia_udi.txt (para copiarlo al documento).
-- Algunas consultas pueden dar error por falta de permiso: eso tambien es evidencia.
-- Si SPOOL da error (SP2-0606), no importa: copie el texto de la ventana de SQL*Plus.
-- =====================================================================
SET LINESIZE 200
SET PAGESIZE 100
SET ECHO ON
SPOOL evidencia_udi.txt

PROMPT === 1. Usuario conectado y fecha ===
SELECT USER AS usuario, TO_CHAR(SYSDATE, 'DD/MM/YYYY HH24:MI') AS fecha FROM dual;

PROMPT === 2. Datos del usuario (estado, tablespace, fecha de creacion) ===
SELECT username, account_status, default_tablespace, temporary_tablespace, created FROM user_users;

PROMPT === 3. Roles concedidos al usuario ===
SELECT * FROM user_role_privs;

PROMPT === 4. Privilegios de sistema ===
SELECT * FROM user_sys_privs;

PROMPT === 5. Privilegios sobre objetos de otros usuarios ===
SELECT grantee, owner, table_name, grantor, privilege FROM user_tab_privs ORDER BY table_name, privilege;

PROMPT === 6. Cuota en el tablespace ===
SELECT tablespace_name, bytes, max_bytes FROM user_ts_quotas;

PROMPT === 7. Version del servidor ===
SELECT banner FROM v$version;

PROMPT === 8. Objetos del usuario por tipo ===
SELECT object_type, COUNT(*) AS cantidad FROM user_objects GROUP BY object_type ORDER BY object_type;

PROMPT === 9. Tablas ===
SELECT table_name FROM user_tables ORDER BY table_name;

PROMPT === 10. Objetos invalidos (debe salir sin filas) ===
SELECT object_name, object_type, status FROM user_objects WHERE status <> 'VALID';

PROMPT === 11. Procedimientos, funciones y disparadores ===
SELECT object_type, object_name, status FROM user_objects
 WHERE object_type IN ('PROCEDURE', 'FUNCTION', 'TRIGGER', 'VIEW') ORDER BY object_type, object_name;

PROMPT === 12. Limites del perfil asignado al usuario ===
SELECT resource_name, limit FROM user_resource_limits;

SPOOL OFF
SET ECHO OFF
