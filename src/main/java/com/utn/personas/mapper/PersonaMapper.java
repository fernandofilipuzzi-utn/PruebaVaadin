package com.utn.personas.mapper;

import com.utn.personas.dto.PersonaView;
import com.utn.personas.entity.Persona;
import org.springframework.stereotype.Component;

/**
 * Conversor entre la entidad {@link Persona} y el DTO {@link PersonaView}.
 *
 * <p>Pertenece a la <b>capa de mapeo</b>. Centraliza la traduccion en ambos
 * sentidos para que el servicio reciba/entregue DTOs a la UI y entidades al
 * repositorio. Maneja explicitamente los valores {@code null}.</p>
 */
@Component
public class PersonaMapper {

    /**
     * Convierte una entidad a su DTO de vista.
     *
     * @param persona entidad (puede ser {@code null})
     * @return el DTO equivalente, o {@code null} si la entrada es {@code null}
     */
    public PersonaView toView(Persona persona) {
        if (persona == null) {
            return null;
        }
        return new PersonaView(
                persona.getDni(),
                persona.getNombre(),
                persona.getEdad());
    }

    /**
     * Convierte un DTO de vista a su entidad persistible.
     *
     * @param view DTO (puede ser {@code null})
     * @return la entidad equivalente, o {@code null} si la entrada es {@code null}
     */
    public Persona toEntity(PersonaView view) {
        if (view == null) {
            return null;
        }
        return new Persona(
                view.getDni(),
                view.getNombre(),
                view.getEdad());
    }
}
