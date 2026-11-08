package cl.duoc.viviendas.controller;

import cl.duoc.viviendas.dao.ViviendaJsonDAO;
import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;
import cl.duoc.viviendas.repository.ViviendaRepository;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.util.Locale;

public class MainController {

    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtSuperficie;
    @FXML private TextField txtHabitaciones;

    @FXML private TitledPane paneDepto;
    @FXML private TextField txtPiso;
    @FXML private CheckBox chkGastoComun;
    @FXML private CheckBox chkEstacionamiento;

    @FXML private TitledPane paneCasa;
    @FXML private CheckBox chkPatio;

    @FXML private Button btnRegistrar;
    @FXML private Button btnEliminar;

    @FXML private TableView<Vivienda> tblViviendas;
    @FXML private TableColumn<Vivienda, String> colTipo;
    @FXML private TableColumn<Vivienda, String> colCodigo;
    @FXML private TableColumn<Vivienda, Double> colSuperficie;
    @FXML private TableColumn<Vivienda, Integer> colHabitaciones;
    @FXML private TableColumn<Vivienda, String> colCosto;

    @FXML private Label lblMensaje;

    private ViviendaRepository repository;

    @FXML
    public void initialize() {
        repository = new ViviendaRepository(new ViviendaJsonDAO());

        cmbTipo.getItems().addAll("Departamento", "Casa");
        cmbTipo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> actualizarCamposSegunTipo(newVal));
        actualizarCamposSegunTipo(null);

        colTipo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTipo()));
        colCodigo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCodigo()));
        colSuperficie.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getSuperficie()));
        colHabitaciones.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getHabitaciones()));
        colCosto.setCellValueFactory(cell -> new SimpleStringProperty(formatearMoneda(cell.getValue().calcularCostoArriendo())));

        // Asigna directamente la lista completa a la tabla
        tblViviendas.setItems(repository.obtenerTodas());
    }

    private void actualizarCamposSegunTipo(String tipo) {
        if ("Departamento".equals(tipo)) {
            paneDepto.setDisable(false);
            paneCasa.setDisable(true);
            limpiarCamposCasa();
        } else if ("Casa".equals(tipo)) {
            paneDepto.setDisable(true);
            paneCasa.setDisable(false);
            limpiarCamposDepto();
        } else {
            paneDepto.setDisable(true);
            paneCasa.setDisable(true);
            limpiarCamposDepto();
            limpiarCamposCasa();
        }
    }

    @FXML
    private void handleRegistrar() {
        String tipo = cmbTipo.getValue();
        if (tipo == null || tipo.isEmpty()) {
            mostrarMensaje("Debe seleccionar un tipo de vivienda.", true);
            return;
        }

        String codigo = txtCodigo.getText() != null ? txtCodigo.getText().trim() : "";
        if (codigo.isEmpty()) {
            mostrarMensaje("Código: no puede estar vacío ni contener solo espacios.", true);
            return;
        }

        double superficie;
        try {
            superficie = Double.parseDouble(txtSuperficie.getText().trim().replace(',', '.'));
            if (superficie < 20 || superficie > 500) {
                mostrarMensaje("Superficie: debe ser un número entre 20 y 500.", true);
                return;
            }
        } catch (Exception e) {
            mostrarMensaje("Superficie: debe ser un número entre 20 y 500.", true);
            return;
        }

        int habitaciones;
        try {
            habitaciones = Integer.parseInt(txtHabitaciones.getText().trim());
            if (habitaciones <= 0) {
                mostrarMensaje("Habitaciones: debe ser un número entero mayor que cero.", true);
                return;
            }
        } catch (Exception e) {
            mostrarMensaje("Habitaciones: debe ser un número entero mayor que cero.", true);
            return;
        }

        Vivienda nuevaVivienda;
        if ("Departamento".equals(tipo)) {
            int piso;
            try {
                piso = Integer.parseInt(txtPiso.getText().trim());
                if (piso <= 0) {
                    mostrarMensaje("Piso: debe ser un número entero mayor que cero.", true);
                    return;
                }
            } catch (Exception e) {
                mostrarMensaje("Piso: debe ser un número entero mayor que cero.", true);
                return;
            }

            boolean gastoComun = chkGastoComun.isSelected();
            Departamento depto = new Departamento(codigo, superficie, habitaciones, piso, gastoComun);
            if (chkEstacionamiento.isSelected()) {
                depto.asignarEstacionamiento();
            }
            nuevaVivienda = depto;
        } else {
            boolean tienePatio = chkPatio.isSelected();
            nuevaVivienda = new Casa(codigo, superficie, habitaciones, tienePatio);
        }

        repository.agregar(nuevaVivienda);
        mostrarMensaje(nuevaVivienda.getCodigo() + " (" + tipo + ") registrada correctamente.", false);
        limpiarFormularioCompleto();
    }

    @FXML
    private void handleEliminar() {
        Vivienda seleccionada = tblViviendas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarMensaje("Debe seleccionar una vivienda de la tabla para eliminarla.", true);
            return;
        }

        repository.eliminar(seleccionada);
        mostrarMensaje("Vivienda " + seleccionada.getCodigo() + " eliminada correctamente.", false);
    }

    private void mostrarMensaje(String texto, boolean esError) {
        lblMensaje.setText(texto);
        if (esError) {
            lblMensaje.setTextFill(Color.RED);
        } else {
            lblMensaje.setTextFill(Color.web("#2e7d32"));
        }
    }

    private void limpiarCamposDepto() {
        txtPiso.clear();
        chkGastoComun.setSelected(false);
        chkEstacionamiento.setSelected(false);
    }

    private void limpiarCamposCasa() {
        chkPatio.setSelected(false);
    }

    private void limpiarFormularioCompleto() {
        cmbTipo.getSelectionModel().clearSelection();
        txtCodigo.clear();
        txtSuperficie.clear();
        txtHabitaciones.clear();
        limpiarCamposDepto();
        limpiarCamposCasa();
        actualizarCamposSegunTipo(null);
    }

    private String formatearMoneda(double monto) {
        return "$" + String.format(Locale.GERMAN, "%,d", Math.round(monto)).replace(',', '.');
    }
}