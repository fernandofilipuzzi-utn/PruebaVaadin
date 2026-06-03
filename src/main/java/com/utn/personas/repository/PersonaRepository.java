package com.utn.personas.repository;

import com.utn.personas.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA de la entidad {@link Persona}.
 *
 * <p>Pertenece a la <b>capa de repositorio</b>. Hereda de
 * {@link JpaRepository} los metodos CRUD estandar
 * ({@code save}, {@code findById}, {@code findAll}, {@code deleteById}, etc.).
 * El tipo de la clave primaria es {@code String} (el DNI).</p>
 */
@Repository
public interface PersonaRepository extends JpaRepository<Persona, String> {
}
