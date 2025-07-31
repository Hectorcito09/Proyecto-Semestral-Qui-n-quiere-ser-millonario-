import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

    public class PantallaInicio extends JFrame {
        public PantallaInicio() {
            setTitle("¿Quién quiere ser millonario?");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setUndecorated(true);

            // Configurar el panel de fondo con imagen
            JPanel fondoPanel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    ImageIcon imagenFondo = new ImageIcon("Recursos/pantalla_inicio.png");
                    g.drawImage(imagenFondo.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            };

            // Crear botón Jugar
            JButton jugarBtn = new JButton("JUGAR");
            jugarBtn.setFont(new Font("Arial", Font.BOLD, 24));
            jugarBtn.setForeground(Color.WHITE);
            jugarBtn.setBackground(new Color(0, 100, 0));
            jugarBtn.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
            jugarBtn.setFocusPainted(false);
            jugarBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            jugarBtn.setPreferredSize(new Dimension(200, 60));

            // Efecto hover para el botón
            jugarBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    jugarBtn.setBackground(new Color(0, 150, 0));
                    jugarBtn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    jugarBtn.setBackground(new Color(0, 100, 0));
                    jugarBtn.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
                }
            });

            // Acción del botón
            jugarBtn.addActionListener(e -> {
                dispose(); // Cierra esta ventana
                new MenuPrincipal(); // Abre el menú principal
            });

            // Panel para centrar el botón en la parte inferior
            JPanel panelBoton = new JPanel();
            panelBoton.setOpaque(false);
            panelBoton.setLayout(new GridBagLayout());
            panelBoton.add(jugarBtn);

            // Añadir margen inferior
            panelBoton.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));

            fondoPanel.add(panelBoton, BorderLayout.SOUTH);
            setContentPane(fondoPanel);

            setVisible(true);
        }

        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                new PantallaInicio();
            });
        }
    }

