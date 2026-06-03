package com.utn.personas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA que representa una persona persistida en la base de datos.
 *
 * <p>Pertenece a la <b>capa de entidad</b>: se mapea a la tabla {@code PERSONAS}
 * y solo debe usarse en las capas de repositorio y servicio. La UI nunca la
 * manipula directamente, sino a traves del DTO {@code PersonaView}.</p>
 */
@Entity
@Table(name = "PERSONAS")
public class Persona {

    /** DNI: clave primaria (no autogenerada, la provee el usuario). */
    @Id
    @Column(name = "DNI", nullable = false, length = 20)
    private String dni;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Column(name = "EDAD", nullable = false)
    private int edad;

    /** Constructor vacio requerido por JPA. */
    public Persona() {
    }

    public Persona(String dni, String nombre, int edad) {
        this.dni = dni;
        this.nombre = nombre;
        this.edad = edad;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }
}
