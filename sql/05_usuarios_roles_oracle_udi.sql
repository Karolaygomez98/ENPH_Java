-- =====================================================================
-- ENPH - Usuarios y roles de la base de datos (RF20) - VERSION PARA EL SERVIDOR DEL UDI
-- Las tablas, procedimientos, funciones y la vista pertenecen al usuario PROENPH.
-- Parte A (roles y privilegios): se ejecuta conectada como PROENPH, o como un
--   usuario con permiso para crear roles y entregar privilegios.
-- Parte B (usuarios): CREATE USER necesita permiso de administrador (DBA) en el UDI;
--   si PROENPH no lo tiene, esas sentencias las ejecuta quien administra el servidor.
-- La clave de los tres usuarios se pide por pantalla (no queda escrita en el archivo).
-- La clave debe empezar por una letra y no llevar espacios (ej. Enph2026).
-- =====================================================================
SET VERIFY OFF
ACCEPT clave CHAR PROMPT 'Clave para los usuarios de la base de datos: ' HIDE

-- Se borra lo anterior para poder ejecutar el script varias veces
BEGIN EXECUTE IMMEDIATE 'DROP USER enc_kgomez CASCADE';   EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP USER sup_dfuentes CASCADE'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP USER cri_earias CASCADE';   EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP ROLE rol_encuestador';        EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP ROLE rol_supervisor';         EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP ROLE rol_critico_digitador';  EXCEPTION WHEN OTHERS THEN NULL; END;
/

-- ---------- PARTE A: ROLES Y PRIVILEGIOS ----------
CREATE ROLE rol_encuestador;
CREATE ROLE rol_supervisor;
CREATE ROLE rol_critico_digitador;

-- ---------- Encuestador: diligencia los capitulos A, B, C, D y D1 y cierra el cuadernillo ----------
GRANT SELECT, INSERT, UPDATE ON proenph.hogar TO rol_encuestador;
GRANT SELECT ON proenph.cuadernillo2 TO rol_encuestador;
GRANT SELECT, INSERT, UPDATE, DELETE ON proenph.visita TO rol_encuestador;
GRANT SELECT, INSERT, UPDATE, DELETE ON proenph.caracteristica_gasto_alimento TO rol_encuestador;
GRANT SELECT, INSERT, UPDATE, DELETE ON proenph.dia_recoleccion TO rol_encuestador;
GRANT SELECT, INSERT, UPDATE, DELETE ON proenph.gasto_diario TO rol_encuestador;
GRANT SELECT, INSERT, UPDATE, DELETE ON proenph.comida_preparada TO rol_encuestador;
GRANT EXECUTE ON proenph.sp_cerrar_cuadernillo TO rol_encuestador;
GRANT EXECUTE ON proenph.fn_total_gastos_hogar TO rol_encuestador;
GRANT EXECUTE ON proenph.fn_total_comidas_hogar TO rol_encuestador;
GRANT EXECUTE ON proenph.fn_dias_con_gastos TO rol_encuestador;

-- ---------- Supervisor: consulta todo y revisa (finaliza o devuelve) ----------
GRANT SELECT ON proenph.hogar TO rol_supervisor;
GRANT SELECT ON proenph.cuadernillo2 TO rol_supervisor;
GRANT SELECT ON proenph.visita TO rol_supervisor;
GRANT SELECT ON proenph.caracteristica_gasto_alimento TO rol_supervisor;
GRANT SELECT ON proenph.dia_recoleccion TO rol_supervisor;
GRANT SELECT ON proenph.gasto_diario TO rol_supervisor;
GRANT SELECT ON proenph.comida_preparada TO rol_supervisor;
-- informe gerencial (RF26)
GRANT SELECT ON proenph.v_gasto_unificado TO rol_supervisor;
GRANT SELECT ON proenph.forma_pago TO rol_supervisor;
GRANT EXECUTE ON proenph.sp_revisar_cuadernillo TO rol_supervisor;
GRANT EXECUTE ON proenph.sp_validar_hogar TO rol_supervisor;
GRANT EXECUTE ON proenph.fn_total_gastos_hogar TO rol_supervisor;
GRANT EXECUTE ON proenph.fn_total_comidas_hogar TO rol_supervisor;
GRANT EXECUTE ON proenph.fn_dias_con_gastos TO rol_supervisor;

-- ---------- Critico-Digitador: solo lectura y validacion de consistencia ----------
GRANT SELECT ON proenph.hogar TO rol_critico_digitador;
GRANT SELECT ON proenph.cuadernillo2 TO rol_critico_digitador;
GRANT SELECT ON proenph.visita TO rol_critico_digitador;
GRANT SELECT ON proenph.caracteristica_gasto_alimento TO rol_critico_digitador;
GRANT SELECT ON proenph.dia_recoleccion TO rol_critico_digitador;
GRANT SELECT ON proenph.gasto_diario TO rol_critico_digitador;
GRANT SELECT ON proenph.comida_preparada TO rol_critico_digitador;
GRANT EXECUTE ON proenph.sp_validar_hogar TO rol_critico_digitador;
GRANT SELECT, INSERT ON proenph.reporte_critico TO rol_critico_digitador;
GRANT SELECT ON proenph.reporte_critico TO rol_encuestador, rol_supervisor;
GRANT SELECT ON proenph.usuario TO rol_encuestador, rol_supervisor, rol_critico_digitador;

-- Catalogos: lectura para los tres roles
GRANT SELECT ON proenph.grupo_alimento TO rol_encuestador, rol_supervisor, rol_critico_digitador;
GRANT SELECT ON proenph.forma_pago TO rol_encuestador, rol_supervisor, rol_critico_digitador;
GRANT SELECT ON proenph.tipo_comida TO rol_encuestador, rol_supervisor, rol_critico_digitador;
GRANT SELECT ON proenph.tipo_gasto TO rol_encuestador, rol_supervisor, rol_critico_digitador;
GRANT SELECT ON proenph.frecuencia_compra TO rol_encuestador, rol_supervisor, rol_critico_digitador;
GRANT SELECT ON proenph.lugar_compra TO rol_encuestador, rol_supervisor, rol_critico_digitador;
GRANT SELECT ON proenph.forma_adquisicion TO rol_encuestador, rol_supervisor, rol_critico_digitador;
GRANT SELECT ON proenph.unidad_medida TO rol_encuestador, rol_supervisor, rol_critico_digitador;

-- ---------- PARTE B: USUARIOS ----------
CREATE USER enc_kgomez IDENTIFIED BY "&clave" DEFAULT TABLESPACE users;
GRANT CREATE SESSION, rol_encuestador TO enc_kgomez;

CREATE USER sup_dfuentes IDENTIFIED BY "&clave" DEFAULT TABLESPACE users;
GRANT CREATE SESSION, rol_supervisor TO sup_dfuentes;

CREATE USER cri_earias IDENTIFIED BY "&clave" DEFAULT TABLESPACE users;
GRANT CREATE SESSION, rol_critico_digitador TO cri_earias;

-- ---------- COMPROBACION ----------
SELECT username FROM all_users WHERE username IN ('ENC_KGOMEZ', 'SUP_DFUENTES', 'CRI_EARIAS') ORDER BY username;
SELECT grantee, granted_role FROM dba_role_privs WHERE grantee IN ('ENC_KGOMEZ', 'SUP_DFUENTES', 'CRI_EARIAS') ORDER BY grantee;

-- ---------- PRUEBA (para el video) ----------
-- connect sup_dfuentes/CLAVE
-- SELECT COUNT(*) FROM proenph.gasto_diario;                 -- permitido: el supervisor consulta
-- DELETE FROM proenph.gasto_diario;                          -- rechazado: ORA-01031 privilegios insuficientes
-- connect cri_earias/CLAVE
-- INSERT INTO proenph.visita (fecha, hora_inicio, hora_fin, resultado, id_hogar) VALUES (SYSDATE,'08:00','09:00','Completa','HOG-68001-014');   -- rechazado
