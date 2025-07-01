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
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.modal-body img').forEach(img => {
        let scale = 1;
        let translateX = 0;
        let translateY = 0;
        let isDragging = false;
        let startX = 0;
        let startY = 0;

        img.style.transformOrigin = "center center";

        img.addEventListener('wheel', (e) => {
            e.preventDefault();

            const container = img.closest('.modal-body');
            const containerWidth = container.clientWidth;
            const containerHeight = container.clientHeight;

            const imgWidth = img.clientWidth;
            const imgHeight = img.clientHeight;

            const prevScale = scale;
            const zoomSpeed = 0.1;

            if (e.deltaY < 0) {
                scale += zoomSpeed;
            } else {
                scale -= zoomSpeed;
            }

            scale = Math.min(Math.max(1, scale));

            const scaledWidth = imgWidth * scale;
            const scaledHeight = imgHeight * scale;

            // Limitar para que no se desplace fuera del modal
            const maxX = Math.max(0, (scaledWidth - containerWidth) / 2);
            const maxY = Math.max(0, (scaledHeight - containerHeight) / 2);

            translateX = Math.min(maxX, Math.max(-maxX, translateX));
            translateY = Math.min(maxY, Math.max(-maxY, translateY));

            img.style.transform = `translate(${translateX}px, ${translateY}px) scale(${scale})`;
        });

        img.addEventListener('mousedown', (e) => {
            if (scale <= 1) return;
            isDragging = true;
            startX = e.clientX - translateX;
            startY = e.clientY - translateY;
            img.style.cursor = 'grabbing';
            e.preventDefault();
        });

        document.addEventListener('mousemove', (e) => {
            if (!isDragging) return;

            const container = img.closest('.modal-body');
            const containerWidth = container.clientWidth;
            const containerHeight = container.clientHeight;

            const imgWidth = img.clientWidth;
            const imgHeight = img.clientHeight;

            translateX = e.clientX - startX;
            translateY = e.clientY - startY;

            const scaledWidth = imgWidth * scale;
            const scaledHeight = imgHeight * scale;

            const maxX = Math.max(0, (scaledWidth - containerWidth) / 2);
            const maxY = Math.max(0, (scaledHeight - containerHeight) / 2);

            translateX = Math.min(maxX, Math.max(-maxX, translateX));
            translateY = Math.min(maxY, Math.max(-maxY, translateY));

            img.style.transform = `translate(${translateX}px, ${translateY}px) scale(${scale})`;
        });

        document.addEventListener('mouseup', () => {
            if (isDragging) {
                isDragging = false;
                img.style.cursor = scale > 1 ? 'grab' : 'default';
            }
        });

        img.closest('.modal').addEventListener('hidden.bs.modal', () => {
            scale = 1;
            translateX = 0;
            translateY = 0;
            img.style.transform = 'scale(1)';
            img.style.cursor = 'default';
        });
    });
});


