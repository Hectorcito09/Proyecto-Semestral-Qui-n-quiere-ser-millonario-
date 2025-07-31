import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        setTitle("¿Quién quiere ser millonario?");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Para pantalla completa
        setUndecorated(true); // Opcional: quita bordes de ventana

        // Fondo
        setContentPane(new FondoPanel("Recursos/Menu.gif"));
        setLayout(new BorderLayout());

        // Panel principal para centrar contenido
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setOpaque(false);

        // Panel de botones
        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0); // Espaciado entre botones
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        // Crear botones
        JButton jugarBtn = crearBoton("JUGAR");
        JButton marcadoresBtn = crearBoton("MARCADORES");
        JButton salirBtn = crearBoton("SALIR");

        // Añadir botones al panel
        panelBotones.add(jugarBtn, gbc);
        panelBotones.add(marcadoresBtn, gbc);
        panelBotones.add(salirBtn, gbc);

        // ►►► CONFIGURACIÓN DE POSICIÓN AVANZADA ►►►
        // Espacio superior (40% de la pantalla)
        GridBagConstraints gbcSpaceTop = new GridBagConstraints();
        gbcSpaceTop.gridy = 0;
        gbcSpaceTop.weighty = 1;
        panelPrincipal.add(Box.createVerticalGlue(), gbcSpaceTop);

        // Panel de botones (20% de la pantalla)
        GridBagConstraints gbcButtons = new GridBagConstraints();
        gbcButtons.gridy = 1;
        gbcButtons.weighty = 0.06;
        gbcButtons.anchor = GridBagConstraints.CENTER;
        panelPrincipal.add(panelBotones, gbcButtons);

        // Espacio inferior (40% de la pantalla)
        GridBagConstraints gbcSpaceBottom = new GridBagConstraints();
        gbcSpaceBottom.gridy = 2;
        gbcSpaceBottom.weighty = 0.1;
        panelPrincipal.add(Box.createVerticalGlue(), gbcSpaceBottom);

        // Añadir panel principal al frame
        add(panelPrincipal, BorderLayout.CENTER);

        // Listeners de botones
        jugarBtn.addActionListener(e -> {
            reproducirSonido("click");
            mostrarSeleccionGenero();
        });

        marcadoresBtn.addActionListener(e -> {
            reproducirSonido("click");
            Marcadores.mostrarMarcadores(MenuPrincipal.this);
        });

        salirBtn.addActionListener(e -> {
            reproducirSonido("click");
            System.exit(0);
        });

        setVisible(true);
    }

    private void mostrarSeleccionGenero() {
        // Crear panel personalizado para selección de género
        JPanel panelGenero = new JPanel(new GridLayout(1, 2, 20, 20));
        panelGenero.setOpaque(false);

        // Botón para hombre
        JButton btnHombre = new JButton();
        configurarBotonGenero(btnHombre, "Recursos/hombre.png", "Recursos/hombre_cambio.png");

        // Botón para mujer
        JButton btnMujer = new JButton();
        configurarBotonGenero(btnMujer, "Recursos/mujer.png", "Recursos/mujer_cambio.png");

        // Configurar acciones ANTES de mostrar el diálogo
        btnHombre.addActionListener(ev -> {
            reproducirSonido("click");
            solicitarNombreYDificultad("hombre");
        });

        btnMujer.addActionListener(ev -> {
            reproducirSonido("click");
            solicitarNombreYDificultad("mujer");
        });

        panelGenero.add(btnHombre);
        panelGenero.add(btnMujer);

        // Crear un JOptionPane personalizado
        JOptionPane opcion = new JOptionPane(panelGenero,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[]{}, // Sin botones adicionales
                null);

        // Crear el diálogo
        JDialog dialog = opcion.createDialog(this, "Selecciona tu género");
        dialog.setModal(true);
        dialog.setVisible(true);
        dialog.dispose(); // Limpiar después de cerrar
    }

    private void configurarBotonGenero(JButton boton, String imagenNormal, String imagenCambio) {
        boton.setIcon(new ImageIcon(imagenNormal));
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setOpaque(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setIcon(new ImageIcon(imagenCambio));
                reproducirSonido("navegacion");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setIcon(new ImageIcon(imagenNormal));
            }
        });
    }

    private void solicitarNombreYDificultad(String genero) {
        String nombre = "";
        boolean nombreValido = false;

        while (!nombreValido) {
            // Solicitar nombre del jugador
            nombre = JOptionPane.showInputDialog(this,
                    "Ingresa como quieres que te llamen:",
                    "Nombre del jugador",
                    JOptionPane.QUESTION_MESSAGE);

            // Si el usuario cancela, salir del método
            if (nombre == null) {
                return;
            }

            // Validar el nombre
            if (nombre.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No puedes dejar este campo vacío",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                nombreValido = true;
            }
        }

        // Solicitar dificultad (solo si el nombre es válido)
        String[] opciones = {"Fácil", "Media", "Difícil"};
        String seleccion = (String) JOptionPane.showInputDialog(
                this,
                "Selecciona la dificultad:",
                "Dificultad",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (seleccion != null) {
            dispose();
            new VentanaJuego(seleccion, obtenerPreguntasPorDificultad(seleccion), genero, nombre.trim());
        }
    }

    private JButton crearBoton(String texto) {
        String ruta = "Recursos/" + texto.toLowerCase() + ".png";
        ImageIcon iconoNormal = new ImageIcon(ruta);

        // Crea un botón con el icono normal
        JButton boton = new JButton(iconoNormal);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setOpaque(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(iconoNormal.getIconWidth(), iconoNormal.getIconHeight()));

        // Opción 1: Cambiar el icono cuando el mouse está encima
        String rutaHover = "Recursos/" + texto.toLowerCase() + "_cambio.png";
        try {
            ImageIcon iconoHover = new ImageIcon(rutaHover);
            boton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    boton.setIcon(iconoHover);
                    reproducirSonido("navegacion");
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    boton.setIcon(iconoNormal);
                }
            });
        } catch (Exception e) {
            // Opción 2: Efecto de escala si no hay imagen hover
            boton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    boton.setIcon(new ImageIcon(escalarImagen(iconoNormal.getImage(), 1.1f)));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    boton.setIcon(iconoNormal);
                }
            });
        }

        return boton;
    }

    private Image escalarImagen(Image imagen, float factor) {
        int nuevoAncho = (int)(imagen.getWidth(null) * factor);
        int nuevoAlto = (int)(imagen.getHeight(null) * factor);
        return imagen.getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);
    }

    private void reproducirSonido(String tipo) {
        String rutaSonido;
        if (tipo.equals("navegacion")) {
            rutaSonido = "Recursos/navegacion.wav";
        } else {
            rutaSonido = "Recursos/click.wav";
        }
        ReproductorSonido.reproducir(rutaSonido);
    }

    private Pregunta[] obtenerPreguntasPorDificultad(String dificultad) {
        if (dificultad.equalsIgnoreCase("Fácil")) {
            return PreguntasFaciles.getPreguntas();
        } else if (dificultad.equalsIgnoreCase("Media")) {
            return PreguntasMedias.getPreguntas();
        } else  {
            return PreguntasDificiles.getPreguntas();
        }
    }
}