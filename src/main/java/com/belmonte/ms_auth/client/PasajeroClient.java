package com.belmonte.ms_auth.client;

import com.belmonte.ms_auth.client.dto.PasajeroClientRequest;
import com.belmonte.ms_auth.client.dto.PasajeroClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//Con Eureka activo, "clients.pasajeros.url" se deja vacio y se resuelve por el nombre ms-pasajeros
@FeignClient(name = "ms-pasajeros", url = "${clients.pasajeros.url:}")
public interface PasajeroClient {

    //crea la ficha del pasajero (409 si el email ya existe, 400 si los datos no son validos)
    @PostMapping("/api/v1/pasajeros")
    PasajeroClientResponse crear(@RequestBody PasajeroClientRequest request);
}
