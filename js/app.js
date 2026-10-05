document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');

    // Limpia la sesión que quedó guardada de versiones anteriores
    localStorage.removeItem('currentUser');
    localStorage.removeItem('userRole');

    // La sesión vive solo mientras la pestaña esté abierta
    const savedUser = sessionStorage.getItem('currentUser');
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

                sessionStorage.setItem('currentUser', nombreCompleto);
                sessionStorage.setItem('userRole', 'Encuestador');

                cargarDashboardInicioSuperior(nombreCompleto);
            } else {
                alert('Por favor ingrese su usuario y contraseña.');
            }
        });
    }
});

// Base de datos oficial de los 25 grupos de alimentos del Capítulo C
const alimentosCapituloC = [
    { id: "01", nombre: "Pan, arepas, bollos y almojábanas" },
    { id: "02", nombre: "Galletas de sal y de dulce" },
    { id: "03", nombre: "Arroz, pastas alimenticias, avena, maíz, harinas y otros cereales (para el desayuno)" },
    { id: "04", nombre: "Carne de res, cerdo, hueso y vísceras" },
    { id: "05", nombre: "Pollo, otras aves y menudencias" },
    { id: "06", nombre: "Salchichas, jamón, mortadela, salchichón y otras carnes frías preparadas (incluidos los embutidos vegetarianos)" },
    { id: "07", nombre: "Pescado de río, mar y otros productos marinos (frescos, congelados y enlatados)" },
    { id: "08", nombre: "Leche (animal y vegetal)" },
    { id: "09", nombre: "Queso (animal y vegetal)" },
    { id: "10", nombre: "Otros derivados de la leche: yogur, kumis, etc." },
    { id: "11", nombre: "Huevos" },
    { id: "12", nombre: "Aceites y grasas: aceite, manteca, mantequilla, margarina, etc." },
    { id: "13", nombre: "Frutas" },
    { id: "14", nombre: "Verduras y hortalizas: tomate, cebolla, arveja, espinaca, apio, zanahoria, etc." },
    { id: "15", nombre: "Granos secos: fríjol, lenteja, garbanzo, etc." },
    { id: "16", nombre: "Enlatados y encurtidos: arveja, fríjol, maíz, espárragos, zanahoria, habichuela, etc." },
    { id: "17", nombre: "Plátano, yuca, arracacha, ñame, papa y otros tubérculos" },
    { id: "18", nombre: "Azúcar, panela y otros endulzantes" },
    { id: "19", nombre: "Mermeladas, arequipe, bocadillos, compotas, dulces, helados, chocolatinas, ponquecitos, etc." },
    { id: "20", nombre: "Salsas, mayonesa, mostaza, vinagre, etc." },
    { id: "21", nombre: "Sal y condimentos" },
    { id: "22", nombre: "Café, chocolate, otras bebidas con cacao (milo, chocolisto), té y hierbas aromáticas" },
    { id: "23", nombre: "Papas fritas, chitos, maicitos, patacones, besitos, maní, etc." },
    { id: "24", nombre: "Agua, gaseosas, refrescos, jugos, té frío y otras bebidas no alcohólicas" },
    { id: "25", nombre: "Alimentos y bebidas preparados fuera del hogar, para consumir dentro o fuera del hogar" }
];

// Opciones de mercados globales (Códigos 95 al 98)
const mercadosGlobalesCapC = [
    { codigo: "95", concepto: "Carnes, huevos, leche y sus derivados" },
    { codigo: "96", concepto: "Frutas y verduras" },
    { codigo: "97", concepto: "Granos, harinas, cereales, azúcar y abarrotes en general" },
    { codigo: "98", concepto: "Mercado único (carnes, frutas, verduras y granos)" }
];

// Base de datos inicial por defecto
const hogaresPredeterminados = [
    {
        codigo: "HOG-68001-014",
        jefe: "María Gómez",
        direccion: "Calle 12 # 34-56 · Cabecera",
        direccionFisica: "Calle 12 # 34-56",
        estado: "proceso",
        etiqueta: "En proceso",
        orden: "01",
        progreso: 72,
        resultado: "EC – Encuesta completa",
        visitas: [
            { num: 1, fecha: "01/09/2026", inicio: "08:00", fin: "08:40", resultado: "COMPLETA", observacion: "Información inicial registrada." },
            { num: 2, fecha: "03/09/2026", inicio: "09:10", fin: "09:50", resultado: "COMPLETA", observacion: "Se continúa seguimiento." },
            { num: 3, fecha: "05/09/2026", inicio: "08:30", fin: "09:15", resultado: "EN PROCESO", observacion: "Pendiente capítulo D." }
        ],
        capituloC: {},
        gastosDiarios: [],
        comidasFuera: []
    },
    {
        codigo: "HOG-68001-015",
        jefe: "Carlos Rodríguez",
        direccion: "Carrera 27 # 45-12 · Centro",
        direccionFisica: "Carrera 27 # 45-12",
        estado: "completado",
        etiqueta: "Completado",
        orden: "02",
        progreso: 100,
        resultado: "EC – Encuesta completa",
        visitas: [
            { num: 1, fecha: "02/09/2026", inicio: "10:00", fin: "11:30", resultado: "COMPLETA", observacion: "Cuadernillo diligenciado en su totalidad." }
        ],
        capituloC: {},
        gastosDiarios: [],
        comidasFuera: []
    },
    {
        codigo: "HOG-68001-016",
        jefe: "Ana Lucía Martínez",
        direccion: "Calle 36 # 15-20 · San Francisco",
        direccionFisica: "Calle 36 # 15-20",
        estado: "proceso",
        etiqueta: "En proceso",
        orden: "03",
        progreso: 45,
        resultado: "EIn – Encuesta incompleta",
        visitas: [
            { num: 1, fecha: "04/09/2026", inicio: "14:00", fin: "14:45", resultado: "EN PROCESO", observacion: "Inició capítulo A y B." }
        ],
        capituloC: {},
        gastosDiarios: [],
        comidasFuera: []
    },
    {
        codigo: "HOG-68001-017",
        jefe: "Jorge Eliecer Prado",
        direccion: "Diagonal 15 # 56-08 · La Concordia",
        direccionFisica: "Diagonal 15 # 56-08",
        estado: "pendiente",
        etiqueta: "Pendiente",
        orden: "04",
        progreso: 0,
        resultado: "EIn – Encuesta incompleta",
        visitas: [],
        capituloC: {},
        gastosDiarios: [],
        comidasFuera: []
    },
    {
        codigo: "HOG-68001-018",
        jefe: "Esperanza Caicedo",
        direccion: "Transversal 78 # 21-34 · Provenza",
        direccionFisica: "Transversal 78 # 21-34",
        estado: "completado",
        etiqueta: "Completado",
        orden: "05",
        progreso: 100,
        resultado: "EC – Encuesta completa",
        visitas: [],
        capituloC: {},
        gastosDiarios: [],
        comidasFuera: []
    },
    {
        codigo: "HOG-68001-019",
        jefe: "Pedro Antonio Suárez",
        direccion: "Calle 105 # 24-11 · Cañaveral",
        direccionFisica: "Calle 105 # 24-11",
        estado: "proceso",
        etiqueta: "En proceso",
        orden: "06",
        progreso: 60,
        resultado: "EC – Encuesta completa",
        visitas: [],
        capituloC: {},
        gastosDiarios: [],
        comidasFuera: []
    },
    {
        codigo: "HOG-68001-020",
        jefe: "Claudia Patricia Reyes",
        direccion: "Carrera 33 # 52-40 · Cabecera",
        direccionFisica: "Carrera 33 # 52-40",
        estado: "proceso",
        etiqueta: "En proceso",
        orden: "07",
        progreso: 30,
        resultado: "EIn – Encuesta incompleta",
        visitas: [],
        capituloC: {},
        gastosDiarios: [],
        comidasFuera: []
    },
    {
        codigo: "HOG-68001-021",
        jefe: "Hernando Vargas",
        direccion: "Calle 45 # 9-18 · Real de Minas",
        direccionFisica: "Calle 45 # 9-18",
        estado: "pendiente",
        etiqueta: "Pendiente",
        orden: "08",
        progreso: 0,
        resultado: "EIn – Encuesta incompleta",
        visitas: [],
        capituloC: {},
        gastosDiarios: [],
        comidasFuera: []
    }
];

// Cargar o inicializar la Base de Datos desde LocalStorage
let hogaresBD = JSON.parse(localStorage.getItem('hogaresBD_dane')) || hogaresPredeterminados;

// Función que se asigna al abrir el panel para mostrar "Guardado automáticamente"
let onGuardado = null;

function guardarHogaresBD() {
    try {
        localStorage.setItem('hogaresBD_dane', JSON.stringify(hogaresBD));
        if (onGuardado) onGuardado(true);
    } catch (e) {
        console.error('No se pudo guardar:', e);
        if (onGuardado) onGuardado(false);
    }
}

// Guarda los datos iniciales si no existen
if (!localStorage.getItem('hogaresBD_dane')) {
    guardarHogaresBD();
}

function cargarDashboardInicioSuperior(nombreUsuario) {
    const primerNombre = nombreUsuario.split(' ')[0] || 'Karolay';

    // Banner del hogar activo
    const bannerHogar = `
        <div class="banner-hogar-activo" style="background-color: #3b1928; color: white; border-radius: 12px; padding: 1.2rem 1.8rem; display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; box-shadow: 0 4px 12px rgba(0,0,0,0.08);">
            <div style="display: flex; align-items: center; gap: 1.2rem;">
                <div style="background: rgba(255,255,255,0.1); width: 42px; height: 42px; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 1.2rem;">🏠</div>
                <div>
                    <div class="det-header-codigo" style="font-size: 0.75rem; color: #ddd; letter-spacing: 0.5px; font-weight: bold;">HOGAR ACTIVO</div>
                    <div class="det-header-jefe" style="font-size: 1.3rem; font-weight: 700; margin: 2px 0;"></div>
                    <div class="det-header-dir" style="font-size: 0.85rem; color: #ccc;"></div>
                </div>
            </div>
            <div style="display: flex; align-items: center; gap: 2rem;">
                <div style="text-align: right; width: 180px;">
                    <div style="display: flex; justify-content: space-between; font-size: 0.8rem; color: #ddd; margin-bottom: 6px;">
                        <span>Progreso</span>
                        <span class="det-header-progreso-val" style="font-weight: bold;">0%</span>
                    </div>
                    <div style="width: 100%; background: rgba(255,255,255,0.2); height: 6px; border-radius: 10px; overflow: hidden;">
                        <div class="det-header-progreso-bar" style="width: 0%; background-color: #ff6b8b; height: 100%;"></div>
                    </div>
                </div>
                <button class="btn-cambiar-hogar" style="background-color: white; color: #333; border: none; padding: 0.6rem 1.2rem; border-radius: 8px; font-weight: 600; cursor: pointer;">Cambiar hogar</button>
            </div>
        </div>
    `;

    document.body.innerHTML = `
        <style>
            .hogares-grid-container {
                display: flex;
                flex-direction: column;
                gap: 0.75rem;
                margin-top: 1.5rem;
            }
            .hogar-card-modern {
                background: #ffffff;
                border: 1px solid #e5e7eb;
                border-left: 5px solid #800020;
                border-radius: 12px;
                padding: 1rem 1.25rem;
                display: flex;
                flex-direction: row;
                align-items: center;
                justify-content: space-between;
                gap: 1rem;
                box-shadow: 0 2px 6px rgba(0,0,0,0.04);
                cursor: pointer;
                transition: transform 0.2s ease, box-shadow 0.2s ease, border-left-color 0.2s ease;
            }
            .hogar-card-modern:hover {
                transform: translateY(-2px);
                box-shadow: 0 8px 16px rgba(0,0,0,0.08);
                border-left-color: #1e5235;
            }
            .hogar-code-tag {
                font-size: 0.75rem;
                font-weight: 700;
                color: #6b7280;
                letter-spacing: 0.5px;
            }
            .badge-status {
                padding: 0.25rem 0.65rem;
                border-radius: 20px;
                font-size: 0.72rem;
                font-weight: 700;
                white-space: nowrap;
            }
            .badge-proceso { background-color: #fef3c7; color: #d97706; }
            .badge-completado { background-color: #d1fae5; color: #059669; }
            .badge-pendiente { background-color: #dbeafe; color: #2563eb; }

            .hogar-card-body {
                display: flex;
                align-items: center;
                gap: 1rem;
                flex: 1;
                margin: 0;
            }
            .hogar-avatar-icon {
                width: 44px;
                height: 44px;
                border-radius: 10px;
                background-color: #f3f4f6;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 1.25rem;
                flex-shrink: 0;
            }
            .hogar-card-title {
                margin: 0.1rem 0 0.25rem 0;
                font-size: 1.1rem;
                font-weight: 700;
                color: #111827;
            }
            .hogar-card-dir {
                margin: 0;
                font-size: 0.83rem;
                color: #6b7280;
                line-height: 1.3;
            }
            @media (max-width: 600px) {
                .hogar-card-modern { flex-direction: column; align-items: flex-start; }
            }
        </style>

        <div class="app-layout-top">
            <div id="indicador-guardado" style="position: fixed; bottom: 16px; right: 16px; z-index: 9999; background: #1e5235; color: white; padding: 0.5rem 1rem; border-radius: 20px; font-size: 0.8rem; font-weight: 600; box-shadow: 0 4px 12px rgba(0,0,0,0.2); opacity: 0.55; transition: opacity 0.3s;">✓ Autoguardado activo</div>

            <!-- HEADER SUPERIOR PRINCIPAL -->
            <header class="main-top-bar">
                <div class="top-bar-left">
                    <img src="img/logo-udi-dane.png" alt="Emblema UDI · DANE de Vivienda y Análisis" class="brand-logo-top">
                    <div class="brand-titles">
                        <span class="dane-tag">DANE · ENPH</span>
                        <h1>Gestión de hogares <span class="sub-cuadernillo">· Cuadernillo 2</span></h1>
                    </div>
                </div>

                <div class="top-bar-right">
                    <div class="status-badge">
                        <span class="dot" style="background:#2ec4b6; width:8px; height:8px; border-radius:50%; display:inline-block;"></span>
                        <span>Sistema conectado</span>
                    </div>
                    <div class="user-profile-btn" id="btn-user-profile" title="Haga clic para cerrar sesión">
                        <div class="user-avatar-top">KG</div>
                        <span class="user-name">${nombreUsuario}</span>
                    </div>
                </div>
            </header>

            <!-- BARRA DE NAVEGACIÓN SUPERIOR -->
            <nav class="top-navigation" style="background-color: #2d6a4f; padding: 0.4rem 1.5rem; display: flex; gap: 0.5rem; overflow-x: auto; border-bottom: 2px solid #1b4332;">
                <a href="#inicio" class="nav-link active" style="color: white; text-decoration: none; padding: 0.6rem 1rem; border-radius: 6px; font-weight: 500;"><span class="icon">🏠</span> Inicio</a>
                <a href="#identificacion" class="nav-link" style="color: white; text-decoration: none; padding: 0.6rem 1rem; border-radius: 6px; font-weight: 500;"><span class="badge">A</span> Identificación</a>
                <a href="#control-visitas" class="nav-link" style="color: white; text-decoration: none; padding: 0.6rem 1rem; border-radius: 6px; font-weight: 500;"><span class="badge">B</span> Control de visitas</a>
                <a href="#gasto-alimentos" class="nav-link" style="color: white; text-decoration: none; padding: 0.6rem 1rem; border-radius: 6px; font-weight: 500;"><span class="badge">C</span> Gasto en alimentos</a>
                <a href="#gastos-diarios" class="nav-link" style="color: white; text-decoration: none; padding: 0.6rem 1rem; border-radius: 6px; font-weight: 500;"><span class="badge">D</span> Gastos diarios</a>
                <a href="#consultas" class="nav-link" style="color: white; text-decoration: none; padding: 0.6rem 1rem; border-radius: 6px; font-weight: 500;"><span class="icon">🔍</span> Consultas</a>
                <a href="#reportes" class="nav-link" style="color: white; text-decoration: none; padding: 0.6rem 1rem; border-radius: 6px; font-weight: 500;"><span class="icon">📊</span> Reportes</a>
            </nav>

            <!-- CONTENIDO DE SECCIONES -->
            <main class="top-content-area" style="padding: 1.5rem; background-color: #f8f9fa; min-height: 85vh;">

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

                    <div class="metrics-grid">
                        <div class="metric-card" data-metric="todos" style="cursor: pointer;" title="Clic para ver todos los hogares">
                            <div class="card-icon pink-bg">🏠</div>
                            <div class="card-val" id="metrica-todos">8</div>
                            <div class="card-lab">Hogares asignados</div>
                        </div>
                        <div class="metric-card" data-metric="completado" style="cursor: pointer;" title="Clic para ver completados">
                            <div class="card-icon green-bg">✓</div>
                            <div class="card-val" id="metrica-completados">2</div>
                            <div class="card-lab">Hogares completados</div>
                        </div>
                        <div class="metric-card" data-metric="proceso" style="cursor: pointer;" title="Clic para ver en proceso">
                            <div class="card-icon orange-bg">◐</div>
                            <div class="card-val" id="metrica-proceso">4</div>
                            <div class="card-lab">En proceso</div>
                        </div>
                        <div class="metric-card" data-metric="pendiente" style="cursor: pointer;" title="Clic para ver pendientes">
                            <div class="card-icon blue-bg">!</div>
                            <div class="card-val" id="metrica-pendientes">2</div>
                            <div class="card-lab">Pendientes</div>
                        </div>
                    </div>

                    <div class="hogares-section" style="background: white; border-radius: 12px; padding: 1.5rem; border: 1px solid #e9ecef;">
                        <div class="section-header" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.2rem;">
                            <div>
                                <h3 style="margin:0; font-size: 1.25rem;">Hogares asignados</h3>
                                <p style="margin: 0.2rem 0 0 0; color: #666; font-size: 0.88rem;">Haz clic en un hogar para iniciar o continuar el diligenciamiento.</p>
                            </div>
                            <button class="btn-refresh" id="btn-actualizar" style="background: #f1f3f5; border: 1px solid #ced4da; padding: 0.5rem 1rem; border-radius: 6px; cursor: pointer; font-weight: 600;">🔄 Actualizar</button>
                        </div>

                        <div class="filters-bar" style="display: flex; gap: 1rem; justify-content: space-between; align-items: center; flex-wrap: wrap;">
                            <div class="search-box" style="flex: 1; min-width: 280px; background: #f8f9fa; border: 1px solid #ced4da; border-radius: 8px; padding: 0.5rem 0.8rem; display: flex; align-items: center; gap: 0.5rem;">
                                🔍 <input type="text" id="search-hogar" placeholder="Buscar por código, responsable o dirección..." style="border: none; background: transparent; outline: none; width: 100%; font-size: 0.9rem;">
                            </div>
                            <div class="filter-tabs" style="display: flex; gap: 0.4rem;">
                                <button class="tab-btn active" data-filter="todos">Todos</button>
                                <button class="tab-btn" data-filter="pendiente">Pendientes</button>
                                <button class="tab-btn" data-filter="proceso">En proceso</button>
                                <button class="tab-btn" data-filter="completado">Completados</button>
                            </div>
                        </div>

                        <!-- LISTA VERTICAL DE HOGARES -->
                        <div class="hogares-grid-container" id="hogares-container"></div>
                    </div>
                </div>

                <!-- SECCIÓN 2: IDENTIFICACIÓN (A) -->
                <div id="identificacion" class="tab-section" style="display:none; max-width: 1100px; margin: 0 auto;">
                    <div style="font-size: 0.8rem; color: #777; font-weight: bold; letter-spacing: 0.5px; margin-bottom: 0.2rem;">
                        HOGAR ACTIVO · CAPÍTULO A
                    </div>
                    <h2 style="font-size: 1.8rem; color: #222; margin-top: 0; margin-bottom: 1.5rem; font-weight: 800;">
                        Identificación
                    </h2>

                    ${bannerHogar}

                    <div style="background: white; border-radius: 12px; padding: 2rem; box-shadow: 0 2px 8px rgba(0,0,0,0.04); border: 1px solid #e9ecef;">
                        <h3 style="margin-top: 0; font-size: 1.2rem; color: #222; margin-bottom: 0.2rem;">Información territorial</h3>
                        <p style="color: #777; font-size: 0.85rem; margin-bottom: 1.8rem;">Los datos territoriales precargados son de solo lectura.</p>

                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; margin-bottom: 1.5rem;">
                            <div>
                                <label style="display: block; font-size: 0.75rem; font-weight: 800; color: #555; text-transform: uppercase; margin-bottom: 0.5rem; letter-spacing: 0.5px;">ID DEL HOGAR</label>
                                <input type="text" id="det-input-id" readonly style="width: 100%; padding: 0.8rem; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #f1f3f5; color: #6c757d; font-weight: 600; cursor: not-allowed; box-sizing: border-box;">
                            </div>
                            <div>
                                <label style="display: block; font-size: 0.75rem; font-weight: 800; color: #555; text-transform: uppercase; margin-bottom: 0.5rem; letter-spacing: 0.5px;">DIRECCIÓN FÍSICA TERRITORIAL</label>
                                <input type="text" id="det-input-dir" readonly style="width: 100%; padding: 0.8rem; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #f1f3f5; color: #6c757d; font-weight: 600; cursor: not-allowed; box-sizing: border-box;">
                            </div>
                        </div>

                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; margin-bottom: 1.5rem;">
                            <div>
                                <label style="display: block; font-size: 0.75rem; font-weight: 800; color: #555; text-transform: uppercase; margin-bottom: 0.5rem; letter-spacing: 0.5px;">RESPONSABLE DEL GASTO</label>
                                <input type="text" id="det-input-jefe" readonly style="width: 100%; padding: 0.8rem; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #f1f3f5; color: #6c757d; font-weight: 600; cursor: not-allowed; box-sizing: border-box;">
                            </div>
                            <div>
                                <label style="display: block; font-size: 0.75rem; font-weight: 800; color: #555; text-transform: uppercase; margin-bottom: 0.5rem; letter-spacing: 0.5px;">NÚMERO DE ORDEN</label>
                                <input type="text" id="det-input-orden" style="width: 100%; padding: 0.8rem; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #fff; color: #222; font-weight: 500; box-sizing: border-box;">
                            </div>
                        </div>

                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; margin-bottom: 2.5rem;">
                            <div>
                                <label style="display: block; font-size: 0.75rem; font-weight: 800; color: #555; text-transform: uppercase; margin-bottom: 0.5rem; letter-spacing: 0.5px;">RESULTADO DEL CUADERNILLO 2</label>
                                <select id="det-select-resultado" style="width: 100%; padding: 0.8rem; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #fff; color: #222; font-weight: 500; box-sizing: border-box;">
                                    <option value="EC – Encuesta completa">EC – Encuesta completa</option>
                                    <option value="EIn – Encuesta incompleta">EIn – Encuesta incompleta</option>
                                </select>
                            </div>
                            <div>
                                <label style="display: block; font-size: 0.75rem; font-weight: 800; color: #555; text-transform: uppercase; margin-bottom: 0.5rem; letter-spacing: 0.5px;">ESTADO DEL CASO</label>
                                <select id="det-select-estado" style="width: 100%; padding: 0.8rem; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #fff; color: #222; font-weight: 500; box-sizing: border-box;">
                                    <option value="proceso">En proceso</option>
                                    <option value="completado">Completado</option>
                                    <option value="pendiente">Pendiente</option>
                                </select>
                            </div>
                        </div>

                        <div style="display: flex; gap: 1rem;">
                            <button id="btn-guardar-cambios" style="background-color: #a11242; color: white; border: none; padding: 0.8rem 1.6rem; border-radius: 8px; font-weight: 600; cursor: pointer;">Guardar cambios</button>
                            <button id="btn-continuar-ident" style="background-color: #f8f9fa; color: #333; border: 1px solid #e0e0e0; padding: 0.8rem 1.6rem; border-radius: 8px; font-weight: 600; cursor: pointer;">Continuar →</button>
                        </div>
                    </div>
                </div>

                <!-- SECCIÓN 3: CONTROL DE VISITAS (B) -->
                <div id="control-visitas" class="tab-section" style="display:none; max-width: 1100px; margin: 0 auto;">
                    <div style="font-size: 0.8rem; color: #777; font-weight: bold; letter-spacing: 0.5px; margin-bottom: 0.2rem;">
                        HOGAR ACTIVO · CAPÍTULO B
                    </div>
                    <h2 style="font-size: 1.8rem; color: #222; margin-top: 0; margin-bottom: 1.5rem; font-weight: 800;">
                        Control de visitas
                    </h2>

                    ${bannerHogar}

                    <div style="background: white; border-radius: 12px; padding: 2rem; box-shadow: 0 2px 8px rgba(0,0,0,0.04); border: 1px solid #e9ecef;">
                        <h3 style="margin-top: 0; font-size: 1.2rem; color: #222; margin-bottom: 0.2rem;">Historial de visitas</h3>
                        <p style="color: #777; font-size: 0.85rem; margin-bottom: 1.5rem;">Registro cronológico del contacto con el hogar.</p>

                        <div style="overflow-x: auto; margin-bottom: 2rem;">
                            <table style="width: 100%; border-collapse: collapse; text-align: left; font-size: 0.85rem;">
                                <thead>
                                    <tr style="background-color: #f8f9fa; color: #555; text-transform: uppercase; font-size: 0.7rem; letter-spacing: 0.5px; border-bottom: 1px solid #e0e0e0;">
                                        <th style="padding: 0.8rem 1rem;">VISITA</th>
                                        <th style="padding: 0.8rem 1rem;">FECHA</th>
                                        <th style="padding: 0.8rem 1rem;">INICIO</th>
                                        <th style="padding: 0.8rem 1rem;">FIN</th>
                                        <th style="padding: 0.8rem 1rem;">RESULTADO</th>
                                        <th style="padding: 0.8rem 1rem;">OBSERVACIÓN</th>
                                    </tr>
                                </thead>
                                <tbody id="tabla-visitas-body"></tbody>
                            </table>
                        </div>

                        <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 1.2rem; margin-bottom: 1.2rem;">
                            <div>
                                <label style="display: block; font-size: 0.7rem; font-weight: 800; color: #555; text-transform: uppercase; margin-bottom: 0.4rem;">FECHA</label>
                                <input type="text" id="visita-fecha" value="06/10/2026" style="width: 100%; padding: 0.75rem; border: 1px solid #e0e0e0; border-radius: 8px; box-sizing: border-box;">
                            </div>
                            <div>
                                <label style="display: block; font-size: 0.7rem; font-weight: 800; color: #555; text-transform: uppercase; margin-bottom: 0.4rem;">HORA INICIO</label>
                                <input type="text" id="visita-inicio" value="08:30" style="width: 100%; padding: 0.75rem; border: 1px solid #e0e0e0; border-radius: 8px; box-sizing: border-box;">
                            </div>
                            <div>
                                <label style="display: block; font-size: 0.7rem; font-weight: 800; color: #555; text-transform: uppercase; margin-bottom: 0.4rem;">HORA FIN</label>
                                <input type="text" id="visita-fin" value="09:15" style="width: 100%; padding: 0.75rem; border: 1px solid #e0e0e0; border-radius: 8px; box-sizing: border-box;">
                            </div>
                        </div>

                        <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 1.2rem; margin-bottom: 2rem;">
                            <div>
                                <label style="display: block; font-size: 0.7rem; font-weight: 800; color: #555; text-transform: uppercase; margin-bottom: 0.4rem;">RESULTADO</label>
                                <select id="visita-resultado" style="width: 100%; padding: 0.75rem; border: 1px solid #e0e0e0; border-radius: 8px; background: white; box-sizing: border-box;">
                                    <option value="Completa">Completa</option>
                                    <option value="En proceso">En proceso</option>
                                    <option value="Incompleta">Incompleta</option>
                                </select>
                            </div>
                            <div>
                                <label style="display: block; font-size: 0.7rem; font-weight: 800; color: #555; text-transform: uppercase; margin-bottom: 0.4rem;">PARTES PENDIENTES</label>
                                <input type="text" id="visita-partes" placeholder="Ej. capítulo D" style="width: 100%; padding: 0.75rem; border: 1px solid #e0e0e0; border-radius: 8px; box-sizing: border-box;">
                            </div>
                            <div>
                                <label style="display: block; font-size: 0.7rem; font-weight: 800; color: #555; text-transform: uppercase; margin-bottom: 0.4rem;">PRÓXIMA CITA</label>
                                <input type="text" id="visita-proxima" placeholder="dd/mm/aaaa --:--" style="width: 100%; padding: 0.75rem; border: 1px solid #e0e0e0; border-radius: 8px; box-sizing: border-box;">
                            </div>
                        </div>

                        <div style="display: flex; gap: 1rem;">
                            <button id="btn-registrar-visita" style="background-color: #a11242; color: white; border: none; padding: 0.8rem 1.6rem; border-radius: 8px; font-weight: 600; cursor: pointer;">
                                Registrar visita
                            </button>
                            <button id="btn-continuar-visita" style="background-color: #f8f9fa; color: #333; border: 1px solid #e0e0e0; padding: 0.8rem 1.6rem; border-radius: 8px; font-weight: 600; cursor: pointer;">
                                Continuar →
                            </button>
                        </div>
                    </div>
                </div>

                <!-- SECCIÓN 4: GASTO EN ALIMENTOS (C) -->
                <div id="gasto-alimentos" class="tab-section" style="display:none; max-width: 1200px; margin: 0 auto;">
                    <div style="font-size: 0.8rem; color: #777; font-weight: bold; letter-spacing: 0.5px; margin-bottom: 0.2rem;">
                        HOGAR ACTIVO · CAPÍTULO C
                    </div>
                    <h2 style="font-size: 1.8rem; color: #222; margin-top: 0; margin-bottom: 1.5rem; font-weight: 800;">
                        C. CARACTERÍSTICAS DEL GASTO EN ALIMENTOS DE LOS HOGARES
                    </h2>

                    ${bannerHogar}

                    <div style="background: white; border-radius: 12px; padding: 2rem; box-shadow: 0 2px 8px rgba(0,0,0,0.04); border: 1px solid #e9ecef; margin-bottom: 2rem;">
                        <h3 style="margin-top: 0; font-size: 1.1rem; color: #333; margin-bottom: 0.5rem;">
                            ¿Con qué frecuencia compran generalmente los siguientes alimentos o grupos de alimentos en el hogar?
                        </h3>
                        <p style="color: #666; font-size: 0.82rem; margin-bottom: 1.5rem;">
                            Diligencie la frecuencia de compra (1 a 9). Si la frecuencia es superior a la bisemana, complete los campos condicionales 3.1 y 3.2.
                        </p>

                        <div style="overflow-x: auto; margin-bottom: 1.5rem; border: 1px solid #dee2e6; border-radius: 8px;">
                            <table style="width: 100%; border-collapse: collapse; font-size: 0.8rem; text-align: left;">
                                <thead>
                                    <tr style="background-color: #f1f3f5; color: #333; text-align: center; border-bottom: 2px solid #dee2e6;">
                                        <th style="padding: 0.6rem; border-right: 1px solid #dee2e6; width: 40px;" rowspan="2">Item</th>
                                        <th style="padding: 0.6rem; border-right: 1px solid #dee2e6; text-align: left;" rowspan="2">1. Alimentos o grupos de alimentos</th>
                                        <th style="padding: 0.6rem; border-right: 1px solid #dee2e6;" colspan="8">2. Frecuencia de compra</th>
                                        <th style="padding: 0.6rem;" colspan="2">3. Si la frecuencia es superior a la bisemana</th>
                                    </tr>
                                    <tr style="background-color: #f8f9fa; color: #555; text-align: center; border-bottom: 2px solid #dee2e6; font-size: 0.72rem;">
                                        <th style="padding: 0.4rem; border-right: 1px solid #dee2e6;">1. Nunca</th>
                                        <th style="padding: 0.4rem; border-right: 1px solid #dee2e6;">2. Diario</th>
                                        <th style="padding: 0.4rem; border-right: 1px solid #dee2e6;">2.1. V/Sem</th>
                                        <th style="padding: 0.4rem; border-right: 1px solid #dee2e6;">3. Sem</th>
                                        <th style="padding: 0.4rem; border-right: 1px solid #dee2e6;">4. Quinc</th>
                                        <th style="padding: 0.4rem; border-right: 1px solid #dee2e6;">5. Mens</th>
                                        <th style="padding: 0.4rem; border-right: 1px solid #dee2e6;">6. Bim</th>
                                        <th style="padding: 0.4rem; border-right: 1px solid #dee2e6;">7/9. Trim/Esp</th>
                                        <th style="padding: 0.4rem; border-right: 1px solid #dee2e6; width: 130px;">3.1. ¿Valor última compra? ($)</th>
                                        <th style="padding: 0.4rem; width: 140px;">3.2. ¿Comprará próx. 14 días?</th>
                                    </tr>
                                </thead>
                                <tbody id="tabla-capitulo-c-body"></tbody>
                            </table>
                        </div>

                        <div style="background-color: #f8f9fa; border: 1px solid #e0e0e0; border-radius: 8px; padding: 1.2rem; margin-top: 2rem;">
                            <h4 style="margin-top: 0; color: #222; font-size: 0.95rem; margin-bottom: 0.4rem;">
                                Registro de Mercado Global (Si la frecuencia es superior a la bisemana y no se logró desagregar)
                            </h4>
                            <div id="contenedor-mercados-globales" style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;"></div>
                        </div>

                        <div style="margin-top: 1.5rem;">
                            <label style="display: block; font-size: 0.75rem; font-weight: 800; color: #555; text-transform: uppercase; margin-bottom: 0.4rem;">OBSERVACIONES DEL CAPÍTULO C</label>
                            <textarea id="obs-capitulo-c" rows="3" style="width: 100%; padding: 0.75rem; border: 1px solid #e0e0e0; border-radius: 8px; box-sizing: border-box;" placeholder="Escriba observaciones del capítulo..."></textarea>
                        </div>

                        <div style="display: flex; gap: 1rem; margin-top: 1.8rem;">
                            <button id="btn-guardar-alimentos" style="background-color: #a11242; color: white; border: none; padding: 0.8rem 1.6rem; border-radius: 8px; font-weight: 600; cursor: pointer;">
                                Guardar Capítulo C
                            </button>
                            <button id="btn-continuar-alimentos" style="background-color: #f8f9fa; color: #333; border: 1px solid #e0e0e0; padding: 0.8rem 1.6rem; border-radius: 8px; font-weight: 600; cursor: pointer;">
                                Continuar a Capítulo D →
                            </button>
                        </div>
                    </div>
                </div>

                <!-- SECCIÓN 5: GASTOS DIARIOS (D) -->
                <div id="gastos-diarios" class="tab-section" style="display:none; max-width: 1100px; margin: 0 auto;">
                    <div style="font-size: 0.8rem; color: #777; font-weight: bold; letter-spacing: 0.5px; margin-bottom: 0.2rem;">
                        HOGAR ACTIVO · CAPÍTULO D
                    </div>
                    <h2 style="font-size: 1.8rem; color: #222; margin-top: 0; margin-bottom: 1.5rem; font-weight: 800;">Gastos diarios</h2>

                    ${bannerHogar}

                    <div style="background: white; border-radius: 12px; padding: 2rem; box-shadow: 0 2px 8px rgba(0,0,0,0.04); border: 1px solid #e9ecef;">

                        <div style="margin-bottom: 1.5rem;">
                            <label style="display: block; font-size: 0.75rem; font-weight: 800; color: #555; text-transform: uppercase; margin-bottom: 0.6rem; letter-spacing: 0.5px;">
                                Día de recolección (1 al 14)
                            </label>
                            <div id="selector-dias" style="display: flex; flex-wrap: wrap; gap: 0.4rem;"></div>
                            <p style="margin: 0.6rem 0 0 0; color: #777; font-size: 0.78rem;">El punto verde indica que ese día ya tiene información registrada.</p>
                        </div>

                        <h3 id="titulo-dia-d" style="margin: 0 0 1rem 0; font-size: 1.2rem; color: #5c1822; border-bottom: 2px solid #e0e0e0; padding-bottom: 0.4rem;">Día 1 de 14</h3>

                        <h4 style="margin: 0 0 0.8rem 0; color: #222;">Gastos del día</h4>
                        <p style="margin: -0.4rem 0 1rem 0; color: #777; font-size: 0.8rem;">Puede editar directamente cualquier celda de la tabla; los cambios se guardan automáticamente.</p>
                        <div style="display: grid; grid-template-columns: 1fr 1fr 2fr 1fr; gap: 1rem; margin-bottom: 1rem;">
                            <div>
                                <label style="display: block; font-size: 0.7rem; font-weight: 800; color: #555; text-transform: uppercase; margin-bottom: 0.4rem;">FECHA COMPRA</label>
                                <input type="date" id="gasto-d-fecha" style="width: 100%; padding: 0.75rem; border: 1px solid #e0e0e0; border-radius: 8px; box-sizing: border-box;">
                            </div>
                            <div>
                                <label style="display: block; font-size: 0.7rem; font-weight: 800; color: #555; text-transform: uppercase; margin-bottom: 0.4rem;">CATEGORÍA</label>
                                <select id="gasto-d-categoria" style="width: 100%; padding: 0.75rem; border: 1px solid #e0e0e0; border-radius: 8px; background: white; box-sizing: border-box;">
                                    <option value="Alimentos">Alimentos</option>
                                    <option value="Bebidas">Bebidas</option>
                                    <option value="Aseo personal">Aseo personal</option>
                                    <option value="Aseo del hogar">Aseo del hogar</option>
                                    <option value="Transporte">Transporte</option>
                                    <option value="Comunicaciones">Comunicaciones (recargas, minutos)</option>
                                    <option value="Salud">Salud (medicamentos)</option>
                                    <option value="Otro">Otro (escribir)</option>
                                </select>
                            </div>
                            <div>
                                <label style="display: block; font-size: 0.7rem; font-weight: 800; color: #555; text-transform: uppercase; margin-bottom: 0.4rem;">DESCRIPCIÓN DEL ARTÍCULO / SERVICIO</label>
                                <input type="text" id="gasto-d-concepto" placeholder="Ej. Pan tajado, Pasaje de bus, etc." style="width: 100%; padding: 0.75rem; border: 1px solid #e0e0e0; border-radius: 8px; box-sizing: border-box;">
                            </div>
                            <div>
                                <label style="display: block; font-size: 0.7rem; font-weight: 800; color: #555; text-transform: uppercase; margin-bottom: 0.4rem;">VALOR ($)</label>
                                <input type="number" id="gasto-d-valor" placeholder="0" style="width: 100%; padding: 0.75rem; border: 1px solid #e0e0e0; border-radius: 8px; box-sizing: border-box;">
                            </div>
                        </div>

                        <div id="contenedor-categoria-otro" style="display: none; margin-bottom: 1rem; max-width: 400px;">
                            <label style="display: block; font-size: 0.7rem; font-weight: 800; color: #555; text-transform: uppercase; margin-bottom: 0.4rem;">ESPECIFIQUE LA CATEGORÍA</label>
                            <input type="text" id="gasto-d-categoria-otro" placeholder="Escriba la categoría" style="width: 100%; padding: 0.75rem; border: 1px solid #e0e0e0; border-radius: 8px; box-sizing: border-box;">
                        </div>

                        <div style="display: flex; gap: 0.8rem; flex-wrap: wrap; margin-bottom: 1.5rem;">
                            <button id="btn-agregar-gasto-d" style="background-color: #2d6a4f; color: white; border: none; padding: 0.6rem 1.2rem; border-radius: 6px; font-weight: 600; cursor: pointer;">+ Agregar gasto</button>
                            <button id="btn-traer-ayer" type="button" style="background-color: #f8f9fa; color: #333; border: 1px solid #ced4da; padding: 0.6rem 1.2rem; border-radius: 6px; font-weight: 600; cursor: pointer;">↺ Traer del día anterior</button>
                        </div>

                        <div style="overflow-x: auto;">
                        <table style="width: 100%; border-collapse: collapse; text-align: left; font-size: 0.85rem; margin-bottom: 2rem;">
                            <thead>
                                <tr style="background-color: #f8f9fa; border-bottom: 1px solid #e0e0e0;">
                                    <th style="padding: 0.8rem;">Fecha</th>
                                    <th style="padding: 0.8rem;">Categoría</th>
                                    <th style="padding: 0.8rem;">Artículo / Servicio</th>
                                    <th style="padding: 0.8rem;">Valor ($)</th>
                                    <th style="padding: 0.8rem; text-align: center;">Acciones</th>
                                </tr>
                            </thead>
                            <tbody id="tabla-gastos-d-body"></tbody>
                        </table>
                        </div>

                        <div style="background: #fdfbf7; border: 1px solid #e2d9cf; border-radius: 8px; padding: 1.2rem; margin-bottom: 1.5rem;">
                            <h4 style="margin: 0 0 0.8rem 0; color: #222;">¿Algún miembro del hogar consumió alimentos preparados fuera del hogar este día?</h4>
                            <div style="display: flex; gap: 2rem;">
                                <label style="display: flex; align-items: center; gap: 0.4rem; cursor: pointer; font-weight: 600;">
                                    <input type="radio" name="comio-fuera" value="si"> Sí
                                </label>
                                <label style="display: flex; align-items: center; gap: 0.4rem; cursor: pointer; font-weight: 600;">
                                    <input type="radio" name="comio-fuera" value="no"> No
                                </label>
                            </div>
                        </div>

                        <div id="bloque-comidas-fuera" style="display: none; margin-bottom: 2rem;">
                            <h4 style="margin: 0 0 0.8rem 0; color: #222;">Comidas fuera del hogar</h4>
                            <div style="display: grid; grid-template-columns: 1fr 2fr 1fr; gap: 1rem; margin-bottom: 1.5rem;">
                                <div>
                                    <label style="display: block; font-size: 0.7rem; font-weight: 800; color: #555; text-transform: uppercase; margin-bottom: 0.4rem;">TIPO DE COMIDA</label>
                                    <select id="comida-d1-tipo" style="width: 100%; padding: 0.75rem; border: 1px solid #e0e0e0; border-radius: 8px; background: white; box-sizing: border-box;">
                                        <option value="Desayuno">Desayuno</option>
                                        <option value="Almuerzo">Almuerzo</option>
                                        <option value="Comida / Cena">Comida / Cena</option>
                                        <option value="Refrigerio / Bebidas">Refrigerio / Bebidas</option>
                                    </select>
                                </div>
                                <div>
                                    <label style="display: block; font-size: 0.7rem; font-weight: 800; color: #555; text-transform: uppercase; margin-bottom: 0.4rem;">ESTABLECIMIENTO Y DETALLES</label>
                                    <input type="text" id="comida-d1-lugar" placeholder="Ej. Restaurante San Juan" style="width: 100%; padding: 0.75rem; border: 1px solid #e0e0e0; border-radius: 8px; box-sizing: border-box;">
                                </div>
                                <div>
                                    <label style="display: block; font-size: 0.7rem; font-weight: 800; color: #555; text-transform: uppercase; margin-bottom: 0.4rem;">VALOR TOTAL ($)</label>
                                    <input type="number" id="comida-d1-valor" placeholder="0" style="width: 100%; padding: 0.75rem; border: 1px solid #e0e0e0; border-radius: 8px; box-sizing: border-box;">
                                </div>
                            </div>

                            <button id="btn-agregar-comida-d1" style="background-color: #2d6a4f; color: white; border: none; padding: 0.6rem 1.2rem; border-radius: 6px; font-weight: 600; cursor: pointer; margin-bottom: 1.5rem;">+ Agregar consumo fuera</button>

                            <table style="width: 100%; border-collapse: collapse; text-align: left; font-size: 0.85rem;">
                                <thead>
                                    <tr style="background-color: #f8f9fa; border-bottom: 1px solid #e0e0e0;">
                                        <th style="padding: 0.8rem;">Tipo</th>
                                        <th style="padding: 0.8rem;">Establecimiento</th>
                                        <th style="padding: 0.8rem;">Valor ($)</th>
                                        <th style="padding: 0.8rem; text-align: center;">Acciones</th>
                                    </tr>
                                </thead>
                                <tbody id="tabla-comidas-d1-body"></tbody>
                            </table>
                        </div>

                        <div style="display: flex; gap: 1rem; flex-wrap: wrap;">
                            <button id="btn-dia-anterior" style="background-color: #f8f9fa; color: #333; border: 1px solid #e0e0e0; padding: 0.8rem 1.6rem; border-radius: 8px; font-weight: 600; cursor: pointer;">← Día anterior</button>
                            <button id="btn-dia-siguiente" style="background-color: #a11242; color: white; border: none; padding: 0.8rem 1.6rem; border-radius: 8px; font-weight: 600; cursor: pointer;">Día siguiente →</button>
                            <button id="btn-continuar-gastos" style="background-color: #f8f9fa; color: #333; border: 1px solid #e0e0e0; padding: 0.8rem 1.6rem; border-radius: 8px; font-weight: 600; cursor: pointer;">Ir a Consultas →</button>
                        </div>
                    </div>
                </div>

                <!-- SECCIÓN 6: CONSULTAS -->
                <div id="consultas" class="tab-section" style="display:none; max-width: 1100px; margin: 0 auto;">
                    <div style="font-size: 0.8rem; color: #777; font-weight: bold; letter-spacing: 0.5px; margin-bottom: 0.2rem;">
                        SISTEMA DE MONITOREO
                    </div>
                    <h2 style="font-size: 1.8rem; color: #222; margin-top: 0; margin-bottom: 1.5rem; font-weight: 800;">Consultas</h2>
                    <div style="background: white; border-radius: 12px; padding: 2rem; box-shadow: 0 2px 8px rgba(0,0,0,0.04); border: 1px solid #e9ecef;">
                        <h3 style="margin-top: 0; font-size: 1.2rem; color: #222;">Resumen general de avance</h3>
                        <div id="resumen-estadistico" style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 1.5rem;"></div>
                    </div>
                </div>

                <!-- SECCIÓN 7: REPORTES Y MONITOREO -->
                <div id="reportes" class="tab-section" style="display:none; max-width: 1100px; margin: 0 auto;">
                    <div style="font-size: 0.8rem; color: #777; font-weight: bold; letter-spacing: 0.5px; margin-bottom: 0.2rem;">
                        SISTEMA DE INFORMACIÓN Y REPORTES
                    </div>
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
                        <h2 style="font-size: 1.8rem; color: #222; margin: 0; font-weight: 800;">Reportes de Operación</h2>
                        <button id="btn-exportar-csv" style="background-color: #2d6a4f; color: white; border: none; padding: 0.6rem 1.2rem; border-radius: 8px; font-weight: 600; cursor: pointer; display: flex; align-items: center; gap: 0.5rem;">
                            📥 Exportar Reporte (CSV)
                        </button>
                    </div>

                    <!-- Métricas destacadas del reporte -->
                    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem; margin-bottom: 1.8rem;">
                        <div style="background: white; padding: 1.2rem; border-radius: 10px; border: 1px solid #e9ecef; box-shadow: 0 2px 4px rgba(0,0,0,0.02);">
                            <div style="font-size: 0.8rem; color: #666; font-weight: 600;">HOGARES TOTALES</div>
                            <div id="rep-met-totales" style="font-size: 1.8rem; font-weight: 800; color: #111827; margin-top: 0.2rem;">0</div>
                        </div>
                        <div style="background: white; padding: 1.2rem; border-radius: 10px; border: 1px solid #e9ecef; box-shadow: 0 2px 4px rgba(0,0,0,0.02);">
                            <div style="font-size: 0.8rem; color: #666; font-weight: 600;">AVANCE COBERTURA</div>
                            <div id="rep-met-avance" style="font-size: 1.8rem; font-weight: 800; color: #059669; margin-top: 0.2rem;">0%</div>
                        </div>
                        <div style="background: white; padding: 1.2rem; border-radius: 10px; border: 1px solid #e9ecef; box-shadow: 0 2px 4px rgba(0,0,0,0.02);">
                            <div style="font-size: 0.8rem; color: #666; font-weight: 600;">VISITAS REALIZADAS</div>
                            <div id="rep-met-visitas" style="font-size: 1.8rem; font-weight: 800; color: #a11242; margin-top: 0.2rem;">0</div>
                        </div>
                        <div style="background: white; padding: 1.2rem; border-radius: 10px; border: 1px solid #e9ecef; box-shadow: 0 2px 4px rgba(0,0,0,0.02);">
                            <div style="font-size: 0.8rem; color: #666; font-weight: 600;">GASTOS REGISTRADOS</div>
                            <div id="rep-met-gastos" style="font-size: 1.8rem; font-weight: 800; color: #2563eb; margin-top: 0.2rem;">0</div>
                        </div>
                    </div>

                    <!-- Tabla del reporte -->
                    <div style="background: white; border-radius: 12px; padding: 1.5rem; border: 1px solid #e9ecef; box-shadow: 0 2px 8px rgba(0,0,0,0.04);">
                        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; flex-wrap: wrap; gap: 1rem;">
                            <h3 style="margin: 0; font-size: 1.1rem; color: #333;">Consolidado de Hogares</h3>
                            <div style="display: flex; gap: 0.5rem; align-items: center;">
                                <label style="font-size: 0.82rem; font-weight: 600; color: #555;">Filtrar por estado:</label>
                                <select id="filtro-reporte-estado" style="padding: 0.4rem 0.8rem; border-radius: 6px; border: 1px solid #ced4da; font-size: 0.85rem; background: white;">
                                    <option value="todos">Todos</option>
                                    <option value="completado">Completados</option>
                                    <option value="proceso">En proceso</option>
                                    <option value="pendiente">Pendientes</option>
                                </select>
                            </div>
                        </div>

                        <div style="overflow-x: auto;">
                            <table style="width: 100%; border-collapse: collapse; text-align: left; font-size: 0.85rem;">
                                <thead>
                                    <tr style="background-color: #f8f9fa; color: #4b5563; text-transform: uppercase; font-size: 0.72rem; letter-spacing: 0.5px; border-bottom: 2px solid #e5e7eb;">
                                        <th style="padding: 0.8rem 1rem;">CÓDIGO</th>
                                        <th style="padding: 0.8rem 1rem;">JEFE DE HOGAR</th>
                                        <th style="padding: 0.8rem 1rem;">DIRECCIÓN</th>
                                        <th style="padding: 0.8rem 1rem;">ESTADO</th>
                                        <th style="padding: 0.8rem 1rem; text-align: center;">PROGRESO</th>
                                        <th style="padding: 0.8rem 1rem; text-align: center;">VISITAS</th>
                                        <th style="padding: 0.8rem 1rem; text-align: center;">GASTOS REG.</th>
                                        <th style="padding: 0.8rem 1rem; text-align: right;">TOTAL GASTADO</th>
                                    </tr>
                                </thead>
                                <tbody id="tabla-reporte-body"></tbody>
                            </table>
                        </div>
                    </div>
                </div>

            </main>
        </div>
    `;

    // ==========================================
    // LÓGICA Y FUNCIONALIDADES
    // ==========================================

    let hogarSeleccionadoActual = null;
    let hogarAbierto = false;
    let diaActual = 1;

    // ==========================================
    // AUTOGUARDADO
    // ==========================================

    onGuardado = function (ok) {
        const el = document.getElementById('indicador-guardado');
        if (!el) return;
        const hora = new Date().toLocaleTimeString('es-CO', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
        el.textContent = ok ? `✓ Guardado automáticamente · ${hora}` : '⚠ No se pudo guardar (almacenamiento lleno)';
        el.style.background = ok ? '#1e5235' : '#a11242';
        el.style.opacity = '1';
        clearTimeout(el._t);
        el._t = setTimeout(() => { el.style.opacity = '0.55'; }, 2500);
    };

    const CAMPOS_BORRADOR = [
        'visita-fecha', 'visita-inicio', 'visita-fin', 'visita-resultado', 'visita-partes', 'visita-proxima',
        'gasto-d-fecha', 'gasto-d-categoria', 'gasto-d-categoria-otro', 'gasto-d-concepto', 'gasto-d-valor',
        'comida-d1-tipo', 'comida-d1-lugar', 'comida-d1-valor'
    ];

    function leerBorradores() {
        try {
            return JSON.parse(localStorage.getItem('borradores_dane')) || {};
        } catch (e) {
            return {};
        }
    }

    function escribirBorradores(todos) {
        try {
            localStorage.setItem('borradores_dane', JSON.stringify(todos));
        } catch (e) {
            console.error('No se pudo guardar el borrador:', e);
        }
    }

    function guardarBorrador(id, valor) {
        if (!hogarSeleccionadoActual) return;
        const todos = leerBorradores();
        const k = hogarSeleccionadoActual.codigo;
        todos[k] = todos[k] || {};
        todos[k][id] = valor;
        escribirBorradores(todos);
    }

    function limpiarBorrador(ids) {
        if (!hogarSeleccionadoActual) return;
        const todos = leerBorradores();
        const k = hogarSeleccionadoActual.codigo;
        if (!todos[k]) return;
        ids.forEach(id => { delete todos[k][id]; });
        escribirBorradores(todos);
    }

    function restaurarBorradores() {
        if (!hogarSeleccionadoActual) return;
        const b = leerBorradores()[hogarSeleccionadoActual.codigo] || {};
        CAMPOS_BORRADOR.forEach(id => {
            const el = document.getElementById(id);
            if (!el) return;
            if (b[id] !== undefined) el.value = b[id];
            else if (el.tagName === 'SELECT') el.selectedIndex = 0;
            else el.value = el.defaultValue || '';
        });
        const sel = document.getElementById('gasto-d-categoria');
        const cont = document.getElementById('contenedor-categoria-otro');
        if (sel && cont) cont.style.display = (sel.value === 'Otro') ? 'block' : 'none';
    }

    CAMPOS_BORRADOR.forEach(id => {
        const el = document.getElementById(id);
        if (!el) return;
        const guardar = () => guardarBorrador(id, el.value);
        el.addEventListener('input', guardar);
        el.addEventListener('change', guardar);
    });

    function actualizarContadoresMetricas() {
        const total = hogaresBD.length;
        const completados = hogaresBD.filter(h => h.estado === 'completado').length;
        const proceso = hogaresBD.filter(h => h.estado === 'proceso').length;
        const pendientes = hogaresBD.filter(h => h.estado === 'pendiente').length;

        const elTodos = document.getElementById('metrica-todos');
        const elComp = document.getElementById('metrica-completados');
        const elProc = document.getElementById('metrica-proceso');
        const elPend = document.getElementById('metrica-pendientes');

        if (elTodos) elTodos.innerText = total;
        if (elComp) elComp.innerText = completados;
        if (elProc) elProc.innerText = proceso;
        if (elPend) elPend.innerText = pendientes;

        const contResumen = document.getElementById('resumen-estadistico');
        if (contResumen) {
            contResumen.innerHTML = `
                <div style="background: #f8f9fa; padding: 1.2rem; border-radius: 8px; text-align: center;">
                    <div style="font-size: 2rem; font-weight: bold; color: #137333;">${total ? Math.round((completados / total) * 100) : 0}%</div>
                    <div style="font-size: 0.85rem; color: #555;">Efectividad de Cobertura</div>
                </div>
                <div style="background: #f8f9fa; padding: 1.2rem; border-radius: 8px; text-align: center;">
                    <div style="font-size: 2rem; font-weight: bold; color: #a11242;">${hogaresBD.reduce((acc, h) => acc + (h.visitas ? h.visitas.length : 0), 0)}</div>
                    <div style="font-size: 0.85rem; color: #555;">Visitas Registradas</div>
                </div>
                <div style="background: #f8f9fa; padding: 1.2rem; border-radius: 8px; text-align: center;">
                    <div style="font-size: 2rem; font-weight: bold; color: #1a73e8;">${total}</div>
                    <div style="font-size: 0.85rem; color: #555;">Total Cuadernillos Asignados</div>
                </div>
            `;
        }
    }

    // ==========================================
    // LÓGICA DE REPORTES
    // ==========================================
    function aNumero(v) {
        const n = Number(String(v ?? '').replace(/[^\d.-]/g, ''));
        return isFinite(n) ? n : 0;
    }

    function totalesHogar(h) {
        let alimentos = 0;
        const c = h.capituloC || {};
        Object.keys(c).forEach(k => {
            if (c[k] && typeof c[k] === 'object' && k !== 'mercadosGlobales') alimentos += aNumero(c[k].valor);
        });
        Object.values(c.mercadosGlobales || {}).forEach(v => { alimentos += aNumero(v); });
        const diarios = (h.gastosDiarios || []).reduce((a, g) => a + aNumero(g.valor), 0);
        const fuera = (h.comidasFuera || []).reduce((a, g) => a + aNumero(g.valor), 0);
        return { alimentos, diarios, fuera, total: alimentos + diarios + fuera };
    }

    const fmtCOP = (n) => '$' + Math.round(n).toLocaleString('es-CO');

    function renderSeccionReportes() {
        const tbody = document.getElementById('tabla-reporte-body');
        if (!tbody) return;

        const filtroEstado = document.getElementById('filtro-reporte-estado')?.value || 'todos';

        let lista = hogaresBD;
        if (filtroEstado !== 'todos') {
            lista = hogaresBD.filter(h => h.estado === filtroEstado);
        }

        // Métricas rápidas
        const totalHogares = hogaresBD.length;
        const completados = hogaresBD.filter(h => h.estado === 'completado').length;
        const avancePct = totalHogares ? Math.round((completados / totalHogares) * 100) : 0;
        const totalVisitas = hogaresBD.reduce((acc, h) => acc + (h.visitas ? h.visitas.length : 0), 0);
        const totalGastos = hogaresBD.reduce((acc, h) => acc + (h.gastosDiarios ? h.gastosDiarios.length : 0), 0);

        document.getElementById('rep-met-totales').innerText = totalHogares;
        document.getElementById('rep-met-avance').innerText = `${avancePct}%`;
        document.getElementById('rep-met-visitas').innerText = totalVisitas;
        document.getElementById('rep-met-gastos').innerText = totalGastos;

        tbody.innerHTML = '';

        if (lista.length === 0) {
            tbody.innerHTML = `<tr><td colspan="8" style="padding: 1rem; color: #888; text-align: center;">No hay registros para mostrar.</td></tr>`;
            return;
        }

        lista.forEach(h => {
            const tr = document.createElement('tr');
            tr.style.borderBottom = '1px solid #f3f4f6';

            let badgeBg = '#fef3c7', badgeColor = '#d97706';
            if (h.estado === 'completado') { badgeBg = '#d1fae5'; badgeColor = '#059669'; }
            if (h.estado === 'pendiente') { badgeBg = '#dbeafe'; badgeColor = '#2563eb'; }

            const numVisitas = h.visitas ? h.visitas.length : 0;
            const numGastos = h.gastosDiarios ? h.gastosDiarios.length : 0;

            tr.innerHTML = `
                <td style="padding: 0.8rem 1rem; font-weight: 700; color: #374151;">${h.codigo}</td>
                <td style="padding: 0.8rem 1rem; font-weight: 600; color: #111827;">${h.jefe}</td>
                <td style="padding: 0.8rem 1rem; color: #6b7280;">${h.direccion}</td>
                <td style="padding: 0.8rem 1rem;">
                    <span style="background-color: ${badgeBg}; color: ${badgeColor}; padding: 0.2rem 0.6rem; border-radius: 12px; font-size: 0.72rem; font-weight: 700;">
                        ${h.etiqueta}
                    </span>
                </td>
                <td style="padding: 0.8rem 1rem; text-align: center; font-weight: 700; color: #1f2937;">${h.progreso}%</td>
                <td style="padding: 0.8rem 1rem; text-align: center; color: #4b5563;">${numVisitas}</td>
                <td style="padding: 0.8rem 1rem; text-align: center; color: #4b5563;">${numGastos}</td>
                <td style="padding: 0.8rem 1rem; text-align: right; font-weight: 700; color: #1f2937;">${fmtCOP(totalesHogar(h).total)}</td>
            `;

            tbody.appendChild(tr);
        });
    }

    // Exportar datos del reporte a CSV
    function exportarReporteCSV() {
        if (!hogaresBD || hogaresBD.length === 0) {
            alert('No hay datos disponibles para exportar.');
            return;
        }

        const esc = (v) => `"${String(v ?? '').replace(/"/g, '""')}"`;
        const filas = ["Codigo,Jefe de Hogar,Direccion,Estado,Progreso,Visitas Registradas,Gastos Registrados,Total Gastado (COP)"];

        hogaresBD.forEach(h => {
            const t = totalesHogar(h);
            filas.push([
                esc(h.codigo), esc(h.jefe), esc(h.direccion), esc(h.etiqueta),
                esc(`${h.progreso}%`),
                esc(h.visitas ? h.visitas.length : 0),
                esc(h.gastosDiarios ? h.gastosDiarios.length : 0),
                esc(t.total)
            ].join(","));
        });

        // BOM (\uFEFF) para que Excel respete tildes; Blob para que '#' no corte el archivo
        const blob = new Blob(["\uFEFF" + filas.join("\n")], { type: "text/csv;charset=utf-8;" });
        const url = URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = url;
        link.download = `Reporte_Hogares_DANE_${new Date().toISOString().slice(0,10)}.csv`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        setTimeout(() => URL.revokeObjectURL(url), 1000);
    }

    const btnExportar = document.getElementById('btn-exportar-csv');
    if (btnExportar) {
        btnExportar.addEventListener('click', exportarReporteCSV);
    }

    const filtroReporte = document.getElementById('filtro-reporte-estado');
    if (filtroReporte) {
        filtroReporte.addEventListener('change', renderSeccionReportes);
    }

    // Renderizado dinámico de la Tabla C
    function renderTablaCapituloC() {
        const tbody = document.getElementById('tabla-capitulo-c-body');
        if (!tbody || !hogarSeleccionadoActual) return;

        tbody.innerHTML = '';
        const datosCapC = hogarSeleccionadoActual.capituloC || {};

        alimentosCapituloC.forEach(item => {
            const tr = document.createElement('tr');
            tr.style.borderBottom = '1px solid #e9ecef';

            const itemGuardado = datosCapC[item.id] || {};
            const frecVal = itemGuardado.frecuencia || '';
            const valUltima = itemGuardado.valor || '';
            const compraraVal = itemGuardado.comprara || '';

            tr.innerHTML = `
                <td style="padding: 0.5rem; text-align: center; font-weight: bold; background: #f8f9fa;">${item.id}</td>
                <td style="padding: 0.5rem; color: #333; font-weight: 500;">${item.nombre}</td>
                <td style="padding: 0.3rem; text-align: center;"><input type="radio" name="frec_${item.id}" value="1" ${frecVal === '1' ? 'checked' : ''}></td>
                <td style="padding: 0.3rem; text-align: center;"><input type="radio" name="frec_${item.id}" value="2" ${frecVal === '2' ? 'checked' : ''}></td>
                <td style="padding: 0.3rem; text-align: center;"><input type="radio" name="frec_${item.id}" value="2.1" ${frecVal === '2.1' ? 'checked' : ''}></td>
                <td style="padding: 0.3rem; text-align: center;"><input type="radio" name="frec_${item.id}" value="3" ${frecVal === '3' ? 'checked' : ''}></td>
                <td style="padding: 0.3rem; text-align: center;"><input type="radio" name="frec_${item.id}" value="4" ${frecVal === '4' ? 'checked' : ''}></td>
                <td style="padding: 0.3rem; text-align: center;"><input type="radio" name="frec_${item.id}" value="5" ${frecVal === '5' ? 'checked' : ''}></td>
                <td style="padding: 0.3rem; text-align: center;"><input type="radio" name="frec_${item.id}" value="6" ${frecVal === '6' ? 'checked' : ''}></td>
                <td style="padding: 0.3rem; text-align: center;"><input type="radio" name="frec_${item.id}" value="7" ${frecVal === '7' ? 'checked' : ''}></td>
                <td style="padding: 0.3rem; text-align: center; border-left: 1px solid #dee2e6;">
                    <input type="number" id="val_${item.id}" value="${valUltima}" placeholder="$" style="width: 100%; padding: 0.25rem; font-size: 0.75rem; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
                </td>
                <td style="padding: 0.3rem; text-align: center;">
                    <select id="comprara_${item.id}" style="width: 100%; padding: 0.25rem; font-size: 0.75rem; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
                        <option value="">- Seleccione -</option>
                        <option value="1" ${compraraVal === '1' ? 'selected' : ''}>1. Sí</option>
                        <option value="2" ${compraraVal === '2' ? 'selected' : ''}>2. No</option>
                    </select>
                </td>
            `;

            tbody.appendChild(tr);
        });

        const contMercados = document.getElementById('contenedor-mercados-globales');
        if (contMercados) {
            contMercados.innerHTML = '';
            mercadosGlobalesCapC.forEach(m => {
                const div = document.createElement('div');
                div.style.background = 'white';
                div.style.padding = '0.8rem';
                div.style.borderRadius = '6px';
                div.style.border = '1px solid #e0e0e0';

                const valMercado = (datosCapC.mercadosGlobales && datosCapC.mercadosGlobales[m.codigo]) || '';

                div.innerHTML = `
                    <label style="display: block; font-size: 0.72rem; font-weight: bold; color: #444; margin-bottom: 0.3rem;">
                        Código ${m.codigo}: ${m.concepto}
                    </label>
                    <input type="number" id="mercado_${m.codigo}" value="${valMercado}" placeholder="Valor total $" style="width: 100%; padding: 0.5rem; font-size: 0.8rem; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
                `;

                contMercados.appendChild(div);
            });
        }

        const obsArea = document.getElementById('obs-capitulo-c');
        if (obsArea) obsArea.value = datosCapC.observaciones || '';
    }

    function renderTablaVisitas(visitas) {
        const tbody = document.getElementById('tabla-visitas-body');
        if (!tbody) return;

        tbody.innerHTML = '';

        if (!visitas || visitas.length === 0) {
            tbody.innerHTML = `<tr><td colspan="6" style="padding: 1rem; color: #888; text-align: center;">No hay visitas registradas para este hogar.</td></tr>`;
            return;
        }

        visitas.forEach(v => {
            const tr = document.createElement('tr');
            tr.style.borderBottom = '1px solid #f0f0f0';

            const esCompleta = (v.resultado || '').toUpperCase() === 'COMPLETA';
            const badgeBg = esCompleta ? '#e6f4ea' : '#e8f0fe';
            const badgeColor = esCompleta ? '#137333' : '#1a73e8';

            tr.innerHTML = `
                <td style="padding: 0.9rem 1rem; font-weight: bold;">${v.num}</td>
                <td style="padding: 0.9rem 1rem; color: #555;">${v.fecha}</td>
                <td style="padding: 0.9rem 1rem; color: #555;">${v.inicio}</td>
                <td style="padding: 0.9rem 1rem; color: #555;">${v.fin}</td>
                <td style="padding: 0.9rem 1rem;">
                    <span style="background-color: ${badgeBg}; color: ${badgeColor}; padding: 0.25rem 0.6rem; border-radius: 12px; font-size: 0.7rem; font-weight: 800;">
                        ${v.resultado}
                    </span>
                </td>
                <td style="padding: 0.9rem 1rem; color: #555;">${v.observacion || '-'}</td>
            `;

            tbody.appendChild(tr);
        });
    }

    function renderSelectorDias() {
        const cont = document.getElementById('selector-dias');
        if (!cont || !hogarSeleccionadoActual) return;

        cont.innerHTML = '';
        const gastos = hogarSeleccionadoActual.gastosDiarios || [];
        const comidas = hogarSeleccionadoActual.comidasFuera || [];
        const resp = hogarSeleccionadoActual.comioFuera || {};

        for (let d = 1; d <= 14; d++) {
            const tieneDatos = gastos.some(g => Number(g.dia || 1) === d) ||
                               comidas.some(c => Number(c.dia || 1) === d) ||
                               !!resp[d];
            const activo = d === diaActual;

            const btn = document.createElement('button');
            btn.type = 'button';
            btn.style.cssText = `
                padding: 0.5rem 0.8rem;
                border-radius: 8px;
                font-weight: 600;
                font-size: 0.85rem;
                cursor: pointer;
                border: 1px solid ${activo ? '#5c1822' : '#d0d5da'};
                background-color: ${activo ? '#5c1822' : '#ffffff'};
                color: ${activo ? '#ffffff' : '#333333'};
            `;
            btn.innerHTML = `Día ${d}${tieneDatos ? ` <span style="color:${activo ? '#9be7b5' : '#2d6a4f'};">●</span>` : ''}`;
            btn.addEventListener('click', () => {
                diaActual = d;
                renderDiaD();
            });
            cont.appendChild(btn);
        }
    }

    function crearBotonEliminar(onConfirmar) {
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.textContent = 'Eliminar';
        btn.style.cssText = 'padding: 0.25rem 0.6rem; font-size: 0.8rem; background-color: #dc3545; color: white; border: none; border-radius: 6px; cursor: pointer; font-weight: 600;';
        btn.addEventListener('click', onConfirmar);
        return btn;
    }

    function asegurarEstilosEditables() {
        if (document.getElementById('estilos-celdas-editables')) return;
        const st = document.createElement('style');
        st.id = 'estilos-celdas-editables';
        st.textContent = `
            .celda-editable {
                width: 100%;
                padding: 0.4rem 0.5rem;
                border: 1px solid transparent;
                border-radius: 6px;
                background: transparent;
                font-size: 0.85rem;
                font-family: inherit;
                color: #222;
                box-sizing: border-box;
                transition: background-color 0.3s, border-color 0.2s;
            }
            .celda-editable:hover { border-color: #ced4da; background: #fff; }
            .celda-editable:focus { border-color: #a11242; background: #fff; outline: none; }
        `;
        document.head.appendChild(st);
    }

    function asegurarListaCategorias() {
        if (document.getElementById('lista-categorias-d')) return;
        const dl = document.createElement('datalist');
        dl.id = 'lista-categorias-d';
        ['Alimentos', 'Bebidas', 'Aseo personal', 'Aseo del hogar', 'Transporte', 'Comunicaciones', 'Salud']
            .forEach(c => {
                const o = document.createElement('option');
                o.value = c;
                dl.appendChild(o);
            });
        document.body.appendChild(dl);
    }

    function crearCeldaInput(tipo, valorActual, onCambio, listaId) {
        const inp = document.createElement('input');
        inp.type = tipo;
        inp.className = 'celda-editable';
        inp.value = (valorActual === undefined || valorActual === null) ? '' : valorActual;
        if (listaId) inp.setAttribute('list', listaId);
        if (tipo === 'number') inp.min = '0';
        inp.addEventListener('change', () => {
            const ok = onCambio(inp.value, inp);
            if (ok !== false) {
                guardarHogaresBD();
                inp.style.backgroundColor = '#e6f4ea';
                setTimeout(() => { inp.style.backgroundColor = ''; }, 700);
            }
        });
        return inp;
    }

    function renderTablaGastosD() {
        const tbody = document.getElementById('tabla-gastos-d-body');
        if (!tbody || !hogarSeleccionadoActual) return;

        asegurarEstilosEditables();
        asegurarListaCategorias();

        tbody.innerHTML = '';
        const todos = hogarSeleccionadoActual.gastosDiarios || [];
        const lista = todos.map((g, idx) => ({ g, idx })).filter(x => Number(x.g.dia || 1) === diaActual);

        if (lista.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" style="padding: 1rem; color: #888; text-align: center;">No hay gastos registrados para el día ${diaActual}.</td></tr>`;
            return;
        }

        lista.forEach(({ g, idx }) => {
            const tr = document.createElement('tr');
            tr.style.borderBottom = '1px solid #eee';

            const celdas = [];
            for (let k = 0; k < 5; k++) {
                const td = document.createElement('td');
                td.style.padding = '0.4rem 0.5rem';
                celdas.push(td);
                tr.appendChild(td);
            }

            celdas[0].appendChild(crearCeldaInput('date', g.fecha, (v) => { g.fecha = v; }));
            celdas[1].appendChild(crearCeldaInput('text', g.categoria || '', (v) => { g.categoria = v.trim(); }, 'lista-categorias-d'));
            celdas[2].appendChild(crearCeldaInput('text', g.concepto, (v, inp) => {
                if (!v.trim()) {
                    alert('La descripción no puede quedar vacía.');
                    inp.value = g.concepto;
                    return false;
                }
                g.concepto = v.trim();
            }));
            celdas[3].appendChild(crearCeldaInput('number', g.valor, (v, inp) => {
                if (v === '' || Number(v) < 0) {
                    alert('Ingrese un valor válido.');
                    inp.value = g.valor;
                    return false;
                }
                g.valor = v;
            }));

            celdas[4].style.textAlign = 'center';
            celdas[4].appendChild(crearBotonEliminar(() => {
                if (confirm('¿Eliminar este gasto?')) {
                    todos.splice(idx, 1);
                    guardarHogaresBD();
                    renderDiaD();
                }
            }));

            tbody.appendChild(tr);
        });
    }

    function renderTablaComidasD1() {
        const tbody = document.getElementById('tabla-comidas-d1-body');
        if (!tbody || !hogarSeleccionadoActual) return;

        asegurarEstilosEditables();

        tbody.innerHTML = '';
        const todos = hogarSeleccionadoActual.comidasFuera || [];
        const lista = todos.map((c, idx) => ({ c, idx })).filter(x => Number(x.c.dia || 1) === diaActual);

        if (lista.length === 0) {
            tbody.innerHTML = `<tr><td colspan="4" style="padding: 1rem; color: #888; text-align: center;">No hay comidas fuera registradas para el día ${diaActual}.</td></tr>`;
            return;
        }

        const tipos = ['Desayuno', 'Almuerzo', 'Comida / Cena', 'Refrigerio / Bebidas'];

        lista.forEach(({ c, idx }) => {
            const tr = document.createElement('tr');
            tr.style.borderBottom = '1px solid #eee';

            const celdas = [];
            for (let k = 0; k < 4; k++) {
                const td = document.createElement('td');
                td.style.padding = '0.4rem 0.5rem';
                celdas.push(td);
                tr.appendChild(td);
            }

            const sel = document.createElement('select');
            sel.className = 'celda-editable';
            const opciones = tipos.includes(c.tipo) ? tipos : [c.tipo, ...tipos];
            opciones.forEach(t => {
                const o = document.createElement('option');
                o.value = t;
                o.textContent = t;
                if (t === c.tipo) o.selected = true;
                sel.appendChild(o);
            });
            sel.addEventListener('change', () => {
                c.tipo = sel.value;
                guardarHogaresBD();
                sel.style.backgroundColor = '#e6f4ea';
                setTimeout(() => { sel.style.backgroundColor = ''; }, 700);
            });
            celdas[0].appendChild(sel);

            celdas[1].appendChild(crearCeldaInput('text', c.lugar, (v, inp) => {
                if (!v.trim()) {
                    alert('El establecimiento no puede quedar vacío.');
                    inp.value = c.lugar;
                    return false;
                }
                c.lugar = v.trim();
            }));

            celdas[2].appendChild(crearCeldaInput('number', c.valor, (v, inp) => {
                if (v === '' || Number(v) < 0) {
                    alert('Ingrese un valor válido.');
                    inp.value = c.valor;
                    return false;
                }
                c.valor = v;
            }));

            celdas[3].style.textAlign = 'center';
            celdas[3].appendChild(crearBotonEliminar(() => {
                if (confirm('¿Eliminar esta comida fuera del hogar?')) {
                    todos.splice(idx, 1);
                    guardarHogaresBD();
                    renderDiaD();
                }
            }));

            tbody.appendChild(tr);
        });
    }

    function renderDiaD() {
        if (!hogarSeleccionadoActual) return;

        renderSelectorDias();

        const titulo = document.getElementById('titulo-dia-d');
        if (titulo) titulo.innerText = `Día ${diaActual} de 14`;

        renderTablaGastosD();

        const btnAyer = document.getElementById('btn-traer-ayer');
        if (btnAyer) btnAyer.style.display = (diaActual > 1) ? 'inline-block' : 'none';

        const respuesta = (hogarSeleccionadoActual.comioFuera || {})[diaActual] || '';
        document.querySelectorAll('input[name="comio-fuera"]').forEach(r => {
            r.checked = (r.value === respuesta);
        });

        const bloque = document.getElementById('bloque-comidas-fuera');
        if (bloque) bloque.style.display = (respuesta === 'si') ? 'block' : 'none';

        renderTablaComidasD1();
    }

    function cargarDetalleHogar(hogar) {
        flushGuardados();
        hogarSeleccionadoActual = hogar;

        document.querySelectorAll('.det-header-codigo').forEach(el => el.innerText = `HOGAR ACTIVO - ${hogar.codigo}`);
        document.querySelectorAll('.det-header-jefe').forEach(el => el.innerText = hogar.jefe);
        document.querySelectorAll('.det-header-dir').forEach(el => el.innerText = hogar.direccion);
        document.querySelectorAll('.det-header-progreso-val').forEach(el => el.innerText = `${hogar.progreso}%`);
        document.querySelectorAll('.det-header-progreso-bar').forEach(el => el.style.width = `${hogar.progreso}%`);

        const inpId = document.getElementById('det-input-id');
        if (inpId) inpId.value = hogar.codigo;

        const inpDir = document.getElementById('det-input-dir');
        if (inpDir) inpDir.value = hogar.direccionFisica;

        const inpJefe = document.getElementById('det-input-jefe');
        if (inpJefe) inpJefe.value = hogar.jefe;

        const inpOrden = document.getElementById('det-input-orden');
        if (inpOrden) inpOrden.value = hogar.orden;

        const selRes = document.getElementById('det-select-resultado');
        if (selRes) selRes.value = hogar.resultado;

        const selEst = document.getElementById('det-select-estado');
        if (selEst) selEst.value = hogar.estado;

        renderTablaVisitas(hogar.visitas);
        renderTablaCapituloC();
        diaActual = 1;
        renderDiaD();
        restaurarBorradores();
    }

    function irAPestana(href) {
        const tab = document.querySelector(`.top-navigation a[href="${href}"]`);
        if (tab) tab.click();
    }

    function renderHogares(lista) {
        const container = document.getElementById('hogares-container');
        if (!container) return;

        container.innerHTML = '';

        if (lista.length === 0) {
            container.innerHTML = `<p style="padding: 1rem; color: #666;">No se encontraron hogares con el filtro seleccionado.</p>`;
            actualizarContadoresMetricas();
            return;
        }

        lista.forEach(hogar => {
            const card = document.createElement('div');
            card.className = 'hogar-card-modern';

            let badgeClass = 'badge-proceso';
            if (hogar.estado === 'completado') badgeClass = 'badge-completado';
            if (hogar.estado === 'pendiente') badgeClass = 'badge-pendiente';

            card.innerHTML = `
                <div class="hogar-card-body">
                    <div class="hogar-avatar-icon">🏠</div>
                    <div>
                        <span class="hogar-code-tag">${hogar.codigo}</span>
                        <h4 class="hogar-card-title">${hogar.jefe}</h4>
                        <p class="hogar-card-dir">${hogar.direccion}</p>
                    </div>
                </div>
                <span class="badge-status ${badgeClass}">${hogar.etiqueta}</span>
            `;

            card.addEventListener('click', () => {
                hogarAbierto = true;
                cargarDetalleHogar(hogar);
                irAPestana('#identificacion');
            });

            container.appendChild(card);
        });

        actualizarContadoresMetricas();
    }

    // ===== Filtros de la lista de hogares (tarjetas de métricas, pestañas y búsqueda) =====
    let filtroEstadoActual = 'todos';
    let textoBusquedaActual = '';

    function aplicarFiltrosHogares() {
        const texto = textoBusquedaActual.trim().toLowerCase();

        const lista = hogaresBD.filter(h => {
            const coincideEstado = filtroEstadoActual === 'todos' || h.estado === filtroEstadoActual;
            const coincideTexto = !texto ||
                String(h.codigo || '').toLowerCase().includes(texto) ||
                String(h.jefe || '').toLowerCase().includes(texto) ||
                String(h.direccion || '').toLowerCase().includes(texto);
            return coincideEstado && coincideTexto;
        });

        renderHogares(lista);

        document.querySelectorAll('.tab-btn[data-filter]').forEach(btn => {
            btn.classList.toggle('active', btn.dataset.filter === filtroEstadoActual);
        });
        document.querySelectorAll('.metric-card[data-metric]').forEach(card => {
            card.classList.toggle('metric-active', card.dataset.metric === filtroEstadoActual);
        });
    }

    // Clic en las tarjetas de arriba (asignados, completados, en proceso, pendientes)
    document.querySelectorAll('.metric-card[data-metric]').forEach(card => {
        card.addEventListener('click', () => {
            filtroEstadoActual = card.dataset.metric;
            aplicarFiltrosHogares();
            const seccion = document.querySelector('.hogares-section');
            if (seccion) seccion.scrollIntoView({ behavior: 'smooth', block: 'start' });
        });
    });

    // Pestañas: Todos / Pendientes / En proceso / Completados
    document.querySelectorAll('.tab-btn[data-filter]').forEach(btn => {
        btn.addEventListener('click', () => {
            filtroEstadoActual = btn.dataset.filter;
            aplicarFiltrosHogares();
        });
    });

    // Buscador
    const inputBusqueda = document.getElementById('search-hogar');
    if (inputBusqueda) {
        inputBusqueda.addEventListener('input', () => {
            textoBusquedaActual = inputBusqueda.value;
            aplicarFiltrosHogares();
        });
    }

    // Botón Actualizar
    const btnActualizarLista = document.getElementById('btn-actualizar');
    if (btnActualizarLista) {
        btnActualizarLista.addEventListener('click', aplicarFiltrosHogares);
    }

    renderHogares(hogaresBD);
    renderTablaVisitas([]);

    // Navegación entre pestañas
    const navLinks = document.querySelectorAll('.top-navigation .nav-link');
    const sections = document.querySelectorAll('.tab-section');

    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();

            const destino = link.getAttribute('href');

            if (!hogarAbierto && destino !== '#inicio' && destino !== '#consultas' && destino !== '#reportes') {
                alert('Primero seleccione un hogar en Inicio.');
                return;
            }

            navLinks.forEach(l => {
                l.classList.remove('active');
                l.style.backgroundColor = 'transparent';
            });

            sections.forEach(s => s.style.display = 'none');

            link.classList.add('active');
            link.style.backgroundColor = '#1b4332';

            const targetSection = document.getElementById(destino.replace('#', ''));
            if (targetSection) {
                targetSection.style.display = 'block';
            }

            if (destino === '#reportes') {
                renderSeccionReportes();
            }
            if (destino === '#consultas') {
                flushGuardados();
                actualizarContadoresMetricas();
            }
        });
    });

    const activeLink = document.querySelector('.top-navigation .nav-link.active');
    if (activeLink) activeLink.style.backgroundColor = '#1b4332';

    document.querySelectorAll('.btn-cambiar-hogar').forEach(btn => {
        btn.addEventListener('click', () => {
            flushGuardados();
            hogarAbierto = false;
            hogarSeleccionadoActual = null;
            irAPestana('#inicio');
        });
    });

    const btnPerfil = document.getElementById('btn-user-profile');
    if (btnPerfil) {
        btnPerfil.style.cursor = 'pointer';
        btnPerfil.addEventListener('click', () => {
            if (!confirm('¿Desea cerrar la sesión?')) return;
            flushGuardados();
            sessionStorage.removeItem('currentUser');
            sessionStorage.removeItem('userRole');
            location.reload();
        });
    }

    function actualizarEncabezados(hogar) {
        document.querySelectorAll('.det-header-progreso-val').forEach(el => el.innerText = `${hogar.progreso}%`);
        document.querySelectorAll('.det-header-progreso-bar').forEach(el => el.style.width = `${hogar.progreso}%`);
    }

    function guardarIdentificacion() {
        if (!hogarSeleccionadoActual) return;

        hogarSeleccionadoActual.orden = document.getElementById('det-input-orden').value;
        hogarSeleccionadoActual.resultado = document.getElementById('det-select-resultado').value;
        hogarSeleccionadoActual.estado = document.getElementById('det-select-estado').value;

        if (hogarSeleccionadoActual.estado === 'completado') {
            hogarSeleccionadoActual.etiqueta = 'Completado';
            hogarSeleccionadoActual.progreso = 100;
        } else if (hogarSeleccionadoActual.estado === 'proceso') {
            hogarSeleccionadoActual.etiqueta = 'En proceso';
        } else {
            hogarSeleccionadoActual.etiqueta = 'Pendiente';
            hogarSeleccionadoActual.progreso = 0;
        }

        guardarHogaresBD();
        actualizarEncabezados(hogarSeleccionadoActual);
    }

    ['det-input-orden', 'det-select-resultado', 'det-select-estado'].forEach(id => {
        const el = document.getElementById(id);
        if (!el) return;
        el.addEventListener('input', guardarIdentificacion);
        el.addEventListener('change', guardarIdentificacion);
    });

    const btnGuardarCambios = document.getElementById('btn-guardar-cambios');
    if (btnGuardarCambios) {
        btnGuardarCambios.addEventListener('click', () => {
            if (!hogarSeleccionadoActual) return;
            guardarIdentificacion();
            alert(`✅ Cambios guardados para el hogar ${hogarSeleccionadoActual.codigo}`);
        });
    }

    const btnContIdent = document.getElementById('btn-continuar-ident');
    if (btnContIdent) {
        btnContIdent.addEventListener('click', () => irAPestana('#control-visitas'));
    }

    const btnRegVisita = document.getElementById('btn-registrar-visita');
    if (btnRegVisita) {
        btnRegVisita.addEventListener('click', () => {
            if (!hogarSeleccionadoActual) return;

            const fecha = document.getElementById('visita-fecha').value;
            const inicio = document.getElementById('visita-inicio').value;
            const fin = document.getElementById('visita-fin').value;
            const resultado = document.getElementById('visita-resultado').value;
            const partes = document.getElementById('visita-partes').value;

            if (!fecha || !inicio || !fin) {
                alert('Por favor diligencie la fecha y las horas de la visita.');
                return;
            }

            if (!hogarSeleccionadoActual.visitas) hogarSeleccionadoActual.visitas = [];

            const nuevaVisita = {
                num: hogarSeleccionadoActual.visitas.length + 1,
                fecha: fecha,
                inicio: inicio,
                fin: fin,
                resultado: resultado.toUpperCase(),
                observacion: partes ? `Pendiente ${partes}.` : 'Seguimiento registrado.'
            };

            hogarSeleccionadoActual.visitas.push(nuevaVisita);
            guardarHogaresBD();
            renderTablaVisitas(hogarSeleccionadoActual.visitas);
            actualizarContadoresMetricas();

            document.getElementById('visita-partes').value = '';
            document.getElementById('visita-proxima').value = '';
            limpiarBorrador(['visita-partes', 'visita-proxima']);

            alert(`✅ Visita #${nuevaVisita.num} registrada exitosamente.`);
        });
    }

    const btnContVisita = document.getElementById('btn-continuar-visita');
    if (btnContVisita) {
        btnContVisita.addEventListener('click', () => irAPestana('#gasto-alimentos'));
    }

    function guardarCapituloC() {
        if (!hogarSeleccionadoActual || !hogarAbierto) return false;
        const obs = document.getElementById('obs-capitulo-c');
        if (!obs) return false;

        const capCData = {
            mercadosGlobales: {},
            observaciones: obs.value
        };

        alimentosCapituloC.forEach(item => {
            const checkedRadio = document.querySelector(`input[name="frec_${item.id}"]:checked`);
            const valInput = document.getElementById(`val_${item.id}`);
            const compSelect = document.getElementById(`comprara_${item.id}`);

            capCData[item.id] = {
                frecuencia: checkedRadio ? checkedRadio.value : '',
                valor: valInput ? valInput.value : '',
                comprara: compSelect ? compSelect.value : ''
            };
        });

        mercadosGlobalesCapC.forEach(m => {
            const mercInput = document.getElementById(`mercado_${m.codigo}`);
            capCData.mercadosGlobales[m.codigo] = mercInput ? mercInput.value : '';
        });

        hogarSeleccionadoActual.capituloC = capCData;
        guardarHogaresBD();
        return true;
    }

    let temporizadorCapC = null;

    function programarGuardadoCapC() {
        clearTimeout(temporizadorCapC);
        temporizadorCapC = setTimeout(() => {
            temporizadorCapC = null;
            guardarCapituloC();
        }, 400);
    }

    function flushGuardados() {
        if (temporizadorCapC) {
            clearTimeout(temporizadorCapC);
            temporizadorCapC = null;
            guardarCapituloC();
        }
    }

    const seccionC = document.getElementById('gasto-alimentos');
    if (seccionC) {
        seccionC.addEventListener('input', programarGuardadoCapC);
        seccionC.addEventListener('change', programarGuardadoCapC);
    }

    window.addEventListener('beforeunload', flushGuardados);
    document.addEventListener('visibilitychange', () => {
        if (document.visibilityState === 'hidden') flushGuardados();
    });

    const btnGuardarAlimentos = document.getElementById('btn-guardar-alimentos');
    if (btnGuardarAlimentos) {
        btnGuardarAlimentos.addEventListener('click', () => {
            clearTimeout(temporizadorCapC);
            temporizadorCapC = null;
            if (guardarCapituloC()) {
                alert(`✅ Registro del Capítulo C guardado para el hogar ${hogarSeleccionadoActual.codigo}.`);
            }
        });
    }

    const btnContAlimentos = document.getElementById('btn-continuar-alimentos');
    if (btnContAlimentos) {
        btnContAlimentos.addEventListener('click', () => irAPestana('#gastos-diarios'));
    }

    const selCategoria = document.getElementById('gasto-d-categoria');
    const contCategoriaOtro = document.getElementById('contenedor-categoria-otro');
    if (selCategoria && contCategoriaOtro) {
        selCategoria.addEventListener('change', () => {
            contCategoriaOtro.style.display = (selCategoria.value === 'Otro') ? 'block' : 'none';
            if (selCategoria.value === 'Otro') {
                const inp = document.getElementById('gasto-d-categoria-otro');
                if (inp) inp.focus();
            }
        });
    }

    const btnAgregarGastoD = document.getElementById('btn-agregar-gasto-d');
    if (btnAgregarGastoD) {
        btnAgregarGastoD.addEventListener('click', () => {
            if (!hogarSeleccionadoActual) return;

            const fecha = document.getElementById('gasto-d-fecha').value;
            let categoria = document.getElementById('gasto-d-categoria').value;
            const concepto = document.getElementById('gasto-d-concepto').value.trim();
            const valor = document.getElementById('gasto-d-valor').value;

            if (categoria === 'Otro') {
                categoria = document.getElementById('gasto-d-categoria-otro').value.trim();
                if (!categoria) {
                    alert('Por favor escriba la categoría del gasto.');
                    return;
                }
            }

            if (!fecha || !concepto || !valor) {
                alert('Por favor complete todos los campos del gasto.');
                return;
            }

            if (!hogarSeleccionadoActual.gastosDiarios) hogarSeleccionadoActual.gastosDiarios = [];

            hogarSeleccionadoActual.gastosDiarios.push({ dia: diaActual, fecha, categoria, concepto, valor });
            guardarHogaresBD();
            renderDiaD();

            document.getElementById('gasto-d-concepto').value = '';
            document.getElementById('gasto-d-valor').value = '';
            document.getElementById('gasto-d-categoria-otro').value = '';
            limpiarBorrador(['gasto-d-concepto', 'gasto-d-valor', 'gasto-d-categoria-otro']);
        });
    }

    function sumarUnDia(fechaISO) {
        if (!/^\d{4}-\d{2}-\d{2}$/.test(fechaISO || '')) return fechaISO || '';
        const f = new Date(fechaISO + 'T00:00:00');
        f.setDate(f.getDate() + 1);
        const y = f.getFullYear();
        const m = String(f.getMonth() + 1).padStart(2, '0');
        const dd = String(f.getDate()).padStart(2, '0');
        return `${y}-${m}-${dd}`;
    }

    const btnTraerAyer = document.getElementById('btn-traer-ayer');
    if (btnTraerAyer) {
        btnTraerAyer.addEventListener('click', () => {
            if (!hogarSeleccionadoActual || diaActual <= 1) return;

            const todos = hogarSeleccionadoActual.gastosDiarios || [];
            const deAyer = todos.filter(g => Number(g.dia || 1) === diaActual - 1);
            const deHoy = todos.filter(g => Number(g.dia || 1) === diaActual);

            if (deAyer.length === 0) {
                alert(`El día ${diaActual - 1} no tiene gastos para traer.`);
                return;
            }

            if (deHoy.length > 0) {
                if (!confirm(`El día ${diaActual} ya tiene ${deHoy.length} gasto(s). ¿Agregar de todos modos los ${deAyer.length} del día anterior?`)) {
                    return;
                }
            }

            deAyer.forEach(g => {
                todos.push({
                    dia: diaActual,
                    fecha: sumarUnDia(g.fecha),
                    categoria: g.categoria || '',
                    concepto: g.concepto,
                    valor: g.valor
                });
            });

            hogarSeleccionadoActual.gastosDiarios = todos;
            guardarHogaresBD();
            renderDiaD();
        });
    }

    document.querySelectorAll('input[name="comio-fuera"]').forEach(radio => {
        radio.addEventListener('change', () => {
            if (!hogarSeleccionadoActual) return;

            if (!hogarSeleccionadoActual.comioFuera) hogarSeleccionadoActual.comioFuera = {};
            if (!hogarSeleccionadoActual.comidasFuera) hogarSeleccionadoActual.comidasFuera = [];

            const delDia = hogarSeleccionadoActual.comidasFuera.filter(c => Number(c.dia || 1) === diaActual);

            if (radio.value === 'no' && delDia.length > 0) {
                if (!confirm(`Hay ${delDia.length} comida(s) fuera registradas en el día ${diaActual}. Si responde "No" se eliminarán. ¿Continuar?`)) {
                    renderDiaD();
                    return;
                }
                hogarSeleccionadoActual.comidasFuera = hogarSeleccionadoActual.comidasFuera.filter(c => Number(c.dia || 1) !== diaActual);
            }

            hogarSeleccionadoActual.comioFuera[diaActual] = radio.value;
            guardarHogaresBD();
            renderDiaD();
        });
    });

    const btnAgregarComidaD1 = document.getElementById('btn-agregar-comida-d1');
    if (btnAgregarComidaD1) {
        btnAgregarComidaD1.addEventListener('click', () => {
            if (!hogarSeleccionadoActual) return;

            const tipo = document.getElementById('comida-d1-tipo').value;
            const lugar = document.getElementById('comida-d1-lugar').value;
            const valor = document.getElementById('comida-d1-valor').value;

            if (!lugar || !valor) {
                alert('Por favor especifique el establecimiento y el valor.');
                return;
            }

            if (!hogarSeleccionadoActual.comidasFuera) hogarSeleccionadoActual.comidasFuera = [];

            hogarSeleccionadoActual.comidasFuera.push({ dia: diaActual, tipo, lugar, valor });
            guardarHogaresBD();
            renderDiaD();

            document.getElementById('comida-d1-lugar').value = '';
            document.getElementById('comida-d1-valor').value = '';
            limpiarBorrador(['comida-d1-lugar', 'comida-d1-valor']);
        });
    }

    const btnDiaAnterior = document.getElementById('btn-dia-anterior');
    if (btnDiaAnterior) {
        btnDiaAnterior.addEventListener('click', () => {
            if (diaActual > 1) {
                diaActual--;
                renderDiaD();
            }
        });
    }

    const btnDiaSiguiente = document.getElementById('btn-dia-siguiente');
    if (btnDiaSiguiente) {
        btnDiaSiguiente.addEventListener('click', () => {
            if (diaActual < 14) {
                diaActual++;
                renderDiaD();
            }
        });
    }

    const btnContinuarGastos = document.getElementById('btn-continuar-gastos');
    if (btnContinuarGastos) {
        btnContinuarGastos.addEventListener('click', () => irAPestana('#consultas'));
    }
}