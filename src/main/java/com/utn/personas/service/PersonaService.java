package com.utn.personas.service;

import com.utn.personas.dto.PersonaView;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de la <b>capa de servicio</b> para la gestion de personas.
 *
 * <p>Toda la API publica trabaja con el DTO {@link PersonaView}: las capas
 * superiores (la UI) nunca ven la entidad. La conversion entidad/DTO ocurre
 * dentro de la implementacion.</p>
 */
public interface PersonaService {

    /** Crea (alta) una persona y devuelve el DTO persistido. */
    PersonaView crear(PersonaView persona);

    /** Actualiza una persona existente y devuelve el DTO actualizado. */
    PersonaView actualizar(PersonaView persona);

    /** Lista todas las personas como DTOs. */
    List<PersonaView> listarTodas();

    /** Busca una persona por su DNI; vacio si no existe. */
    Optional<PersonaView> buscarPorDni(String dni);

    /** Elimina una persona por su DNI. */
    void eliminar(String dni);
}
