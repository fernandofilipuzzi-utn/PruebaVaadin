package com.utn.personas.service;

import com.utn.personas.dto.PersonaView;
import com.utn.personas.entity.Persona;
import com.utn.personas.mapper.PersonaMapper;
import com.utn.personas.repository.PersonaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias PURAS de {@link PersonaServiceImpl}.
 *
 * <p>No levantan el contexto de Spring ni tocan la base de datos: el
 * {@link PersonaRepository} se mockea con Mockito, por lo que los tests no
 * dependen de SQL Server ni de la autenticacion de Windows.</p>
 *
 * <p>El {@link PersonaMapper} se usa como {@code @Spy} (objeto real) para
 * verificar de paso que las conversiones entidad/DTO se apliquen de verdad.
 * Patron Arrange-Act-Assert.</p>
 */
@ExtendWith(MockitoExtension.class)
class PersonaServiceImplTest {

    @Mock
    private PersonaRepository repository;

    @Spy
    private PersonaMapper mapper;

    @InjectMocks
    private PersonaServiceImpl service;

    // ----------------------------- CREAR -----------------------------

    @Test
    @DisplayName("crear: guarda la entidad y devuelve el DTO resultante")
    void crearPersona_deberiaGuardarYDevolverDTO() {
        // Arrange
        PersonaView entrada = new PersonaView("30111222", "Ana", 28);
        Persona persistida = new Persona("30111222", "Ana", 28);
        when(repository.save(any(Persona.class))).thenReturn(persistida);

        // Act
        PersonaView resultado = service.crear(entrada);

        // Assert: el DTO devuelto refleja lo persistido
        assertThat(resultado.getDni()).isEqualTo("30111222");
        assertThat(resultado.getNombre()).isEqualTo("Ana");
        assertThat(resultado.getEdad()).isEqualTo(28);

        // Verifica que se persistio una entidad con los datos del DTO de entrada
        ArgumentCaptor<Persona> captor = ArgumentCaptor.forClass(Persona.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getDni()).isEqualTo("30111222");
        assertThat(captor.getValue().getNombre()).isEqualTo("Ana");
        assertThat(captor.getValue().getEdad()).isEqualTo(28);
    }

    // --------------------------- ACTUALIZAR ---------------------------

    @Test
    @DisplayName("actualizar: persiste los cambios y devuelve el DTO actualizado")
    void actualizarPersona_deberiaGuardarYDevolverDTO() {
        // Arrange
        PersonaView cambios = new PersonaView("30111222", "Ana Maria", 29);
        Persona actualizada = new Persona("30111222", "Ana Maria", 29);
        when(repository.save(any(Persona.class))).thenReturn(actualizada);

        // Act
        PersonaView resultado = service.actualizar(cambios);

        // Assert
        assertThat(resultado.getNombre()).isEqualTo("Ana Maria");
        assertThat(resultado.getEdad()).isEqualTo(29);
        verify(repository).save(any(Persona.class));
    }

    // -------------------------- LISTAR TODAS --------------------------

    @Test
    @DisplayName("listarTodas: convierte todas las entidades a DTOs")
    void listarTodas_deberiaDevolverListaDeDTOs() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of(
                new Persona("1", "Ana", 28),
                new Persona("2", "Beto", 45)));

        // Act
        List<PersonaView> resultado = service.listarTodas();

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(PersonaView::getNombre)
                .containsExactly("Ana", "Beto");
        verify(repository).findAll();
    }

    @Test
    @DisplayName("listarTodas: devuelve lista vacia cuando no hay personas")
    void listarTodas_sinPersonas_deberiaDevolverListaVacia() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of());

        // Act
        List<PersonaView> resultado = service.listarTodas();

        // Assert
        assertThat(resultado).isEmpty();
        verify(repository).findAll();
    }

    // ------------------------- BUSCAR POR DNI -------------------------

    @Test
    @DisplayName("buscarPorDni: devuelve el DTO cuando el DNI existe")
    void buscarPorDni_existente_deberiaDevolverDTO() {
        // Arrange
        when(repository.findById("30111222"))
                .thenReturn(Optional.of(new Persona("30111222", "Ana", 28)));

        // Act
        Optional<PersonaView> resultado = service.buscarPorDni("30111222");

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Ana");
        verify(repository).findById("30111222");
    }

    @Test
    @DisplayName("buscarPorDni: devuelve Optional vacio cuando el DNI no existe (borde)")
    void buscarPorDni_inexistente_deberiaDevolverVacio() {
        // Arrange
        when(repository.findById("00000000")).thenReturn(Optional.empty());

        // Act
        Optional<PersonaView> resultado = service.buscarPorDni("00000000");

        // Assert
        assertThat(resultado).isEmpty();
        verify(repository).findById("00000000");
        // El mapper no debe invocarse si no hay entidad que convertir.
        verify(mapper, never()).toView(any());
    }

    // ---------------------------- ELIMINAR ----------------------------

    @Test
    @DisplayName("eliminar: delega el borrado por DNI en el repositorio")
    void eliminarPersona_deberiaInvocarDeleteById() {
        // Act
        service.eliminar("30111222");

        // Assert
        verify(repository, times(1)).deleteById("30111222");
        // Eliminar no requiere conversiones de mapper.
        verifyNoInteractions(mapper);
    }
}
