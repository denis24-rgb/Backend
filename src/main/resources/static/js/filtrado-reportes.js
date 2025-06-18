// NUEVO CÓDIGO LISETH - filtrado-reportes.js

document.addEventListener("DOMContentLoaded", function () {
    const botonesEstado = document.querySelectorAll(".estado-btn");
    const contenedor = document.getElementById("contenedor-reportes");

    botonesEstado.forEach(btn => {
        btn.addEventListener("click", () => {
            const estado = btn.getAttribute("data-estado");

            fetch(`/tecnico/filtrar?estado=${encodeURIComponent(estado)}`)
                .then(res => res.json())
                .then(reportes => {
                    contenedor.innerHTML = "";

                    if (reportes.length === 0) {
                        contenedor.innerHTML = `<div class='alert alert-info'>No hay reportes para este estado.</div>`;
                        return;
                    }

                    reportes.forEach(reporte => {
                        const card = document.createElement("div");
                        card.className = "card card-reporte";
                        card.innerHTML = `
                            <div class="card-body">
                                <h5 class="titulo-card">${reporte.descripcion}</h5>
                                <p>${reporte.ubicacion}</p>
                                <small class="text-muted">Estado: ${reporte.estado.nombre}</small>

                                <form action="/tecnico/actualizar-reporte" method="post" class="mt-3">
                                    <input type="hidden" name="reporteId" value="${reporte.id}" />
                                    <div class="mb-2">
                                        <label class="form-label">Nuevo estado:</label>
                                        <select name="nuevoEstado" class="form-select form-select-sm" required>
                                            <option value="">Seleccionar</option>
                                            <option value="EN_PROCESO">En proceso</option>
                                            <option value="FINALIZADO">Finalizado</option>
                                        </select>
                                    </div>
                                    <div class="mb-2">
                                        <label class="form-label">Comentario del técnico:</label>
                                        <textarea name="comentario" class="form-control form-control-sm" rows="2" required></textarea>
                                    </div>
                                    <div class="d-flex justify-content-between">
                                        <a href="#" class="btn btn-sm btn-info">Ver en mapa</a>
                                        <button type="submit" class="btn btn-sm btn-success">Guardar cambios</button>
                                    </div>
                                </form>
                            </div>
                        `;
                        contenedor.appendChild(card);
                    });
                })
                .catch(err => {
                    console.error("Error al cargar reportes:", err);
                    contenedor.innerHTML = `<div class='alert alert-danger'>Error al cargar reportes</div>`;
                });
        });
    });
});

// FIN NUEVO CÓDIGO LISETH
