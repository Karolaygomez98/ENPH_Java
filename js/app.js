document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');

    // Verificar si ya hay un usuario en sesión
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
        cargarDashboardInicioSuperior(savedUser);
    }

    if (loginForm) {
        loginForm.addEventListener('submit', (e) => {
            e.preventDefault();

            const usernameInput = document.getElementById('username');
            const passwordInput = document.getElementById('password');

            const username = usernameInput ? usernameInput.value.trim().toLowerCase() : '';
            const password = passwordInput ? passwordInput.value : '';

            if (username && password) {
                let nombreCompleto = 'Karolay Daniela Gómez';
                if (username === 'kdgomez') {
                    nombreCompleto = 'Karolay Daniela Gómez';
                }

                localStorage.setItem('currentUser', nombreCompleto);
                localStorage.setItem('userRole', 'Encuestador');

                cargarDashboardInicioSuperior(nombreCompleto);
            } else {
                alert('Por favor ingrese su usuario y contraseña.');
            }
        });
    }
});

function cargarDashboardInicioSuperior(nombreUsuario) {
    const primerNombre = nombreUsuario.split(' ')[0] || 'Karolay';

    document.body.innerHTML = `
        <div class="app-layout-top">
            <!-- HEADER SUPERIOR PRINCIPAL (VINO TINTO DANE) -->
            <header class="main-top-bar">
                <div class="top-bar-left">
                    <div class="brand-badge-top">EN</div>
                    <div class="brand-titles">
                        <span class="dane-tag">DANE · ENPH</span>
                        <h1>Gestión de hogares <span class="sub-cuadernillo">· Cuadernillo 2</span></h1>
                    </div>
                </div>

                <div class="top-bar-right">
                    <div class="status-badge">
                        <span class="dot"></span>
                        <span>Sistema conectado</span>
                    </div>
                    <div class="user-profile-btn" id="btn-user-profile" title="Haga clic para cerrar sesión">
                        <div class="user-avatar-top">KG</div>
                        <span class="user-name">${nombreUsuario}</span>
                    </div>
                </div>
            </header>

            <!-- BARRA DE NAVEGACIÓN EN LA PARTE SUPERIOR (AHORA CON FONDO VERDE DANE) -->
            <nav class="top-navigation" style="background-color: #2d6a4f; padding: 0.4rem 1.5rem; display: flex; gap: 0.5rem; overflow-x: auto; border-bottom: 2px solid #1b4332;">
                <a href="#inicio" class="nav-link active" style="color: white; text-decoration: none; padding: 0.6rem 1rem; border-radius: 6px; font-weight: 500;"><span class="icon">🏠</span> Inicio</a>
                <a href="#identificacion" class="nav-link" style="color: white; text-decoration: none; padding: 0.6rem 1rem; border-radius: 6px; font-weight: 500;"><span class="badge">A</span> Identificación</a>
                <a href="#control-visitas" class="nav-link" style="color: white; text-decoration: none; padding: 0.6rem 1rem; border-radius: 6px; font-weight: 500;"><span class="badge">B</span> Control de visitas</a>
                <a href="#gasto-alimentos" class="nav-link" style="color: white; text-decoration: none; padding: 0.6rem 1rem; border-radius: 6px; font-weight: 500;"><span class="badge">C</span> Gasto en alimentos</a>
                <a href="#gastos-diarios" class="nav-link" style="color: white; text-decoration: none; padding: 0.6rem 1rem; border-radius: 6px; font-weight: 500;"><span class="badge">D</span> Gastos diarios</a>
                <a href="#comidas-fuera" class="nav-link" style="color: white; text-decoration: none; padding: 0.6rem 1rem; border-radius: 6px; font-weight: 500;"><span class="badge">D1</span> Comidas fuera</a>
                <a href="#consultas" class="nav-link" style="color: white; text-decoration: none; padding: 0.6rem 1rem; border-radius: 6px; font-weight: 500;"><span class="icon">🔍</span> Consultas</a>
            </nav>

            <!-- CONTENIDO DE SECCIONES INTERACTIVAS -->
            <main class="top-content-area" style="padding: 1.5rem;">
                
                <!-- SECCIÓN 1: INICIO -->
                <div id="inicio" class="tab-section active">
                    <div class="welcome-banner">
                        <div>
                            <h2>Buenos días, ${primerNombre} 👋</h2>
                            <p>Consulta y gestiona los hogares que tienes asignados.</p>
                        </div>
                        <div class="jornada-badge">
                            • Jornada activa · Octubre 2026
                        </div>
                    </div>

                    <!-- Tarjetas de Conteo -->
                    <div class="metrics-grid">
                        <div class="metric-card">
                            <div class="card-icon pink-bg">🏠</div>
                            <div class="card-val">8</div>
                            <div class="card-lab">Hogares asignados</div>
                        </div>
                        <div class="metric-card">
                            <div class="card-icon green-bg">✓</div>
                            <div class="card-val">2</div>
                            <div class="card-lab">Hogares completados</div>
                        </div>
                        <div class="metric-card">
                            <div class="card-icon orange-bg">◐</div>
                            <div class="card-val">4</div>
                            <div class="card-lab">En proceso</div>
                        </div>
                        <div class="metric-card">
                            <div class="card-icon blue-bg">!</div>
                            <div class="card-val">2</div>
                            <div class="card-lab">Pendientes</div>
                        </div>
                    </div>

                    <!-- Lista de Hogares -->
                    <div class="hogares-section">
                        <div class="section-header">
                            <div>
                                <h3>Hogares asignados</h3>
                                <p>Selecciona un hogar para iniciar o continuar el diligenciamiento.</p>
                            </div>
                            <button class="btn-refresh" id="btn-actualizar">🔄 Actualizar</button>
                        </div>

                        <div class="filters-bar">
                            <div class="search-box">
                                🔍 <input type="text" id="search-hogar" placeholder="Buscar por código, responsable o dirección...">
                            </div>
                            <div class="filter-tabs">
                                <button class="tab-btn active" data-filter="todos">Todos</button>
                                <button class="tab-btn" data-filter="pendiente">Pendientes</button>
                                <button class="tab-btn" data-filter="proceso">En proceso</button>
                                <button class="tab-btn" data-filter="completado">Completados</button>
                            </div>
                        </div>

                        <div class="hogares-list">
                            <div class="hogar-card" data-status="proceso">
                                <div class="hogar-avatar">MG</div>
                                <div class="hogar-info">
                                    <span class="code">HOG-68001-014</span>
                                    <h4>María Gómez</h4>
                                    <p>Calle 12 # 34-56 · Cabecera</p>
                                </div>
                                <div class="hogar-status status-proceso">En proceso</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- SECCIÓN 2: IDENTIFICACIÓN (A) -->
                <div id="identificacion" class="tab-section" style="display:none;">
                    <div class="hogares-section" style="padding: 2rem;">
                        <h3>Sección A: Identificación del Hogar</h3>
                        <p>Datos generales de la vivienda y del encuestado.</p>
                        <br>
                        <label><strong>Código del Hogar:</strong></label><br>
                        <input type="text" value="HOG-68001-014" readonly style="width:100%; padding: 0.5rem; margin-top:0.5rem;"><br><br>
                        <label><strong>Nombre del Jefe de Hogar:</strong></label><br>
                        <input type="text" value="María Gómez" style="width:100%; padding: 0.5rem; margin-top:0.5rem;"><br><br>
                        <button style="background:#2d6a4f; color:white; border:none; padding:0.6rem 1.2rem; border-radius:4px; cursor:pointer;">Guardar Datos de Identificación</button>
                    </div>
                </div>

                <!-- SECCIÓN 3: GASTOS DIARIOS (D - CUADERNILLO 2) -->
                <div id="gastos-diarios" class="tab-section" style="display:none;">
                    <div class="hogares-section" style="padding: 2rem;">
                        <h3>Sección D: Cuadernillo 2 - Registro de Gastos Diarios</h3>
                        <p>Diligenciamiento de compras del hogar para el backend Java y Base de Datos.</p>
                        <br>
                        <form id="form-gasto">
                            <label><strong>Descripción del Producto:</strong></label><br>
                            <input type="text" id="gasto-producto" placeholder="Ej: Arroz blanco 1Kg" style="width:100%; padding: 0.5rem; margin:0.5rem 0;" required><br>
                            
                            <label><strong>Cantidad:</strong></label><br>
                            <input type="number" step="0.1" id="gasto-cantidad" placeholder="1.0" style="width:100%; padding: 0.5rem; margin:0.5rem 0;" required><br>
                            
                            <label><strong>Valor Pagado ($):</strong></label><br>
                            <input type="number" id="gasto-valor" placeholder="4200" style="width:100%; padding: 0.5rem; margin:0.5rem 0;" required><br><br>
                            
                            <button type="submit" style="background:#2d6a4f; color:white; border:none; padding:0.6rem 1.2rem; border-radius:4px; cursor:pointer; font-weight:bold;">Guardar Gasto en Java / Base de Datos</button>
                        </form>
                    </div>
                </div>

                <!-- OTRAS SECCIONES MUESTRA -->
                <div id="control-visitas" class="tab-section" style="display:none;"><div class="hogares-section" style="padding:2rem;"><h3>Sección B: Control de Visitas</h3><p>Módulo de registro de visitas efectivas o no efectivas.</p></div></div>
                <div id="gasto-alimentos" class="tab-section" style="display:none;"><div class="hogares-section" style="padding:2rem;"><h3>Sección C: Gasto en Alimentos</h3><p>Módulo de alimentos comprados para el hogar.</p></div></div>
                <div id="comidas-fuera" class="tab-section" style="display:none;"><div class="hogares-section" style="padding:2rem;"><h3>Sección D1: Comidas Fuera del Hogar</h3><p>Gastos en restaurantes y establecimientos.</p></div></div>
                <div id="consultas" class="tab-section" style="display:none;"><div class="hogares-section" style="padding:2rem;"><h3>Consultas y Reportes</h3><p>Resumen del estado de los cuadernillos.</p></div></div>

            </main>
        </div>
    `;

    // ==========================================
    // LÓGICA DE INTERACTIVIDAD (CLICS Y PESTAÑAS)
    // ==========================================

    // 1. Manejo de clics en las pestañas verdes superiores
    const navLinks = document.querySelectorAll('.top-navigation .nav-link');
    const sections = document.querySelectorAll('.tab-section');

    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();

            // Desactivar estilos de todas las pestañas
            navLinks.forEach(l => {
                l.classList.remove('active');
                l.style.backgroundColor = 'transparent';
            });

            // Ocultar todas las secciones del contenido
            sections.forEach(s => s.style.display = 'none');

            // Activar la pestaña clickeada
            link.classList.add('active');
            link.style.backgroundColor = '#1b4332'; // Verde oscuro al seleccionar

            // Mostrar la sección correspondiente
            const targetId = link.getAttribute('href').replace('#', '');
            const targetSection = document.getElementById(targetId);

            if (targetSection) {
                targetSection.style.display = 'block';
            }
        });
    });

    // Estilo inicial para la pestaña activa ("Inicio")
    const activeLink = document.querySelector('.top-navigation .nav-link.active');
    if (activeLink) activeLink.style.backgroundColor = '#1b4332';

    // 2. Cerrar sesión al hacer clic en el perfil
    document.getElementById('btn-user-profile').addEventListener('click', () => {
        if (confirm('¿Desea cerrar la sesión activa?')) {
            localStorage.clear();
            location.reload();
        }
    });

    // 3. Formulario de Gastos (Cuadernillo 2)
    const formGasto = document.getElementById('form-gasto');
    if (formGasto) {
        formGasto.addEventListener('submit', (e) => {
            e.preventDefault();
            const producto = document.getElementById('gasto-producto').value;
            const cantidad = document.getElementById('gasto-cantidad').value;
            const valor = document.getElementById('gasto-valor').value;

            alert(`✅ Gasto registrado en el sistema Java / BD:\n\n• Producto: ${producto}\n• Cantidad: ${cantidad}\n• Valor: $${valor}`);
            formGasto.reset();
        });
    }

    // 4. Botón Actualizar
    const btnActualizar = document.getElementById('btn-actualizar');
    if (btnActualizar) {
        btnActualizar.addEventListener('click', () => {
            alert('🔄 Sincronizando información de hogares con la Base de Datos PostgreSQL...');
        });
    }
}