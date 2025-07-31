import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class ReproductorSonido {
    public static void reproducir(String rutaArchivo) {
        new Thread(() -> {
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                        new File(rutaArchivo).getAbsoluteFile());
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            } catch (UnsupportedAudioFileException e) {
                System.err.println("Error al reproducir sonido: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("Error al reproducir sonido: " + e.getMessage());
            } catch (LineUnavailableException e) {
                System.err.println("Error al reproducir sonido: " + e.getMessage());
            }
        }).start();
    }
}