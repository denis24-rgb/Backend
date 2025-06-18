package com.reporte_ciudadano.backend.repositorio;

import com.reporte_ciudadano.backend.modelo.Reporte;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReporteRepositorio extends JpaRepository<Reporte, Long> {

    //  Correcto: obtiene reportes por usuario
    List<Reporte> findByUsuarioId(Long usuarioId);

    //Correcto: obtiene reportes por múltiples estados usando JPQL
    @Query("SELECT r FROM Reporte r WHERE r.estado IN :estados")
    List<Reporte> findByEstadoIn(@Param("estados") List<String> estados);

    // ✅ Correcto: obtiene reportes por institución y estados
    @Query("SELECT r FROM Reporte r WHERE r.institucion.id = :institucionId AND r.estado IN :estados")
    List<Reporte> findByInstitucionIdAndEstadoIn(@Param("institucionId") Long institucionId,
                                                 @Param("estados") List<String> estados);

    // Correcto: ahora que usas campo `ubicacion` único, esta búsqueda es válida
    List<Reporte> findByUbicacionIsNotNull();

    // Correcto: cuenta por estado ignorando mayúsculas
    long countByEstadoIgnoreCase(String estado);

    // Correcto: cuenta solo reportes con ubicación y una institución específica
    List<Reporte> findByInstitucionIdAndUbicacionIsNotNull(Long institucionId);

    // Correcto: contar por institución y estado exacto
    int countByInstitucionIdAndEstado(Long institucionId, String estado);

    //  Correcto: contar por institución y estados distintos al dado
    int countByInstitucionIdAndEstadoNot(Long institucionId, String estado);
}
