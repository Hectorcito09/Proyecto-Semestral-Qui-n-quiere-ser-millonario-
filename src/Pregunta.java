//Clase pregunta con multiples opciones y una correcta
public class Pregunta {
    String enunciado;//Contiene la pregunta
    String[] opciones;//Contiene las opciones multiplces de la pregunta
    int indiceCorrecto;//Contiene el indice de la respuesta correcta

    public Pregunta(String enunciado, String[] opciones, int indiceCorrecto) {//Constructor
        this.enunciado = enunciado;//Recibe el texto de la pregunta
        this.opciones = opciones;//recibe las opciones de la pregunta
        this.indiceCorrecto = indiceCorrecto;//Recibe el indice de la respuesta correcta
    }

    //Metodo para verificar la respuesta del usuario
    public boolean esCorrecta(int respuestaUsuario) {
        return respuestaUsuario - 1 == indiceCorrecto;//A la respuesta del usuario se le resta -1 y lo igualamos al indice correcto
    }
}