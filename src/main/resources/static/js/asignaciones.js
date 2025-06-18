function filtrarEstado(estado) {
    const filas = document.querySelectorAll("tbody tr");
    filas.forEach(fila => {
        fila.style.display = (fila.dataset.estado === estado) ? "" : "none";
    });

    // Activar visualmente el botón
    document.querySelectorAll(".estado-btn").forEach(btn => btn.classList.remove("activo"));
    document.getElementById("btn-" + estado).classList.add("activo");
}

// Mostrar por defecto "en-proceso"
document.addEventListener("DOMContentLoaded", function () {
    filtrarEstado('en-proceso');
});
