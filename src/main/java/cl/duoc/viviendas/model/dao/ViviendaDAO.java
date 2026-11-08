package cl.duoc.viviendas.dao;

import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import java.util.List;

public interface ViviendaDAO {
    List<Departamento> cargarDepartamentos();
    List<Casa> cargarCasas();
    void guardarDepartamentos(List<Departamento> departamentos);
    void guardarCasas(List<Casa> casas);
}