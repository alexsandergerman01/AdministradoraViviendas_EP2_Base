package cl.duoc.viviendas.dao;

import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ViviendaJsonDAO implements ViviendaDAO {
    private static final String FILE_DEPTO = "departamentos.json";
    private static final String FILE_CASA = "casas.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public List<Departamento> cargarDepartamentos() {
        File file = new File(FILE_DEPTO);
        if (!file.exists()) return new ArrayList<>();
        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<List<Departamento>>() {}.getType();
            List<Departamento> lista = gson.fromJson(reader, type);
            return lista != null ? lista : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Casa> cargarCasas() {
        File file = new File(FILE_CASA);
        if (!file.exists()) return new ArrayList<>();
        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<List<Casa>>() {}.getType();
            List<Casa> lista = gson.fromJson(reader, type);
            return lista != null ? lista : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void guardarDepartamentos(List<Departamento> departamentos) {
        try (FileWriter writer = new FileWriter(FILE_DEPTO)) {
            gson.toJson(departamentos, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void guardarCasas(List<Casa> casas) {
        try (FileWriter writer = new FileWriter(FILE_CASA)) {
            gson.toJson(casas, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}