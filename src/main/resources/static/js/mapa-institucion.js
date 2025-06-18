function initMap() {
    const centroYapacani = { lat: -17.4006, lng: -63.8349 };

    const mapa = new google.maps.Map(document.getElementById("map"), {
        center: centroYapacani,
        zoom: 13,
        mapId: "MAPA_INSTITUCION"
    });

    const marcadores = reportes.map(reporte => {
        const posicion = { lat: reporte.latitud, lng: reporte.longitud };

        // Ícono personalizado según el tipo (si existe, si no, ícono por defecto)
        const icono = reporte.tipoReporte?.icono
            ? `/imagenes/${reporte.tipoReporte.icono}`
            : "/imagenes/icono-default.png";

        const marcador = new google.maps.Marker({
            position: posicion,
            map: mapa,
            title: reporte.tipoReporte?.nombre || "Reporte",
            icon: {
                url: icono,
                scaledSize: new google.maps.Size(40, 40)
            }
        });

        // Evento al hacer clic: abre modal con detalles
        marcador.addListener("click", () => {
            const contenido = `
                <div>
                    <h6 class="text-primary">${reporte.tipoReporte?.nombre || "Reporte"}</h6>
                    <p><strong>Descripción:</strong> ${reporte.descripcion}</p>
                    <p><strong>Ubicación:</strong> ${reporte.ubicacion || "No especificada"}</p>
                    <p><strong>Fecha:</strong> ${reporte.fechaReporte || "Sin fecha"}</p>
                    <p><strong>Estado:</strong> ${reporte.estado?.nombre || "N/A"}</p>
                </div>
            `;
            document.getElementById("contenidoModal").innerHTML = contenido;
            const modal = new bootstrap.Modal(document.getElementById("modalDetalleReporte"));
            modal.show();
        });

        return marcador;
    });

    // Agrupar marcadores si hay muchos
    new markerClusterer.MarkerClusterer({ map: mapa, markers: marcadores });
}
