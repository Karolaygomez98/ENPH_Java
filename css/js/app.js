document.addEventListener('DOMContentLoaded', () => {
    const productoSelect = document.getElementById('producto-select');
    const campoOtroProducto = document.getElementById('campo-otro-producto');
    const otroProductoInput = document.getElementById('otro-producto-nombre');
    const formGastos = document.getElementById('form-gastos');
    const tablaGastosBody = document.querySelector('#tabla-gastos tbody');

    // Mostrar/Ocultar campo personalizado según la selección
    productoSelect.addEventListener('change', () => {
        if (productoSelect.value === 'otro') {
            campoOtroProducto.classList.remove('hidden');
            otroProductoInput.required = true;
        } else {
            campoOtroProducto.classList.add('hidden');
            otroProductoInput.required = false;
            otroProductoInput.value = '';
        }
    });

    // Agregar registro a la tabla
    formGastos.addEventListener('submit', (e) => {
        e.preventDefault();

        // Obtener el nombre del producto
        let nombreProducto = productoSelect.options[productoSelect.selectedIndex].text;
        if (productoSelect.value === 'otro') {
            nombreProducto = otroProductoInput.value.trim();
        }

        const cantidad = document.getElementById('cantidad').value;
        const valor = parseFloat(document.getElementById('valor').value).toLocaleString('es-CO');

        // Crear una nueva fila en la tabla
        const fila = document.createElement('tr');
        fila.innerHTML = `
            <td>${nombreProducto}</td>
            <td>${cantidad}</td>
            <td>$${valor} COP</td>
        `;

        tablaGastosBody.appendChild(fila);

        // Limpiar el formulario
        formGastos.reset();
        campoOtroProducto.classList.add('hidden');
    });
});