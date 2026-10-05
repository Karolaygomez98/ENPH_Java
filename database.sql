-- Creación de la base de datos
-- CREATE DATABASE enph_db;

-- 1. Tabla Encuestador
CREATE TABLE encuestadores (
    usuario VARCHAR(50) PRIMARY KEY,
    nombre_completo VARCHAR(100) NOT NULL,
    rol VARCHAR(30) DEFAULT 'Encuestador'
);

-- 2. Tabla Hogar
CREATE TABLE hogares (
    id_hogar VARCHAR(20) PRIMARY KEY,
    codigo_ubicacion VARCHAR(50) NOT NULL,
    jefe_hogar VARCHAR(100) NOT NULL,
    direccion VARCHAR(150) NOT NULL,
    estado VARCHAR(30) DEFAULT 'En proceso'
);

-- 3. Tabla Cuadernillo 2: Gastos Diarios
CREATE TABLE gastos_diarios (
    id_gasto SERIAL PRIMARY KEY,
    id_hogar VARCHAR(20) REFERENCES hogares(id_hogar) ON DELETE CASCADE,
    descripcion_producto VARCHAR(150) NOT NULL,
    cantidad NUMERIC(10, 2) NOT NULL,
    unidad_medida VARCHAR(30) NOT NULL,
    valor_pagado NUMERIC(12, 2) NOT NULL,
    lugar_compra VARCHAR(100) NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Registros de prueba iniciales
INSERT INTO encuestadores (usuario, nombre_completo, rol) 
VALUES ('kdgomez', 'Karolay Daniela Gómez', 'Encuestador');

INSERT INTO hogares (id_hogar, codigo_ubicacion, jefe_hogar, direccion, estado) 
VALUES ('HOG-68001-014', '68001', 'María Gómez', 'Calle 12 # 34-56 · Cabecera', 'En proceso');

INSERT INTO gastos_diarios (id_hogar, descripcion_producto, cantidad, unidad_medida, valor_pagado, lugar_compra) 
VALUES ('HOG-68001-014', 'Arroz blanco', 1.00, 'Kg', 4200.00, 'Supermercado de barrio');