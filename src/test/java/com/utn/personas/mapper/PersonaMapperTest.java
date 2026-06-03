package com.utn.personas.mapper;

import com.utn.personas.dto.PersonaView;
import com.utn.personas.entity.Persona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias del {@link PersonaMapper}.
 *
 * <p>Validan la conversion en ambos sentidos y el manejo de {@code null}.
 * Patron Arrange-Act-Assert.</p>
 */
class PersonaMapperTest {

    private PersonaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PersonaMapper();
    }

    @Test
    @DisplayName("toView: convierte entidad a DTO copiando todos los campos")
    void toView_deberiaCopiarTodosLosCampos() {
        // Arrange
        Persona entidad = new Persona("30111222", "Ana", 28);

        // Act
        PersonaView view = mapper.toView(entidad);

        // Assert
        assertThat(view).isNotNull();
        assertThat(view.getDni()).isEqualTo("30111222");
        assertThat(view.getNombre()).isEqualTo("Ana");
        assertThat(view.getEdad()).isEqualTo(28);
    }

    @Test
    @DisplayName("toEntity: convierte DTO a entidad copiando todos los campos")
    void toEntity_deberiaCopiarTodosLosCampos() {
        // Arrange
        PersonaView view = new PersonaView("27999888", "Beto", 45);

        // Act
        Persona entidad = mapper.toEntity(view);

        // Assert
        assertThat(entidad).isNotNull();
        assertThat(entidad.getDni()).isEqualTo("27999888");
        assertThat(entidad.getNombre()).isEqualTo("Beto");
        assertThat(entidad.getEdad()).isEqualTo(45);
    }

    @Test
    @DisplayName("toView: devuelve null cuando la entidad es null")
    void toView_conEntidadNull_deberiaDevolverNull() {
        // Act + Assert
        assertThat(mapper.toView(null)).isNull();
    }

    @Test
    @DisplayName("toEntity: devuelve null cuando el DTO es null")
    void toEntity_conViewNull_deberiaDevolverNull() {
        // Act + Assert
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("ida y vuelta: entidad -> DTO -> entidad preserva los datos")
    void idaYVuelta_deberiaPreservarLosDatos() {
        // Arrange
        Persona original = new Persona("12345678", "Carla", 33);

        // Act
        Persona resultado = mapper.toEntity(mapper.toView(original));

        // Assert
        assertThat(resultado.getDni()).isEqualTo(original.getDni());
        assertThat(resultado.getNombre()).isEqualTo(original.getNombre());
        assertThat(resultado.getEdad()).isEqualTo(original.getEdad());
    }
}
