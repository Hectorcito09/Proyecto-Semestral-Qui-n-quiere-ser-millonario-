import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class ReproductorSonido {//Creacion de la clase
    public static void reproducir(String rutaArchivo) {//Creacion de un metodo estatico
        new Thread(() -> {//Se crea un nuevo hilo
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(//Convierte el audio en una secuencia de datos legible por java
                        new File(rutaArchivo).getAbsoluteFile());//se asegura que la ruta sea completa
                Clip clip = AudioSystem.getClip();//Obtiene un clib del audio para controlarlo
                clip.open(audioInputStream);//carga el archivo del audio
                clip.start();//Se reproduce el audio
            } catch (UnsupportedAudioFileException e) {//Excepciones de posbiles errores
                System.err.println("Error al reproducir sonido: " + e.getMessage());//si el archivo no es un formato de audio soportado
            } catch (IOException e) {
                System.err.println("Error al reproducir sonido: " + e.getMessage());//Si hay un problema al leer el archivo
            } catch (LineUnavailableException e) {
                System.err.println("Error al reproducir sonido: " + e.getMessage());//Si el sistema no esta disponible para producir el sonido
            }
        }).start();//Comienza a sonar el audio
    }
}