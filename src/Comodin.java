public class Comodin {
    private boolean usado5050;
    private boolean usadoLlamada;
    private boolean usadoPublico;

    public Comodin() {
        usado5050 = false;
        usadoLlamada = false;
        usadoPublico = false;
    }

    public boolean usar5050(String[] opciones, String correcta) {
        if (usado5050) return false;

        int eliminados = 0;
        for (int i = 0; i < opciones.length; i++) {
            if (!opciones[i].equals(correcta) && eliminados < 2) {
                opciones[i] = "";  // O se puede usar null
                eliminados++;
            }
        }

        usado5050 = true;
        return true;
    }

    public String usarLlamada(String[] opciones, String correcta) {
        if (usadoLlamada) return "Ya usaste este comodín.";
        usadoLlamada = true;

        if (Math.random() < 0.7) {
            return "Creo que la respuesta correcta es: " + correcta;
        } else {
            for (String op : opciones) {
                if (!op.equals(correcta) && !op.isEmpty()) {
                    return "Mmm... podría ser: " + op;
                }
            }
        }
        return "No sabría decirte.";
    }

    public int[] usarPublico(String[] opciones, String correcta) {
        if (usadoPublico) return null;
        usadoPublico = true;

        int[] porcentajes = new int[4];
        int indiceCorrecto = -1;

        // Encontrar índice de la respuesta correcta
        for (int i = 0; i < opciones.length; i++) {
            if (opciones[i].equals(correcta)) {
                indiceCorrecto = i;
                break;
            }
        }

        if (indiceCorrecto == -1) return null;

        // Asignar porcentaje mayor a la correcta (60-80%)
        porcentajes[indiceCorrecto] = 60 + (int)(Math.random() * 21);
        int restante = 100 - porcentajes[indiceCorrecto];

        // Distribuir el resto entre las otras opciones
        for (int i = 0; i < porcentajes.length; i++) {
            if (i != indiceCorrecto) {
                int porcentaje = (int)(Math.random() * restante / 2) + 5;
                porcentaje = Math.min(porcentaje, restante);
                porcentajes[i] = porcentaje;
                restante -= porcentaje;
            }
        }

        // Ajustar por posibles redondeos
        if (restante > 0) {
            porcentajes[indiceCorrecto] += restante;
        }

        return porcentajes;
    }

    public boolean isUsado5050() { return usado5050; }
    public boolean isUsadoLlamada() { return usadoLlamada; }
    public boolean isUsadoPublico() { return usadoPublico; }
}
