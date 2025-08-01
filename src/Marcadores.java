import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Marcadores {
    private static final int MAX_RECORDS = 100; // Máximo de records a guardar
    private static Record[] records = new Record[MAX_RECORDS];//Arreglo que almacena las puntuaciones
    private static int numeroRecords = 0;//Cuanta la cantidad de registros

    // Clase para representar cada registro
    private static class Record implements Serializable {
        String nombre;//Nombre del jugador
        String dificultad;//Dificultad seleccionada
        int puntuacion;//Puntuacion obtenida
        long fecha;//Fecha en que se registro

        //Constructor del record
        public Record(String nombre, String dificultad, int puntuacion) {
            this.nombre = nombre;
            this.dificultad = dificultad;
            this.puntuacion = puntuacion;
            this.fecha = System.currentTimeMillis();//Guarda la fecha y hora en milisegundos
        }
        //Metodo para mostrar el record en formato legible
        public String toString() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return nombre + " - " + dificultad + " - " + puntuacion + "/5 (" + sdf.format(new Date(fecha)) + ")";
        }
    }

    // Metodo para cargar marcadores desde archivo
    public static void cargarMarcadores() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("marcadores.dat"))) {
            records = (Record[]) ois.readObject();//Lee los record guardados en el archivo
            // Contar records no nulos
            numeroRecords = 0;
            for (Record record : records) {
                if (record != null) numeroRecords++;
            }
        } catch (FileNotFoundException e) {
            // Archivo no existe aún, se creará después
        } catch (Exception e) {//Si ocurre otro error al leer el archivo, imrpime el error
            e.printStackTrace();
        }
    }

    // Guardar marcadores en archivo
    public static void guardarMarcadores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("marcadores.dat"))) {
            oos.writeObject(records);//Escribe el arreglo completo al archivo
        } catch (Exception e) {
            e.printStackTrace();//Muestra cualquier error que ocurra
        }
    }

    // Metodo para Añadir un nuevo record
    public static void añadirRecord(String nombre, String dificultad, int puntuacion) {
        if (puntuacion == 5) {
            if (numeroRecords < MAX_RECORDS) {//Si aun hay espacion en el arreglo, añade uno nuevo
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
            guardarMarcadores();//Guarda los cambioes
        }
    }

    // Metodo para mostrar marcadores en una ventana emergente
    public static void mostrarMarcadores(JFrame parent) {
        cargarMarcadores();//cargar los marcadores existentes

        //Se crea una ventana emergente
        JDialog dialog = new JDialog(parent, "Marcadores - Ganadores del Millón de Dolares", true);
        dialog.setSize(500, 400);//Se define el tamaño
        dialog.setLocationRelativeTo(parent);

        JTextArea textArea = new JTextArea();//Se crea para mostrar los marcadores
        textArea.setEditable(false);//Se desactiva la edicion
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));//fuente y tamaño del texto

        // Ordenar records por puntuación y fecha (mejores primero)
        ordenarRecords();

        // Construir texto con los records
        StringBuilder cadena = new StringBuilder();
        cadena.append("=== GANADORES DEL MILLÓN DE DOLARES ===\n\n");
        for (int i = 0; i < numeroRecords; i++) {
            cadena.append((i+1)).append(". ").append(records[i].toString()).append("\n");
        }

        textArea.setText(cadena.toString());//el texto contruido se inserta en el area de texto
        dialog.add(new JScrollPane(textArea));//si hay muchos, permite hacer un scroll vertical
        dialog.setVisible(true);//Muestra la ventana
    }

    // Metodo para ordenar los records
    private static void ordenarRecords() {
        for (int i = 0; i < numeroRecords-1; i++) {//Recorre todos los registros
            for (int j = 0; j < numeroRecords-i-1; j++) {//Realia las comparaciones y posibles intercambios
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