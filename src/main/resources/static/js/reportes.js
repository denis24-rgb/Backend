document.addEventListener("DOMContentLoaded", function () {
// -------------------- BLOQUEAR PARPADEO DE IMAGEN DE EVIDENCIA --------------------
    document.querySelectorAll('.btn-evidencia').forEach(boton => {
        boton.addEventListener('click', () => {
            setTimeout(() => {
                boton.blur();
            }, 100);
        });
    });

    // -------------------- ELIMINACIÓN --------------------
    const modalEliminar = document.getElementById('modalEliminar');
    let idAEliminar = null;

    if (modalEliminar) {
        modalEliminar.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            idAEliminar = button.getAttribute('data-id');
            document.getElementById('reporteIdEliminar').value = idAEliminar;
        });

        const formEliminar = modalEliminar.querySelector("form");
        if (formEliminar) {
            formEliminar.addEventListener('submit', async function (e) {
                e.preventDefault();

                const formData = new FormData();
                formData.append("reporteId", idAEliminar);

                try {
                    const response = await fetch("/reportes/eliminar", {
                        method: "POST",
                        body: formData
                    });

                    if (response.ok || response.redirected) {
                        document.querySelector(`button[data-id="${idAEliminar}"]`)
                            .closest(".col-md-6, .col-lg-4").remove();

                        bootstrap.Modal.getInstance(modalEliminar).hide();
                    } else {
                        alert("Error al eliminar el reporte.");
                    }
                } catch (error) {
                    alert("Error al eliminar el reporte.");
                }
            });
        }
    }

    // -------------------- FILTROS --------------------
    const estadoSelect = document.getElementById("filtroEstado");
    const tipoSelect = document.getElementById("filtroTipo");
    const tecnicoSelect = document.getElementById("filtroTecnico");
    const asignadoSelect = document.getElementById("filtroAsignados");

    const tarjetas = document.querySelectorAll(".tarjeta-reporte");

    function aplicarFiltros() {
        const estado = estadoSelect.value.toLowerCase();
        const tipo = tipoSelect.value.toLowerCase();
        const tecnico = tecnicoSelect.value.toLowerCase();
        const soloAsignados = asignadoSelect && asignadoSelect.value === "asignados";

        tarjetas.forEach(card => {
            const estadoCard = card.querySelector(".estado-reporte")?.innerText.toLowerCase() || "";
            const tipoCard = card.querySelector(".card-title").innerText.toLowerCase();
            const tecnicoSpan = card.querySelector("span.badge.bg-success");
            const tecnicoCard = tecnicoSpan ? tecnicoSpan.innerText.toLowerCase() : "";

            const coincideEstado = !estado || estadoCard.includes(estado);
            const coincideTipo = !tipo || tipoCard.includes(tipo);
            const coincideTecnico = !tecnico || tecnicoCard.includes(tecnico);
            const coincideAsignado = !soloAsignados || (tecnicoSpan && estadoCard === "recibido");

            card.closest(".col-md-6, .col-lg-4").style.display =
                (coincideEstado && coincideTipo && coincideTecnico && coincideAsignado) ? "block" : "none";
        });
    }

    estadoSelect.addEventListener("change", aplicarFiltros);
    tipoSelect.addEventListener("change", aplicarFiltros);
    tecnicoSelect.addEventListener("change", aplicarFiltros);
    if (asignadoSelect) asignadoSelect.addEventListener("change", aplicarFiltros);

    window.aplicarFiltros = aplicarFiltros;
    window.limpiarFiltros = () => {
        estadoSelect.value = "";
        tipoSelect.value = "";
        tecnicoSelect.value = "";
        if (asignadoSelect) asignadoSelect.value = "";
        aplicarFiltros();
    };

    // -------------------- TOAST MENSAJES --------------------
    const toastExito = document.getElementById('toastExito');
    const toastError = document.getElementById('toastError');

    if (toastExito && toastExito.querySelector('span').innerText.trim() !== '') {
        const toast = new bootstrap.Toast(toastExito, { delay: 2000 });
        toast.show();
    }

    if (toastError && toastError.querySelector('span').innerText.trim() !== '') {
        const toast = new bootstrap.Toast(toastError, { delay: 3000 });
        toast.show();
    }
});

// -------------------- MAPA INDIVIDUAL --------------------
let mapaIndividual;
const modalMapa = document.getElementById('modalMapa');

modalMapa.addEventListener('shown.bs.modal', function (event) {
    const button = event.relatedTarget;
    const ubicacion = button.getAttribute('data-ubicacion');
    const icono = button.getAttribute('data-icono') || 'icono-default.png';

    const [lat, lng] = ubicacion.split(',').map(parseFloat);
    const contenedor = document.getElementById('mapaIndividual');

    contenedor.innerHTML = "";
    contenedor.style.height = "400px";
    contenedor.style.width = "100%";

    // Forzar un pequeño reflow visual antes de crear el mapa
    contenedor.offsetHeight; // Esto provoca un reflow

    mapaIndividual = new google.maps.Map(contenedor, {
        center: { lat, lng },
        zoom: 16,
        mapTypeId: "roadmap",
        gestureHandling: "greedy"
    });

    const marcador = new google.maps.Marker({
        position: { lat, lng },
        map: mapaIndividual,
        icon: {
            url: "/imagenes/" + icono,
            scaledSize: new google.maps.Size(40, 40)
        },
        animation: google.maps.Animation.DROP
    });

    marcador.addListener("click", () => {
        let zoomActual = mapaIndividual.getZoom();
        if (zoomActual < 21) {
            mapaIndividual.setZoom(zoomActual + 3);
            mapaIndividual.panTo(marcador.getPosition());
        }
    });

    // Reforzamos el resize después de que el modal ya está visible y el reflow hecho
    setTimeout(() => {
        google.maps.event.trigger(mapaIndividual, "resize");
        mapaIndividual.setCenter({ lat, lng });
    }, 500);
});


