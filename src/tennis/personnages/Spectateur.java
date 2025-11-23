package tennis.personnages;

import java.time.LocalDate;

/**
 * Spectateur d'un match: billet/tribune/place et petites actions d'ambiance (applaudir, crier, huer, dormir).
 * Différenciation homme/femme (chemise vs lunettes).
 */
public class Spectateur extends Personne implements ActionsSpectateur
{
    // Attributs spécifiques à un spectateur.
    private final double prixBillet;
    private final String nomTribune;
    private final int numeroPlace;
    
    // Attributs qui dépendent du genre du spectateur.
    private String couleurChemise; 
    private final boolean porteLunettes; 

    // Constructeur pour créer un nouveau spectateur.
    public Spectateur(String nomNaissance, String prenom, Genre genre, LocalDate dateNaissance, String lieuNaissance,
                        double prixBillet, String nomTribune, int numeroPlace) 
    {
        super(nomNaissance, prenom, genre, dateNaissance, lieuNaissance);
        this.prixBillet = prixBillet;
        this.nomTribune = nomTribune;
        this.numeroPlace = numeroPlace;
        
        // On initialise les attributs spécifiques au genre à des valeurs par défaut.
        if (genre == Genre.HOMME) 
        {
            this.couleurChemise = "inconnue";
            this.porteLunettes = false;
        } 
        else 
        {
            this.couleurChemise = null; // Une femme n'a pas de couleur de chemise distinctive
            this.porteLunettes = true; // Par défaut, on suppose qu'elle est reconnue par ses lunettes
        }
    }

    
    
    // Le spectateur applaudit.
    @Override
    public void applaudir() 
    {
        System.out.println(getPrenom() + " applaudit dans la tribune " + nomTribune + " !");
    }

    // Le spectateur crie.
    @Override
    public void crier() 
    {
        System.out.println(getPrenom() + " crie 'Allez !'");
    }

    // Le spectateur hue une décision ou une action.
    @Override
    public void huer() 
    {
        System.out.println(getPrenom() + " n'est pas content et le fait savoir.");
    }

    // Le spectateur s'endort .
    public void dormir() 
    {
        System.out.println(getPrenom() + " s'endort.");
    }
    

    // --- Getters et Setters pour les attributs du spectateur ---
    
    public double getPrixBillet() 
    {
        return prixBillet;
    }

    public String getPlace() 
    {
        return nomTribune + "-" + numeroPlace;
    }
    
    // Getters pour les attributs spécifiques au genre
    public String getCouleurChemise()
    {
        return couleurChemise != null ? couleurChemise : "Non spécifiée";
    }
    
    public boolean porteLunettes()
    {
        return porteLunettes;
    }

    // Permet à un spectateur homme de changer la couleur de sa chemise.
    public void changerChemise(String nouvelleCouleur)
    {
        if (getGenre() != Genre.HOMME)
        {
            System.out.println(getPrenom() + " ne porte pas de chemise distinctive à changer.");
            return;
        }
        if (nouvelleCouleur == null || nouvelleCouleur.isBlank())
        {
            System.out.println("Couleur invalide: la chemise reste " + getCouleurChemise());
            return;
        }
        this.couleurChemise = nouvelleCouleur.trim();
        System.out.println(getPrenom() + " change de chemise: " + this.couleurChemise);
    }

    // Pour afficher les infos du spectateur.
    @Override
    public String toString() 
    {
        return "Spectateur: " + getPrenom() + " " + getNomCourant() + " (Place: " + getPlace() + ")";
    }
}