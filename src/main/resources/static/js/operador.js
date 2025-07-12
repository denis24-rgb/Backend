// Mapa y marcadores
let map;
let markerCluster;
let markers = [];

// Inicializar mapa
function initMap() {
    map = new google.maps.Map(document.getElementById("map"), {
        center: { lat: -17.402354, lng: -63.881410 },
        zoom: 13,
        mapTypeId: "roadmap",
        gestureHandling: "greedy"
    });

    cargarReportes();
}

// Cargar marcadores de reportes
function cargarReportes() {
    if (!reportes || reportes.length === 0) return;

    // Limpiar marcadores anteriores si los hay
    markers.forEach(m => m.setMap(null));
    markers = [];
    if (markerCluster) markerCluster.clearMarkers();

    // Filtrar reportes válidos
    const reportesValidos = reportes.filter(r =>
        r.latitud !== undefined && r.longitud !== undefined &&
        !isNaN(parseFloat(r.latitud)) && !isNaN(parseFloat(r.longitud))
    );

    reportesValidos.forEach(reporte => {
        const posicion = { lat: parseFloat(reporte.latitud), lng: parseFloat(reporte.longitud) };

        const icono = reporte.tipoReporte?.icono
            ? `/imagenes/iconos/${reporte.tipoReporte.icono}`
            : "/imagenes/icono-default.png";

        const marker = new google.maps.Marker({
            position: posicion,
            icon: {
                url: icono,
                scaledSize: new google.maps.Size(40, 40)
            },
            title: reporte.tipoReporte?.nombre || "Reporte"
        });

        marker.reporte = reporte;
        marker.addListener("click", () => mostrarDetalleReporte(reporte));
        markers.push(marker);
    });

    markerCluster = new MarkerClusterer(map, markers, {
        imagePath: "https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m"
    });
}

// Mostrar detalle en modal
function mostrarDetalleReporte(reporte) {
    const contenido = `
        <h6><strong>Tipo:</strong> ${reporte.tipoReporte?.nombre || "No especificado"}</h6>
        <h6><strong>Descripción:</strong> ${reporte.descripcion || "Sin descripción"}</h6>
        <h6><strong>Estado:</strong> ${reporte.estado || "Sin estado"}</h6>
        <h6><strong>Ubicación:</strong> ${reporte.latitud}, ${reporte.longitud}</h6>
    `;

    document.getElementById("contenidoModal").innerHTML = contenido;
    new bootstrap.Modal(document.getElementById("modalDetalleReporte")).show();
}

// Mostrar/Ocultar filtro
function mostrarFiltro() {
    document.getElementById("filtroFlotante").style.display = "block";
    document.getElementById("btnMostrarFiltro").style.display = "none";
}

function ocultarFiltro() {
    document.getElementById("filtroFlotante").style.display = "none";
    document.getElementById("btnMostrarFiltro").style.display = "block";
}

// Mostrar todos los reportes
function mostrarTodosReportes() {
    cargarReportes();
}

// Cargar filtros dinámicamente
window.addEventListener("DOMContentLoaded", () => {
    cargarFiltros();
    cargarAsignaciones();
    verificarNuevosReportes();
});

function cargarFiltros() {
    const contenedorTipos = document.getElementById("filtroTipos");
    const contenedorEstados = document.getElementById("filtroEstados");

    const tiposUnicos = [...new Set(reportes.map(r => r.tipoReporte?.nombre).filter(Boolean))];
    const estadosUnicos = [...new Set(reportes.map(r => r.estado).filter(Boolean))];

    tiposUnicos.forEach(tipo => {
        const btn = document.createElement("button");
        btn.className = "btn btn-outline-primary btn-sm";
        btn.textContent = tipo;
        btn.onclick = () => filtrarPorTipo(tipo);
        contenedorTipos.appendChild(btn);
    });

    estadosUnicos.forEach(estado => {
        const btn = document.createElement("button");
        btn.className = "btn btn-outline-secondary btn-sm";
        btn.textContent = estado;
        btn.onclick = () => filtrarPorEstado(estado);
        contenedorEstados.appendChild(btn);
    });
}

function filtrarPorTipo(tipoSeleccionado) {
    markers.forEach(m => m.setMap(null));

    const filtrados = markers.filter(m =>
        m.reporte.tipoReporte?.nombre.toLowerCase() === tipoSeleccionado.toLowerCase()
    );

    filtrados.forEach(m => m.setMap(map));

    if (markerCluster) markerCluster.clearMarkers();
    markerCluster = new MarkerClusterer(map, filtrados, {
        imagePath: "https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m"
    });
}

function filtrarPorEstado(estadoSeleccionado) {
    markers.forEach(m => m.setMap(null));

    const filtrados = markers.filter(m =>
        m.reporte.estado?.toLowerCase() === estadoSeleccionado.toLowerCase()
    );

    filtrados.forEach(m => m.setMap(map));

    if (markerCluster) markerCluster.clearMarkers();
    markerCluster = new MarkerClusterer(map, filtrados, {
        imagePath: "https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m"
    });
}

// Cargar tabla de asignaciones
function cargarAsignaciones() {
    const tabla = document.getElementById("tablaAsignaciones");
    tabla.innerHTML = "";

    if (!window.asignaciones || asignaciones.length === 0) {
        tabla.innerHTML = "<tr><td colspan='4' class='text-center'>Sin asignaciones recientes.</td></tr>";
        return;
    }

    asignaciones.forEach(asig => {
        const tr = document.createElement("tr");

        tr.innerHTML = `
            <td>${asig.reporteDescripcion}</td>
            <td>${asig.tecnicoNombre}</td>
            <td>${asig.estado}</td>
            <td>
                <button class="btn btn-sm btn-success" onclick="confirmarAsignacion('${asig.tecnicoNombre}', ${asig.reporteId})">
                    <i class="bi bi-check-circle"></i> Confirmar
                </button>
            </td>
        `;

        tabla.appendChild(tr);
    });
}

// Confirmar asignación (modal)
function confirmarAsignacion(nombreTecnico, idReporte) {
    document.getElementById("nombreTecnicoModal").textContent = nombreTecnico;

    const btnConfirmar = document.getElementById("btnConfirmarAsignacion");
    btnConfirmar.onclick = () => {
        console.log(`Asignado el reporte ${idReporte} a ${nombreTecnico}`);
        const modal = bootstrap.Modal.getInstance(document.getElementById("modalConfirmarAsignacion"));
        modal.hide();
        // Aquí va tu fetch al backend real para guardar la asignación
    };

    new bootstrap.Modal(document.getElementById("modalConfirmarAsignacion")).show();
}

// Alerta de nuevos reportes
function verificarNuevosReportes() {
    const alerta = document.getElementById("alertaNuevosReportes");
    if (!alerta) return;

    const hayPendientes = Array.isArray(reportes) && reportes.some(r => r.estado && r.estado.toLowerCase() === "recibido");

    if (hayPendientes) {
        alerta.style.display = "block";
        alerta.classList.remove("d-none");
    } else {
        alerta.style.display = "none";
        alerta.classList.add("d-none");
    }
}



