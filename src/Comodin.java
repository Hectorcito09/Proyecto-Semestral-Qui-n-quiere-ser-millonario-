public class Comodin {
    private boolean usado5050;
    private boolean usadoLlamada;
    private boolean usadoPublico;

    // Al iniciar, ningún comodín ha sido usado
    public Comodin() {
        usado5050 = false;
        usadoLlamada = false;
        usadoPublico = false;
    }

    // Este método elimina dos respuestas incorrectas (como el comodín 50/50 en el juego real)
    public boolean usar5050(String[] opciones, String correcta) {
        if (usado5050) return false;

        int eliminados = 0;
        for (int i = 0; i < opciones.length; i++) {
            // Recorre las opciones y borra dos que no sean la correcta
            if (!opciones[i].equals(correcta) && eliminados < 2) {
                opciones[i] = "";  // Aquí se "elimina" visualmente la opción
                eliminados++;
            }
        }

        usado5050 = true;  // Ya no se puede volver a usar
        return true;
    }

    // Simula llamar a alguien para que te diga cuál cree que es la respuesta
    public String usarLlamada(String[] opciones, String correcta) {
        if (usadoLlamada) return "Ya usaste este comodín.";
        usadoLlamada = true;

        // 70% de probabilidad de decir la correcta, el resto puede fallar
        if (Math.random() < 0.7) {
            return "Creo que la respuesta correcta es: " + correcta;
        } else {
            for (String op : opciones) {
                // Busca una opción que no sea la correcta (y que no esté vacía)
                if (!op.equals(correcta) && !op.isEmpty()) {
                    return "Mmm... podría ser: " + op;
                }
            }
        }
        return "No sabría decirte.";
    }

    // El clásico comodín del público. Devuelve un arreglo con porcentajes.
    public int[] usarPublico(String[] opciones, String correcta) {
        if (usadoPublico) return null;
        usadoPublico = true;

        int[] porcentajes = new int[4];
        int indiceCorrecto = -1;

        // Encuentra en qué posición está la respuesta correcta
        for (int i = 0; i < opciones.length; i++) {
            if (opciones[i].equals(correcta)) {
                indiceCorrecto = i;
                break;
            }
        }

        if (indiceCorrecto == -1) return null;

        // Al público le damos entre 60% y 80% de chance de elegir la correcta
        porcentajes[indiceCorrecto] = 60 + (int)(Math.random() * 21);
        int restante = 100 - porcentajes[indiceCorrecto];

        // Reparte lo que queda entre las demás opciones
        for (int i = 0; i < porcentajes.length; i++) {
            if (i != indiceCorrecto) {
                int porcentaje = (int)(Math.random() * restante / 2) + 5;
                porcentaje = Math.min(porcentaje, restante);
                porcentajes[i] = porcentaje;
                restante -= porcentaje;
            }
        }

        // Ajusta por si sobró algo por redondeo
        if (restante > 0) {
            porcentajes[indiceCorrecto] += restante;
        }

        return porcentajes;
    }

    // Estos son los getters para saber si un comodín ya se usó
    public boolean isUsado5050() { return usado5050; }
    public boolean isUsadoLlamada() { return usadoLlamada; }
    public boolean isUsadoPublico() { return usadoPublico; }
}