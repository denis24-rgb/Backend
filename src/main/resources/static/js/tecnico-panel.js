let mapa;
let marcadores = [];


// Filtro por estado
function filtrarPorEstado(estado) {
    cargarReportesPorEstado(estado);
}

// Inicializar mapa
function inicializarMapa() {
    mapa = new google.maps.Map(document.getElementById("mapa"), {
        center: { lat: -17.4024, lng: -63.8843 },
        zoom: 13,
        scrollwheel: true
    });
}

// Cargar reportes por estado
function cargarReportesPorEstado(estado) {
    fetch(`/tecnico/reportes?estado=${estado}`)
        .then(res => res.json())
        .then(data => {
            const contenedor = document.getElementById("contenedor-reportes");
            contenedor.innerHTML = "";

            // Limpiar marcadores anteriores
            marcadores.forEach(m => m.setMap(null));
            marcadores = [];

            if (data.length === 0) {
                contenedor.innerHTML = `<p>No hay reportes en <strong>${estado}</strong>.</p>`;
                return;
            }


            data.forEach(reporte => {
                const card = document.createElement("div");
                card.className = "card card-reporte mb-3";

                const estadoVisible = reporte.estado === "resuelto" ? "FINALIZADO" : reporte.estado.toUpperCase();

                let html = `
                    <div class="card-body">
                        <h5 class="titulo-card">${reporte.tipoReporte.nombre}</h5>
                        <p><strong>Descripción:</strong> ${reporte.descripcion}</p>
                        <p><strong>Ubicación:</strong> <button class="btn btn-sm btn-outline-primary btn-ubicar" data-id="${reporte.id}">Ubicar</button></p>
                        <p><strong>Fecha de reporte:</strong> ${formatearFecha(reporte.fechaReporte)}</p>
                        <p><strong>Fecha de asignación:</strong> ${formatearFecha(reporte.fechaAsignacion)}</p>
                        <p><strong>Asignado por:</strong> ${reporte.asignadoPor}</p>`;

                if (reporte.imagen) {
                    html += `
                        <div class="mb-2">
                            <img src="/imagenes/${reporte.imagen}" alt="Imagen del reporte" class="img-fluid rounded imagen-reporte">
                        </div>`;
                }

                html += `<p><strong>Estado:</strong> ${estadoVisible}</p>`;

                if (reporte.estado === "recibido") {
                    html += `
                        <div class="d-flex gap-2 mt-3">
                            <button class="btn btn-sm btn-outline-primary btn-ubicar" data-id="${reporte.id}">Ubicar</button>
                            <button class="btn btn-sm btn-warning btn-tomar" data-id="${reporte.id}">Tomar reporte</button>
                        </div>`;
                }

                if (reporte.estado === "en proceso") {
                    html += `
        <form class="form-finalizar mt-3" data-id="${reporte.id}" enctype="multipart/form-data">
            <input type="hidden" name="nuevoEstado" value="resuelto" />
            
            <div class="mb-2">
                <label class="form-label">Comentario del técnico:</label>
                <textarea name="comentario" class="form-control form-control-sm" rows="2"
                          placeholder="Breve descripción de la intervención..." required></textarea>
            </div>
            
            <div class="mb-2">
                <label class="form-label">Evidencia (opcional):</label>
                <input type="file" name="imagenTrabajo" accept="image/*" class="form-control form-control-sm" />
            </div>
            
            <div class="d-flex gap-2">
                <button type="button" class="btn btn-sm btn-outline-primary btn-ubicar" data-id="${reporte.id}">Ubicar</button>
                <button type="submit" class="btn btn-sm btn-success">Finalizar</button>
            </div>
        </form>`;
                }


                html += `</div>`;
                card.innerHTML = html;
                contenedor.appendChild(card);

                // Crear marcador si tiene ubicación
                // Limpiar marcadores anteriores
                marcadores.forEach(m => m.setMap(null));
                marcadores = [];

// Crear marcador si tiene ubicación
                if (reporte.ubicacion) {
                    const [lat, lng] = reporte.ubicacion.split(",").map(Number);

                    const icono = reporte.tipoReporte?.icono
                        ? `/imagenes/${reporte.tipoReporte.icono}`
                        : "/imagenes/icono-default.png";

                    const marker = new google.maps.Marker({
                        position: { lat, lng },
                        map: mapa,
                        icon: {
                            url: icono,
                            scaledSize: new google.maps.Size(35, 35)
                        },
                        customId: reporte.id
                    });

                    const infoWindow = new google.maps.InfoWindow({
                        content: `<strong>${reporte.tipoReporte?.nombre || 'Reporte'}</strong><br>${reporte.descripcion}`
                    });

                    marker.addListener("click", () => {
                        infoWindow.open(mapa, marker);
                    });

                    marcadores.push(marker);
                }

            });


            activarEventos();

        });
}

// Contador de reportes
function contarReportes() {
    fetch(`/tecnico/reportes/contar`)
        .then(res => res.json())
        .then(data => {
            const pendientesEl = document.getElementById("count-pendientes");
            const procesoEl = document.getElementById("count-proceso");
            const finalizadosEl = document.getElementById("count-finalizados");
            const cerradoEl = document.getElementById("count-cerrado");

            if (pendientesEl) pendientesEl.textContent = data["recibido"] || 0;
            if (procesoEl) procesoEl.textContent = data["en proceso"] || 0;
            if (finalizadosEl) finalizadosEl.textContent = data["resuelto"] || 0;
        });
}

// Formato de fecha
function formatearFecha(fechaISO) {
    if (!fechaISO) return "Sin fecha";
    const fecha = new Date(fechaISO);
    return fecha.toLocaleString('es-BO', {
        day: '2-digit', month: '2-digit', year: 'numeric',
        hour: '2-digit', minute: '2-digit'
    });
}

// Activar botones y formularios
function activarEventos() {
    // Click en imagen para abrir modal
    document.querySelectorAll(".imagen-reporte").forEach(img => {
        img.addEventListener("click", () => {
            const src = img.getAttribute("src");
            const imagenModal = document.getElementById("imagenModalAmpliada");
            imagenModal.src = src;
            imagenModal.style.transform = "scale(1)"; // Reset zoom
            scale = 1;
            const modal = new bootstrap.Modal(document.getElementById("modalImagen"));
            modal.show();
        });
    });

    // Zoom con la rueda del mouse
    const imagenModal = document.getElementById("imagenModalAmpliada");
    let scale = 1;

    imagenModal.addEventListener("wheel", (e) => {
        e.preventDefault();
        const delta = e.deltaY;
        if (delta < 0) {
            scale += 0.1;
        } else {
            scale = Math.max(1, scale - 0.1);
        }
        imagenModal.style.transform = `scale(${scale})`;
    });

    // Tomar reporte
    document.querySelectorAll(".btn-tomar").forEach(btn => {
        btn.addEventListener("click", () => {
            const id = btn.dataset.id;
            fetch("/tecnico/tomar-reporte", {
                method: "POST",
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: `reporteId=${id}`
            }).then(() => {
                cargarReportesPorEstado("recibido");
                contarReportes();
            });
        });
    });

    // Finalizar reporte
    document.querySelectorAll(".form-finalizar").forEach(form => {
        form.addEventListener("submit", e => {
            e.preventDefault();

            const id = form.dataset.id;
            const comentario = form.querySelector("textarea[name='comentario']").value;
            const imagenInput = form.querySelector('input[name="imagenTrabajo"]');
            const imagen = imagenInput.files[0];

            if (!comentario.trim()) {
                alert("El comentario es obligatorio para finalizar el reporte.");
                return;
            }

            const formData = new FormData();
            formData.append("reporteId", id);
            formData.append("comentario", comentario);
            if (imagen) {
                formData.append("imagenTrabajo", imagen);
            }

            fetch("/tecnico/actualizar-reporte", {
                method: "POST",
                body: formData
            }).then(res => res.text())
                .then(data => {
                    if (data === "ok") {
                        cargarReportesPorEstado("en proceso");
                        contarReportes();
                    } else {
                        alert("Ocurrió un error al finalizar el reporte.");
                    }
                });
        });
    });



    // Botón ubicar en mapa
    document.querySelectorAll(".btn-ubicar").forEach(btn => {
        btn.addEventListener("click", () => {
            const id = btn.dataset.id;
            ubicarEnMapa(id);
        });
    });
}

// Centrar el mapa en el marcador correspondiente
function ubicarEnMapa(id) {
    const marcador = marcadores.find(m => m.customId == id);
    if (marcador) {
        mapa.setCenter(marcador.getPosition());
        mapa.setZoom(16);
    }
}

window.initMap = () => {
    inicializarMapa();
    cargarReportesPorEstado("recibido");
    contarReportes();
};
const imagenModal = document.getElementById("imagenModalAmpliada");
let scale = 1;

imagenModal.addEventListener("wheel", (e) => {
    e.preventDefault();
    const delta = e.deltaY;
    if (delta < 0) {
        scale += 0.1;
    } else {
        scale = Math.max(1, scale - 0.1);
    }
    imagenModal.style.transform = `scale(${scale})`;
});
