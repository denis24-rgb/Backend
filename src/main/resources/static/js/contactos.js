let mapa;
let marcadorNuevo = null;
const iconoExistente = "http://maps.google.com/mapfiles/ms/icons/blue-dot.png";
const iconoNuevo = "http://maps.google.com/mapfiles/ms/icons/red-dot.png";

function initMap() {
    const yapacani = { lat: -17.400339, lng: -63.888043 };

    mapa = new google.maps.Map(document.getElementById("mapa"), {
        center: yapacani,
        zoom: 14,
        mapTypeId: "hybrid"
    });

    cargarContactosEnMapa();

    mapa.addListener("click", (e) => {
        colocarMarcadorNuevo(e.latLng);
    });
}

function cargarContactosEnMapa() {
    const contactos = document.querySelectorAll(".card[data-lat][data-lng]");

    const iconoEmergencia = "http://maps.google.com/mapfiles/ms/icons/green-dot.png";
    const iconoPolicia = "http://maps.google.com/mapfiles/ms/icons/blue-dot.png";

    contactos.forEach(c => {
        const lat = parseFloat(c.getAttribute("data-lat"));
        const lng = parseFloat(c.getAttribute("data-lng"));
        const nombre = c.getAttribute("data-nombre");
        const tipo = c.getAttribute("data-tipo");

        const icono = tipo.includes("Policía") ? iconoPolicia : iconoEmergencia;

        const marcador = new google.maps.Marker({
            position: { lat, lng },
            map: mapa,
            icon: icono,
            title: nombre
        });

        const infoWindow = new google.maps.InfoWindow({
            content: `<strong>${nombre}</strong>`
        });

        marcador.addListener("click", () => {
            infoWindow.open(mapa, marcador);
        });
    });
}

function colocarMarcadorNuevo(ubicacion) {
    let lat, lng;

    if (typeof ubicacion.lat === "function") {
        lat = ubicacion.lat();
        lng = ubicacion.lng();
    } else {
        lat = ubicacion.lat;
        lng = ubicacion.lng;
    }

    const posicion = { lat, lng };

    if (marcadorNuevo) {
        marcadorNuevo.setPosition(posicion);
    } else {
        marcadorNuevo = new google.maps.Marker({
            position: posicion,
            map: mapa,
            icon: iconoNuevo
        });
    }

    document.getElementById("lat").value = lat;
    document.getElementById("lng").value = lng;
}

function verUbicacion(boton) {
    const targetLat = parseFloat(boton.getAttribute("data-lat"));
    const targetLng = parseFloat(boton.getAttribute("data-lng"));

    const targetZoom = 19;
    const retroZoom = 14;
    const pasoZoom = 0.2;
    const delay = 400;

    let zoomActual = mapa.getZoom();

    const intervaloAlejar = setInterval(() => {
        zoomActual -= pasoZoom;
        mapa.setZoom(zoomActual);

        if (zoomActual <= retroZoom) {
            clearInterval(intervaloAlejar);
            mapa.panTo({ lat: targetLat, lng: targetLng });

            setTimeout(() => {
                let zoom = mapa.getZoom();
                const intervaloAcercar = setInterval(() => {
                    zoom += pasoZoom;
                    mapa.setZoom(zoom);
                    if (zoom >= targetZoom) {
                        clearInterval(intervaloAcercar);
                        mapa.setZoom(targetZoom);
                    }
                }, 50);
            }, delay);
        }
    }, 50);
}

document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("formContacto");

    form.addEventListener("submit", (e) => {
        const lat = document.getElementById("lat").value;
        const lng = document.getElementById("lng").value;

        if (!lat || !lng) {
            e.preventDefault();
            mostrarToastError();
        }
    });

    document.getElementById("btnCancelar").addEventListener("click", () => {
        resetearFormulario();
    });
});

function mostrarToastError() {
    const toast = document.getElementById("toastError");
    toast.style.display = "block";
    setTimeout(() => {
        toast.style.display = "none";
    }, 4000);
}

function editarContacto(card) {
    const id = card.getAttribute("data-id");
    const nombre = card.getAttribute("data-nombre");
    const tipo = card.getAttribute("data-tipo");
    const telefono = card.getAttribute("data-telefono");
    const direccion = card.getAttribute("data-direccion");
    const lat = parseFloat(card.getAttribute("data-lat"));
    const lng = parseFloat(card.getAttribute("data-lng"));

    document.getElementById("idContacto").value = id;
    document.getElementById("nombreContacto").value = nombre;
    document.getElementById("tipoContacto").value = tipo;
    document.getElementById("telefono").value = telefono;
    document.getElementById("direccion").value = direccion;

    colocarMarcadorNuevo({ lat, lng });

    document.getElementById("btnGuardar").innerText = "Actualizar Contacto";
    document.getElementById("btnCancelar").style.display = "inline-block";
    document.getElementById("tituloFormulario").innerText = "Editar Contacto";
}

function resetearFormulario() {
    document.getElementById("formContacto").reset();
    document.getElementById("idContacto").value = "";
    document.getElementById("lat").value = "";
    document.getElementById("lng").value = "";

    if (marcadorNuevo) {
        marcadorNuevo.setMap(null);
        marcadorNuevo = null;
    }

    document.getElementById("btnGuardar").innerText = "Guardar Contacto";
    document.getElementById("btnCancelar").style.display = "none";
    document.getElementById("tituloFormulario").innerText = "Agregar Contacto";
}
