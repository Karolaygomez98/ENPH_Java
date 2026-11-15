-- =====================================================================
-- ENPH - Cuadernillo 2 | Script de base de datos para Oracle 10g
-- Implementa el Modelo Relacional del documento (17 tablas):
--   10 tablas principales: usuario, hogar, cuadernillo2, visita,
--   caracteristica_gasto_alimento, grupo_alimento, dia_recoleccion,
--   gasto_diario, comida_preparada, reporte_critico
--   7 catalogos: forma_pago, tipo_comida, tipo_gasto, frecuencia_compra,
--   lugar_compra, forma_adquisicion, unidad_medida
--
-- COMO EJECUTARLO (SQL*Plus, conectado con SU usuario, no SYS ni SYSTEM):
--   @"C:\ruta\de\la\carpeta\database.sql"
--
-- ATENCION: la seccion 0 BORRA las tablas anteriores del proyecto y las
-- vuelve a crear. Sirve para volver a ejecutar el script desde cero.
-- (Archivo solo con caracteres ASCII: las tildes y la enie se escriben con
-- UNISTR para que funcione desde cualquier computador.)
-- =====================================================================
SET DEFINE OFF
SET ECHO OFF

-- 0. LIMPIEZA (tablas anteriores y nuevas; si no existen, no pasa nada)
BEGIN EXECUTE IMMEDIATE 'DROP VIEW v_gasto_unificado'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE reporte_critico CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE gasto_diario CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE comida_preparada CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE caracteristica_gasto_alimento CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE dia_recoleccion CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE visita CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE cuadernillo2 CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE hogar CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE usuario CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE grupo_alimento CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE forma_pago CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE tipo_comida CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE tipo_gasto CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE frecuencia_compra CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE lugar_compra CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE forma_adquisicion CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE unidad_medida CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE hogares CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE encuestadores CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_reporte_critico'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_gasto_diario'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_comida_preparada'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_caracteristica'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_dia_recoleccion'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE seq_visita'; EXCEPTION WHEN OTHERS THEN NULL; END;
/

-- 1. CATALOGOS
CREATE TABLE forma_pago (
    id_forma_pago VARCHAR2(10) PRIMARY KEY,
    nombre VARCHAR2(100) NOT NULL
);
INSERT INTO forma_pago (id_forma_pago, nombre) VALUES ('EFE', 'Efectivo');
INSERT INTO forma_pago (id_forma_pago, nombre) VALUES ('TDB', 'Tarjeta d' || UNISTR('\00E9') || 'bito');
INSERT INTO forma_pago (id_forma_pago, nombre) VALUES ('TCR', 'Tarjeta cr' || UNISTR('\00E9') || 'dito');
INSERT INTO forma_pago (id_forma_pago, nombre) VALUES ('FIA', 'Fiado');
INSERT INTO forma_pago (id_forma_pago, nombre) VALUES ('BON', 'Bonos');
INSERT INTO forma_pago (id_forma_pago, nombre) VALUES ('OTR', 'Otra');
CREATE TABLE tipo_comida (
    id_tipo_comida VARCHAR2(10) PRIMARY KEY,
    nombre VARCHAR2(100) NOT NULL
);
INSERT INTO tipo_comida (id_tipo_comida, nombre) VALUES ('DES', 'Desayuno');
INSERT INTO tipo_comida (id_tipo_comida, nombre) VALUES ('ALM', 'Almuerzo');
INSERT INTO tipo_comida (id_tipo_comida, nombre) VALUES ('CEN', 'Comida / Cena');
INSERT INTO tipo_comida (id_tipo_comida, nombre) VALUES ('REF', 'Refrigerio / Bebidas');
CREATE TABLE tipo_gasto (
    id_tipo_gasto VARCHAR2(10) PRIMARY KEY,
    nombre VARCHAR2(100) NOT NULL
);
INSERT INTO tipo_gasto (id_tipo_gasto, nombre) VALUES ('HOG', 'Hogar');
INSERT INTO tipo_gasto (id_tipo_gasto, nombre) VALUES ('PER', 'Personal');
CREATE TABLE frecuencia_compra (
    id_frecuencia_compra VARCHAR2(10) PRIMARY KEY,
    nombre VARCHAR2(100) NOT NULL
);
INSERT INTO frecuencia_compra (id_frecuencia_compra, nombre) VALUES ('1', 'Nunca');
INSERT INTO frecuencia_compra (id_frecuencia_compra, nombre) VALUES ('2', 'Diario');
INSERT INTO frecuencia_compra (id_frecuencia_compra, nombre) VALUES ('2.1', 'Varias veces por semana');
INSERT INTO frecuencia_compra (id_frecuencia_compra, nombre) VALUES ('3', 'Semanal');
INSERT INTO frecuencia_compra (id_frecuencia_compra, nombre) VALUES ('4', 'Quincenal');
INSERT INTO frecuencia_compra (id_frecuencia_compra, nombre) VALUES ('5', 'Mensual');
INSERT INTO frecuencia_compra (id_frecuencia_compra, nombre) VALUES ('6', 'Bimestral');
INSERT INTO frecuencia_compra (id_frecuencia_compra, nombre) VALUES ('7', 'Trimestral / Espor' || UNISTR('\00E1') || 'dico');
CREATE TABLE lugar_compra (
    id_lugar_compra VARCHAR2(10) PRIMARY KEY,
    nombre VARCHAR2(100) NOT NULL
);
INSERT INTO lugar_compra (id_lugar_compra, nombre) VALUES ('TIE', 'Tienda de barrio');
INSERT INTO lugar_compra (id_lugar_compra, nombre) VALUES ('SUP', 'Supermercado');
INSERT INTO lugar_compra (id_lugar_compra, nombre) VALUES ('PLA', 'Plaza de mercado');
INSERT INTO lugar_compra (id_lugar_compra, nombre) VALUES ('VEN', 'Vendedor ambulante');
INSERT INTO lugar_compra (id_lugar_compra, nombre) VALUES ('PAN', 'Panader' || UNISTR('\00ED') || 'a');
INSERT INTO lugar_compra (id_lugar_compra, nombre) VALUES ('DRO', 'Droguer' || UNISTR('\00ED') || 'a');
INSERT INTO lugar_compra (id_lugar_compra, nombre) VALUES ('NOE', 'No especificado');
CREATE TABLE forma_adquisicion (
    id_forma_adquisicion VARCHAR2(10) PRIMARY KEY,
    nombre VARCHAR2(100) NOT NULL
);
INSERT INTO forma_adquisicion (id_forma_adquisicion, nombre) VALUES ('COM', 'Comprado');
INSERT INTO forma_adquisicion (id_forma_adquisicion, nombre) VALUES ('PRO', 'Producido en el hogar');
INSERT INTO forma_adquisicion (id_forma_adquisicion, nombre) VALUES ('OBS', 'Obsequio');
INSERT INTO forma_adquisicion (id_forma_adquisicion, nombre) VALUES ('ESP', 'Pago en especie');
INSERT INTO forma_adquisicion (id_forma_adquisicion, nombre) VALUES ('OTR', 'Otra');
CREATE TABLE unidad_medida (
    id_unidad_medida VARCHAR2(10) PRIMARY KEY,
    nombre VARCHAR2(100) NOT NULL
);
INSERT INTO unidad_medida (id_unidad_medida, nombre) VALUES ('UNI', 'Unidad');
INSERT INTO unidad_medida (id_unidad_medida, nombre) VALUES ('KIL', 'Kilos');
INSERT INTO unidad_medida (id_unidad_medida, nombre) VALUES ('GRA', 'Gramos');
INSERT INTO unidad_medida (id_unidad_medida, nombre) VALUES ('LIB', 'Libras');
INSERT INTO unidad_medida (id_unidad_medida, nombre) VALUES ('LIT', 'Litros');
INSERT INTO unidad_medida (id_unidad_medida, nombre) VALUES ('MIL', 'Mililitros');
INSERT INTO unidad_medida (id_unidad_medida, nombre) VALUES ('PAQ', 'Paquete');
INSERT INTO unidad_medida (id_unidad_medida, nombre) VALUES ('DOC', 'Docena');
INSERT INTO unidad_medida (id_unidad_medida, nombre) VALUES ('SER', 'Servicio');

CREATE TABLE grupo_alimento (
    id_grupo_alimento VARCHAR2(10) PRIMARY KEY,
    nombre VARCHAR2(300) NOT NULL
);
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('01', 'Pan, arepas, bollos y almoj' || UNISTR('\00E1') || 'banas');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('02', 'Galletas de sal y de dulce');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('03', 'Arroz, pastas alimenticias, avena, ma' || UNISTR('\00ED') || 'z, harinas y otros cereales (para el desayuno)');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('04', 'Carne de res, cerdo, hueso y v' || UNISTR('\00ED') || 'sceras');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('05', 'Pollo, otras aves y menudencias');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('06', 'Salchichas, jam' || UNISTR('\00F3') || 'n, mortadela, salchich' || UNISTR('\00F3') || 'n y otras carnes fr' || UNISTR('\00ED') || 'as preparadas (incluidos los embutidos vegetarianos)');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('07', 'Pescado de r' || UNISTR('\00ED') || 'o, mar y otros productos marinos (frescos, congelados y enlatados)');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('08', 'Leche (animal y vegetal)');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('09', 'Queso (animal y vegetal)');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('10', 'Otros derivados de la leche: yogur, kumis, etc.');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('11', 'Huevos');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('12', 'Aceites y grasas: aceite, manteca, mantequilla, margarina, etc.');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('13', 'Frutas');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('14', 'Verduras y hortalizas: tomate, cebolla, arveja, espinaca, apio, zanahoria, etc.');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('15', 'Granos secos: fr' || UNISTR('\00ED') || 'jol, lenteja, garbanzo, etc.');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('16', 'Enlatados y encurtidos: arveja, fr' || UNISTR('\00ED') || 'jol, ma' || UNISTR('\00ED') || 'z, esp' || UNISTR('\00E1') || 'rragos, zanahoria, habichuela, etc.');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('17', 'Pl' || UNISTR('\00E1') || 'tano, yuca, arracacha, ' || UNISTR('\00F1') || 'ame, papa y otros tub' || UNISTR('\00E9') || 'rculos');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('18', 'Az' || UNISTR('\00FA') || 'car, panela y otros endulzantes');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('19', 'Mermeladas, arequipe, bocadillos, compotas, dulces, helados, chocolatinas, ponquecitos, etc.');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('20', 'Salsas, mayonesa, mostaza, vinagre, etc.');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('21', 'Sal y condimentos');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('22', 'Caf' || UNISTR('\00E9') || ', chocolate, otras bebidas con cacao (milo, chocolisto), t' || UNISTR('\00E9') || ' y hierbas arom' || UNISTR('\00E1') || 'ticas');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('23', 'Papas fritas, chitos, maicitos, patacones, besitos, man' || UNISTR('\00ED') || ', etc.');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('24', 'Agua, gaseosas, refrescos, jugos, t' || UNISTR('\00E9') || ' fr' || UNISTR('\00ED') || 'o y otras bebidas no alcoh' || UNISTR('\00F3') || 'licas');
INSERT INTO grupo_alimento (id_grupo_alimento, nombre) VALUES ('25', 'Alimentos y bebidas preparados fuera del hogar, para consumir dentro o fuera del hogar');

-- 2. TABLAS PRINCIPALES
CREATE TABLE usuario (
    id_usuario VARCHAR2(50) PRIMARY KEY,
    nombre VARCHAR2(100) NOT NULL,
    rol VARCHAR2(30) NOT NULL,
    login VARCHAR2(30),
    clave_hash VARCHAR2(100),
    clave_sal VARCHAR2(60),
    CONSTRAINT ck_usuario_rol CHECK (rol IN ('Encuestador', 'Supervisor', 'Critico-Digitador')),
    CONSTRAINT uq_usuario_login UNIQUE (login)
);

CREATE TABLE hogar (
    id_hogar VARCHAR2(50) PRIMARY KEY,
    direccion VARCHAR2(250) NOT NULL,
    responsable VARCHAR2(150),
    orden_responsable NUMBER(2),
    id_usuario VARCHAR2(50),
    CONSTRAINT ck_hogar_orden CHECK (orden_responsable BETWEEN 1 AND 99),
    CONSTRAINT fk_hogar_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
);

CREATE TABLE cuadernillo2 (
    id_cuadernillo VARCHAR2(50) PRIMARY KEY,
    estado VARCHAR2(20) DEFAULT 'Pendiente' NOT NULL,
    fecha_inicio DATE NOT NULL,
    id_hogar VARCHAR2(50) NOT NULL,
    id_forma_pago VARCHAR2(10),
    gastos_fiesta CHAR(1),
    revisado CHAR(1) DEFAULT 'N',
    resultado VARCHAR2(3),
    valor_total_mercado NUMBER(12,2),
    fecha_cierre DATE,
    id_usuario_cierre VARCHAR2(50),
    fecha_revision DATE,
    id_usuario_revision VARCHAR2(50),
    CONSTRAINT ck_cuadernillo_estado CHECK (estado IN ('Pendiente', 'En Proceso', 'Completado')),
    CONSTRAINT ck_cuadernillo_fiesta CHECK (gastos_fiesta IN ('S', 'N')),
    CONSTRAINT ck_cuadernillo_revisado CHECK (revisado IN ('S', 'N')),
    CONSTRAINT ck_cuadernillo_resultado CHECK (resultado IN ('EC', 'EI', 'NH', 'AT', 'R', 'V', 'OC', 'O')),
    CONSTRAINT ck_cuadernillo_mercado CHECK (valor_total_mercado >= 0),
    CONSTRAINT uq_cuadernillo_hogar UNIQUE (id_hogar),
    CONSTRAINT fk_cuadernillo_hogar FOREIGN KEY (id_hogar) REFERENCES hogar (id_hogar) ON DELETE CASCADE,
    CONSTRAINT fk_cuadernillo_pago FOREIGN KEY (id_forma_pago) REFERENCES forma_pago (id_forma_pago),
    CONSTRAINT fk_cuadernillo_ucierre FOREIGN KEY (id_usuario_cierre) REFERENCES usuario (id_usuario),
    CONSTRAINT fk_cuadernillo_urevision FOREIGN KEY (id_usuario_revision) REFERENCES usuario (id_usuario)
);

CREATE TABLE visita (
    id_visita NUMBER PRIMARY KEY,
    fecha DATE NOT NULL,
    hora_inicio VARCHAR2(5) NOT NULL,
    hora_fin VARCHAR2(5) NOT NULL,
    resultado VARCHAR2(100) NOT NULL,
    partes_pendientes VARCHAR2(500),
    fecha_proxima DATE,
    hora_proxima VARCHAR2(5),
    id_hogar VARCHAR2(50) NOT NULL,
    CONSTRAINT fk_visita_hogar FOREIGN KEY (id_hogar) REFERENCES hogar (id_hogar) ON DELETE CASCADE
);

CREATE TABLE caracteristica_gasto_alimento (
    id_caracteristica NUMBER PRIMARY KEY,
    frecuencia_habitual VARCHAR2(50),
    valor_ultima_compra NUMBER(12,2),
    comprara_durante_periodo CHAR(1),
    valor_total_mercado NUMBER(12,2),
    id_hogar VARCHAR2(50) NOT NULL,
    id_grupo_alimento VARCHAR2(10) NOT NULL,
    id_frecuencia_compra VARCHAR2(10),
    CONSTRAINT ck_caract_comprara CHECK (comprara_durante_periodo IN ('S', 'N')),
    CONSTRAINT uq_caract_hogar_grupo UNIQUE (id_hogar, id_grupo_alimento),
    CONSTRAINT fk_caract_hogar FOREIGN KEY (id_hogar) REFERENCES hogar (id_hogar) ON DELETE CASCADE,
    CONSTRAINT fk_caract_grupo FOREIGN KEY (id_grupo_alimento) REFERENCES grupo_alimento (id_grupo_alimento),
    CONSTRAINT fk_caract_frecuencia FOREIGN KEY (id_frecuencia_compra) REFERENCES frecuencia_compra (id_frecuencia_compra)
);

CREATE TABLE dia_recoleccion (
    id_dia NUMBER PRIMARY KEY,
    numero_dia NUMBER(2) NOT NULL,
    fecha DATE NOT NULL,
    id_cuadernillo VARCHAR2(50) NOT NULL,
    CONSTRAINT ck_dia_numero CHECK (numero_dia BETWEEN 1 AND 14),
    CONSTRAINT uq_dia_cuadernillo UNIQUE (id_cuadernillo, numero_dia),
    CONSTRAINT fk_dia_cuadernillo FOREIGN KEY (id_cuadernillo) REFERENCES cuadernillo2 (id_cuadernillo) ON DELETE CASCADE
);

CREATE TABLE gasto_diario (
    id_gasto NUMBER PRIMARY KEY,
    articulo VARCHAR2(200) NOT NULL,
    cantidad NUMBER(10,2) NOT NULL,
    valor_pagado NUMBER(12,2) NOT NULL,
    equivalencia NUMBER(10,2),
    id_dia NUMBER NOT NULL,
    id_unidad_medida VARCHAR2(10) NOT NULL,
    id_forma_adquisicion VARCHAR2(10) DEFAULT 'COM' NOT NULL,
    id_lugar_compra VARCHAR2(10) NOT NULL,
    id_tipo_gasto VARCHAR2(10) NOT NULL,
    id_frecuencia_compra VARCHAR2(10),
    CONSTRAINT ck_gasto_valor CHECK (valor_pagado > 0),
    CONSTRAINT fk_gasto_dia FOREIGN KEY (id_dia) REFERENCES dia_recoleccion (id_dia) ON DELETE CASCADE,
    CONSTRAINT fk_gasto_unidad FOREIGN KEY (id_unidad_medida) REFERENCES unidad_medida (id_unidad_medida),
    CONSTRAINT fk_gasto_adquisicion FOREIGN KEY (id_forma_adquisicion) REFERENCES forma_adquisicion (id_forma_adquisicion),
    CONSTRAINT fk_gasto_lugar FOREIGN KEY (id_lugar_compra) REFERENCES lugar_compra (id_lugar_compra),
    CONSTRAINT fk_gasto_tipo FOREIGN KEY (id_tipo_gasto) REFERENCES tipo_gasto (id_tipo_gasto),
    CONSTRAINT fk_gasto_frecuencia FOREIGN KEY (id_frecuencia_compra) REFERENCES frecuencia_compra (id_frecuencia_compra)
);

CREATE TABLE comida_preparada (
    id_comida NUMBER PRIMARY KEY,
    descripcion VARCHAR2(200) NOT NULL,
    valor_pagado NUMBER(12,2) NOT NULL,
    cantidad NUMBER(5) DEFAULT 1 NOT NULL,
    domicilio CHAR(1) DEFAULT 'N' NOT NULL,
    id_dia NUMBER NOT NULL,
    id_tipo_comida VARCHAR2(10) NOT NULL,
    id_lugar_compra VARCHAR2(10),
    id_frecuencia_compra VARCHAR2(10),
    id_tipo_gasto VARCHAR2(10),
    id_forma_adquisicion VARCHAR2(10),
    CONSTRAINT ck_comida_valor CHECK (valor_pagado > 0),
    CONSTRAINT ck_comida_domicilio CHECK (domicilio IN ('S', 'N')),
    CONSTRAINT fk_comida_dia FOREIGN KEY (id_dia) REFERENCES dia_recoleccion (id_dia) ON DELETE CASCADE,
    CONSTRAINT fk_comida_tipo_comida FOREIGN KEY (id_tipo_comida) REFERENCES tipo_comida (id_tipo_comida),
    CONSTRAINT fk_comida_lugar FOREIGN KEY (id_lugar_compra) REFERENCES lugar_compra (id_lugar_compra),
    CONSTRAINT fk_comida_frecuencia FOREIGN KEY (id_frecuencia_compra) REFERENCES frecuencia_compra (id_frecuencia_compra),
    CONSTRAINT fk_comida_tipo_gasto FOREIGN KEY (id_tipo_gasto) REFERENCES tipo_gasto (id_tipo_gasto),
    CONSTRAINT fk_comida_adquisicion FOREIGN KEY (id_forma_adquisicion) REFERENCES forma_adquisicion (id_forma_adquisicion)
);

CREATE TABLE reporte_critico (
    id_reporte NUMBER PRIMARY KEY,
    id_hogar VARCHAR2(50) NOT NULL,
    id_usuario VARCHAR2(50) NOT NULL,
    fecha_reporte DATE DEFAULT SYSDATE NOT NULL,
    regla VARCHAR2(50) NOT NULL,
    detalle VARCHAR2(500) NOT NULL,
    CONSTRAINT fk_reporte_hogar FOREIGN KEY (id_hogar) REFERENCES hogar (id_hogar) ON DELETE CASCADE,
    CONSTRAINT fk_reporte_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
);

-- 3. SECUENCIAS Y DISPARADORES (Oracle 10g no tiene SERIAL ni IDENTITY)
CREATE SEQUENCE seq_visita START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_caracteristica START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_dia_recoleccion START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_gasto_diario START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_comida_preparada START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_reporte_critico START WITH 1 INCREMENT BY 1 NOCACHE;

CREATE OR REPLACE TRIGGER trg_reporte_critico_id
BEFORE INSERT ON reporte_critico
FOR EACH ROW
BEGIN
  IF :NEW.id_reporte IS NULL THEN
    SELECT seq_reporte_critico.NEXTVAL INTO :NEW.id_reporte FROM dual;
  END IF;
END;
/
CREATE OR REPLACE TRIGGER trg_visita_id
BEFORE INSERT ON visita
FOR EACH ROW
BEGIN
  IF :NEW.id_visita IS NULL THEN
    SELECT seq_visita.NEXTVAL INTO :NEW.id_visita FROM dual;
  END IF;
END;
/
CREATE OR REPLACE TRIGGER trg_caracteristica_id
BEFORE INSERT ON caracteristica_gasto_alimento
FOR EACH ROW
BEGIN
  IF :NEW.id_caracteristica IS NULL THEN
    SELECT seq_caracteristica.NEXTVAL INTO :NEW.id_caracteristica FROM dual;
  END IF;
END;
/
CREATE OR REPLACE TRIGGER trg_gasto_diario_id
BEFORE INSERT ON gasto_diario
FOR EACH ROW
BEGIN
  IF :NEW.id_gasto IS NULL THEN
    SELECT seq_gasto_diario.NEXTVAL INTO :NEW.id_gasto FROM dual;
  END IF;
END;
/
CREATE OR REPLACE TRIGGER trg_comida_preparada_id
BEFORE INSERT ON comida_preparada
FOR EACH ROW
BEGIN
  IF :NEW.id_comida IS NULL THEN
    SELECT seq_comida_preparada.NEXTVAL INTO :NEW.id_comida FROM dual;
  END IF;
END;
/
CREATE OR REPLACE TRIGGER trg_dia_habilitado
BEFORE INSERT ON dia_recoleccion
FOR EACH ROW
DECLARE
  v_inicio DATE;
BEGIN
  SELECT fecha_inicio INTO v_inicio FROM cuadernillo2 WHERE id_cuadernillo = :NEW.id_cuadernillo;
  IF TRUNC(v_inicio) + :NEW.numero_dia - 1 > TRUNC(SYSDATE) THEN
    RAISE_APPLICATION_ERROR(-20010, 'El dia ' || :NEW.numero_dia || ' aun no esta habilitado.');
  END IF;
END;
/
CREATE OR REPLACE TRIGGER trg_dia_recoleccion_id
BEFORE INSERT ON dia_recoleccion
FOR EACH ROW
BEGIN
  IF :NEW.id_dia IS NULL THEN
    SELECT seq_dia_recoleccion.NEXTVAL INTO :NEW.id_dia FROM dual;
  END IF;
  IF :NEW.fecha IS NULL THEN
    SELECT fecha_inicio + :NEW.numero_dia - 1 INTO :NEW.fecha
      FROM cuadernillo2 WHERE id_cuadernillo = :NEW.id_cuadernillo;
  END IF;
END;
/

-- 4. DATOS INICIALES
-- La clave queda vacia: cada persona crea la suya al ingresar por primera vez a la aplicacion.
INSERT INTO usuario (id_usuario, nombre, rol, login) VALUES ('ENC-001', 'Karolay Daniela G' || UNISTR('\00F3') || 'mez Pedraza', 'Encuestador', 'kgomez');
INSERT INTO usuario (id_usuario, nombre, rol, login) VALUES ('SUP-001', 'Diego Andr' || UNISTR('\00E9') || 's Fuentes Lizcano', 'Supervisor', 'dfuentes');
INSERT INTO usuario (id_usuario, nombre, rol, login) VALUES ('CRI-001', 'Edwin Andr' || UNISTR('\00E9') || 's Arias Pineda', 'Critico-Digitador', 'earias');
INSERT INTO hogar (id_hogar, direccion, responsable, id_usuario) VALUES ('HOG-68001-014', 'Calle 12 # 34-56 - Cabecera', 'Mar' || UNISTR('\00ED') || 'a G' || UNISTR('\00F3') || 'mez', 'ENC-001');
INSERT INTO hogar (id_hogar, direccion, responsable, id_usuario) VALUES ('HOG-68001-015', 'Carrera 27 # 45-12 - Centro', 'Carlos Rodr' || UNISTR('\00ED') || 'guez', 'ENC-001');
INSERT INTO hogar (id_hogar, direccion, responsable, id_usuario) VALUES ('HOG-68001-016', 'Calle 36 # 15-20 - San Francisco', 'Ana Luc' || UNISTR('\00ED') || 'a Mart' || UNISTR('\00ED') || 'nez', 'ENC-001');
INSERT INTO hogar (id_hogar, direccion, responsable, id_usuario) VALUES ('HOG-68001-017', 'Diagonal 15 # 56-08 - La Concordia', 'Jorge Eliecer Prado', 'ENC-001');
INSERT INTO hogar (id_hogar, direccion, responsable, id_usuario) VALUES ('HOG-68001-018', 'Transversal 78 # 21-34 - Provenza', 'Esperanza Caicedo', 'ENC-001');
INSERT INTO hogar (id_hogar, direccion, responsable, id_usuario) VALUES ('HOG-68001-019', 'Calle 105 # 24-11 - Ca' || UNISTR('\00F1') || 'averal', 'Pedro Antonio Su' || UNISTR('\00E1') || 'rez', 'ENC-001');
INSERT INTO hogar (id_hogar, direccion, responsable, id_usuario) VALUES ('HOG-68001-020', 'Carrera 33 # 52-40 - Cabecera', 'Claudia Patricia Reyes', 'ENC-001');
INSERT INTO hogar (id_hogar, direccion, responsable, id_usuario) VALUES ('HOG-68001-021', 'Calle 45 # 9-18 - Real de Minas', 'Hernando Vargas', 'ENC-001');
INSERT INTO cuadernillo2 (id_cuadernillo, estado, fecha_inicio, id_hogar, id_forma_pago, gastos_fiesta) VALUES ('CUA-HOG-68001-014', 'En Proceso', TRUNC(SYSDATE) - 15, 'HOG-68001-014', NULL, NULL);
INSERT INTO cuadernillo2 (id_cuadernillo, estado, fecha_inicio, id_hogar, id_forma_pago, gastos_fiesta) VALUES ('CUA-HOG-68001-015', 'Completado', TRUNC(SYSDATE) - 20, 'HOG-68001-015', 'EFE', 'N');
INSERT INTO cuadernillo2 (id_cuadernillo, estado, fecha_inicio, id_hogar, id_forma_pago, gastos_fiesta) VALUES ('CUA-HOG-68001-016', 'Pendiente', TRUNC(SYSDATE) - 15, 'HOG-68001-016', NULL, NULL);
INSERT INTO cuadernillo2 (id_cuadernillo, estado, fecha_inicio, id_hogar, id_forma_pago, gastos_fiesta) VALUES ('CUA-HOG-68001-017', 'Pendiente', TRUNC(SYSDATE), 'HOG-68001-017', NULL, NULL);
INSERT INTO cuadernillo2 (id_cuadernillo, estado, fecha_inicio, id_hogar, id_forma_pago, gastos_fiesta) VALUES ('CUA-HOG-68001-018', 'Completado', TRUNC(SYSDATE) - 20, 'HOG-68001-018', 'EFE', 'N');
INSERT INTO cuadernillo2 (id_cuadernillo, estado, fecha_inicio, id_hogar, id_forma_pago, gastos_fiesta) VALUES ('CUA-HOG-68001-019', 'En Proceso', TRUNC(SYSDATE), 'HOG-68001-019', NULL, NULL);
INSERT INTO cuadernillo2 (id_cuadernillo, estado, fecha_inicio, id_hogar, id_forma_pago, gastos_fiesta) VALUES ('CUA-HOG-68001-020', 'En Proceso', TRUNC(SYSDATE), 'HOG-68001-020', NULL, NULL);
INSERT INTO cuadernillo2 (id_cuadernillo, estado, fecha_inicio, id_hogar, id_forma_pago, gastos_fiesta) VALUES ('CUA-HOG-68001-021', 'Pendiente', TRUNC(SYSDATE), 'HOG-68001-021', NULL, NULL);

-- Numero de orden del responsable y resultado de los cuadernillos ya completados
UPDATE hogar SET orden_responsable = 1;
UPDATE cuadernillo2 SET resultado = 'EC', fecha_cierre = SYSDATE - 5, id_usuario_cierre = 'ENC-001' WHERE estado = 'Completado';

COMMIT;

-- 5. COMPROBACION (ejecute por separado)
-- SELECT table_name FROM user_tables ORDER BY table_name;          (deben ser 17)
-- SELECT COUNT(*) FROM grupo_alimento;                             (25)
-- SELECT id_hogar, responsable FROM hogar ORDER BY id_hogar;       (8 hogares)
-- SELECT object_name, status FROM user_objects WHERE object_type = 'TRIGGER';  (5 disparadores, VALID)

-- =====================================================================
-- RF13 - Categorias excluidas del Cuadernillo 2.
-- Devuelve el nombre de la categoria excluida a la que pertenece el articulo
-- (servicios publicos, vestuario/calzado, aseo/cuidado personal,
-- muebles/electrodomesticos, salud, educacion) o NULL si el articulo es valido.
-- Compara palabras completas, sin importar mayusculas ni tildes.
-- =====================================================================
CREATE OR REPLACE FUNCTION fn_categoria_excluida (p_texto IN VARCHAR2)
RETURN VARCHAR2 IS
    v VARCHAR2(400);
    FUNCTION hay (p_lista IN VARCHAR2) RETURN BOOLEAN IS
    BEGIN
        RETURN REGEXP_LIKE(v, '(^|[^a-z])(' || p_lista || ')([^a-z]|$)');
    END;
BEGIN
    IF p_texto IS NULL THEN
        RETURN NULL;
    END IF;
    v := LOWER(TRANSLATE(p_texto, UNISTR('\00E1\00E9\00ED\00F3\00FA\00C1\00C9\00CD\00D3\00DA\00F1\00D1'),
                                  'aeiouAEIOUnN'));
    IF hay('luz|energia|electricidad|acueducto|alcantarillado|gas|internet|telefono|tv cable|servicio publico|servicios publicos|recibo de agua|factura de agua|recibo de luz') THEN
        RETURN 'servicios publicos';
    ELSIF hay('ropa|camisa|camisas|camiseta|camisetas|pantalon|pantalones|jean|jeans|falda|vestido|chaqueta|buso|medias|calzoncillo|brasier|zapato|zapatos|tenis|sandalia|sandalias|calzado|botas|gorra') THEN
        RETURN 'vestuario/calzado';
    ELSIF hay('aseo|jabon|shampoo|champu|acondicionador|crema dental|pasta dental|cepillo de dientes|desodorante|papel higienico|toallas? higienicas?|panales|detergente|blanqueador|cloro|suavizante|limpiador|desinfectante|crema corporal|perfume|maquillaje|afeitadora|protector solar') THEN
        RETURN 'aseo/cuidado personal';
    ELSIF hay('mueble|muebles|sofa|cama|colchon|mesa|silla|nevera|lavadora|televisor|licuadora|estufa|ventilador|plancha|microondas|computador') THEN
        RETURN 'muebles/electrodomesticos';
    ELSIF hay('medicina|medicinas|medicamento|medicamentos|pastillas|acetaminofen|consulta medica|cita medica|examen|laboratorio|vitaminas|jarabe|antibiotico|analgesico|tratamiento|hospital|clinica') THEN
        RETURN 'salud';
    ELSIF hay('matricula|pension escolar|colegio|universidad|utiles|cuaderno|cuadernos|libro|libros|curso|semestre|uniforme escolar') THEN
        RETURN 'educacion';
    END IF;
    RETURN NULL;
END fn_categoria_excluida;
/
SHOW ERRORS FUNCTION fn_categoria_excluida

-- =====================================================================
-- RF21 - Validar la consistencia de los datos de un hogar.
-- Procedimiento almacenado: devuelve un cursor con las inconsistencias
-- encontradas (regla y detalle). Si no devuelve filas, el hogar es consistente.
-- Ejecutar en SQL*Plus conectado como ENPH.
-- =====================================================================
CREATE OR REPLACE PROCEDURE sp_validar_hogar (
    p_id_hogar  IN  VARCHAR2,
    p_resultado OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_resultado FOR
        -- 1. Fechas dentro del periodo de 14 dias del cuadernillo
        SELECT 'Fechas' AS regla,
               'Dia ' || d.numero_dia || ': la fecha esta fuera del periodo de 14 dias' AS detalle
          FROM dia_recoleccion d
          JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo
         WHERE c.id_hogar = p_id_hogar
           AND (d.fecha < c.fecha_inicio OR d.fecha > c.fecha_inicio + 13)
        UNION ALL
        SELECT 'Fechas',
               'Visita ' || v.id_visita || ': la hora de fin no es posterior a la hora de inicio'
          FROM visita v
         WHERE v.id_hogar = p_id_hogar AND v.hora_fin <= v.hora_inicio
        UNION ALL
        SELECT 'Fechas',
               'Visita ' || v.id_visita || ': la proxima visita es anterior a la visita'
          FROM visita v
         WHERE v.id_hogar = p_id_hogar AND v.fecha_proxima IS NOT NULL AND v.fecha_proxima < v.fecha
        UNION ALL
        -- 2. Valores numericos
        SELECT 'Valores',
               'Gasto ' || g.id_gasto || ' (' || g.articulo || '): el valor o la cantidad no es mayor que cero'
          FROM gasto_diario g
          JOIN dia_recoleccion d ON d.id_dia = g.id_dia
          JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo
         WHERE c.id_hogar = p_id_hogar AND (g.valor_pagado <= 0 OR g.cantidad <= 0)
        UNION ALL
        SELECT 'Valores',
               'Gasto ' || g.id_gasto || ' (' || g.articulo || '): valor atipico, superior a 1.000.000'
          FROM gasto_diario g
          JOIN dia_recoleccion d ON d.id_dia = g.id_dia
          JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo
         WHERE c.id_hogar = p_id_hogar AND g.valor_pagado > 1000000
        UNION ALL
        SELECT 'Valores',
               'Comida fuera ' || m.id_comida || ' (' || m.descripcion || '): el valor no es mayor que cero'
          FROM comida_preparada m
          JOIN dia_recoleccion d ON d.id_dia = m.id_dia
          JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo
         WHERE c.id_hogar = p_id_hogar AND m.valor_pagado <= 0
        UNION ALL
        -- 3. Codigos utilizados
        SELECT 'Codigos',
               'El codigo del hogar no tiene el formato HOG-#####-###'
          FROM hogar
         WHERE id_hogar = p_id_hogar
           AND NOT REGEXP_LIKE(id_hogar, '^HOG-[0-9]{5}-[0-9]{3}$')
        UNION ALL
        SELECT 'Codigos',
               'Gasto ' || g.id_gasto || ' (' || g.articulo || '): lugar de compra sin especificar'
          FROM gasto_diario g
          JOIN dia_recoleccion d ON d.id_dia = g.id_dia
          JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo
         WHERE c.id_hogar = p_id_hogar AND g.id_lugar_compra = 'NOE'
        UNION ALL
        -- RF12 y RF13: reglas del DANE sobre los gastos ya registrados
        SELECT 'Reglas DANE',
               'Gasto ' || g.id_gasto || ' (' || g.articulo || '): categoria excluida (' || fn_categoria_excluida(g.articulo) || ')'
          FROM gasto_diario g
          JOIN dia_recoleccion d ON d.id_dia = g.id_dia
          JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo
         WHERE c.id_hogar = p_id_hogar AND fn_categoria_excluida(g.articulo) IS NOT NULL
        UNION ALL
        SELECT 'Reglas DANE',
               'Gasto ' || g.id_gasto || ' (' || g.articulo || '): gasto personal en el dia ' || d.numero_dia || ' (solo dias 1 a 7)'
          FROM gasto_diario g
          JOIN dia_recoleccion d ON d.id_dia = g.id_dia
          JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo
         WHERE c.id_hogar = p_id_hogar AND g.id_tipo_gasto = 'PER' AND d.numero_dia > 7
        UNION ALL
        SELECT 'Reglas DANE',
               'Comida fuera ' || m.id_comida || ' (' || m.descripcion || '): gasto personal en el dia ' || d.numero_dia || ' (solo dias 1 a 7)'
          FROM comida_preparada m
          JOIN dia_recoleccion d ON d.id_dia = m.id_dia
          JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo
         WHERE c.id_hogar = p_id_hogar AND m.id_tipo_gasto = 'PER' AND d.numero_dia > 7
        UNION ALL
        -- 4. Capitulo C: frecuencia superior a la bisemana exige valor y si comprara
        SELECT 'Capitulo C',
               'Grupo ' || k.id_grupo_alimento || ': falta el valor de la ultima compra o si comprara en los proximos 14 dias'
          FROM caracteristica_gasto_alimento k
         WHERE k.id_hogar = p_id_hogar
           AND k.id_frecuencia_compra IN ('4', '5', '6', '7')
           AND (k.valor_ultima_compra IS NULL OR k.comprara_durante_periodo IS NULL)
        UNION ALL
        -- 5. Informacion completa
        SELECT 'Completitud', 'El hogar no tiene visitas registradas'
          FROM dual
         WHERE NOT EXISTS (SELECT 1 FROM visita WHERE id_hogar = p_id_hogar)
        UNION ALL
        SELECT 'Completitud', 'El hogar no tiene gastos diarios registrados'
          FROM dual
         WHERE NOT EXISTS (SELECT 1 FROM gasto_diario g
                             JOIN dia_recoleccion d ON d.id_dia = g.id_dia
                             JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo
                            WHERE c.id_hogar = p_id_hogar)
        UNION ALL
        SELECT 'Completitud', 'El hogar no tiene respuestas del capitulo C'
          FROM dual
         WHERE NOT EXISTS (SELECT 1 FROM caracteristica_gasto_alimento WHERE id_hogar = p_id_hogar)
        UNION ALL
        -- Dias ya transcurridos sin informacion (ningun dia puede quedar vacio)
        SELECT 'Completitud', 'Dia ' || x.n || ': no tiene gastos ni comidas registrados'
          FROM (SELECT LEVEL AS n FROM dual CONNECT BY LEVEL <= 14) x
          JOIN cuadernillo2 c ON c.id_hogar = p_id_hogar
         WHERE x.n <= TRUNC(SYSDATE) - TRUNC(c.fecha_inicio)
           AND NOT EXISTS (SELECT 1
                             FROM dia_recoleccion d
                            WHERE d.id_cuadernillo = c.id_cuadernillo AND d.numero_dia = x.n
                              AND (EXISTS (SELECT 1 FROM gasto_diario g WHERE g.id_dia = d.id_dia)
                                OR EXISTS (SELECT 1 FROM comida_preparada m WHERE m.id_dia = d.id_dia)));
END sp_validar_hogar;
/
SHOW ERRORS PROCEDURE sp_validar_hogar

-- Prueba rapida (cambie el codigo por un hogar existente):
-- VARIABLE rc REFCURSOR
-- EXEC sp_validar_hogar('HOG-68001-014', :rc)
-- PRINT rc

-- =====================================================================
-- RF15 y RF16 - Cerrar el cuadernillo de un hogar.
-- Procedimiento almacenado: verifica que la informacion requerida este completa
-- y, si lo esta, guarda si hubo gastos por fiestas (RF15), la principal forma de
-- pago (RF16) y deja el cuadernillo en estado Completado.
-- p_ok = 1 si quedo cerrado; 0 si no (p_mensaje dice por que).
-- =====================================================================
CREATE OR REPLACE PROCEDURE sp_cerrar_cuadernillo (
    p_id_hogar   IN  VARCHAR2,
    p_fiesta     IN  VARCHAR2,
    p_forma_pago IN  VARCHAR2,
    p_id_usuario IN  VARCHAR2,
    p_ok         OUT NUMBER,
    p_mensaje    OUT VARCHAR2
) IS
    v_estado       cuadernillo2.estado%TYPE;
    v_inicio       cuadernillo2.fecha_inicio%TYPE;
    v_resultado    cuadernillo2.resultado%TYPE;
    v_encuestador  NUMBER;
    v_pago         NUMBER;
    v_visitas      NUMBER;
    v_gastos       NUMBER;
    v_gastos_malos NUMBER;
    v_caract       NUMBER;
    v_transcurridos NUMBER;
    v_faltan       VARCHAR2(1000);
    v_dias_vacios  VARCHAR2(100);
BEGIN
    p_ok := 0;
    BEGIN
        SELECT estado, fecha_inicio, resultado INTO v_estado, v_inicio, v_resultado
          FROM cuadernillo2 WHERE id_hogar = p_id_hogar;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_mensaje := 'El hogar no tiene cuadernillo registrado.';
            RETURN;
    END;

    -- Solo un encuestador puede cerrar (RF20)
    SELECT COUNT(*) INTO v_encuestador FROM usuario WHERE id_usuario = p_id_usuario AND rol = 'Encuestador';
    IF v_encuestador = 0 THEN
        p_mensaje := 'Solo un encuestador puede cerrar el cuadernillo.';
        RETURN;
    END IF;

    IF v_estado = 'Completado' THEN
        p_mensaje := 'El cuadernillo ya esta cerrado.';
        RETURN;
    END IF;

    -- El periodo de recoleccion debe haber terminado: 14 dias desde la apertura (RF04)
    v_transcurridos := TRUNC(SYSDATE) - TRUNC(v_inicio);
    IF v_transcurridos < 14 THEN
        p_mensaje := 'Aun no termina el periodo de 14 dias: faltan ' || (14 - v_transcurridos) || ' dia(s).';
        RETURN;
    END IF;

    SELECT COUNT(*) INTO v_pago FROM forma_pago WHERE id_forma_pago = p_forma_pago;
    SELECT COUNT(*) INTO v_visitas FROM visita WHERE id_hogar = p_id_hogar;
    SELECT COUNT(*) INTO v_caract FROM caracteristica_gasto_alimento WHERE id_hogar = p_id_hogar;
    SELECT COUNT(*), NVL(SUM(CASE WHEN g.valor_pagado <= 0 OR g.cantidad <= 0 THEN 1 ELSE 0 END), 0)
      INTO v_gastos, v_gastos_malos
      FROM gasto_diario g
      JOIN dia_recoleccion d ON d.id_dia = g.id_dia
      JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo
     WHERE c.id_hogar = p_id_hogar;

    -- Los 14 dias deben tener informacion (gastos o comidas): no puede faltar ningun dia
    v_dias_vacios := NULL;
    FOR r IN (SELECT x.n
                FROM (SELECT LEVEL AS n FROM dual CONNECT BY LEVEL <= 14) x
               WHERE NOT EXISTS (SELECT 1
                                   FROM dia_recoleccion d
                                   JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo
                                  WHERE c.id_hogar = p_id_hogar AND d.numero_dia = x.n
                                    AND (EXISTS (SELECT 1 FROM gasto_diario g WHERE g.id_dia = d.id_dia)
                                      OR EXISTS (SELECT 1 FROM comida_preparada m WHERE m.id_dia = d.id_dia)))
               ORDER BY x.n) LOOP
        v_dias_vacios := v_dias_vacios || r.n || ', ';
    END LOOP;

    v_faltan := NULL;
    IF v_dias_vacios IS NOT NULL THEN
        v_faltan := v_faltan || 'Faltan datos en los dias: ' || RTRIM(v_dias_vacios, ', ') || '. ';
    END IF;
    IF v_resultado IS NULL THEN
        v_faltan := v_faltan || 'Falta registrar el resultado del cuadernillo (capitulo A). ';
    END IF;
    IF p_fiesta IS NULL OR p_fiesta NOT IN ('S', 'N') THEN
        v_faltan := v_faltan || 'Indique si hubo gastos por fiestas o reuniones. ';
    END IF;
    IF v_pago = 0 THEN
        v_faltan := v_faltan || 'Seleccione la principal forma de pago. ';
    END IF;
    IF v_visitas = 0 THEN
        v_faltan := v_faltan || 'Falta registrar al menos una visita. ';
    END IF;
    IF v_caract = 0 THEN
        v_faltan := v_faltan || 'Falta diligenciar el capitulo C. ';
    END IF;
    IF v_gastos = 0 THEN
        v_faltan := v_faltan || 'Falta registrar gastos diarios. ';
    END IF;
    IF v_gastos_malos > 0 THEN
        v_faltan := v_faltan || 'Hay gastos con valor o cantidad invalidos. ';
    END IF;

    IF v_faltan IS NOT NULL THEN
        p_mensaje := TRIM(v_faltan);
        RETURN;
    END IF;

    UPDATE cuadernillo2
       SET estado = 'Completado', gastos_fiesta = p_fiesta, id_forma_pago = p_forma_pago,
           fecha_cierre = SYSDATE, id_usuario_cierre = p_id_usuario
     WHERE id_hogar = p_id_hogar;
    COMMIT;
    p_ok := 1;
    p_mensaje := 'Cuadernillo cerrado: el caso queda disponible para revision.';
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_ok := 0;
        p_mensaje := 'Error al cerrar el cuadernillo: ' || SQLERRM;
END sp_cerrar_cuadernillo;
/
SHOW ERRORS PROCEDURE sp_cerrar_cuadernillo

-- =====================================================================
-- Revisar informacion (rol Supervisor).
-- p_accion = 'F' finaliza la revision (solo si el cuadernillo esta completo y
-- consistente) o 'D' devuelve el caso para correccion (estado En Proceso).
-- Reutiliza sp_validar_hogar para verificar que no falte informacion.
-- =====================================================================
CREATE OR REPLACE PROCEDURE sp_revisar_cuadernillo (
    p_id_hogar   IN  VARCHAR2,
    p_accion     IN  VARCHAR2,
    p_id_usuario IN  VARCHAR2,
    p_ok         OUT NUMBER,
    p_mensaje    OUT VARCHAR2
) IS
    v_estado     cuadernillo2.estado%TYPE;
    v_revisado   cuadernillo2.revisado%TYPE;
    v_supervisor NUMBER;
    v_rc         SYS_REFCURSOR;
    v_regla      VARCHAR2(50);
    v_detalle    VARCHAR2(500);
    v_graves     NUMBER := 0;
BEGIN
    p_ok := 0;
    -- Solo un supervisor puede revisar (RF20)
    SELECT COUNT(*) INTO v_supervisor FROM usuario WHERE id_usuario = p_id_usuario AND rol = 'Supervisor';
    IF v_supervisor = 0 THEN
        p_mensaje := 'Solo un supervisor puede revisar el cuadernillo.';
        RETURN;
    END IF;
    BEGIN
        SELECT estado, NVL(revisado, 'N') INTO v_estado, v_revisado FROM cuadernillo2 WHERE id_hogar = p_id_hogar;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            p_mensaje := 'El hogar no tiene cuadernillo registrado.';
            RETURN;
    END;

    IF v_estado <> 'Completado' THEN
        p_mensaje := 'El cuadernillo aun no esta cerrado por el encuestador.';
        RETURN;
    END IF;

    IF p_accion = 'D' THEN
        UPDATE cuadernillo2
           SET estado = 'En Proceso', revisado = 'N', fecha_revision = NULL, id_usuario_revision = NULL
         WHERE id_hogar = p_id_hogar;
        COMMIT;
        p_ok := 1;
        p_mensaje := 'Caso devuelto al encuestador para correccion.';
    ELSIF p_accion = 'F' THEN
        IF v_revisado = 'S' THEN
            p_mensaje := 'El caso ya fue revisado. Si hay un error, devuelvalo para correccion.';
            RETURN;
        END IF;
        sp_validar_hogar(p_id_hogar, v_rc);
        LOOP
            FETCH v_rc INTO v_regla, v_detalle;
            EXIT WHEN v_rc%NOTFOUND;
            IF TRIM(v_regla) IN ('Completitud', 'Valores', 'Fechas') THEN
                v_graves := v_graves + 1;
            END IF;
        END LOOP;
        CLOSE v_rc;
        IF v_graves > 0 THEN
            p_mensaje := 'No se puede finalizar: falta informacion o hay valores/fechas inconsistentes (' || v_graves || ').';
            RETURN;
        END IF;
        UPDATE cuadernillo2
           SET revisado = 'S', fecha_revision = SYSDATE, id_usuario_revision = p_id_usuario
         WHERE id_hogar = p_id_hogar;
        COMMIT;
        p_ok := 1;
        p_mensaje := 'Revision finalizada: el caso queda revisado.';
    ELSE
        p_mensaje := 'Accion no valida.';
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        p_ok := 0;
        p_mensaje := 'Error en la revision: ' || SQLERRM;
END sp_revisar_cuadernillo;
/
SHOW ERRORS PROCEDURE sp_revisar_cuadernillo

-- =====================================================================
-- Funciones y disparadores adicionales del modelo ENPH.
-- Se puede ejecutar varias veces (CREATE OR REPLACE).
-- Ejecutar en SQL*Plus conectado como ENPH:   @"ruta\objetos_extra.sql"
-- =====================================================================

-- ---------- FUNCIONES ----------

-- Total pagado en gastos diarios (capitulo D) de un hogar
CREATE OR REPLACE FUNCTION fn_total_gastos_hogar (p_id_hogar IN VARCHAR2)
RETURN NUMBER IS
    v_total NUMBER;
BEGIN
    SELECT NVL(SUM(g.valor_pagado), 0)
      INTO v_total
      FROM gasto_diario g
      JOIN dia_recoleccion d ON d.id_dia = g.id_dia
      JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo
     WHERE c.id_hogar = p_id_hogar;
    RETURN v_total;
END fn_total_gastos_hogar;
/
SHOW ERRORS FUNCTION fn_total_gastos_hogar

-- Total pagado en comidas preparadas fuera del hogar (capitulo D1) de un hogar
CREATE OR REPLACE FUNCTION fn_total_comidas_hogar (p_id_hogar IN VARCHAR2)
RETURN NUMBER IS
    v_total NUMBER;
BEGIN
    SELECT NVL(SUM(m.valor_pagado), 0)
      INTO v_total
      FROM comida_preparada m
      JOIN dia_recoleccion d ON d.id_dia = m.id_dia
      JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo
     WHERE c.id_hogar = p_id_hogar;
    RETURN v_total;
END fn_total_comidas_hogar;
/
SHOW ERRORS FUNCTION fn_total_comidas_hogar

-- Numero de dias (de los 14) en los que el hogar registro gastos
CREATE OR REPLACE FUNCTION fn_dias_con_gastos (p_id_hogar IN VARCHAR2)
RETURN NUMBER IS
    v_dias NUMBER;
BEGIN
    SELECT COUNT(DISTINCT d.numero_dia)
      INTO v_dias
      FROM gasto_diario g
      JOIN dia_recoleccion d ON d.id_dia = g.id_dia
      JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo
     WHERE c.id_hogar = p_id_hogar;
    RETURN v_dias;
END fn_dias_con_gastos;
/
SHOW ERRORS FUNCTION fn_dias_con_gastos

-- ---------- DISPARADORES ----------

-- Un cuadernillo cerrado (Completado) no admite cambios en sus gastos diarios.
-- Para corregirlo, el supervisor debe devolver el caso (estado En Proceso).
CREATE OR REPLACE TRIGGER trg_gasto_cuadernillo_cerrado
BEFORE INSERT OR UPDATE OR DELETE ON gasto_diario
FOR EACH ROW
DECLARE
    v_dia    NUMBER;
    v_estado cuadernillo2.estado%TYPE;
BEGIN
    IF DELETING THEN
        v_dia := :OLD.id_dia;
    ELSE
        v_dia := :NEW.id_dia;
    END IF;
    SELECT c.estado INTO v_estado
      FROM dia_recoleccion d
      JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo
     WHERE d.id_dia = v_dia;
    IF v_estado = 'Completado' THEN
        RAISE_APPLICATION_ERROR(-20001, 'El cuadernillo esta cerrado: no se pueden modificar los gastos.');
    END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        NULL;
    WHEN OTHERS THEN
        IF SQLCODE = -4091 THEN   -- borrado en cascada del hogar: se permite
            NULL;
        ELSE
            RAISE;
        END IF;
END;
/
SHOW ERRORS TRIGGER trg_gasto_cuadernillo_cerrado

-- Lo mismo para las comidas preparadas fuera del hogar
CREATE OR REPLACE TRIGGER trg_comida_cuadernillo_cerrado
BEFORE INSERT OR UPDATE OR DELETE ON comida_preparada
FOR EACH ROW
DECLARE
    v_dia    NUMBER;
    v_estado cuadernillo2.estado%TYPE;
BEGIN
    IF DELETING THEN
        v_dia := :OLD.id_dia;
    ELSE
        v_dia := :NEW.id_dia;
    END IF;
    SELECT c.estado INTO v_estado
      FROM dia_recoleccion d
      JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo
     WHERE d.id_dia = v_dia;
    IF v_estado = 'Completado' THEN
        RAISE_APPLICATION_ERROR(-20003, 'El cuadernillo esta cerrado: no se pueden modificar las comidas.');
    END IF;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        NULL;
    WHEN OTHERS THEN
        IF SQLCODE = -4091 THEN
            NULL;
        ELSE
            RAISE;
        END IF;
END;
/
SHOW ERRORS TRIGGER trg_comida_cuadernillo_cerrado

-- Al registrar el primer gasto de un hogar Pendiente, su cuadernillo pasa a En Proceso
CREATE OR REPLACE TRIGGER trg_gasto_inicia_cuadernillo
AFTER INSERT ON gasto_diario
FOR EACH ROW
BEGIN
    UPDATE cuadernillo2
       SET estado = 'En Proceso'
     WHERE estado = 'Pendiente'
       AND id_cuadernillo = (SELECT id_cuadernillo FROM dia_recoleccion WHERE id_dia = :NEW.id_dia);
END;
/
SHOW ERRORS TRIGGER trg_gasto_inicia_cuadernillo

-- La hora de fin de una visita debe ser posterior a la de inicio (tambien en la base de datos)
CREATE OR REPLACE TRIGGER trg_visita_horas
BEFORE INSERT OR UPDATE ON visita
FOR EACH ROW
BEGIN
    IF :NEW.hora_fin <= :NEW.hora_inicio THEN
        RAISE_APPLICATION_ERROR(-20002, 'La hora de fin de la visita debe ser posterior a la de inicio.');
    END IF;
END;
/
SHOW ERRORS TRIGGER trg_visita_horas

-- =====================================================================
-- RF12 y RF13 en la base de datos (disparadores).
-- RF12: un gasto o comida PERSONAL solo puede estar en los dias 1 a 7.
-- RF13: no se aceptan articulos de las categorias excluidas.
-- =====================================================================
CREATE OR REPLACE TRIGGER trg_gasto_reglas_dane
BEFORE INSERT OR UPDATE ON gasto_diario
FOR EACH ROW
DECLARE
    v_dia NUMBER;
    v_cat VARCHAR2(40);
BEGIN
    SELECT numero_dia INTO v_dia FROM dia_recoleccion WHERE id_dia = :NEW.id_dia;
    IF :NEW.id_tipo_gasto = 'PER' AND v_dia > 7 THEN
        RAISE_APPLICATION_ERROR(-20003, 'Los gastos personales solo se registran en los primeros 7 dias.');
    END IF;
    v_cat := fn_categoria_excluida(:NEW.articulo);
    IF v_cat IS NOT NULL THEN
        RAISE_APPLICATION_ERROR(-20004, 'Categoria excluida del Cuadernillo 2: ' || v_cat || '.');
    END IF;
END;
/
SHOW ERRORS TRIGGER trg_gasto_reglas_dane

CREATE OR REPLACE TRIGGER trg_comida_reglas_dane
BEFORE INSERT OR UPDATE ON comida_preparada
FOR EACH ROW
DECLARE
    v_dia NUMBER;
BEGIN
    SELECT numero_dia INTO v_dia FROM dia_recoleccion WHERE id_dia = :NEW.id_dia;
    IF :NEW.id_tipo_gasto = 'PER' AND v_dia > 7 THEN
        RAISE_APPLICATION_ERROR(-20003, 'Los gastos personales solo se registran en los primeros 7 dias.');
    END IF;
END;
/
SHOW ERRORS TRIGGER trg_comida_reglas_dane

-- ---------- VISTA PARA EL INFORME GERENCIAL (RF26) ----------
-- Une compras y comidas fuera del hogar; el informe del supervisor la consulta con GROUP BY.
CREATE OR REPLACE VIEW v_gasto_unificado AS
SELECT d.numero_dia AS dia,
       g.valor_pagado AS valor,
       'Compras' AS origen,
       tg.nombre AS tipo,
       lc.nombre AS lugar,
       c.id_hogar AS hogar,
       c.estado AS estado,
       c.id_forma_pago AS fp
  FROM gasto_diario g
  JOIN dia_recoleccion d ON d.id_dia = g.id_dia
  JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo
  JOIN tipo_gasto tg ON tg.id_tipo_gasto = g.id_tipo_gasto
  JOIN lugar_compra lc ON lc.id_lugar_compra = g.id_lugar_compra
UNION ALL
SELECT d.numero_dia,
       m.valor_pagado,
       'Comidas fuera del hogar',
       NVL(tg.nombre, 'Hogar'),
       NVL(lc.nombre, 'No especificado'),
       c.id_hogar,
       c.estado,
       c.id_forma_pago
  FROM comida_preparada m
  JOIN dia_recoleccion d ON d.id_dia = m.id_dia
  JOIN cuadernillo2 c ON c.id_cuadernillo = d.id_cuadernillo
  LEFT JOIN tipo_gasto tg ON tg.id_tipo_gasto = m.id_tipo_gasto
  LEFT JOIN lugar_compra lc ON lc.id_lugar_compra = m.id_lugar_compra
/
SHOW ERRORS VIEW v_gasto_unificado
