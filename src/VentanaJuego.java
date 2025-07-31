import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.Arrays;
import javax.sound.sampled.*;
import java.io.File;

public class VentanaJuego extends JFrame {
    private Clip sonidoFondo;
    private Clip sonidoCorrecto;
    private Clip sonidoIncorrecto;
    private JLayeredPane layeredPane;
    private JLabel fondoLabel, dialogoLabel;
    private String nombreJugador;
    private String genero;
    private int paso = 0;
    private Timer temporizador;
    private String dificultad;
    private Pregunta[] preguntas;
    private int indicePreguntaActual = 0;
    private JButton[] botonesOpciones = new JButton[4];
    private boolean enPregunta = false;
    private int puntuacion = 0;
    private JLabel comodin5050, comodinLlamada, comodinPublico;
    private Comodin comodines = new Comodin();

    public VentanaJuego(String dificultad, Pregunta[] preguntasOriginal, String genero, String nombreJugador) {
        this.dificultad = dificultad;
        this.genero = genero;
        this.nombreJugador = nombreJugador;

        // Copiar y mezclar las preguntas
        this.preguntas = Arrays.copyOf(preguntasOriginal, preguntasOriginal.length);
        Collections.shuffle(Arrays.asList(this.preguntas));

        // Limitar a 5 preguntas
        if (this.preguntas.length > 5) {
            this.preguntas = Arrays.copyOf(this.preguntas, 5);
        }

        setTitle("¿Quién quiere ser millonario? - " + dificultad);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        iniciarComponentes();
        setVisible(true);
        cargarSonidos();
        reproducirSonidoFondo();
    }

    private void cargarSonidos() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    new File("Recursos/plenafondo.wav").getAbsoluteFile());
            sonidoFondo = AudioSystem.getClip();
            sonidoFondo.open(audioInputStream);
            FloatControl gainControl = (FloatControl) sonidoFondo.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-10.0f);

            audioInputStream = AudioSystem.getAudioInputStream(
                    new File("Recursos/correcto.wav").getAbsoluteFile());
            sonidoCorrecto = AudioSystem.getClip();
            sonidoCorrecto.open(audioInputStream);

            audioInputStream = AudioSystem.getAudioInputStream(
                    new File("Recursos/incorrecto.wav").getAbsoluteFile());
            sonidoIncorrecto = AudioSystem.getClip();
            sonidoIncorrecto.open(audioInputStream);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los sonidos");
        }
    }

    private void reproducirSonidoFondo() {
        if (sonidoFondo != null) {
            sonidoFondo.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private void pausarSonidoFondo() {
        if (sonidoFondo != null && sonidoFondo.isRunning()) {
            sonidoFondo.stop();
        }
    }

    private void reanudarSonidoFondo() {
        if (sonidoFondo != null && !sonidoFondo.isRunning()) {
            sonidoFondo.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    private void reproducirSonidoRespuesta(boolean correcta) {
        pausarSonidoFondo();

        Clip sonido = correcta ? sonidoCorrecto : sonidoIncorrecto;
        if (sonido != null) {
            sonido.setFramePosition(0);
            LineListener listener = new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        reanudarSonidoFondo();
                        sonido.removeLineListener(this);
                    }
                }
            };
            sonido.addLineListener(listener);
            sonido.start();
        }
    }

    private void iniciarComponentes() {
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        setContentPane(layeredPane);

        // Configurar fondo
        String fondoPath = genero.equals("hombre") ? "Recursos/juegohombre.png" : "Recursos/juegomujer.png";
        fondoLabel = new JLabel(new ImageIcon(fondoPath));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Image img = ((ImageIcon)fondoLabel.getIcon()).getImage()
                .getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_SMOOTH);
        fondoLabel.setIcon(new ImageIcon(img));
        fondoLabel.setBounds(0, 0, screenSize.width, screenSize.height);
        layeredPane.add(fondoLabel, JLayeredPane.DEFAULT_LAYER);

        // Configurar comodines
        configurarComodines();

        // Configurar área de diálogo
        dialogoLabel = new JLabel("", SwingConstants.CENTER);
        dialogoLabel.setOpaque(true);
        dialogoLabel.setBackground(new Color(20, 20, 80));
        dialogoLabel.setForeground(Color.WHITE);
        dialogoLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        dialogoLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(genero.equals("hombre") ? Color.CYAN : Color.PINK, 4),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        dialogoLabel.setBounds((screenSize.width - 1000) / 2, screenSize.height - 250, 1000, 120);
        dialogoLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dialogoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                avanzarDialogo();
            }
        });
        layeredPane.add(dialogoLabel, JLayeredPane.PALETTE_LAYER);

        // Configurar botones de opciones
        for (int i = 0; i < botonesOpciones.length; i++) {
            botonesOpciones[i] = new JButton();
            botonesOpciones[i].setContentAreaFilled(false);
            botonesOpciones[i].setOpaque(true);
            botonesOpciones[i].setBackground(new Color(40, 40, 120));
            botonesOpciones[i].setForeground(Color.WHITE);
            botonesOpciones[i].setFont(new Font("Arial", Font.BOLD, 18));
            botonesOpciones[i].setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            layeredPane.add(botonesOpciones[i], JLayeredPane.MODAL_LAYER);
        }

        temporizador = new Timer(1000, e -> {
            dialogoLabel.setVisible(true);
            temporizador.stop();
        });
        temporizador.setInitialDelay(500);
        temporizador.start();
    }

    private void configurarComodines() {
        // Configurar imágenes INICIALES primero
        comodin5050 = new JLabel(new ImageIcon("Recursos/comodin_5050.png"));
        comodinLlamada = new JLabel(new ImageIcon("Recursos/comodin_llamada.png"));
        comodinPublico = new JLabel(new ImageIcon("Recursos/comodin_publico.png"));

        // Escalar imágenes (usa el mismo tamaño en todos lados)
        int ancho = 100, alto = 100;
        comodin5050.setIcon(escalarIcono(new ImageIcon("Recursos/comodin_5050.png"), ancho, alto));
        comodinLlamada.setIcon(escalarIcono(new ImageIcon("Recursos/comodin_llamada.png"), ancho, alto));
        comodinPublico.setIcon(escalarIcono(new ImageIcon("Recursos/comodin_publico.png"), ancho, alto));

        // Posicionar
        int yPos = 30;
        comodin5050.setBounds(50, yPos, ancho, alto);
        comodinLlamada.setBounds(160, yPos, ancho, alto);
        comodinPublico.setBounds(270, yPos, ancho, alto);

        // Configurar hover (solo para el efecto, no para la imagen inicial)
        configurarHover(comodin5050, "Recursos/comodin_5050_navegado.png");
        configurarHover(comodinLlamada, "Recursos/comodin_llamada_navegado.png");
        configurarHover(comodinPublico, "Recursos/comodin_publico_navegado.png");


        // Listeners para clicks
        comodin5050.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!comodines.isUsado5050()) {
                    usarComodin5050();
                }
            }
        });

        comodinPublico.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!comodines.isUsadoPublico()) {
                    usarComodinPublico();
                }
            }
        });

        comodinLlamada.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!comodines.isUsadoLlamada()) {
                    usarComodinLlamada();
                }
            }
        });



        // Añadir al layeredPane
        layeredPane.add(comodin5050, Integer.valueOf(2));
        layeredPane.add(comodinLlamada, Integer.valueOf(2));
        layeredPane.add(comodinPublico, Integer.valueOf(2));

        // Inicialmente ocultos (esto está bien)
        comodin5050.setVisible(false);
        comodinLlamada.setVisible(false);
        comodinPublico.setVisible(false);
    }


    private void configurarHover(JLabel comodin, String imagenHover) {
        comodin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!comodin.getIcon().toString().contains("usado")) { // No cambiar si ya está usado
                    comodin.setIcon(new ImageIcon(imagenHover));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!comodin.getIcon().toString().contains("usado")) {
                    // Volver a la imagen normal (no hover)
                    if (comodin == comodin5050) comodin.setIcon(new ImageIcon("Recursos/comodin_5050.png"));
                    else if (comodin == comodinLlamada) comodin.setIcon(new ImageIcon("Recursos/comodin_llamada.png"));
                    else if (comodin == comodinPublico) comodin.setIcon(new ImageIcon("Recursos/comodin_publico.png"));
                }
            }
        });
    }

    private void usarComodin5050() {
        if (comodines.isUsado5050()) return;

        Pregunta preguntaActual = preguntas[indicePreguntaActual];
        String[] opciones = new String[4];

        for (int i = 0; i < botonesOpciones.length; i++) {
            String texto = botonesOpciones[i].getText();
            opciones[i] = texto.substring(texto.indexOf(":") + 2).trim();
        }

        if (comodines.usar5050(opciones, preguntaActual.opciones[preguntaActual.indiceCorrecto])) {
            for (int i = 0; i < botonesOpciones.length; i++) {
                if (opciones[i].isEmpty()) {
                    botonesOpciones[i].setVisible(false);
                }
            }
            // Cambiar imagen y desactivar hover
            comodin5050.setIcon(new ImageIcon("Recursos/comodin_5050_usado.png"));
            comodin5050.removeMouseListener(comodin5050.getMouseListeners()[0]);
        }
    }

    private void usarComodinLlamada() {
        if (comodines.isUsadoLlamada()) return;

        Pregunta preguntaActual = preguntas[indicePreguntaActual];
        String[] opciones = new String[4];

        for (int i = 0; i < botonesOpciones.length; i++) {
            String texto = botonesOpciones[i].getText();
            opciones[i] = texto.substring(texto.indexOf(":") + 2).trim();
        }

        String sugerencia = comodines.usarLlamada(opciones, preguntaActual.opciones[preguntaActual.indiceCorrecto]);

        // Mostrar diálogo
        JOptionPane.showMessageDialog(this, sugerencia, "Ayuda por Llamada", JOptionPane.INFORMATION_MESSAGE);

        // Actualizar imagen y desactivar hover (IGUAL QUE EL 50:50)
        comodinLlamada.setIcon(new ImageIcon("Recursos/comodin_llamada_usado.png"));
        comodinLlamada.removeMouseListener(comodinLlamada.getMouseListeners()[0]);
    }

    private void usarComodinPublico() {
        if (comodines.isUsadoPublico()) return;

        Pregunta preguntaActual = preguntas[indicePreguntaActual];
        String[] opciones = new String[4];

        for (int i = 0; i < botonesOpciones.length; i++) {
            String texto = botonesOpciones[i].getText();
            opciones[i] = texto.substring(texto.indexOf(":") + 2).trim();
        }

        int[] porcentajes = comodines.usarPublico(opciones, preguntaActual.opciones[preguntaActual.indiceCorrecto]);

        if (porcentajes != null) {
            // Crear panel con resultados (sencillo)
            JPanel panel = new JPanel(new GridLayout(4, 1));
            for (int i = 0; i < porcentajes.length; i++) {
                JLabel resultado = new JLabel((char)('A' + i) + ": " + porcentajes[i] + "%");
                resultado.setFont(new Font("Arial", Font.BOLD, 14));
                panel.add(resultado);
            }

            JOptionPane.showMessageDialog(this, panel, "Resultado del Público", JOptionPane.PLAIN_MESSAGE);

            // Actualizar imagen y desactivar hover (IGUAL QUE EL 50:50)
            comodinPublico.setIcon(new ImageIcon("Recursos/comodin_publico_usado.png"));
            comodinPublico.removeMouseListener(comodinPublico.getMouseListeners()[0]);
        }
    }


    private void avanzarDialogo() {
        if (!enPregunta) {
            paso++;
            switch (paso) {
                case 1:
                    dialogoLabel.setText("¡Bienvenid" + (genero.equals("hombre") ? "o" : "a") +
                            " a Quién quiere ser millonario, " + nombreJugador + "!");
                    break;
                case 2:
                    dialogoLabel.setText("Te deseo la mejor de las suertes... La necesitarás...");
                    break;
                case 3:
                    dialogoLabel.setText(indicePreguntaActual == preguntas.length - 1 ?
                            "Última pregunta:" : "Primera pregunta:");
                    break;
                case 4:
                    enPregunta = true;
                    mostrarPregunta(preguntas[indicePreguntaActual]);
                    break;
            }
        }
    }

    private ImageIcon escalarIcono(ImageIcon icono, int ancho, int alto) {
        return new ImageIcon(icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH));
    }

    private void mostrarPregunta(Pregunta pregunta) {
        dialogoLabel.setBounds(250, 150, 1400, 120);
        dialogoLabel.setText(pregunta.enunciado);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int buttonWidth = 650;
        int buttonHeight = 100;
        int marginBottom = 50;
        int startY = screenSize.height - (2 * buttonHeight) - marginBottom;

        int[][] posiciones = {
                {250, startY},
                {250 + buttonWidth + 20, startY},
                {250, startY + buttonHeight + 20},
                {250 + buttonWidth + 20, startY + buttonHeight + 20}
        };

        Color colorBase = genero.equals("hombre") ? new Color(0, 80, 120) : new Color(120, 0, 80);
        Color colorBorde = genero.equals("hombre") ? Color.CYAN : Color.PINK;

        for (int i = 0; i < botonesOpciones.length; i++) {
            for (ActionListener al : botonesOpciones[i].getActionListeners()) {
                botonesOpciones[i].removeActionListener(al);
            }

            botonesOpciones[i].setText((char)('A' + i) + ": " + pregunta.opciones[i]);
            botonesOpciones[i].setBounds(posiciones[i][0], posiciones[i][1], buttonWidth, buttonHeight);
            botonesOpciones[i].setBackground(colorBase);
            botonesOpciones[i].setForeground(Color.WHITE);
            botonesOpciones[i].setFont(new Font("Arial", Font.BOLD, 18));
            botonesOpciones[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(colorBorde, 3),
                    BorderFactory.createEmptyBorder(5, 20, 5, 20)
            ));
            botonesOpciones[i].setOpaque(true);
            botonesOpciones[i].setContentAreaFilled(true);

            final int opcion = i + 1;
            botonesOpciones[i].addActionListener(e -> verificarRespuesta(pregunta, opcion));
            botonesOpciones[i].setVisible(true);
        }

        if (!comodines.isUsado5050()) {
            comodin5050.setIcon(escalarIcono(new ImageIcon("Recursos/comodin_5050.png"), 100, 100));
        }
        if (!comodines.isUsadoLlamada()) {
            comodinLlamada.setIcon(escalarIcono(new ImageIcon("Recursos/comodin_llamada.png"), 100, 100));
        }
        if (!comodines.isUsadoPublico()) {
            comodinPublico.setIcon(escalarIcono(new ImageIcon("Recursos/comodin_publico.png"), 100, 100));
        }

        // Mostrar comodines
        comodin5050.setVisible(true);
        comodinLlamada.setVisible(true);
        comodinPublico.setVisible(true);

        revalidate();
        repaint();
    }

    private void verificarRespuesta(Pregunta pregunta, int opcionSeleccionada) {
        boolean correcta = pregunta.esCorrecta(opcionSeleccionada);
        reproducirSonidoRespuesta(correcta);

        if (correcta) {
            puntuacion++;
            JOptionPane.showMessageDialog(this, "¡Respuesta correcta!");
            avanzarASiguientePregunta();
        } else {
            char letraCorrecta = (char)('A' + pregunta.indiceCorrecto);
            JOptionPane.showMessageDialog(this,
                    "Respuesta incorrecta. La respuesta correcta era la letra " + letraCorrecta + ". Fin del juego.");
            terminarJuego();
        }
    }

    private void avanzarASiguientePregunta() {
        for (JButton btn : botonesOpciones) {
            btn.setVisible(false);
        }

        comodin5050.setVisible(false);
        comodinLlamada.setVisible(false);
        comodinPublico.setVisible(false);

        if (indicePreguntaActual == preguntas.length - 1) {
            mostrarMensajeFinal();
            return;
        }

        dialogoLabel.setBounds(250, 500, 1400, 120);
        dialogoLabel.setText(indicePreguntaActual + 1 == preguntas.length - 1 ?
                "Última pregunta:" : "Siguiente pregunta:");
        dialogoLabel.setVisible(true);

        new Timer(2000, e -> {
            ((Timer)e.getSource()).stop();
            indicePreguntaActual++;
            if (indicePreguntaActual < preguntas.length) {
                enPregunta = true;
                mostrarPregunta(preguntas[indicePreguntaActual]);
            }
        }).start();
    }

    private void mostrarMensajeFinal() {
        pausarSonidoFondo();
        if (sonidoCorrecto != null) {
            sonidoCorrecto.setFramePosition(0);
            sonidoCorrecto.start();
        }

        dialogoLabel.setVisible(false);
        for (JButton btn : botonesOpciones) {
            btn.setVisible(false);
        }
        comodin5050.setVisible(false);
        comodinLlamada.setVisible(false);
        comodinPublico.setVisible(false);

        JPanel panelFinal = new JPanel();
        panelFinal.setLayout(new BorderLayout(0, 20));
        panelFinal.setOpaque(false);
        panelFinal.setBounds(350, 200, 1200, 400);

        JPanel panelMensaje = new JPanel();
        panelMensaje.setLayout(new BoxLayout(panelMensaje, BoxLayout.Y_AXIS));
        panelMensaje.setOpaque(true);
        panelMensaje.setBackground(new Color(20, 20, 80));
        panelMensaje.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(genero.equals("hombre") ? Color.CYAN : Color.PINK, 4),
                BorderFactory.createEmptyBorder(40, 60, 40, 60)
        ));

        JLabel lblFelicitaciones = new JLabel("¡FELICIDADES " + nombreJugador.toUpperCase() + "!");
        lblFelicitaciones.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblFelicitaciones.setFont(new Font("Monospaced", Font.BOLD, 42));
        lblFelicitaciones.setForeground(Color.WHITE);

        JLabel lblPremio = new JLabel("¡HAS GANADO EL MILLÓN DE DÓLARES!");
        lblPremio.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPremio.setFont(new Font("Monospaced", Font.BOLD, 36));
        lblPremio.setForeground(Color.YELLOW);

        panelMensaje.add(Box.createVerticalGlue());
        panelMensaje.add(lblFelicitaciones);
        panelMensaje.add(Box.createVerticalStrut(20));
        panelMensaje.add(lblPremio);
        panelMensaje.add(Box.createVerticalGlue());

        JButton btnSalir = new JButton("VOLVER AL MENÚ PRINCIPAL");
        btnSalir.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSalir.setMaximumSize(new Dimension(300, 60));
        btnSalir.setFont(new Font("Arial", Font.BOLD, 20));
        btnSalir.setBackground(new Color(0, 150, 0));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFocusPainted(false);
        btnSalir.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        btnSalir.addActionListener(e -> {
            layeredPane.remove(panelFinal);
            terminarJuego();
        });

        panelFinal.add(panelMensaje, BorderLayout.CENTER);
        panelFinal.add(btnSalir, BorderLayout.SOUTH);
        layeredPane.add(panelFinal, JLayeredPane.PALETTE_LAYER);

        revalidate();
        repaint();
    }

    private void terminarJuego() {
        if (sonidoFondo != null) {
            sonidoFondo.stop();
            sonidoFondo.close();
        }
        if (sonidoCorrecto != null) {
            sonidoCorrecto.stop();
            sonidoCorrecto.close();
        }
        if (sonidoIncorrecto != null) {
            sonidoIncorrecto.stop();
            sonidoIncorrecto.close();
        }

        if (puntuacion == 5) {
            Marcadores.añadirRecord(nombreJugador, dificultad, puntuacion);
        } else if (indicePreguntaActual < preguntas.length) {
            JOptionPane.showMessageDialog(this,
                    "Juego terminado. Puntuación: " + puntuacion + "/5\n" +
                            "Necesitas 5/5 para ganar el millón de dolares.");
        }

        dispose();
        new MenuPrincipal();
    }
}