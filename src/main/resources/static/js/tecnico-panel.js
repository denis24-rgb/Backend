document.addEventListener("DOMContentLoaded", () => {
    cargarReportesPorEstado("RECIBIDO");
    contarReportes();
});

// Filtro por estado
function filtrarPorEstado(estado) {
    cargarReportesPorEstado(estado);
}

// Cargar reportes por estado
function cargarReportesPorEstado(estado) {
    fetch(`/tecnico/reportes?estado=${estado}`)
        .then(res => res.json())
        .then(data => {
            const contenedor = document.getElementById("contenedor-reportes");
            contenedor.innerHTML = "";

            if (data.length === 0) {
                contenedor.innerHTML = "<p>No hay reportes en este estado.</p>";
                return;
            }

            data.forEach(reporte => {
                const card = document.createElement("div");
                card.className = "card card-reporte mb-3";

                let estadoVisible = reporte.estado.nombre === "RESUELTO" ? "FINALIZADO" : reporte.estado.nombre;

                let html = `
        <div class="card-body">
            <p><strong>Tipo de Reporte:</strong> ${reporte.tipoReporte?.nombre || "No especificado"}</p>
            <p><strong>Descripción:</strong> ${reporte.descripcion}</p>
            <p><strong>Fecha y hora:</strong> ${formatearFecha(reporte.fechaReporte)}</p>
            <p><strong>Estado:</strong> ${estadoVisible}</p>
            <p><strong>Operador asignado:</strong> ${reporte.institucion?.nombre || "No asignado"}</p>
    `;

                if (reporte.estado.nombre === "RECIBIDO") {
                    html += `
            <div class="d-flex justify-content-start gap-2 mt-3">
                <button class="btn btn-sm btn-outline-primary btn-ver-mapa" data-id="${reporte.id}">Ver mapa</button>
                <button class="btn btn-sm btn-warning btn-tomar" data-id="${reporte.id}">Tomar reporte</button>
            </div>`;
                }

                if (reporte.estado.nombre === "EN PROCESO") {
                    html += `
        <form class="form-finalizar mt-3" data-id="${reporte.id}">
            <input type="hidden" name="nuevoEstado" value="RESUELTO" />
            <div class="mb-2">
                <label class="form-label">Comentario del técnico:</label>
                <textarea name="comentario" class="form-control form-control-sm" rows="2"
                          placeholder="Breve descripción de la intervención..." required></textarea>
            </div>
            <div class="d-flex justify-content-start gap-2">
                <button type="button" class="btn btn-sm btn-outline-primary btn-ver-mapa" data-id="${reporte.id}">Ver mapa</button>
                <button type="submit" class="btn btn-sm btn-success">Finalizar</button>
            </div>
        </form>`;
                }


                if (reporte.estado.nombre === "RESUELTO") {
                    html += `
            <p><strong>Comentario del técnico:</strong> ${reporte.comentarioTecnico || "Sin comentario registrado."}</p>
            <p><strong>Fecha de resolución:</strong> ${formatearFecha(reporte.fecha)}</p>
            <div class="mt-2">
                <button class="btn btn-sm btn-outline-secondary btn-ver-mapa" data-id="${reporte.id}">Ver mapa</button>
            </div>`;
                }

                html += `</div>`;
                card.innerHTML = html;
                contenedor.appendChild(card);
            });



            activarEventos();
        });
}

// Contador de reportes
function contarReportes() {
    fetch(`/tecnico/reportes/contar`)
        .then(res => res.json())
        .then(data => {
            document.getElementById("count-pendientes").textContent = data["RECIBIDO"] || 0;
            document.getElementById("count-proceso").textContent = data["EN PROCESO"] || 0;
            document.getElementById("count-finalizados").textContent = data["FINALIZADO"] || 0;
        });
}

// Formato de fecha
function formatearFecha(fechaISO) {
    const fecha = new Date(fechaISO);
    return fecha.toLocaleString('es-BO', {
        day: '2-digit', month: '2-digit', year: 'numeric',
        hour: '2-digit', minute: '2-digit'
    });
}

// Activar botones y formularios
function activarEventos() {
    // Tomar reporte
    document.querySelectorAll(".btn-tomar").forEach(btn => {
        btn.addEventListener("click", () => {
            const id = btn.dataset.id;
            fetch("/tecnico/actualizar-reporte", {
                method: "POST",
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: `reporteId=${id}&nuevoEstado=EN PROCESO`
            }).then(() => {
                cargarReportesPorEstado("RECIBIDO");
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

            fetch("/tecnico/actualizar-reporte", {
                method: "POST",
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: `reporteId=${id}&nuevoEstado=RESUELTO&comentario=${encodeURIComponent(comentario)}`
            }).then(() => {
                cargarReportesPorEstado("EN PROCESO");
                contarReportes();
            });
        });
    });

    // Escuchar clics en las tarjetas
    document.addEventListener("click", function (event) {
        const tarjeta = event.target.closest(".tarjeta-desplegable");

        // Si haces clic en una tarjeta
        if (tarjeta) {
            const id = tarjeta.querySelector(".titulo-card").dataset.id;
            const contenido = document.querySelector(`.contenido-card[data-id='${id}']`);
            const estabaVisible = !contenido.classList.contains("d-none");

            // Cerrar todas
            document.querySelectorAll(".contenido-card").forEach(c => c.classList.add("d-none"));

            // Si no estaba visible, mostrarla
            if (!estabaVisible) {
                contenido.classList.remove("d-none");
            }

            return; // No cierres inmediatamente
        }

        // Si haces clic fuera de cualquier tarjeta, cerrar todo
        document.querySelectorAll(".contenido-card").forEach(c => c.classList.add("d-none"));
    });


    // Ver en mapa
    document.querySelectorAll(".btn-ver-mapa").forEach(btn => {
        btn.addEventListener("click", () => {
            const id = btn.dataset.id;
            alert("Ver en mapa: " + id);
        });
    });
}
