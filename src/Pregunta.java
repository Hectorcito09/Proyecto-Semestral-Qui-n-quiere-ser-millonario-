public class Pregunta {
    String enunciado;
    String[] opciones;
    int indiceCorrecto;

    public Pregunta(String enunciado, String[] opciones, int indiceCorrecto) {
        this.enunciado = enunciado;
        this.opciones = opciones;
        this.indiceCorrecto = indiceCorrecto;
    }


    public boolean esCorrecta(int respuestaUsuario) {
        return respuestaUsuario - 1 == indiceCorrecto;
    }
}