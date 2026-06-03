package com.utn.personas.service;

import com.utn.personas.dto.PersonaView;
import com.utn.personas.entity.Persona;
import com.utn.personas.mapper.PersonaMapper;
import com.utn.personas.repository.PersonaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementacion de la <b>capa de servicio</b>.
 *
 * <p>Contiene la logica de negocio (CRUD): recibe/entrega {@link PersonaView}
 * hacia la UI y delega la persistencia en {@link PersonaRepository} usando la
 * entidad {@link Persona}. La traduccion la realiza {@link PersonaMapper}.</p>
 *
 * <p>Las dependencias se inyectan por constructor (estilo recomendado: facilita
 * el testeo unitario con Mockito).</p>
 */
@Service
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository repository;
    private final PersonaMapper mapper;

    public PersonaServiceImpl(PersonaRepository repository, PersonaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public PersonaView crear(PersonaView persona) {
        // DTO -> entidad -> persistir -> entidad guardada -> DTO
        Persona entidad = mapper.toEntity(persona);
        Persona guardada = repository.save(entidad);
        return mapper.toView(guardada);
    }

    @Override
    @Transactional
    public PersonaView actualizar(PersonaView persona) {
        // save() hace insert o update segun exista la clave primaria (DNI).
        Persona entidad = mapper.toEntity(persona);
        Persona actualizada = repository.save(entidad);
        return mapper.toView(actualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonaView> listarTodas() {
        return repository.findAll().stream()
                .map(mapper::toView)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PersonaView> buscarPorDni(String dni) {
        // map() aplica la conversion solo si el Optional trae valor;
        // si el DNI no existe, devuelve Optional.empty().
        return repository.findById(dni).map(mapper::toView);
    }

    @Override
    @Transactional
    public void eliminar(String dni) {
        repository.deleteById(dni);
    }
}
