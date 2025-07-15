let mapa;
let marcadores = [];

let scale = 1;
let targetScale = 1;
let animating = false;



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

            // Limpiar marcadores anteriores (SOLO UNA VEZ)
            marcadores.forEach(m => m.setMap(null));
            marcadores = [];

            if (data.length === 0) {
                contenedor.innerHTML = `<p>No hay reportes en <strong>${estado}</strong>.</p>`;
                return;
            }

            // Recorremos los reportes
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
            <p class="mb-1 fw-bold">Imagen del reporte:</p>
            <img src="/api/evidencias/ver/${reporte.imagen}" alt="Imagen del reporte" class="img-fluid rounded imagen-reporte">
        </div>`;
                }

                if (reporte.imagenTrabajo) {
                    html += `
        <div class="mb-2">
            <p class="mb-1 fw-bold">Trabajo realizado:</p>
            <img src="/api/trabajos/ver/${reporte.imagenTrabajo}" alt="Trabajo realizado" class="img-fluid rounded imagen-reporte">
        </div>`;
                }

                html += `<p><strong>Estado:</strong> ${estadoVisible}</p>`;

                if (reporte.estado === "recibido") {
                    html += `
                        <div class="d-flex gap-2 mt-3">
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
                                <button type="submit" class="btn btn-sm btn-success">Finalizar</button>
                            </div>
                        </form>`;
                }

                html += `</div>`;
                card.innerHTML = html;
                contenedor.appendChild(card);

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
            if (cerradoEl) cerradoEl.textContent = data["cerrado"] || 0;
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
    // Click en imagen del reporte para abrir modal
    document.querySelectorAll(".imagen-reporte").forEach(img => {
        img.addEventListener("click", () => {
            const src = img.getAttribute("src");
            const imagenModal = document.getElementById("imagenModalAmpliada");
            imagenModal.src = src;
            scale = 1;
            imagenModal.style.transform = `scale(${scale})`;
            imagenModal.style.transformOrigin = "center center";
            const modal = new bootstrap.Modal(document.getElementById("modalImagen"));
            modal.show();
        });
    });

    // Click en imagen del trabajo para abrir modal
    document.querySelectorAll(".imagen-trabajo").forEach(img => {
        img.addEventListener("click", () => {
            const src = img.getAttribute("src");
            const imagenModal = document.getElementById("imagenModalAmpliada");
            imagenModal.src = src;

// Reseteo completo del zoom
            scale = 1;
            targetScale = 1;
            animating = false;
            imagenModal.style.transform = `scale(1)`;
            imagenModal.style.transformOrigin = "center center";

            const modal = new bootstrap.Modal(document.getElementById("modalImagen"));
            modal.show();
        });
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

        // Paso 1: Si ya está muy cerca, aleja un poco
        const targetZoom = 18;
        const zoomActual = mapa.getZoom();

        if (zoomActual >= targetZoom - 1) {
            mapa.setZoom(15); // Aleja un poco para que se note el deslizamiento
        }

        // Paso 2: Desliza hacia el marcador
        mapa.panTo(marcador.getPosition());

        // Paso 3: Después de un pequeño retardo, hace el zoom suave
        setTimeout(() => {
            let zoom = mapa.getZoom();
            const intervalo = setInterval(() => {
                zoom++;
                mapa.setZoom(zoom);
                if (zoom >= targetZoom) {
                    clearInterval(intervalo);
                }
            }, 200);
        }, 500); // Espera que termine el deslizamiento antes de hacer zoom
    }
}


window.initMap = () => {
    inicializarMapa();
    cargarReportesPorEstado("recibido");
    contarReportes();
};
document.addEventListener("DOMContentLoaded", () => {
    const imagenModal = document.getElementById("imagenModalAmpliada");

    let currentWheelHandler = null;

    // Cada vez que se abre el modal, se reinicia el zoom y el listener
    function prepararImagenZoom() {
        scale = 1;
        targetScale = 1;
        animating = false;
        imagenModal.style.transform = `scale(1)`;
        imagenModal.style.transformOrigin = "center center";

        // Eliminar cualquier listener anterior
        if (currentWheelHandler) {
            imagenModal.removeEventListener("wheel", currentWheelHandler);
        }

        // Crear y guardar nuevo listener
        currentWheelHandler = (e) => {
            e.preventDefault();

            const rect = imagenModal.getBoundingClientRect();
            const offsetX = e.clientX - rect.left;
            const offsetY = e.clientY - rect.top;

            const xPercent = (offsetX / rect.width) * 100;
            const yPercent = (offsetY / rect.height) * 100;

            imagenModal.style.transformOrigin = `${xPercent}% ${yPercent}%`;

            const delta = e.deltaY;
            if (delta < 0) {
                targetScale += 0.2;
            } else {
                targetScale = Math.max(1, targetScale - 0.2);
            }

            if (!animating) {
                animating = true;
                requestAnimationFrame(animateZoom);
            }
        };

        imagenModal.addEventListener("wheel", currentWheelHandler);
    }

    // Cuando clickeás una imagen, se prepara el zoom
    document.body.addEventListener("click", (e) => {
        if (e.target.classList.contains("imagen-reporte") || e.target.classList.contains("imagen-trabajo")) {
            prepararImagenZoom();
        }
    });
});

function animateZoom() {
    const imagenModal = document.getElementById("imagenModalAmpliada");
    const zoomSpeed = 0.1;
    scale += (targetScale - scale) * zoomSpeed;

    imagenModal.style.transform = `scale(${scale})`;

    if (Math.abs(targetScale - scale) > 0.01) {
        requestAnimationFrame(animateZoom);
    } else {
        scale = targetScale;
        imagenModal.style.transform = `scale(${scale})`;

        if (scale === 1) {
            imagenModal.style.transformOrigin = "center center";
        }
        animating = false;
    }
}


