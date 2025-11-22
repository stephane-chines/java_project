package tennis.personnages;

import java.time.LocalDate;

/**
 * Spectateur d'un match: billet/tribune/place et petites actions d'ambiance (applaudir, crier, huer, dormir).
 * Différenciation homme/femme (chemise vs lunettes).
 */
public class Spectateur extends Personne implements ActionsSpectateur
{
    // Attributs spécifiques à un spectateur.
    private double prixBillet;
    private String nomTribune;
    private int numeroPlace;
    
    // Attributs qui dépendent du genre du spectateur.
    private String couleurChemise; // Uniquement pour les hommes
    private boolean porteLunettes; // Uniquement pour les femmes

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

    // --- Méthodes pour les actions d'un spectateur ---
    
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

    // Le spectateur s'endort (c'est rare).
    public void dormir() 
    {
        System.out.println(getPrenom() + " s'endort.");
    }
    
    // --- Méthodes pour les caractéristiques distinctives ---

    // Pour changer la couleur de la chemise d'un spectateur homme.
    public void changerCouleurChemise(String nouvelleCouleur) 
    {
        if (getGenre() == Genre.HOMME) 
        {
            this.couleurChemise = nouvelleCouleur;
            System.out.println(getPrenom() + " met en évidence sa nouvelle chemise de couleur " + nouvelleCouleur + ".");
        } 
        else 
        {
            System.out.println("Seules les spectateurs hommes peuvent changer de chemise.");
        }
    }

    // Pour indiquer si la spectatrice met ou enlève ses lunettes.
    public void mettreOuEnleverLunettes(boolean porte) 
    {
        if (getGenre() == Genre.FEMME) 
        {
            this.porteLunettes = porte;
            if (porte) 
            {
                System.out.println(getPrenom() + " met ses lunettes.");
            } 
            else 
            {
                System.out.println(getPrenom() + " enlève ses lunettes.");
            }
        } 
        else 
        {
            System.out.println("Cette caractéristique est pour les spectatrices.");
        }
    }
    
    // --- Getters et Setters pour les attributs du spectateur ---
    
    public double getPrixBillet() 
    {
        return prixBillet;
    }

    public void setPrixBillet(double prixBillet) 
    {
        this.prixBillet = prixBillet;
    }

    public String getPlace() 
    {
        return nomTribune + "-" + numeroPlace;
    }

    // Pour afficher les infos du spectateur.
    @Override
    public String toString() 
    {
        return "Spectateur: " + getPrenom() + " " + getNomCourant() + " (Place: " + getPlace() + ")";
    }
}