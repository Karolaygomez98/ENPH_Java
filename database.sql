-- =====================================================================
-- ENPH - Cuadernillo 2 | Script para Oracle Database 10g Express
-- Ejecutar conectado como el usuario ENPH (no como SYS ni SYSTEM).
-- =====================================================================

-- (Opcional) Para volver a ejecutar el script desde cero, quite los "--":
-- DROP TABLE gasto_diario;
-- DROP TABLE hogares;
-- DROP TABLE encuestadores;
-- DROP SEQUENCE seq_gasto_diario;

-- 1. Tabla Encuestador
CREATE TABLE encuestadores (
    usuario VARCHAR2(50) PRIMARY KEY,
    nombre_completo VARCHAR2(100) NOT NULL,
    rol VARCHAR2(30) DEFAULT 'Encuestador'
);

-- 2. Tabla Hogar
CREATE TABLE hogares (
    id_hogar VARCHAR2(20) PRIMARY KEY,
    codigo_ubicacion VARCHAR2(50) NOT NULL,
    jefe_hogar VARCHAR2(100) NOT NULL,
    direccion VARCHAR2(150) NOT NULL,
    estado VARCHAR2(30) DEFAULT 'En proceso'
);

-- 3. Tabla Cuadernillo 2: Gastos Diarios
--    (el nombre coincide con el que usa GastoDiarioDAO: gasto_diario)
--    dia: 1 a 14 del periodo de recoleccion; categoria: alimentos, transporte, etc.
CREATE TABLE gasto_diario (
    id_gasto NUMBER PRIMARY KEY,
    id_hogar VARCHAR2(20) NOT NULL REFERENCES hogares(id_hogar) ON DELETE CASCADE,
    dia NUMBER(2) DEFAULT 1 NOT NULL CHECK (dia BETWEEN 1 AND 14),
    categoria VARCHAR2(50),
    descripcion_producto VARCHAR2(150) NOT NULL,
    cantidad NUMBER(10,2) NOT NULL,
    unidad_medida VARCHAR2(30) NOT NULL,
    valor_pagado NUMBER(12,2) NOT NULL,
    lugar_compra VARCHAR2(100) NOT NULL,
    fecha_registro TIMESTAMP DEFAULT SYSTIMESTAMP
);

-- Oracle 10g no tiene SERIAL ni IDENTITY: el consecutivo se genera con secuencia + trigger
CREATE SEQUENCE seq_gasto_diario START WITH 1 INCREMENT BY 1 NOCACHE;

CREATE OR REPLACE TRIGGER trg_gasto_diario_id
BEFORE INSERT ON gasto_diario
FOR EACH ROW
BEGIN
  IF :NEW.id_gasto IS NULL THEN
    SELECT seq_gasto_diario.NEXTVAL INTO :NEW.id_gasto FROM dual;
  END IF;
END;
/

-- Registros de prueba iniciales
INSERT INTO encuestadores (usuario, nombre_completo, rol)
VALUES ('kdgomez', 'Karolay Daniela Gomez', 'Encuestador');

INSERT INTO hogares (id_hogar, codigo_ubicacion, jefe_hogar, direccion, estado)
VALUES ('HOG-68001-014', '68001', 'Maria Gomez', 'Calle 12 # 34-56 - Cabecera', 'En proceso');

INSERT INTO gasto_diario (id_hogar, dia, categoria, descripcion_producto, cantidad, unidad_medida, valor_pagado, lugar_compra)
VALUES ('HOG-68001-014', 1, 'Alimentos', 'Arroz blanco', 1.00, 'Kg', 4200.00, 'Supermercado de barrio');

COMMIT;

-- Para comprobar, ejecute por separado:
-- SELECT table_name FROM user_tables;
-- SELECT * FROM gasto_diario;