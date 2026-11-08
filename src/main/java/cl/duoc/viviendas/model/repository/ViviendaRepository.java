package cl.duoc.viviendas.repository;

import cl.duoc.viviendas.dao.ViviendaDAO;
import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class ViviendaRepository {
    private final ViviendaDAO dao;
    private final ObservableList<Vivienda> listaViviendas;

    public ViviendaRepository(ViviendaDAO dao) {
        this.dao = dao;
        this.listaViviendas = FXCollections.observableArrayList();
        cargarDatos();
    }

    private void cargarDatos() {
        listaViviendas.clear();
        listaViviendas.addAll(dao.cargarDepartamentos());
        listaViviendas.addAll(dao.cargarCasas());
    }

    public ObservableList<Vivienda> obtenerTodas() {
        return listaViviendas;
    }

    public void agregar(Vivienda vivienda) {
        listaViviendas.add(vivienda);
        guardarTodo();
    }

    public void eliminar(Vivienda vivienda) {
        listaViviendas.remove(vivienda);
        guardarTodo();
    }

    private void guardarTodo() {
        List<Departamento> departamentos = new ArrayList<>();
        List<Casa> casas = new ArrayList<>();

        for (Vivienda v : listaViviendas) {
            if (v instanceof Departamento d) {
                departamentos.add(d);
            } else if (v instanceof Casa c) {
                casas.add(c);
            }
        }

        dao.guardarDepartamentos(departamentos);
        dao.guardarCasas(casas);
    }
}