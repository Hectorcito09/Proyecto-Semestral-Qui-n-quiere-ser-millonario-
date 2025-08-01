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
    //Los atributos de la clase

    public VentanaJuego(String dificultad, Pregunta[] preguntasOriginal, String genero, String nombreJugador) {//Constructor
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
        setExtendedState(JFrame.MAXIMIZED_BOTH);//Pantalla completa
        setUndecorated(true);// Sin bordes ni botones de ventana
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        iniciarComponentes();
        setVisible(true);//Mostrar la ventana
        cargarSonidos();//Carga los sonidos
        reproducirSonidoFondo();//Reproduce los sonidos
    }
    //Metodo para cargar los sonidos
    private void cargarSonidos() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    new File("Recursos/plenafondo.wav").getAbsoluteFile());//Sonido de fondo
            sonidoFondo = AudioSystem.getClip();//Crea un objeto que puede reproducir la musica, pausarla o reproducirla
            sonidoFondo.open(audioInputStream);//carga el audio
            FloatControl gainControl = (FloatControl) sonidoFondo.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-10.0f);//Baja el volumen del juego mientras juegas

            audioInputStream = AudioSystem.getAudioInputStream(
                    new File("Recursos/correcto.wav").getAbsoluteFile());//Sonido de respuesta correcta
            sonidoCorrecto = AudioSystem.getClip();//Crea un objeto que puede reproducir la musica, pausarla o reproducirla
            sonidoCorrecto.open(audioInputStream);//Cargar el audio

            audioInputStream = AudioSystem.getAudioInputStream(
                    new File("Recursos/incorrecto.wav").getAbsoluteFile());//Sonido de respuesta incorrecta
            sonidoIncorrecto = AudioSystem.getClip();//Crea un objeto que puede reproducir la musica, pausarla o reproducirla
            sonidoIncorrecto.open(audioInputStream);//Cargar el audio

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar los sonidos");
        }//Las excepciones del try catch
    }
    //Metodo para reproducir los sonidos de fondo
    private void reproducirSonidoFondo() {
        if (sonidoFondo != null) {//verifica que el sonido este cargado
            sonidoFondo.loop(Clip.LOOP_CONTINUOUSLY);//Si lo esta, lo reproduce en bucle
        }
    }
    //Metodo para pausar los sonidos de fondo
    private void pausarSonidoFondo() {
        if (sonidoFondo != null && sonidoFondo.isRunning()) {
            sonidoFondo.stop();//Si la musica de fondo esta en reproduccion, la pausa
        }
    }
    //Metodos para reanudar los sonidos de fondo
    private void reanudarSonidoFondo() {
        if (sonidoFondo != null && !sonidoFondo.isRunning()) {
            sonidoFondo.loop(Clip.LOOP_CONTINUOUSLY);//Si la musica de fondo esta en pausa, la reanuda
        }
    }
    //Metodo para reproducir los sonidos de respuestas
    private void reproducirSonidoRespuesta(boolean correcta) {
        pausarSonidoFondo();//pausa la musica

        Clip sonido = correcta ? sonidoCorrecto : sonidoIncorrecto; //Si el parametro es correcto, reproduce el sonidoCorrecto
        if (sonido != null) {  //Comprueba que no sea Null                                     //Si es falso, reproduce sonidoIncorrecto
            sonido.setFramePosition(0);//Reinicia el sonido
            LineListener listener = new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        reanudarSonidoFondo();//Si detecta que el sonido se detuvo, reanuda el sonidoFondo
                        sonido.removeLineListener(this);
                    }
                }
            };
            sonido.addLineListener(listener);//Añade el sonido
            sonido.start();//reproduce el sonido
        }
    }
    //Metodo para iniciar los componentes
    private void iniciarComponentes() {
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        setContentPane(layeredPane);//Se establece como el contenido principal

        // Configurar fondo
        String fondoPath = genero.equals("hombre") ? "Recursos/juegohombre.png" : "Recursos/juegomujer.png";//El fondo cambia segun el genero
        fondoLabel = new JLabel(new ImageIcon(fondoPath));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();//Obtiene el tamaño de la pantalla
        Image img = ((ImageIcon)fondoLabel.getIcon()).getImage()
                .getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_SMOOTH);
        fondoLabel.setIcon(new ImageIcon(img));
        fondoLabel.setBounds(0, 0, screenSize.width, screenSize.height);
        layeredPane.add(fondoLabel, JLayeredPane.DEFAULT_LAYER);//Se posiciona en la capa base

        // Configurar comodines
        configurarComodines();//Agrega los comodines

        // Configurar área de diálogo
        dialogoLabel = new JLabel("", SwingConstants.CENTER);//Muestra el texto
        dialogoLabel.setOpaque(true);
        dialogoLabel.setBackground(new Color(20, 20, 80));
        dialogoLabel.setForeground(Color.WHITE);
        dialogoLabel.setFont(new Font("Monospaced", Font.BOLD, 24));//Se define el fondo, color de texto y fuente
        dialogoLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(genero.equals("hombre") ? Color.CYAN : Color.PINK, 4),//Crea un borde que varia segun el genero
                BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        dialogoLabel.setBounds((screenSize.width - 1000) / 2, screenSize.height - 250, 1000, 120);
        dialogoLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));//El cursor cambia a una mano
        dialogoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                avanzarDialogo();
            }
        });//El click llama a avanzarDialogo()
        layeredPane.add(dialogoLabel, JLayeredPane.PALETTE_LAYER);//Se añade a una capa mas alta

        // Configurar botones de opciones, este apartado ajusta varios estilos de
        //los botones
        for (int i = 0; i < botonesOpciones.length; i++) {
            botonesOpciones[i] = new JButton();
            botonesOpciones[i].setContentAreaFilled(false);
            botonesOpciones[i].setOpaque(true);
            botonesOpciones[i].setBackground(new Color(40, 40, 120));//Color del fondo
            botonesOpciones[i].setForeground(Color.WHITE);
            botonesOpciones[i].setFont(new Font("Arial", Font.BOLD, 18));//tamaño y fuente de la letra
            botonesOpciones[i].setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            layeredPane.add(botonesOpciones[i], JLayeredPane.MODAL_LAYER);//Se muestran en una capa superior al fondo
        }

        temporizador = new Timer(1000, e -> {//Se crea un temporizador de 1 segundo
            dialogoLabel.setVisible(true);//Hace visible el bloque de texto
            temporizador.stop();//Detiene el temporizador
        });
        temporizador.setInitialDelay(500);//Un delay de 0.5 segundos para poder cargar el fondo y demas aspectos
        temporizador.start();//Incia el temporizador
    }
    //Metodo para configurar los comodines
    private void configurarComodines() {
        //Crea 3 objetos y les asigna imagenes
        comodin5050 = new JLabel(new ImageIcon("Recursos/comodin_5050.png"));//Imagen de 5050
        comodinLlamada = new JLabel(new ImageIcon("Recursos/comodin_llamada.png"));//Imagen de llamada
        comodinPublico = new JLabel(new ImageIcon("Recursos/comodin_publico.png"));//Imagen de publico

        // Escalar imágenes
        int ancho = 100, alto = 100;//define el ancho y alto de las imagenes
        comodin5050.setIcon(escalarIcono(new ImageIcon("Recursos/comodin_5050.png"), ancho, alto));//Aplica la escala a cada comodin
        comodinLlamada.setIcon(escalarIcono(new ImageIcon("Recursos/comodin_llamada.png"), ancho, alto));//Aplica la escala a cada comodin
        comodinPublico.setIcon(escalarIcono(new ImageIcon("Recursos/comodin_publico.png"), ancho, alto));//Aplica la escala a cada comodin

        // Posiciona los tres comodines
        int yPos = 30;//La altura vertical del comodin
        comodin5050.setBounds(50, yPos, ancho, alto);//Define la separacion entre estos
        comodinLlamada.setBounds(160, yPos, ancho, alto);//Define la separacion entre estos
        comodinPublico.setBounds(270, yPos, ancho, alto);//Define la separacion entre estos

        // Configurar hover
        configurarHover(comodin5050, "Recursos/comodin_5050_navegado.png");//Si el mouse pasa por encima, la imagen cambia
        configurarHover(comodinLlamada, "Recursos/comodin_llamada_navegado.png");//Si el mouse pasa por encima, la imagen cambia
        configurarHover(comodinPublico, "Recursos/comodin_publico_navegado.png");//Si el mouse pasa por encima, la imagen cambia


        // Listeners para clicks(verifica si estan disponibles)
        comodin5050.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!comodines.isUsado5050()) {// AL hacer click verifica si fue usado
                    usarComodin5050();//Si esta disponible, llama al metodo
                }
            }
        });

        comodinPublico.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!comodines.isUsadoPublico()) {// AL hacer click verifica si fue usado
                    usarComodinPublico();//Si esta disponible, llama al metodo
                }
            }
        });

        comodinLlamada.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!comodines.isUsadoLlamada()) {// AL hacer click verifica si fue usado
                    usarComodinLlamada();//Si esta disponible, llama al metodo
                }
            }
        });



        // Añadir al layeredPane
        layeredPane.add(comodin5050, Integer.valueOf(2));//Añade el comodin a la capa 2
        layeredPane.add(comodinLlamada, Integer.valueOf(2));//Añade el comodin a la capa 2
        layeredPane.add(comodinPublico, Integer.valueOf(2));//Añade el comodin a la capa 2

        // Inicialmente los comodines estan oculto,
        comodin5050.setVisible(false);
        comodinLlamada.setVisible(false);
        comodinPublico.setVisible(false);
    }
    //Metodo para configurar el Hover
    private void configurarHover(JLabel comodin, String imagenHover) {
        comodin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {//Se activa cuando el mouse entra al area del comodin
                if (!comodin.getIcon().toString().contains("usado")) { // No cambia si ya está usado el comodin
                    comodin.setIcon(new ImageIcon(imagenHover));//Si no es asi, cambia la version de hover
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {//Se activa cuando el mouse sale del area del comodin
                if (!comodin.getIcon().toString().contains("usado")) {
                    //Si el comodin no ha sido usado, vuelve a la imagen original
                    if (comodin == comodin5050) comodin.setIcon(new ImageIcon("Recursos/comodin_5050.png"));
                    else if (comodin == comodinLlamada) comodin.setIcon(new ImageIcon("Recursos/comodin_llamada.png"));
                    else if (comodin == comodinPublico) comodin.setIcon(new ImageIcon("Recursos/comodin_publico.png"));
                }
            }
        });
    }
    //Metodo para usar el comodin 50/50
    private void usarComodin5050() {
        if (comodines.isUsado5050()) return;//Verifica si fue usado

        Pregunta preguntaActual = preguntas[indicePreguntaActual];//Obtiene la pregunta actual
        String[] opciones = new String[4];//Se crea un arreglo vacio de 4 opciones

        //Extra el texto
        for (int i = 0; i < botonesOpciones.length; i++) {
            String texto = botonesOpciones[i].getText();//Obtiene el texto completo
            opciones[i] = texto.substring(texto.indexOf(":") + 2).trim();//Extrae la parte luego del : y lo guarda en un arreglo
        }
        //Llama el metodo usar5050 de la clase comodines pasandole el arreglo de las opciones actuales y la opcion correcta
        if (comodines.usar5050(opciones, preguntaActual.opciones[preguntaActual.indiceCorrecto])) {
            for (int i = 0; i < botonesOpciones.length; i++) {//recorre los botones
                if (opciones[i].isEmpty()) {
                    botonesOpciones[i].setVisible(false);//si estan vacio, los oculta
                }
            }
            // Cambiar imagen y desactivar hover
            comodin5050.setIcon(new ImageIcon("Recursos/comodin_5050_usado.png"));
            comodin5050.removeMouseListener(comodin5050.getMouseListeners()[0]);
        }
    }
    //Metodo par usar el comodin de llamada
    private void usarComodinLlamada() {
        if (comodines.isUsadoLlamada()) return;//Verifica si fue usado

        Pregunta preguntaActual = preguntas[indicePreguntaActual];//Obtiene la pregunta actual
        String[] opciones = new String[4];//Se crea un arreglo vacio de 4 opciones

        for (int i = 0; i < botonesOpciones.length; i++) {
            String texto = botonesOpciones[i].getText();//Obtiene el texto completo
            opciones[i] = texto.substring(texto.indexOf(":") + 2).trim();//Extrae la parte luego del : y lo guarda en un arreglo
        }
        //Llama el metodo de la clase comodines pasandole las 4 opciones actuales y la correcta
        String sugerencia = comodines.usarLlamada(opciones, preguntaActual.opciones[preguntaActual.indiceCorrecto]);

        // Mostrar diálogo
        JOptionPane.showMessageDialog(this, sugerencia, "Ayuda por Llamada", JOptionPane.INFORMATION_MESSAGE);

        // Actualizar imagen y desactivar hover
        comodinLlamada.setIcon(new ImageIcon("Recursos/comodin_llamada_usado.png"));
        comodinLlamada.removeMouseListener(comodinLlamada.getMouseListeners()[0]);
    }
    //Metodo para usar el comodin del publico
    private void usarComodinPublico() {
        if (comodines.isUsadoPublico()) return;//Verifica si fue usado

        Pregunta preguntaActual = preguntas[indicePreguntaActual];//Obtiene la pregunta actual
        String[] opciones = new String[4];//Se crea un arreglo vacio de 4 opciones

        for (int i = 0; i < botonesOpciones.length; i++) {
            String texto = botonesOpciones[i].getText();//Obtiene el texto completo
            opciones[i] = texto.substring(texto.indexOf(":") + 2).trim();//Extrae la parte luego del : y lo guarda en un arreglo
        }
        //Llama el metodo usarPublico del objeto comodines, que debvuelve un arreglo con los porcentajes
        int[] porcentajes = comodines.usarPublico(opciones, preguntaActual.opciones[preguntaActual.indiceCorrecto]);

        if (porcentajes != null) {//verifica los resultados
            // Crear panel con resultados (sencillo)
            JPanel panel = new JPanel(new GridLayout(4, 1));//crea 4 filas y una columna
            for (int i = 0; i < porcentajes.length; i++) {
                JLabel resultado = new JLabel((char)('A' + i) + ": " + porcentajes[i] + "%");
                resultado.setFont(new Font("Arial", Font.BOLD, 14));//fuente y tamaño de la letra
                panel.add(resultado);//Para cada porcentaje, crea una etiqueta (A,B,C,D)
            }
            //Muestra el resultado del publico
            JOptionPane.showMessageDialog(this, panel, "Resultado del Público", JOptionPane.PLAIN_MESSAGE);

            // Actualizar imagen y desactivar hover
            comodinPublico.setIcon(new ImageIcon("Recursos/comodin_publico_usado.png"));
            comodinPublico.removeMouseListener(comodinPublico.getMouseListeners()[0]);
        }
    }
    //Metodo para avanzar el dialogo
    private void avanzarDialogo() {//gestiona el avanze del dialogo
        if (!enPregunta) {//verifica que no se este mostrando la pregunta
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
            }//Por cada paso, muestra un mensaje diferente
        }
    }
    //Este metodo devuelve una escala de un icono utilizando un suavizado
    private ImageIcon escalarIcono(ImageIcon icono, int ancho, int alto) {
        return new ImageIcon(icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH));
    }
    //Metodo para mostrar las preguntas
    private void mostrarPregunta(Pregunta pregunta) {
        dialogoLabel.setBounds(250, 150, 1400, 120);
        dialogoLabel.setText(pregunta.enunciado);//Muestra el texto de la pregunta en la posicion estipulada

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();//Obtiene el tamaño de la pantalla
        int buttonWidth = 650;
        int buttonHeight = 100;
        int marginBottom = 50;
        int startY = screenSize.height - (2 * buttonHeight) - marginBottom;

        int[][] posiciones = {//Matriz que indica las coordenadas de los botones
                {250, startY},
                {250 + buttonWidth + 20, startY},
                {250, startY + buttonHeight + 20},
                {250 + buttonWidth + 20, startY + buttonHeight + 20}
        };

        //Dependiendo del genero, se eligen diferentes colores de fondo y borde
        Color colorBase = genero.equals("hombre") ? new Color(0, 80, 120) : new Color(120, 0, 80);
        Color colorBorde = genero.equals("hombre") ? Color.CYAN : Color.PINK;

        for (int i = 0; i < botonesOpciones.length; i++) {//Configurar cada boton de opcion
            for (ActionListener al : botonesOpciones[i].getActionListeners()) {
                botonesOpciones[i].removeActionListener(al);//elimina los actionListener antiguos
            }

            botonesOpciones[i].setText((char)('A' + i) + ": " + pregunta.opciones[i]);//Asignar el texto del boton
            botonesOpciones[i].setBounds(posiciones[i][0], posiciones[i][1], buttonWidth, buttonHeight);//posicion del boton
            botonesOpciones[i].setBackground(colorBase);//Color del fondo
            botonesOpciones[i].setForeground(Color.WHITE);//Color del texto
            botonesOpciones[i].setFont(new Font("Arial", Font.BOLD, 18));//Fuente y tamaño de la letra
            botonesOpciones[i].setBorder(BorderFactory.createCompoundBorder(//crea un borde
                    BorderFactory.createLineBorder(colorBorde, 3),
                    BorderFactory.createEmptyBorder(5, 20, 5, 20)
            ));
            botonesOpciones[i].setOpaque(true);//Muestra el color del fondo
            botonesOpciones[i].setContentAreaFilled(true);//Rellena el boton con el color de fondo

            final int opcion = i + 1;//Se guarda el numero seleccionado en opcion
            botonesOpciones[i].addActionListener(e -> verificarRespuesta(pregunta, opcion));//LLama el metodo verificarRespuesta cuando el usuario hace click en el boton
            botonesOpciones[i].setVisible(true);//Asegura que el boton sea visible
        }

        if (!comodines.isUsado5050()) {//Si el comodin no se ha usado, se le asigna su imagen original con su escala
            comodin5050.setIcon(escalarIcono(new ImageIcon("Recursos/comodin_5050.png"), 100, 100));
        }
        if (!comodines.isUsadoLlamada()) {//Si el comodin no se ha usado, se le asigna su imagen original con su escala
            comodinLlamada.setIcon(escalarIcono(new ImageIcon("Recursos/comodin_llamada.png"), 100, 100));
        }
        if (!comodines.isUsadoPublico()) {//Si el comodin no se ha usado, se le asigna su imagen original con su escala
            comodinPublico.setIcon(escalarIcono(new ImageIcon("Recursos/comodin_publico.png"), 100, 100));
        }

        //Mostrar comodines
        comodin5050.setVisible(true);
        comodinLlamada.setVisible(true);
        comodinPublico.setVisible(true);

        //actualiza la interfaz grafica
        revalidate();
        repaint();
    }
    //Metodo para verificar las respuestas
    private void verificarRespuesta(Pregunta pregunta, int opcionSeleccionada) {
        boolean correcta = pregunta.esCorrecta(opcionSeleccionada);//Este metodo se ejecuta cuando el jugar elige una respuesta y el resultado se guarda en correcta
        reproducirSonidoRespuesta(correcta);//Se reproduce el sonido dependiendo si es correcta o no

        if (correcta) {//Si la opcion es correcta
            puntuacion++;//Se incrementa el puntaje
            JOptionPane.showMessageDialog(this, "¡Respuesta correcta!");//Se muestra un cuadro de dialogo
            avanzarASiguientePregunta();//Y llama al metodo avanzarSiguientePregunta para mostrar la siguiente
        } else {//Si la respuesta es incorrecta
            char letraCorrecta = (char)('A' + pregunta.indiceCorrecto);//Obtiene la letra correspondiente a la opcion correcta
            JOptionPane.showMessageDialog(this,
                    "Respuesta incorrecta. La respuesta correcta era la letra " + letraCorrecta + ". Fin del juego.");//Muestra que la opcion elegida era incorrecta y la que es la correcta
            terminarJuego();//Llama al metodo terminarJuego y lo finaliza
        }
    }
    //Metodo para verificar las respuestas
    private void avanzarASiguientePregunta() {
        for (JButton btn : botonesOpciones) {
            btn.setVisible(false);//Oculta los botones de opciones para limpiarlos antes de la siguiente pregunta
        }

        //Oculta los botones de comodines
        comodin5050.setVisible(false);
        comodinLlamada.setVisible(false);
        comodinPublico.setVisible(false);

        if (indicePreguntaActual == preguntas.length - 1) {
            mostrarMensajeFinal();//Si llego al final de las preguntas, se muestra el mensaje final
            return;
        }

        //Muestra un mensaje de transicion, dependiendo del punto de la partida
        dialogoLabel.setBounds(250, 500, 1400, 120);
        dialogoLabel.setText(indicePreguntaActual + 1 == preguntas.length - 1 ?
                "Última pregunta:" : "Siguiente pregunta:");
        dialogoLabel.setVisible(true);

        //Se crea un timer de 2 segundos entre preguntas para dar una pausa visual
        new Timer(2000, e -> {
            ((Timer)e.getSource()).stop();//Se detine luego de los 2 segundos
            indicePreguntaActual++;//Aumenta el indice de preguntaActual
            if (indicePreguntaActual < preguntas.length) {//Si aun quedan preguntas, se llama al metodo mostrarPreguntas()
                enPregunta = true;
                mostrarPregunta(preguntas[indicePreguntaActual]);
            }
        }).start();//inicia el temporizador
    }
    //Metodo para avanzar a la siguiente pregunta
    private void mostrarMensajeFinal() {
        pausarSonidoFondo();//Pausa el sonido de fondo
        if (sonidoCorrecto != null) {
            sonidoCorrecto.setFramePosition(0);//Reinicia el audio desde el inicio
            sonidoCorrecto.start();//Reproduce el sonido de respuesta correcta
        }

        dialogoLabel.setVisible(false);//Oculta el texto donde aparece la pregunta
        for (JButton btn : botonesOpciones) {
            btn.setVisible(false);//Oculta todos los botones de opciones
        }
        //Oculta los comodines
        comodin5050.setVisible(false);
        comodinLlamada.setVisible(false);
        comodinPublico.setVisible(false);

        JPanel panelFinal = new JPanel();//Crea un nuevo panel final
        panelFinal.setLayout(new BorderLayout(0, 20));//Distribuye los elementos
        panelFinal.setOpaque(false);//Fondo no visible
        panelFinal.setBounds(350, 200, 1200, 400);//La ubicacion y tamaño

        JPanel panelMensaje = new JPanel();//Crea el panel que contiene el mensaje
        panelMensaje.setLayout(new BoxLayout(panelMensaje, BoxLayout.Y_AXIS));
        panelMensaje.setOpaque(true);//Fondo visible
        panelMensaje.setBackground(new Color(20, 20, 80));//El color (Azul)
        panelMensaje.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(genero.equals("hombre") ? Color.CYAN : Color.PINK, 4),
                BorderFactory.createEmptyBorder(40, 60, 40, 60)//Borde de diferente color dependiendo del genero
        ));

        //Mensajes de felicitacion y premio
        JLabel lblFelicitaciones = new JLabel("¡FELICIDADES " + nombreJugador.toUpperCase() + "!");
        lblFelicitaciones.setAlignmentX(Component.CENTER_ALIGNMENT);//Alineacion centrada
        lblFelicitaciones.setFont(new Font("Monospaced", Font.BOLD, 42));//Fuente y tamaño de la letra
        lblFelicitaciones.setForeground(Color.WHITE);

        JLabel lblPremio = new JLabel("¡HAS GANADO EL MILLÓN DE DÓLARES!");
        lblPremio.setAlignmentX(Component.CENTER_ALIGNMENT);//Alineacion centrada
        lblPremio.setFont(new Font("Monospaced", Font.BOLD, 36));//Fuente y tamaño de la letra
        lblPremio.setForeground(Color.YELLOW);

        panelMensaje.add(Box.createVerticalGlue());//Se centra verticalmente
        panelMensaje.add(lblFelicitaciones);
        panelMensaje.add(Box.createVerticalStrut(20));//Crea un espacio entre los mensajes
        panelMensaje.add(lblPremio);
        panelMensaje.add(Box.createVerticalGlue());

        //crear boton para salir al menu
        JButton btnSalir = new JButton("VOLVER AL MENÚ PRINCIPAL");
        btnSalir.setAlignmentX(Component.CENTER_ALIGNMENT);//Se centra
        btnSalir.setMaximumSize(new Dimension(300, 60));//Tamaño maximo
        btnSalir.setFont(new Font("Arial", Font.BOLD, 20));//Fuente y tamaño de letra
        btnSalir.setBackground(new Color(0, 150, 0));//fondo
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFocusPainted(false);
        btnSalir.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),//Borde blanco de 2 pixeles
                BorderFactory.createEmptyBorder(10, 25, 10, 25)//relleno de 10 arriba/abajo y 25 a los lados
        ));
        btnSalir.addActionListener(e -> {
            layeredPane.remove(panelFinal);
            terminarJuego();//Al hacer click, se elimina el panel final y se llama al metodo terminarJuego
        });

        panelFinal.add(panelMensaje, BorderLayout.CENTER);//Los mensajes se colocan en el centro del panel
        panelFinal.add(btnSalir, BorderLayout.SOUTH);
        layeredPane.add(panelFinal, JLayeredPane.PALETTE_LAYER);//Se añade el panelFinal a una capa superior

        //Actuzalicar la interfaz grafica
        revalidate();//recalcula su diseño
        repaint();//Actualiza la ventana
    }
    //Metodo para terminar el juego
    private void terminarJuego() {
        if (sonidoFondo != null) {//si el sonido sigue
            sonidoFondo.stop();//Detiene el sonido
            sonidoFondo.close();//Libera el sonido de la memoria
        }
        if (sonidoCorrecto != null) {//si el sonido sigue
            sonidoCorrecto.stop();//Detiene el sonido
            sonidoCorrecto.close();//Libera el sonido de la memoria
        }
        if (sonidoIncorrecto != null) {//si el sonido sigue
            sonidoIncorrecto.stop();//Detiene el sonido
            sonidoIncorrecto.close();//Libera el sonido de la memoria
        }

        if (puntuacion == 5) {
            Marcadores.añadirRecord(nombreJugador, dificultad, puntuacion);//Guarda la puntacion si fue perfecta
        } else if (indicePreguntaActual < preguntas.length) {//Si no gano, se muestra su puntuacion
            JOptionPane.showMessageDialog(this,
                    "Juego terminado. Puntuación: " + puntuacion + "/5\n" +
                            "Necesitas 5/5 para ganar el millón de dolares.");
        }

        dispose();//Cierra la ventana acutal sin finalizar la aplicacion
        new MenuPrincipal();//Una nueva instancia del menu principal
    }
}