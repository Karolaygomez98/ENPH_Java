// ============================================================================
// Sincronizacion de los gastos diarios (capitulo D) con Oracle.
// Se carga DESPUES de app.js. Cada vez que app.js guarda en el navegador,
// este archivo compara los gastos y envia al servidor Java (ServidorHTTP):
//   - gasto nuevo      -> CREATE  (se guarda el id que devuelve Oracle en g.idBD)
//   - gasto modificado -> UPDATE
//   - gasto eliminado  -> DELETE
// Si el servidor Java no esta encendido, la interfaz sigue funcionando y solo
// guarda en el navegador.
// ============================================================================
(function () {
    const API = 'http://127.0.0.1:8081/api';
    const guardarOriginal = guardarHogaresBD;      // funcion de app.js
    const enCurso = new WeakSet();                 // gastos con una peticion pendiente
    let idsConocidos = new Set();                  // ids de Oracle vistos en la ultima pasada
    let temporizadorAviso = null;

    // ---- Aviso visual (esquina inferior derecha) ----
    function aviso(texto, ok) {
        let caja = document.getElementById('aviso-oracle');
        if (!caja) {
            caja = document.createElement('div');
            caja.id = 'aviso-oracle';
            caja.style.cssText = 'position:fixed;left:16px;bottom:16px;z-index:9999;padding:8px 14px;' +
                'border-radius:8px;font:600 13px sans-serif;color:#fff;box-shadow:0 2px 8px rgba(0,0,0,.25);';
            document.body.appendChild(caja);
        }
        caja.style.background = ok ? '#2d6a4f' : '#a11242';
        caja.textContent = texto;
        caja.style.display = 'block';
        clearTimeout(temporizadorAviso);
        temporizadorAviso = setTimeout(() => { caja.style.display = 'none'; }, 4000);
    }

    function firma(g) {
        return [g.dia || 1, g.categoria || '', g.concepto || '', g.cantidad || 1, g.unidad || 'Unidad', g.lugar || '', g.valor].join('|');
    }

    function post(ruta, datos) {
        return fetch(API + ruta, {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams(datos)
        }).then(r => r.json());
    }

    // ---- CREATE / UPDATE de un gasto ----
    function enviarGasto(hogar, g) {
        enCurso.add(g);
        const datos = {
            hogar: hogar.codigo,
            jefe: hogar.jefe || '',
            direccion: hogar.direccionFisica || hogar.direccion || '',
            dia: g.dia || 1,
            categoria: g.categoria || '',
            concepto: g.concepto || '',
            cantidad: g.cantidad || 1,
            unidad: g.unidad || 'Unidad',
            lugar: g.lugar || '',
            valor: g.valor
        };
        const sig = firma(g);
        const actualizando = !!g.idBD;
        if (actualizando) datos.id = g.idBD;

        let continuar = false;   // solo se repite la pasada si hubo respuesta del servidor
        post(actualizando ? '/gastos/actualizar' : '/gastos/crear', datos)
            .then(r => {
                continuar = true;
                if (r.ok) {
                    g.idBD = r.id || g.idBD;
                    g.firmaBD = sig;
                    idsConocidos.add(g.idBD);
                    aviso('Guardado en Oracle (ID ' + g.idBD + ')', true);
                } else if (r.mensaje === 'noexiste') {
                    // La fila ya no esta en Oracle: se vuelve a crear en la proxima pasada
                    idsConocidos.delete(String(g.idBD));
                    g.idBD = null;
                    g.firmaBD = null;
                } else {
                    aviso('Oracle: ' + r.mensaje, false);
                }
            })
            .catch(() => aviso('Sin conexion con el servidor Java: guardado solo en el navegador', false))
            .finally(() => {
                enCurso.delete(g);
                guardarOriginal();
                if (continuar) sincronizar();   // por si hubo otro cambio mientras se enviaba
            });
    }

    // ---- DELETE ----
    function eliminarGasto(id) {
        post('/gastos/eliminar', { id: id })
            .then(r => aviso(r.ok ? 'Eliminado de Oracle (ID ' + id + ')' : 'Oracle: ' + r.mensaje, r.ok))
            .catch(() => aviso('Sin conexion con el servidor Java', false));
    }

    // ---- Una pasada: compara lo que hay en pantalla con lo ya enviado ----
    let primeraPasada = true;
    function sincronizar() {
        const actuales = new Set();
        (hogaresBD || []).forEach(hogar => {
            (hogar.gastosDiarios || []).forEach(g => {
                if (g.idBD) actuales.add(String(g.idBD));
                if (enCurso.has(g)) return;
                if (!g.idBD || g.firmaBD !== firma(g)) enviarGasto(hogar, g);
            });
        });
        if (primeraPasada) {
            primeraPasada = false;          // el primer recuento solo toma nota de los ids
        } else {
            idsConocidos.forEach(id => {
                if (!actuales.has(String(id))) {
                    idsConocidos.delete(id);
                    eliminarGasto(id);
                }
            });
        }
        actuales.forEach(id => idsConocidos.add(id));
    }

    // app.js llama a guardarHogaresBD() en cada cambio: se envuelve para sincronizar
    guardarHogaresBD = function () {
        guardarOriginal();
        sincronizar();
    };

    // Pasada inicial al abrir la pagina (envia lo pendiente, si el servidor esta activo)
    setTimeout(sincronizar, 600);
})();