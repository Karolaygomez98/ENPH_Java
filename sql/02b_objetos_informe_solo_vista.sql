-- =====================================================================
-- ENPH - Cuadernillo 2 | Vista para el informe gerencial (RF26)
-- Une las compras (gasto_diario) y las comidas fuera del hogar
-- (comida_preparada) en un solo conjunto de gastos. El informe del
-- supervisor la consulta con GROUP BY (clase InformeDAO).
-- Se puede ejecutar varias veces (CREATE OR REPLACE).
-- =====================================================================
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
