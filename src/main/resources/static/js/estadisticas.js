const coloresEstados = ["#ffc107", "#17a2b8", "#28a745", "#6c757d"];
const coloresTipos = ["#007bff", "#6610f2", "#e83e8c", "#fd7e14", "#20c997", "#6c757d"];

new Chart(document.getElementById('pieChartEstados'), {
    type: 'pie',
    data: {
        labels: ["Recibidos", "En Proceso", "Resueltos", "Cerrados"],
        datasets: [{
            data: [recibidos, enProceso, resueltos, cerrados],
            backgroundColor: coloresEstados
        }]
    },
    options: {
        responsive: false,
        maintainAspectRatio: false,
        plugins: {
            legend: { position: 'bottom' },
            datalabels: {
                color: '#fff',
                font: { weight: 'bold', size: 13 },
                formatter: (value, context) => {
                    const total = context.chart.data.datasets[0].data.reduce((a, b) => a + b, 0);
                    return total > 0 ? ((value / total) * 100).toFixed(1) + '%' : '0%';
                }
            }
        }
    },
    plugins: [ChartDataLabels]
});

new Chart(document.getElementById('barChartTipos'), {
    type: 'bar',
    data: {
        labels: tipos,
        datasets: [{
            label: 'Cantidad por Tipo',
            data: cantidadesTipos,
            backgroundColor: coloresTipos,
            borderRadius: 6
        }]
    },
    options: {
        responsive: false,
        maintainAspectRatio: false,
        plugins: {
            legend: { display: false },
            datalabels: {
                anchor: 'end',
                align: 'top',
                color: '#000',
                font: { weight: 'bold', size: 12 },
                formatter: Math.round
            },
            tooltip: {
                backgroundColor: '#fff',
                titleColor: '#000',
                bodyColor: '#000',
                borderColor: '#ddd',
                borderWidth: 1
            }
        },
        scales: {
            y: { beginAtZero: true, ticks: { precision: 0 } }
        }
    },
    plugins: [ChartDataLabels]
});

const selectorEvolucion = document.getElementById("selectorEvolucion");
const fechaInicio = document.getElementById("fechaInicio");
const fechaFin = document.getElementById("fechaFin");
let graficoEvolucion = null;

generarGraficoEvolucion(meses, reportesMes, "Reportes por Mes");

selectorEvolucion.addEventListener("change", () => {
    const opcion = selectorEvolucion.value;

    if (opcion === "rango") {
        fechaInicio.disabled = false;
        fechaFin.disabled = false;
    } else {
        fechaInicio.disabled = true;
        fechaFin.disabled = true;
        fechaInicio.value = "";
        fechaFin.value = "";

        if (opcion === "dia") {
            generarGraficoEvolucion(dias, reportesDia, "Reportes por Día");
        } else if (opcion === "mes") {
            generarGraficoEvolucion(meses, reportesMes, "Reportes por Mes");
        }
    }
});

[fechaInicio, fechaFin].forEach(input => {
    input.addEventListener("change", () => {
        if (fechaInicio.value && fechaFin.value) {
            buscarReportesPorRango(fechaInicio.value, fechaFin.value);
        }
    });
});

function generarGraficoEvolucion(labels, data, titulo) {
    const canvas = document.getElementById('graficoEvolucion');

    if (graficoEvolucion) graficoEvolucion.destroy();

    // 🔥 Ajusta tamaño antes de crear el gráfico
    canvas.width = canvas.parentElement.clientWidth;
    canvas.height = 300; // Puedes poner el alto que deseas, ej: 300, 400...

    graficoEvolucion = new Chart(canvas, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: titulo,
                data: data,
                borderColor: '#17a2b8',
                backgroundColor: 'rgba(23, 162, 184, 0.2)',
                tension: 0.4,
                fill: true,
                pointBackgroundColor: '#17a2b8',
                pointRadius: 5
            }]
        },
        options: {
            responsive: false, // Desactivas responsive porque tú controlas el tamaño
            maintainAspectRatio: false,
            plugins: {
                legend: { position: 'top' },
                tooltip: {
                    backgroundColor: '#fff',
                    titleColor: '#000',
                    bodyColor: '#000',
                    borderColor: '#ddd',
                    borderWidth: 1
                }
            },
            scales: {
                y: { beginAtZero: true, ticks: { precision: 0 } }
            }
        }
    });
}

function buscarReportesPorRango(fechaInicio, fechaFin) {
    fetch(`/api/estadisticas/rango?inicio=${fechaInicio}&fin=${fechaFin}&institucionId=${institucionId}`)
        .then(response => response.json())
        .then(data => {
            if (data.labels && data.valores) {
                generarGraficoEvolucion(data.labels, data.valores, `Reportes entre ${fechaInicio} y ${fechaFin}`);
            } else {
                alert("No se encontraron datos en ese rango.");
            }
        })
        .catch(err => {
            console.error(err);
            alert("Error al obtener los datos.");
        });
}
