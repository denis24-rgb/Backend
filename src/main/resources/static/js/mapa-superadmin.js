console.log("🟢 mapa-superadmin.js cargado");

function initMap() {
    const centro = { lat: -17.4006, lng: -63.8349 };

    const mapa = new google.maps.Map(document.getElementById("map"), {
        zoom: 13,
        center: centro,
        mapTypeId: "roadmap",
        gestureHandling: "greedy"
    });

    if (!Array.isArray(reportes)) {
        console.error("❌ La variable 'reportes' no es un array válido.");
        return;
    }

    const marcadores = [];
    const filtrosTipo = new Set();
    const filtrosEstado = new Set();

    const tiposUnicos = [...new Set(reportes.map(r => r.tipoReporte?.nombre).filter(Boolean))];
    const estadosUnicos = [...new Set(reportes.map(r => r.estado?.nombre).filter(Boolean))];

    const contenedorTipos = document.getElementById("contenedorFiltros");
    const contenedorEstados = document.getElementById("contenedorEstados");

    // 🟦 Crear botones de tipos de reporte
    tiposUnicos.forEach(tipo => {
        const btn = document.createElement("button");
        btn.className = "btn btn-outline-primary btn-sm filtro-btn";
        btn.textContent = tipo;
        btn.onclick = () => {
            if (filtrosTipo.has(tipo)) {
                filtrosTipo.delete(tipo);
            } else {
                filtrosTipo.add(tipo);
            }
            aplicarFiltros();
            actualizarEstiloBotones();
            guardarFiltros();
        };
        contenedorTipos.appendChild(btn);
    });

    // 🟦 Botón mostrar todos tipos
    const btnTodosTipos = document.createElement("button");
    btnTodosTipos.className = "btn btn-outline-dark btn-sm filtro-btn";
    btnTodosTipos.textContent = "Mostrar Todos";
    btnTodosTipos.onclick = () => {
        filtrosTipo.clear();
        aplicarFiltros();
        actualizarEstiloBotones();
        guardarFiltros();
    };
    contenedorTipos.appendChild(btnTodosTipos);

    // 🟩 Crear botones de estados de reporte
    estadosUnicos.forEach(estado => {
        const btn = document.createElement("button");
        btn.className = "btn btn-outline-success btn-sm filtro-btn-estado";
        btn.textContent = estado;
        btn.onclick = () => {
            if (filtrosEstado.has(estado)) {
                filtrosEstado.delete(estado);
            } else {
                filtrosEstado.add(estado);
            }
            aplicarFiltros();
            actualizarEstiloBotones();
            guardarFiltros();
        };
        contenedorEstados.appendChild(btn);
    });

    const btnTodosEstados = document.createElement("button");
    btnTodosEstados.className = "btn btn-outline-dark btn-sm filtro-btn-estado";
    btnTodosEstados.textContent = "Mostrar Todos";
    btnTodosEstados.onclick = () => {
        filtrosEstado.clear();
        aplicarFiltros();
        actualizarEstiloBotones();
        guardarFiltros();
    };
    contenedorEstados.appendChild(btnTodosEstados);

    // 📍 Crear marcadores
    reportes.forEach(reporte => {
        if (reporte.latitud && reporte.longitud) {
            const iconoURL = "/imagenes/" + (reporte.tipoReporte?.icono || "icono-default.png");

            const marcador = new google.maps.Marker({
                position: { lat: reporte.latitud, lng: reporte.longitud },
                icon: {
                    url: iconoURL,
                    scaledSize: new google.maps.Size(40, 40)
                },
                title: reporte.tipoReporte?.nombre || "Reporte",
                animation: google.maps.Animation.DROP
            });

            marcador.reporte = reporte;

            marcador.addListener("click", () => {
                const html = `
                    <p><strong>Tipo:</strong> ${reporte.tipoReporte?.nombre || 'N/A'}</p>
                    <p><strong>Descripción:</strong> ${reporte.descripcion || 'Sin descripción'}</p>
                    <p><strong>Institución:</strong> ${reporte.institucion?.nombre || 'Sin asignar'}</p>
                    <p><strong>Estado:</strong> ${reporte.estado?.nombre || 'Desconocido'}</p>
                    <p><strong>Ubicación:</strong> ${reporte.ubicacion || 'No registrada'}</p>
                    <p><strong>Fecha:</strong> ${reporte.fechaReporte?.substring(0, 10) || 'No registrada'}</p>
                `;
                document.getElementById("contenidoModal").innerHTML = html;
                new bootstrap.Modal(document.getElementById("modalDetalleReporte")).show();
            });

            marcadores.push(marcador);
        }
    });

    // 🔵 Cluster
    const cluster = new MarkerClusterer(mapa, marcadores, {
        imagePath: 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m'
    });

    // 🎯 Aplicar filtros activos
    function aplicarFiltros() {
        const visibles = [];

        marcadores.forEach(m => {
            const tipo = m.reporte?.tipoReporte?.nombre;
            const estado = m.reporte?.estado?.nombre;

            const visibleTipo = filtrosTipo.size === 0 || filtrosTipo.has(tipo);
            const visibleEstado = filtrosEstado.size === 0 || filtrosEstado.has(estado);

            const visible = visibleTipo && visibleEstado;
            m.setVisible(visible);
            if (visible) visibles.push(m);
        });

        cluster.clearMarkers();
        cluster.addMarkers(visibles);
    }

    // 🎨 Actualiza estilos
    function actualizarEstiloBotones() {
        document.querySelectorAll(".filtro-btn").forEach(btn => {
            const texto = btn.textContent.trim();
            btn.classList.toggle("active", filtrosTipo.has(texto) || (texto === "Mostrar Todos" && filtrosTipo.size === 0));
        });

        document.querySelectorAll(".filtro-btn-estado").forEach(btn => {
            const texto = btn.textContent.trim();
            btn.classList.toggle("active", filtrosEstado.has(texto) || (texto === "Mostrar Todos" && filtrosEstado.size === 0));
        });
    }

    // 💾 Guardar filtros en localStorage
    function guardarFiltros() {
        localStorage.setItem("filtrosTipo", JSON.stringify([...filtrosTipo]));
        localStorage.setItem("filtrosEstado", JSON.stringify([...filtrosEstado]));
    }

    // 📥 Cargar filtros si existen
    function cargarFiltrosGuardados() {
        const tipos = JSON.parse(localStorage.getItem("filtrosTipo") || "[]");
        const estados = JSON.parse(localStorage.getItem("filtrosEstado") || "[]");

        tipos.forEach(t => filtrosTipo.add(t));
        estados.forEach(e => filtrosEstado.add(e));
    }

    cargarFiltrosGuardados();
    aplicarFiltros();
    actualizarEstiloBotones();
}

// 🎛️ Mostrar/Ocultar panel filtros con efecto
function toggleFiltros() {
    const panel = document.getElementById("panelFiltros");
    panel.classList.toggle("show");
}

window.initMap = initMap;
