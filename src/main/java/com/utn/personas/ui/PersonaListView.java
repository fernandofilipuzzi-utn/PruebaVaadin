package com.utn.personas.ui;

import com.utn.personas.dto.PersonaView;
import com.utn.personas.service.PersonaService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Vista principal (capa de presentacion) para gestionar personas.
 *
 * <p>Mapeada a la ruta raiz {@code "/"} mediante {@code @Route}. Consume
 * unicamente {@link PersonaService} y trabaja siempre con {@link PersonaView}:
 * nunca toca la entidad JPA directamente.</p>
 *
 * <p>Layout: a la izquierda un {@link Grid} que lista las personas; a la
 * derecha un {@link FormLayout} para alta/edicion con botones guardar/eliminar.</p>
 */
@Route("")
@PageTitle("Gestion de Personas")
public class PersonaListView extends VerticalLayout {

    private final PersonaService personaService;

    // --- Componentes de UI ---
    private final Grid<PersonaView> grid = new Grid<>(PersonaView.class, false);
    private final TextField dni = new TextField("DNI");
    private final TextField nombre = new TextField("Nombre");
    private final IntegerField edad = new IntegerField("Edad");
    private final Button guardar = new Button("Guardar");
    private final Button eliminar = new Button("Eliminar");
    private final Button limpiar = new Button("Limpiar");

    // Binder: vincula los campos del formulario con un PersonaView.
    private final Binder<PersonaView> binder = new Binder<>(PersonaView.class);

    public PersonaListView(PersonaService personaService) {
        this.personaService = personaService;

        setSizeFull();
        configurarGrid();
        configurarFormulario();

        add(new HorizontalLayout(grid, construirPanelFormulario()) {{
            setSizeFull();
            setFlexGrow(2, grid);
        }});

        actualizarLista();
        limpiarFormulario();
    }

    /** Define las columnas del grid y la seleccion de filas. */
    private void configurarGrid() {
        grid.addColumn(PersonaView::getDni).setHeader("DNI").setAutoWidth(true);
        grid.addColumn(PersonaView::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(PersonaView::getEdad).setHeader("Edad").setAutoWidth(true);
        grid.setSizeFull();

        // Al seleccionar una fila, se cargan sus datos en el formulario (modo edicion).
        grid.asSingleSelect().addValueChangeListener(e -> editar(e.getValue()));
    }

    /** Configura el binder y la accion de los botones. */
    private void configurarFormulario() {
        // Vinculacion campo <-> propiedad del DTO.
        binder.forField(dni)
                .asRequired("El DNI es obligatorio")
                .bind(PersonaView::getDni, PersonaView::setDni);
        binder.forField(nombre)
                .asRequired("El nombre es obligatorio")
                .bind(PersonaView::getNombre, PersonaView::setNombre);
        binder.forField(edad)
                .asRequired("La edad es obligatoria")
                .bind(PersonaView::getEdad, PersonaView::setEdad);

        guardar.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        eliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        guardar.addClickListener(e -> guardar());
        eliminar.addClickListener(e -> eliminar());
        limpiar.addClickListener(e -> {
            grid.asSingleSelect().clear();
            limpiarFormulario();
        });
    }

    /** Arma el panel lateral con el formulario y la botonera. */
    private FormLayout construirPanelFormulario() {
        FormLayout form = new FormLayout();
        form.add(dni, nombre, edad, new HorizontalLayout(guardar, eliminar, limpiar));
        form.setWidth("25em");
        return form;
    }

    /** Carga una persona en el formulario; si es null, lo deja vacio para alta. */
    private void editar(PersonaView persona) {
        if (persona == null) {
            limpiarFormulario();
        } else {
            binder.setBean(persona);
            // El DNI es clave primaria: no se edita una vez creada la persona.
            dni.setReadOnly(true);
            eliminar.setEnabled(true);
        }
    }

    /** Deja el formulario listo para un alta nueva. */
    private void limpiarFormulario() {
        binder.setBean(new PersonaView());
        dni.setReadOnly(false);
        eliminar.setEnabled(false);
    }

    /** Valida y persiste el alta/edicion a traves del servicio. */
    private void guardar() {
        PersonaView persona = binder.getBean();
        if (!binder.validate().isOk()) {
            return; // Hay errores de validacion; el binder ya los muestra.
        }
        // Si el DNI es de solo lectura estamos editando; si no, es un alta.
        if (dni.isReadOnly()) {
            personaService.actualizar(persona);
            Notification.show("Persona actualizada");
        } else {
            personaService.crear(persona);
            Notification.show("Persona creada");
        }
        actualizarLista();
        grid.asSingleSelect().clear();
        limpiarFormulario();
    }

    /** Elimina la persona seleccionada a traves del servicio. */
    private void eliminar() {
        PersonaView persona = binder.getBean();
        if (persona != null && persona.getDni() != null) {
            personaService.eliminar(persona.getDni());
            Notification.show("Persona eliminada");
            actualizarLista();
            grid.asSingleSelect().clear();
            limpiarFormulario();
        }
    }

    /** Recarga el grid desde el servicio. */
    private void actualizarLista() {
        grid.setItems(personaService.listarTodas());
    }
}
