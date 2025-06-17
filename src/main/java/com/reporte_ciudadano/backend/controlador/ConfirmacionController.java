package com.reporte_ciudadano.backend.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ConfirmacionController {

  @GetMapping("/confirmacion")
  public String confirmacionRegistro(@RequestParam("token") String token) {
    return "confirmacion"; // Esto renderiza el archivo confirmacion.html
  }

}
