// Función para manejar selección de técnico
function seleccionarTecnico(elemento) {
    const tecnicoId = elemento.getAttribute('data-tecnico-id');
    window.location.replace('/asignaciones/reportes/' + tecnicoId);
}

// Función para filtrar reportes por estado
function filtrarEstado(estado) {
    const filas = document.querySelectorAll('tr[data-estado]');

    filas.forEach(fila => {
        const estadoFila = fila.getAttribute('data-estado');
        if (estadoFila === estado || estado === 'todos') {
            fila.style.display = '';
        } else {
            fila.style.display = 'none';
        }
    });
}
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".modal.fade").forEach(modal => {
        modal.addEventListener("shown.bs.modal", () => {
            const mapaDiv = modal.querySelector(".mapa-contenedor");
            if (!mapaDiv) return;

            const id = mapaDiv.id.split("_")[1];
            const boton = document.querySelector(`button[data-bs-target="#mapaModal${id}"]`);
            if (!boton) return;

            const ubicacion = boton.getAttribute("data-ubicacion");
            const icono = boton.getAttribute("data-icono");
            const descripcion = boton.getAttribute("data-descripcion");

            if (!ubicacion || !icono) return;

            const [lat, lng] = ubicacion.split(",").map(parseFloat);

            const mapa = new google.maps.Map(mapaDiv, {
                center: { lat, lng },
                zoom: 16,
                mapTypeId: "roadmap"
            });

            const marcador = new google.maps.Marker({
                position: { lat, lng },
                map: mapa,
                icon: {
                    url: "/imagenes/" + icono,
                    scaledSize: new google.maps.Size(40, 40)
                },
                animation: google.maps.Animation.DROP
            });
            const tipoReporte = boton.getAttribute("data-tipo");

            // InfoWindow solo con descripción
            const infoWindow = new google.maps.InfoWindow({
                content: `
        <div style="max-width: 250px; background: white; color: black; padding: 8px; border-radius: 4px;">
            <strong style="font-size: 14px;">${tipoReporte}</strong>
            <hr style="margin: 5px 0;">
            <strong>Descripción:</strong>
            <p style="margin: 0;">${descripcion}</p>
        </div>
    `
            });


            marcador.addListener("click", () => {
                let zoomActual = mapa.getZoom();
                if (zoomActual < 21) {
                    mapa.setZoom(zoomActual + 3);
                    mapa.panTo(marcador.getPosition());
                }
                infoWindow.open(mapa, marcador);
            });

            setTimeout(() => {
                google.maps.event.trigger(mapa, "resize");
                mapa.setCenter({ lat, lng });
            }, 500);
        });
    });
});


