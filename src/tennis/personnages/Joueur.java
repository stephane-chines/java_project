package tennis.personnages;

import java.time.LocalDate;
import tennis.stats.StatistiquesCarriere;

/**
 * Représentation d'un joueur de tennis.
 * Étend {@link Personne} et ajoute main de jeu, sponsor, entraîneur, classement, tenue, réputation,
 * ainsi que des actions possibles en match.
 */
public class Joueur extends Personne
{

   

    // Enumération pour indiquer la main dominante d’un joueur.
    public enum MainDeJeu
    {
        DROITIER,
        GAUCHER
    }

    // Attributs spécifiques aux joueurs.
    private final MainDeJeu mainDeJeu;
    private String sponsor;
    private String entraineur;
    private int classement;
    private String couleurTenue;
    private int reputation;
    private StatistiquesCarriere statsCarriere;
    private static volatile boolean afficherEncouragements = true;;

    // Compteur pour attribuer un classement selon l’ordre de création
    private static int compteurClassement = 0;

    // Constructeur principal : initialise les données propres à un joueur.
    public Joueur(String nomNaissance, String prenom, Genre genre, LocalDate dateNaissance, String lieuNaissance,
            MainDeJeu mainDeJeu, String sponsor, String entraineur, String couleurTenue, int reputation)
    {
        this(nomNaissance, prenom, genre, dateNaissance, lieuNaissance, mainDeJeu, sponsor, entraineur, couleurTenue,
                reputation, null);
    }

    // Constructeur alternatif pour injecter des statistiques existantes.
    public Joueur(String nomNaissance, String prenom, Genre genre, LocalDate dateNaissance, String lieuNaissance,
            MainDeJeu mainDeJeu, String sponsor, String entraineur, String couleurTenue, int reputation,
            StatistiquesCarriere stats)
    {
        super(nomNaissance, prenom, genre, dateNaissance, lieuNaissance);
        this.mainDeJeu = requireNonNull(mainDeJeu, "mainDeJeu");
        this.sponsor = safe(sponsor, "N/A");
        this.entraineur = safe(entraineur, "N/A");
        this.couleurTenue = safe(couleurTenue, "Classique");

        // Classement attribué automatiquement pour conserver l’ordre de création.
        this.classement = ++compteurClassement;

        // Réputation comprise entre 1 et 10.
        this.reputation = borneReputation(reputation);

        this.statsCarriere = (stats != null) ? stats : new StatistiquesCarriere();
    }

    // Accesseurs et mutateurs classiques.
    public MainDeJeu getMainDeJeu() 
    {
        return mainDeJeu;
    }

    public String getSponsor() 
    {
        return sponsor;
    }

    public void setSponsor(String sponsor) 
    {
		if (sponsor == null || sponsor.isBlank())
		{
			throw new IllegalArgumentException("Le sponsor ne peut pas être vide.");
		}
		this.sponsor = sponsor.trim();
    }

    public String getEntraineur() 
    {
        return entraineur;
    }

    public void setEntraineur(String entraineur) 
    {
		if (entraineur == null || entraineur.isBlank())
		{
			throw new IllegalArgumentException("L'entraîneur ne peut pas être vide.");
		}
		this.entraineur = entraineur.trim();
    }

    public int getClassement() 
    {
        return classement;
    }

    // Mise à jour du classement réservée au moteur du tournoi.
	public void setClassement(int classement) 
    {
        if (classement > 0) 
        {
            this.classement = classement;
        }
    }

    public String getCouleurTenue() 
    {
        return couleurTenue;
    }

    public void setCouleurTenue(String nouvelleCouleur) 
    {
        this.couleurTenue = safe(nouvelleCouleur, "Classique");

        if (getGenre() == Genre.FEMME)
        {
            System.out.println(getPrenom() + " change la couleur de sa jupe en " + this.couleurTenue + ".");
        }
        else
        {
            System.out.println(getPrenom() + " change la couleur de son short en " + this.couleurTenue + ".");
        }
    }

    public int getReputation()
    {
        return reputation;
    }

    public StatistiquesCarriere getStatsCarriere()
    {
        return statsCarriere;
    }

    // Actions en cours de match pour rendre la simulation plus vivante.
    // Fais semblant de lancer un service.
    public void servir() 
    {
        System.out.println(getPrenom() + " se prépare à servir...");
    }

    // Fais semblant de se préparer au retour.
    public void retournerService() 
    {
        System.out.println(getPrenom() + " se met en position pour retourner le service.");
    }
    
    // Même chose pour le renvoi de balle.
    public void renvoyerBalle() 
    {
        System.out.println(getPrenom() + " frappe un coup puissant !");
    }

    // Petite faute directe pour la forme.
    public void faireFauteDirecte() 
    {
        System.out.println("Faute directe de " + getPrenom() + " ! La balle est dans le filet.");
    }

    // Appelle l'arbitre quand ça râle.
    public void appelerArbitre(Arbitre arbitre, String motif) 
    {
        System.out.println(getPrenom() + " n'est pas d'accord et interpelle l'arbitre: '" + motif + "' !");
        arbitre.resoudreLitige(this, motif);
    }
    public static void setAfficherEncouragements(boolean afficher) {
    afficherEncouragements = afficher;
}

    public static boolean isAfficherEncouragements() {
        return afficherEncouragements;
    }
    // Petit boost perso.
    public void sEncourager() 
    {   
        if (!afficherEncouragements) {
        
        return;
        }
        System.out.println(getPrenom() + " se motive: 'Allez !!'");
    }

    // Pense à boire un coup.
    public void boire() 
    {
        System.out.println(getPrenom() + " boit un peu.");
    }

    public void crierVictoire() 
    {
        System.out.println(getPrenom() + " lève les bras au ciel en signe de victoire !");
    }

    public void crierDefaite() 
    {
        System.out.println(getPrenom() + " crie il ne supporte pas cette défaite.");
    }
    
    @Override
    public String toString() 
    {
        return getPrenom() + " " + getNomCourant() + " (#" + classement + ")";
    }

    // Remet à zéro le compteur de classement pour un nouveau tournoi.
    public static void reinitialiserClassements()
    {
        compteurClassement = 0;
    }

    // Garantit qu'une chaîne vide est remplacée par une valeur par défaut.
    private static String safe(String texte, String valeurParDefaut)
    {
        if (texte == null || texte.isBlank())
        {
            return valeurParDefaut;
        }
        return texte.trim();
    }

    // Valide que l'objet nécessaire est bien fourni.
    private static <T> T requireNonNull(T valeur, String nom)
    {
        if (valeur == null)
        {
            throw new IllegalArgumentException(nom + " obligatoire");
        }
        return valeur;
    }

    // Maintient la réputation dans l'intervalle prévu.
    private static int borneReputation(int reputation)
    {
        if (reputation > 10)
        {
            return 10;
        }
        if (reputation < 1)
        {
            return 1;
        }
        return reputation;
    }
}