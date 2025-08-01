import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        setTitle("¿Quién quiere ser millonario?");
        setDefaultCloseOperation(EXIT_ON_CLOSE);//Al cerrar la ventana, se termina el programa
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); //Para pantalla completa
        setUndecorated(true); //Quita bordes de ventana

        //Fondo
        setContentPane(new FondoPanel("Recursos/Menu.gif"));
        setLayout(new BorderLayout());

        //Panel principal para centrar contenido
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setOpaque(false);

        //Panel de botones
        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);//Fondo transparente
        panelBotones.setLayout(new GridBagLayout());

        //Restriccion de los botones
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0); //Espaciado entre botones
        gbc.fill = GridBagConstraints.HORIZONTAL;//Ocupen el espacio disponible
        gbc.weightx = 1.0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        //Crear botones
        JButton jugarBtn = crearBoton("JUGAR");
        JButton marcadoresBtn = crearBoton("MARCADORES");
        JButton salirBtn = crearBoton("SALIR");

        //Añadir botones al panel
        panelBotones.add(jugarBtn, gbc);
        panelBotones.add(marcadoresBtn, gbc);
        panelBotones.add(salirBtn, gbc);

        // Espacio superior (40% de la pantalla)
        GridBagConstraints gbcSpaceTop = new GridBagConstraints();
        gbcSpaceTop.gridy = 0;
        gbcSpaceTop.weighty = 1;
        panelPrincipal.add(Box.createVerticalGlue(), gbcSpaceTop);

        //Panel de botones (20% de la pantalla)
        GridBagConstraints gbcButtons = new GridBagConstraints();
        gbcButtons.gridy = 1;
        gbcButtons.weighty = 0.06;
        gbcButtons.anchor = GridBagConstraints.CENTER;
        panelPrincipal.add(panelBotones, gbcButtons);

        //Espacio inferior (40% de la pantalla)
        GridBagConstraints gbcSpaceBottom = new GridBagConstraints();
        gbcSpaceBottom.gridy = 2;
        gbcSpaceBottom.weighty = 0.1;
        panelPrincipal.add(Box.createVerticalGlue(), gbcSpaceBottom);

        //Añadir panel principal al frame
        add(panelPrincipal, BorderLayout.CENTER);

        //Listeners de botones
        jugarBtn.addActionListener(e -> {
            reproducirSonido("click");//Sonido de clic
            mostrarSeleccionGenero();//Mostrar seleccion de genero
        });

        marcadoresBtn.addActionListener(e -> {
            reproducirSonido("click");//sonido de clic
            Marcadores.mostrarMarcadores(MenuPrincipal.this);//Mostrar ventana de marcadores
        });
        //Listener para salir
        salirBtn.addActionListener(e -> {
            reproducirSonido("click");//Sonido de clic
            System.exit(0);//cerrar aplicacion
        });

        setVisible(true);//Hacer visible la ventana
    }
    //Metodo para seleccion del genero
    private void mostrarSeleccionGenero() {
        // Crear panel personalizado para selección de género
        JPanel panelGenero = new JPanel(new GridLayout(1, 2, 20, 20));
        panelGenero.setOpaque(false);//Hacer el fondo transparente

        //Botón para hombre
        JButton btnHombre = new JButton();
        configurarBotonGenero(btnHombre, "Recursos/hombre.png", "Recursos/hombre_cambio.png");

        //Botón para mujer
        JButton btnMujer = new JButton();
        configurarBotonGenero(btnMujer, "Recursos/mujer.png", "Recursos/mujer_cambio.png");

        //Configurar acciones ANTES de mostrar el diálogo
        btnHombre.addActionListener(ev -> {
            reproducirSonido("click");
            solicitarNombreYDificultad("hombre");
        });

        btnMujer.addActionListener(ev -> {
            reproducirSonido("click");
            solicitarNombreYDificultad("mujer");
        });
        //Agregar los botones al panel
        panelGenero.add(btnHombre);
        panelGenero.add(btnMujer);

        //Crear un JOptionPane personalizado
        JOptionPane opcion = new JOptionPane(panelGenero,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[]{}, // Sin botones adicionales
                null);

        //Crear el diálogo
        JDialog dialog = opcion.createDialog(this, "Selecciona tu género");
        dialog.setModal(true);//Bloquea otras ventanas hasta que el usuario interactue
        dialog.setVisible(true);//Mostrar el dialogo
        dialog.dispose(); //Limpiar después de cerrar
    }
    //Metodo para configurar los botones de genero
    private void configurarBotonGenero(JButton boton, String imagenNormal, String imagenCambio) {
        boton.setIcon(new ImageIcon(imagenNormal));//establece la imagen normal del boton
        boton.setBorderPainted(false);//Elimina bordes, fondo y opacidad
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setOpaque(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));//Cambia el cursos a una mano al pasar por encima

        //Efecto hover
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {//Cuando el mouse entra al boton, este cambia
                boton.setIcon(new ImageIcon(imagenCambio));
                reproducirSonido("navegacion");
            }

            @Override
            public void mouseExited(MouseEvent e) {//Cuando el mouse sale del boton, este cambia
                boton.setIcon(new ImageIcon(imagenNormal));
            }
        });
    }
    //Metodo para el nombre y la dificultad
    private void solicitarNombreYDificultad(String genero) {
        String nombre = "";
        boolean nombreValido = false;

        while (!nombreValido) {
            // Solicitar nombre del jugador
            nombre = JOptionPane.showInputDialog(this,
                    "Ingresa como quieres que te llamen:",
                    "Nombre del jugador",
                    JOptionPane.QUESTION_MESSAGE);

            // Si el usuario cancela, salir del metodo
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

        if (seleccion != null) {//si se selecciona la dificultad
            dispose();//se cierra la ventana
            new VentanaJuego(seleccion, obtenerPreguntasPorDificultad(seleccion), genero, nombre.trim());
        }
    }
    //Metodo que crea un boton personalizado
    private JButton crearBoton(String texto) {
        String ruta = "Recursos/" + texto.toLowerCase() + ".png";//convierte el texto a mayusculas
        ImageIcon iconoNormal = new ImageIcon(ruta);

        // Crea un botón con el icono normal
        JButton boton = new JButton(iconoNormal);
        boton.setBorderPainted(false);//No muestra bordes
        boton.setContentAreaFilled(false);//No muestra fondo
        boton.setFocusPainted(false);
        boton.setOpaque(false);//Es transparente
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));//Cambia el mouse por una mano
        boton.setPreferredSize(new Dimension(iconoNormal.getIconWidth(), iconoNormal.getIconHeight()));//Ajusta su tamaño al icono de la imagen

        // Opción 1: Cambiar el icono cuando el mouse está encima
        String rutaHover = "Recursos/" + texto.toLowerCase() + "_cambio.png";
        try {
            ImageIcon iconoHover = new ImageIcon(rutaHover);
            boton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {//Al pasar el mouse cambia el icono y se reproduce un sonido
                    boton.setIcon(iconoHover);
                    reproducirSonido("navegacion");
                }

                @Override
                public void mouseExited(MouseEvent e) {//Al salir del boton, vuelve a su imagen original
                    boton.setIcon(iconoNormal);
                }
            });
        } catch (Exception e) {
            // Opción 2: Efecto de escala si no hay imagen hover
            boton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {//si no existe la imagen hover, se aumenta la imagen un 10%
                    boton.setIcon(new ImageIcon(escalarImagen(iconoNormal.getImage(), 1.1f)));
                }

                @Override
                public void mouseExited(MouseEvent e) {//Al salir, vuelve a su tamaño
                    boton.setIcon(iconoNormal);
                }
            });
        }

        return boton;
    }
    //Metodo para escalar imagenes
    private Image escalarImagen(Image imagen, float factor) {
        int nuevoAncho = (int)(imagen.getWidth(null) * factor);//Calcula el nuevo ancho
        int nuevoAlto = (int)(imagen.getHeight(null) * factor);//Calcula el nuevo alto
        return imagen.getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);//Retorna una nueva imagen escala al nuevo tamaño
    }
    //Metodo para reproducir sonido
    private void reproducirSonido(String tipo) {
        String rutaSonido;//Guarda la ruta del archivo a reproducir
        if (tipo.equals("navegacion")) {//Si es de tipo navegacion
            rutaSonido = "Recursos/navegacion.wav";//se asigna la ruta del sonido de navegacion
        } else {//Para cualquier otro
            rutaSonido = "Recursos/click.wav";//Se asigna la ruta del sonido del click
        }
        ReproductorSonido.reproducir(rutaSonido);//Llama el metodo para reproducir el metodo correspondiente
    }
    //Metodo para obtener las preguntas por dificultad
    private Pregunta[] obtenerPreguntasPorDificultad(String dificultad) {
        if (dificultad.equalsIgnoreCase("Fácil")) {//Comprueba si la dificultad es facil
            return PreguntasFaciles.getPreguntas();//Retorna el arreglo de las pregunts faciles
        } else if (dificultad.equalsIgnoreCase("Media")) {//Comprueba si la dificultad es media
            return PreguntasMedias.getPreguntas();//Retorna el arreglo de las pregutas medias
        } else  {//Para cualquier otro valor, retorna el arreglo con las preguntas dificiles
            return PreguntasDificiles.getPreguntas();
        }
    }
}