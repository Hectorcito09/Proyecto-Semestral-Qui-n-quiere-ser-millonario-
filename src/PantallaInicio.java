import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PantallaInicio extends JFrame {

    public PantallaInicio() {
        setTitle("¿Quién quiere ser millonario?");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa
        setUndecorated(true); // Sin bordes ni botones de ventana

        // Panel del fondo
        JPanel fondoPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imagenFondo = new ImageIcon("Recursos/pantalla_inicio.png");//Carga la imagen
                g.drawImage(imagenFondo.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };

        // Crear el botón "JUGAR"
        JButton jugarBtn = new JButton("JUGAR");
        jugarBtn.setFont(new Font("Arial", Font.BOLD, 24));//tipo y tamaño de letra
        jugarBtn.setForeground(Color.WHITE);//texto en blanco
        jugarBtn.setBackground(new Color(0, 100, 0)); // Color del boton (Verde)
        jugarBtn.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));//Borde de color amarillo
        jugarBtn.setFocusPainted(false);
        jugarBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jugarBtn.setPreferredSize(new Dimension(200, 60));//Tamaño

        // Efecto al pasar el mouses por encima del boton
        jugarBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                jugarBtn.setBackground(new Color(0, 150, 0)); // Color del boton (verde claro)
                jugarBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));//Borde en blanco
            }//Ocurre cuando el mouse pasa por encima del boton

            @Override
            public void mouseExited(MouseEvent e) {
                jugarBtn.setBackground(new Color(0, 100, 0)); // Color principal
                jugarBtn.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
            }//Cuando el mouse sale del boton, vuelve a su color original
        });

        // Acción al presionar el "Jugar"
        jugarBtn.addActionListener(e -> {
            dispose(); // Cierra esta ventana
            new MenuPrincipal(); // Abre la ventana del menú principal
        });

        // Panel para colocar el botón en la parte inferior
        JPanel panelBoton = new JPanel();
        panelBoton.setOpaque(false); // Panel transparente
        panelBoton.setLayout(new GridBagLayout()); // Centrado del botón
        panelBoton.add(jugarBtn);
        panelBoton.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0)); // Margen inferior

        fondoPanel.add(panelBoton, BorderLayout.SOUTH);//Coloca el panelBoton en la parte inferior del fondo
        setContentPane(fondoPanel);//Coloca fondoPanel como el contenido principal de la ventana

        setVisible(true); // Mostrar la ventana
    }

    public static void main(String[] args) {//inicia la pantalla de inicio
        SwingUtilities.invokeLater(() -> {
            new PantallaInicio();
        });
    }
}

