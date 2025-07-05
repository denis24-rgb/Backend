let mapa;
let todosLosMarcadores = [];
let cluster; // Referencia del agrupador
let tecnicoSeleccionado = { id: null, nombre: null, reporteId: null };

function initMap() {
    const centro = { lat: -17.402354, lng: -63.881410 };
    mapa = new google.maps.Map(document.getElementById("map"), {
        center: centro,
        zoom: 13,
        mapTypeId: "roadmap",
        gestureHandling: "greedy"
    });

    cargarFiltros();
    renderizarMarcadores(); // Mostrar todos los marcadores
}

function renderizarMarcadores() {
    guardarFiltrosEnLocalStorage();

    // Limpiar marcadores anteriores
    todosLosMarcadores.forEach(m => m.setMap(null));
    todosLosMarcadores = [];

    if (cluster) {
        cluster.clearMarkers(); // Limpiar agrupador anterior
    }

    const tiposSeleccionados = obtenerFiltros("tipo");
    const estadosSeleccionados = obtenerFiltros("estado");

    const filtrados = reportes.filter(r => {
        if (!r.ubicacion || !r.ubicacion.includes(",")) return false;

        const partes = r.ubicacion.split(",").map(p => parseFloat(p.trim()));
        const ubicacionValida = partes.length === 2 && !isNaN(partes[0]) && !isNaN(partes[1]);

        const tipoValido = tiposSeleccionados.length === 0 || tiposSeleccionados.includes(r.tipoReporte?.nombre);
        const estadoValido = estadosSeleccionados.length === 0 || estadosSeleccionados.includes(r.estado?.nombre);

        return ubicacionValida && tipoValido && estadoValido;
    });

    // Actualizar contador
    const contador = document.getElementById("contadorReportes");
    const textoContador = document.getElementById("textoContador");
    if (contador && textoContador) {
        textoContador.textContent = `Mostrando ${filtrados.length} reportes`;
        contador.classList.remove("visually-hidden");
    }

    // Crear nuevos marcadores
    filtrados.forEach(reporte => {
        const partes = reporte.ubicacion.split(",").map(p => parseFloat(p.trim()));
        const posicion = { lat: partes[0], lng: partes[1] };

        const icono = reporte.tipoReporte?.icono
            ? `/imagenes/${reporte.tipoReporte.icono}`
            : "/imagenes/icono-default.png";

        const marcador = new google.maps.Marker({
            position: posicion,
            title: reporte.tipoReporte?.nombre || "Reporte",
            icon: {
                url: icono,
                scaledSize: new google.maps.Size(40, 40)
            },
            animation: google.maps.Animation.DROP
        });

        marcador.reporte = reporte;
        marcador.addListener("click", () => mostrarDetalleReporte(reporte));
        todosLosMarcadores.push(marcador);
    });

    // Cluster
    cluster = new MarkerClusterer(mapa, todosLosMarcadores, {
        imagePath: "https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m"
    });

    // Cambiar color del botón de filtros si hay filtros activos
    const btnFiltro = document.getElementById("btnMostrarFiltro");
    const hayFiltros = tiposSeleccionados.length > 0 || estadosSeleccionados.length > 0;
    if (btnFiltro) {
        btnFiltro.classList.toggle("filtro-activo", hayFiltros);
    }
}


function cargarFiltros() {
    const tiposUnicos = [...new Set(reportes.map(r => r.tipoReporte?.nombre).filter(Boolean))];
    const estadosUnicos = [...new Set(reportes.map(r => r.estado?.nombre).filter(Boolean))];

    const tiposGuardados = JSON.parse(localStorage.getItem("filtrosTipoAdmin") || "[]");
    const estadosGuardados = JSON.parse(localStorage.getItem("filtrosEstadoAdmin") || "[]");

    const contTipos = document.getElementById("filtroTipos");
    const contEstados = document.getElementById("filtroEstados");

    contTipos.innerHTML = "";
    contEstados.innerHTML = "";

    tiposUnicos.forEach(tipo => {
        const checked = tiposGuardados.includes(tipo) ? "checked" : "";
        contTipos.innerHTML += `
            <label class="me-2"><input type="checkbox" class="filtro-tipo me-1" value="${tipo}" onchange="renderizarMarcadores()" ${checked}> ${tipo}</label>
        `;
    });

    estadosUnicos.forEach(estado => {
        const checked = estadosGuardados.includes(estado) ? "checked" : "";
        contEstados.innerHTML += `
            <label class="me-2"><input type="checkbox" class="filtro-estado me-1" value="${estado}" onchange="renderizarMarcadores()" ${checked}> ${estado}</label>
        `;
    });
}

function obtenerFiltros(tipo) {
    const clase = tipo === "tipo" ? "filtro-tipo" : "filtro-estado";
    return Array.from(document.querySelectorAll(`.${clase}:checked`)).map(e => e.value);
}

function mostrarDetalleReporte(reporte) {
    const modalBody = document.getElementById('contenidoModal');

    let contenido = `
        <h6><strong>Tipo:</strong> ${reporte.tipoReporte?.nombre || "No especificado"}</h6>
        <h6><strong>Descripción:</strong> ${reporte.descripcion || "Sin descripción"}</h6>
        <h6><strong>Estado:</strong> ${reporte.estado?.nombre || "Sin estado"}</h6>
        <h6><strong>Fecha:</strong> ${reporte.fechaReporte || "Sin fecha"}</h6>
    `;

    const tecnicoAsignado = reporte.tecnico && reporte.tecnico.nombre;

    if (!tecnicoAsignado) {
        const tecnicosDisponibles = window.listaTecnicos || [];

        const selector = `
            <label class="mt-3"><strong>Técnico:</strong></label>
            <select id="tecnicoSelect-${reporte.id}" class="form-select mt-1"
                    onchange="confirmarAsignacionTecnico(${reporte.id}, this)">
                <option disabled selected>Seleccionar técnico...</option>
                ${tecnicosDisponibles.map(t => `<option value="${t.id}">${t.nombre}</option>`).join('')}
            </select>
        `;
        contenido += selector;
    }else {
        contenido += `
    <div class="mt-3">
        <h6><i class="bi bi-person-check-fill text-success me-2"></i><strong>Técnico:</strong> ${reporte.tecnico.nombre}</h6>
    </div>
`;

    }

    modalBody.innerHTML = contenido;
    const modal = new bootstrap.Modal(document.getElementById('modalDetalleReporte'));
    modal.show();
}

function mostrarSelectorTecnico(reporteId) {
    const container = document.getElementById('selectorTecnicoContainer');

    // Simulación: obtén la lista de técnicos desde una variable o fetch real
    const tecnicosDisponibles = window.listaTecnicos || []; // Define esta variable global al cargar la vista

    let selector = `
        <select id="tecnicoSelect" class="form-select mb-2">
            <option disabled selected>Seleccionar técnico...</option>
            ${tecnicosDisponibles.map(t => `<option value="${t.id}">${t.nombre}</option>`).join('')}
        </select>
        <button class="btn btn-success btn-sm" onclick="asignarTecnico(${reporteId})">
            ✅ Confirmar Asignación
        </button>
    `;

    container.innerHTML = selector;
    container.style.display = 'block';
}
function asignarTecnico(reporteId) {
    const tecnicoId = document.getElementById('tecnicoSelect').value;

    fetch(`/reportes/asignar/${reporteId}?tecnicoId=${tecnicoId}`, {
        method: 'POST'
    })
        .then(res => {
            if (res.ok) {
                alert('✅ Técnico asignado correctamente');
                location.reload(); // o actualizar la tarjeta si prefieres sin recargar
            } else {
                alert('❌ Error al asignar técnico');
            }
        })
        .catch(() => alert('⚠️ Error de conexión'));
}
function confirmarAsignacionTecnico(reporteId, selectElement) {
    const tecnicoId = selectElement.value;
    const nombreTecnico = selectElement.options[selectElement.selectedIndex].text;

    // Guardamos temporalmente
    tecnicoSeleccionado = { id: tecnicoId, nombre: nombreTecnico, reporteId };

    // Mostramos nombre del técnico en el modal
    document.getElementById("nombreTecnicoModal").textContent = nombreTecnico;

    // Mostramos el modal bonito
    const modal = new bootstrap.Modal(document.getElementById("modalConfirmarAsignacion"));
    modal.show();
}
document.addEventListener("DOMContentLoaded", () => {
    const btnConfirmar = document.getElementById("btnConfirmarAsignacion");

    if (btnConfirmar) {
        btnConfirmar.addEventListener("click", () => {
            const { reporteId, id: tecnicoId, nombre: nombreTecnico } = tecnicoSeleccionado;

            fetch(`/reportes/asignar/${reporteId}?tecnicoId=${tecnicoId}`, {
                method: 'POST'
            })
                .then(res => res.ok ? res.json() : Promise.reject())
                .then(reporteActualizado => {
                    // Ocultar modal de confirmación
                    const modal = bootstrap.Modal.getInstance(document.getElementById("modalConfirmarAsignacion"));
                    modal.hide();

                    // Actualizar contenido del modal de detalles
                    const modalBody = document.getElementById('contenidoModal');
                    modalBody.innerHTML += `
                    <div class="alert alert-success mt-3">✅ Técnico asignado correctamente.</div>
                    <div class="mt-3"><strong>Técnico:</strong> ${nombreTecnico}</div>
                `;

                    // Actualizar el objeto en memoria
                    const marcador = todosLosMarcadores.find(m => m.reporte.id === reporteId);
                    if (marcador) {
                        marcador.reporte.tecnico = { id: tecnicoId, nombre: nombreTecnico };
                    }
                })
                .catch(() => {
                    alert('❌ Error al asignar técnico');
                });
        });
    }
});

function reiniciarFiltros() {
    document.querySelectorAll("#filtroTipos input, #filtroEstados input").forEach(c => c.checked = false);
    renderizarMarcadores();
}

window.addEventListener("load", initMap);
function mostrarFiltro() {
    document.getElementById('filtroFlotante').style.display = 'block';
    document.getElementById('btnMostrarFiltro').style.display = 'none';
}

function ocultarFiltro() {
    document.getElementById('filtroFlotante').style.display = 'none';
    document.getElementById('btnMostrarFiltro').style.display = 'block';
}
function mostrarTodosReportes() {
    // Limpiar filtros visuales
    document.querySelectorAll("#filtroTipos input, #filtroEstados input").forEach(c => c.checked = false);

    // Limpiar localStorage
    localStorage.removeItem("filtrosTipoAdmin");
    localStorage.removeItem("filtrosEstadoAdmin");

    renderizarMarcadores();
}

function guardarFiltrosEnLocalStorage() {
    const tipos = obtenerFiltros("tipo");
    const estados = obtenerFiltros("estado");
    localStorage.setItem("filtrosTipoAdmin", JSON.stringify(tipos));
    localStorage.setItem("filtrosEstadoAdmin", JSON.stringify(estados));
}

