-- =====================================================================
-- ENPH - DATOS DE DEMOSTRACION (opcional).
-- Carga informacion de ejemplo para el video y la sustentacion:
--   HOG-68001-014  En Proceso, 14 dias completos, LISTO PARA CERRAR (lo cierra la encuestadora).
--   HOG-68001-015  Completado, pendiente de revision (lo revisa el supervisor).
--   HOG-68001-018  Completado y revisado por el supervisor.
--   HOG-68001-019  Completado y revisado (mas datos para el informe gerencial).
--   HOG-68001-020  Completado, pendiente de revision.
-- Los hogares 016, 017 y 021 quedan vacios para pruebas y para digitar en vivo.
-- No duplica: si un hogar ya tiene gastos, lo omite.
-- Ejecutar con el usuario dueno de las tablas (ENPH en casa, PROENPH en la UDI):
--     @"ruta\datos_demo.sql"
-- =====================================================================
SET DEFINE OFF
SET SERVEROUTPUT ON

DECLARE
    TYPE t_txt IS TABLE OF VARCHAR2(40);
    TYPE t_num IS TABLE OF NUMBER;
    v_art   t_txt := t_txt('Arroz', 'Leche', 'Huevos', 'Pan', 'Papa', 'Pollo', 'Platano', 'Aceite', 'Tomate', 'Cebolla');
    v_uni   t_txt := t_txt('KIL',   'LIT',   'UNI',    'UNI', 'KIL',  'KIL',   'UNI',     'LIT',    'KIL',    'KIL');
    v_lug   t_txt := t_txt('SUP',   'TIE',   'TIE',    'PAN', 'PLA',  'SUP',   'VEN',     'SUP',    'PLA',    'PLA');
    v_can   t_num := t_num(1,       1,       12,       4,     2,      1,       6,         1,        1,        1);
    v_val   t_num := t_num(4500,    3200,    9000,     2000,  2500,   11000,   3000,      9500,     3500,     2800);
    v_frec  t_txt := t_txt('2', '2.1', '3', '4', '5');

    PROCEDURE cargar (p_hogar IN VARCHAR2, p_final IN VARCHAR2, p_var IN NUMBER DEFAULT 0, p_f IN NUMBER DEFAULT 100) IS
        v_cuad    cuadernillo2.id_cuadernillo%TYPE;
        v_estado  cuadernillo2.estado%TYPE;
        v_inicio  DATE;
        v_dia     NUMBER;
        v_n       NUMBER;
        i         NUMBER;
        k         NUMBER;
        ix        NUMBER;
        v_forma   VARCHAR2(10);
        v_fr      VARCHAR2(10);
        v_valor   NUMBER;
        v_comp    VARCHAR2(1);
    BEGIN
        SELECT COUNT(*) INTO v_n FROM cuadernillo2 WHERE id_hogar = p_hogar;
        IF v_n = 0 THEN
            DBMS_OUTPUT.PUT_LINE(p_hogar || ': no existe, se omite.');
            RETURN;
        END IF;
        SELECT id_cuadernillo, estado, fecha_inicio INTO v_cuad, v_estado, v_inicio
          FROM cuadernillo2 WHERE id_hogar = p_hogar;
        SELECT COUNT(*) INTO v_n
          FROM gasto_diario g
          JOIN dia_recoleccion d ON d.id_dia = g.id_dia
         WHERE d.id_cuadernillo = v_cuad;
        IF v_n > 0 THEN
            DBMS_OUTPUT.PUT_LINE(p_hogar || ': ya tiene gastos, se omite.');
            RETURN;
        END IF;

        -- Se abre temporalmente para poder cargar (un cuadernillo cerrado no admite cambios)
        UPDATE cuadernillo2 SET estado = 'En Proceso' WHERE id_cuadernillo = v_cuad;

        -- La fecha de inicio debe ser anterior para que los 14 dias esten habilitados (trg_dia_habilitado)
        UPDATE cuadernillo2 SET fecha_inicio = LEAST(fecha_inicio, TRUNC(SYSDATE) - 16) WHERE id_cuadernillo = v_cuad;
        SELECT fecha_inicio INTO v_inicio FROM cuadernillo2 WHERE id_cuadernillo = v_cuad;

        -- Capitulo A
        UPDATE cuadernillo2 SET resultado = 'EC' WHERE id_cuadernillo = v_cuad;

        -- Visitas
        SELECT COUNT(*) INTO v_n FROM visita WHERE id_hogar = p_hogar;
        IF v_n = 0 THEN
            INSERT INTO visita (fecha, hora_inicio, hora_fin, resultado, partes_pendientes, fecha_proxima, hora_proxima, id_hogar)
            VALUES (TRUNC(v_inicio), '09:00', '09:40', 'Incompleta', 'Faltan los gastos de los ultimos dias',
                    TRUNC(v_inicio) + 7, '15:00', p_hogar);
            INSERT INTO visita (fecha, hora_inicio, hora_fin, resultado, partes_pendientes, id_hogar)
            VALUES (TRUNC(v_inicio) + 7, '15:00', '15:50', 'Incompleta', 'Falta el capitulo C', p_hogar);
            INSERT INTO visita (fecha, hora_inicio, hora_fin, resultado, id_hogar)
            VALUES (TRUNC(v_inicio) + 13, '10:00', '10:35', 'Completa', p_hogar);
        END IF;

        -- Capitulo C: los 25 grupos de alimentos
        SELECT COUNT(*) INTO v_n FROM caracteristica_gasto_alimento WHERE id_hogar = p_hogar;
        IF v_n = 0 THEN
            FOR g IN (SELECT id_grupo_alimento AS id FROM grupo_alimento ORDER BY id_grupo_alimento) LOOP
                i := TO_NUMBER(g.id);
                v_fr := v_frec(MOD(i, 5) + 1);
                v_valor := NULL;
                v_comp := NULL;
                IF v_fr IN ('4', '5') THEN
                    v_valor := 8000 + i * 100;
                    v_comp := CASE WHEN MOD(i, 2) = 0 THEN 'S' ELSE 'N' END;
                END IF;
                INSERT INTO caracteristica_gasto_alimento
                    (id_hogar, id_grupo_alimento, id_frecuencia_compra, frecuencia_habitual,
                     valor_ultima_compra, comprara_durante_periodo)
                VALUES (p_hogar, g.id, v_fr,
                        (SELECT nombre FROM frecuencia_compra WHERE id_frecuencia_compra = v_fr),
                        v_valor, v_comp);
            END LOOP;
        END IF;

        -- Los 14 dias con gastos y algunas comidas fuera del hogar
        FOR d IN 1..14 LOOP
            SELECT COUNT(*) INTO v_n FROM dia_recoleccion WHERE id_cuadernillo = v_cuad AND numero_dia = d;
            IF v_n = 0 THEN
                INSERT INTO dia_recoleccion (numero_dia, id_cuadernillo) VALUES (d, v_cuad);
            END IF;
            SELECT id_dia INTO v_dia FROM dia_recoleccion WHERE id_cuadernillo = v_cuad AND numero_dia = d;

            FOR k IN 0..3 LOOP
                ix := MOD(d * 2 + k + p_var, 10) + 1;
                v_forma := CASE WHEN d = 4 AND k = 1 THEN 'OBS' ELSE 'COM' END;
                INSERT INTO gasto_diario (articulo, cantidad, valor_pagado, id_dia, id_unidad_medida,
                                          id_forma_adquisicion, id_lugar_compra, id_tipo_gasto, id_frecuencia_compra)
                VALUES (v_art(ix), v_can(ix), ROUND(v_val(ix) * 3 * p_f / 100, -2), v_dia, v_uni(ix), v_forma, v_lug(ix), 'HOG', '3');
            END LOOP;

            -- Gasto personal: solo en los primeros 7 dias (RF12)
            IF d <= 7 THEN
                INSERT INTO gasto_diario (articulo, cantidad, valor_pagado, id_dia, id_unidad_medida,
                                          id_forma_adquisicion, id_lugar_compra, id_tipo_gasto, id_frecuencia_compra)
                VALUES ('Gaseosa', 1, 2500, v_dia, 'UNI', 'COM', 'TIE', 'PER', '2');
            END IF;

            -- Comidas fuera del hogar
            IF d IN (2, 5, 9, 12) THEN
                INSERT INTO comida_preparada (descripcion, valor_pagado, cantidad, domicilio, id_dia, id_tipo_comida,
                                              id_lugar_compra, id_frecuencia_compra, id_tipo_gasto, id_forma_adquisicion)
                VALUES ('Corrientazo', 12000, 1, 'N', v_dia, 'ALM', 'TIE', '3',
                        CASE WHEN d <= 7 THEN 'PER' ELSE 'HOG' END, 'COM');
            END IF;
            IF d IN (3, 10) THEN
                INSERT INTO comida_preparada (descripcion, valor_pagado, cantidad, domicilio, id_dia, id_tipo_comida,
                                              id_lugar_compra, id_frecuencia_compra, id_tipo_gasto, id_forma_adquisicion)
                VALUES ('Pizza familiar', 38000, 1, 'S', v_dia, 'CEN', 'TIE', '5', 'HOG', 'COM');
            END IF;
        END LOOP;

        -- Estado final segun el caso
        IF p_final = 'ABIERTO' THEN
            UPDATE cuadernillo2
               SET estado = 'En Proceso', revisado = 'N', fecha_cierre = NULL, id_usuario_cierre = NULL,
                   fecha_revision = NULL, id_usuario_revision = NULL, id_forma_pago = NULL, gastos_fiesta = NULL
             WHERE id_cuadernillo = v_cuad;
        ELSIF p_final = 'CERRADO' THEN
            UPDATE cuadernillo2
               SET estado = 'Completado', revisado = 'N', id_forma_pago = 'EFE', gastos_fiesta = 'N',
                   fecha_cierre = SYSDATE - 3, id_usuario_cierre = 'ENC-001',
                   fecha_revision = NULL, id_usuario_revision = NULL
             WHERE id_cuadernillo = v_cuad;
        ELSE
            UPDATE cuadernillo2
               SET estado = 'Completado', revisado = 'S', id_forma_pago = 'EFE', gastos_fiesta = 'N',
                   fecha_cierre = SYSDATE - 4, id_usuario_cierre = 'ENC-001',
                   fecha_revision = SYSDATE - 2, id_usuario_revision = 'SUP-001'
             WHERE id_cuadernillo = v_cuad;
        END IF;
        COMMIT;
        DBMS_OUTPUT.PUT_LINE(p_hogar || ': datos de demostracion cargados (' || p_final || ').');
    END cargar;
BEGIN
    cargar('HOG-68001-014', 'ABIERTO',    0,  60);
    cargar('HOG-68001-015', 'CERRADO',    2,  85);
    cargar('HOG-68001-018', 'REVISADO',   4, 115);
    cargar('HOG-68001-019', 'REVISADO',   6, 140);
    cargar('HOG-68001-020', 'CERRADO',    8, 180);
END;
/
