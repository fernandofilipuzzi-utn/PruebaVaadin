package com.utn.personas.dto;

/**
 * DTO (modelo de vista) de una persona.
 *
 * <p>Pertenece a la <b>capa de modelo de vista</b>. Esta deliberadamente
 * separado de la entidad {@code Persona} para desacoplar la UI del modelo de
 * persistencia: la vista Vaadin trabaja exclusivamente con esta clase.</p>
 *
 * <p>Tiene constructor vacio y setters porque Vaadin {@code Binder} necesita
 * instanciar y poblar el objeto durante el data-binding del formulario.</p>
 */
public class PersonaView {

    private String dni;
    private String nombre;
    private int edad;

    public PersonaView() {
    }

    public PersonaView(String dni, String nombre, int edad) {
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
