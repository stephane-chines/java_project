package tennis.erreurs;

/**
 * Exception levée lorsqu'une saisie utilisateur n'est pas valide
 * (choix de menu, index ou format incorrect).
 */
public class SaisieInvalideException extends Exception 
{
    public SaisieInvalideException(String message) 
    {
        super(message);
    }
}

