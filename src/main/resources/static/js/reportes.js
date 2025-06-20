document.addEventListener("DOMContentLoaded", function () {
    const modalEliminar = document.getElementById('modalEliminar');
    let idAEliminar = null;

    if (modalEliminar) {
        modalEliminar.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            idAEliminar = button.getAttribute('data-id');
            document.getElementById('reporteIdEliminar').value = idAEliminar;
        });

        const formEliminar = document.getElementById('formEliminar');
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
                            .closest(".col-md-6").remove();

                        bootstrap.Modal.getInstance(modalEliminar).hide();
                    }
                } catch (error) {
                    alert("Error al eliminar el reporte.");
                }
            });
        }
    }
});
let mapaIndividual;
function initMapaIndividual() {
    // Inicial vacío, el mapa se crea al abrir el modal
}

const modalMapa = document.getElementById('modalMapa');
modalMapa.addEventListener('show.bs.modal', function (event) {
    const button = event.relatedTarget;
    const ubicacion = button.getAttribute('data-ubicacion');
    const icono = button.getAttribute('data-icono') || 'icono-default.png';

    const [lat, lng] = ubicacion.split(',').map(parseFloat);
    if (mapaIndividual) {
        mapaIndividual = null;
        document.getElementById('mapaIndividual').innerHTML = "";
    }

    setTimeout(() => {
        mapaIndividual = new google.maps.Map(document.getElementById("mapaIndividual"), {
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
    }, 300);
});
document.addEventListener("DOMContentLoaded", () => {
    const estadoSelect = document.getElementById("filtroEstado");
    const tipoSelect = document.getElementById("filtroTipo");
    const tecnicoSelect = document.getElementById("filtroTecnico");

    const tarjetas = document.querySelectorAll(".tarjeta-reporte");

    function aplicarFiltros() {
        const estado = estadoSelect.value.toLowerCase();
        const tipo = tipoSelect.value.toLowerCase();
        const tecnico = tecnicoSelect.value.toLowerCase();

        tarjetas.forEach(card => {
            const estadoCard = card.querySelector(".estado-reporte")?.innerText.toLowerCase() || "";
            const tipoCard = card.querySelector(".card-title").innerText.toLowerCase();
            const tecnicoSpan = card.querySelector("span.badge.bg-success");
            const tecnicoCard = tecnicoSpan ? tecnicoSpan.innerText.toLowerCase() : "";

            const coincideEstado = !estado || estadoCard.includes(estado);
            const coincideTipo = !tipo || tipoCard.includes(tipo);
            const coincideTecnico = !tecnico || tecnicoCard.includes(tecnico);

            card.closest(".col-md-6").style.display = (coincideEstado && coincideTipo && coincideTecnico) ? "block" : "none";
        });
    }

    estadoSelect.addEventListener("change", aplicarFiltros);
    tipoSelect.addEventListener("change", aplicarFiltros);
    tecnicoSelect.addEventListener("change", aplicarFiltros);

    // Exponer global si quieres usarlo desde botón
    window.aplicarFiltros = aplicarFiltros;
    window.limpiarFiltros = () => {
        estadoSelect.value = "";
        tipoSelect.value = "";
        tecnicoSelect.value = "";
        aplicarFiltros();
    };
});
