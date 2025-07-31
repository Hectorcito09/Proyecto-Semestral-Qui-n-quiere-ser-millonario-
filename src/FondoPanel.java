import javax.swing.*;
import java.awt.*;

public class FondoPanel extends JPanel {
    private Image fondo;

    public FondoPanel(String rutaImagen) {
        fondo = new ImageIcon(rutaImagen).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
    }
}