import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Marcadores {
    private static final int MAX_RECORDS = 100; // Máximo de records a guardar
    private static Record[] records = new Record[MAX_RECORDS];
    private static int numeroRecords = 0;

    // Clase para representar cada registro
    private static class Record implements Serializable {
        String nombre;
        String dificultad;
        int puntuacion;
        long fecha;

        public Record(String nombre, String dificultad, int puntuacion) {
            this.nombre = nombre;
            this.dificultad = dificultad;
            this.puntuacion = puntuacion;
            this.fecha = System.currentTimeMillis();
        }

        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return nombre + " - " + dificultad + " - " + puntuacion + "/5 (" + sdf.format(new Date(fecha)) + ")";
        }
    }

    // Cargar marcadores desde archivo
    public static void cargarMarcadores() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("marcadores.dat"))) {
            records = (Record[]) ois.readObject();
            // Contar records no nulos
            numeroRecords = 0;
            for (Record record : records) {
                if (record != null) numeroRecords++;
            }
        } catch (FileNotFoundException e) {
            // Archivo no existe aún, se creará después
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Guardar marcadores en archivo
    public static void guardarMarcadores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("marcadores.dat"))) {
            oos.writeObject(records);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Añadir nuevo record
    public static void añadirRecord(String nombre, String dificultad, int puntuacion) {
        if (puntuacion == 5) { // Solo guardar si respondió todas correctamente
            if (numeroRecords < MAX_RECORDS) {
                records[numeroRecords++] = new Record(nombre, dificultad, puntuacion);
            } else {
                // Reemplazar el record más antiguo si ya estamos al máximo
                int masAntiguo = 0;
                for (int i = 1; i < records.length; i++) {
                    if (records[i].fecha < records[masAntiguo].fecha) {
                        masAntiguo = i;
                    }
                }
                records[masAntiguo] = new Record(nombre, dificultad, puntuacion);
            }
            guardarMarcadores();
        }
    }

    // Mostrar marcadores en un JDialog
    public static void mostrarMarcadores(JFrame parent) {
        cargarMarcadores();

        JDialog dialog = new JDialog(parent, "Marcadores - Ganadores del Millón de Dolares", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(parent);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        // Ordenar records por puntuación y fecha (mejores primero)
        ordenarRecords();

        // Construir texto con los records
        StringBuilder cadena = new StringBuilder();
        cadena.append("=== GANADORES DEL MILLÓN DE DOLARES ===\n\n");
        for (int i = 0; i < numeroRecords; i++) {
            cadena.append((i+1)).append(". ").append(records[i].toString()).append("\n");
        }

        textArea.setText(cadena.toString());
        dialog.add(new JScrollPane(textArea));
        dialog.setVisible(true);
    }

    // Método para ordenar los records (bubble sort)
    private static void ordenarRecords() {
        for (int i = 0; i < numeroRecords-1; i++) {
            for (int j = 0; j < numeroRecords-i-1; j++) {
                if (records[j].puntuacion < records[j+1].puntuacion ||
                        (records[j].puntuacion == records[j+1].puntuacion && records[j].fecha > records[j+1].fecha)) {
                    // Intercambiar
                    Record acumulador = records[j];
                    records[j] = records[j+1];
                    records[j+1] = acumulador;
                }
            }
        }
    }
}