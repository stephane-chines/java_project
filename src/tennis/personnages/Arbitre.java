package tennis.personnages;

import java.time.LocalDate;
import java.util.Random;

/**
 * Arbitre central: annonces de score et décisions (litiges).
 * Modélise une humeur aléatoire qui influe sur certaines décisions en litige.
 */
public class Arbitre extends Personne 
{
    // Générateur aléatoire utilisé pour modéliser l’humeur du juge.
    private Random generateurAleatoire = new Random();

    // Constructeur de l'arbitre.
    public Arbitre(String nomNaissance, String prenom, Genre genre, LocalDate dateNaissance, String lieuNaissance) 
    {
        super(nomNaissance, prenom, genre, dateNaissance, lieuNaissance);
    }

    // Annonce le score actuel dans le jeu.
    public void annoncerScore(String score) 
    {
        System.out.println("Arbitre: " + score);
    }

    // Annonce la fin d'un jeu, le gagnant, et le score actuel du set.
    public void annoncerFinJeu(String nomGagnant, int jeuxJ1, int jeuxJ2) 
    {
        System.out.println("Arbitre: Jeu " + nomGagnant + ". Score du set: " + jeuxJ1 + "-" + jeuxJ2);
    }

    // Annonce la fin d'un set, le gagnant, et le score actuel du match.
    public void annoncerFinSet(String nomGagnant, int setsJ1, int setsJ2) 
    {
        System.out.println("Arbitre: Set " + nomGagnant + ". Score du match: " + setsJ1 + "-" + setsJ2);
    }

    // Annonce la fin du match et le vainqueur final.
    public void annoncerFinMatch(String nomGagnant) 
    {
        System.out.println("Arbitre: Victoire de " + nomGagnant);
    }

	// Petite annonce claire quand un tie-break démarre
	public void annoncerDebutTieBreak(Joueur j1, Joueur j2)
	{
		System.out.println("Arbitre: Début du tie-break entre " + j1.getPrenom() + " et " + j2.getPrenom() + ".");
	}

    // Annonce une faute de service.
    public void annoncerFaute() 
    {
        System.out.println("Arbitre: Faute !");
    }

    // Gère un litige demandé par un joueur en tenant compte de l’humeur du juge et de la réputation du joueur.
    public boolean resoudreLitige(Joueur joueur, String motif) 
    {
        System.out.println(getPrenom() + " (Arbitre) écoute " + joueur.getPrenom() + " pour motif: " + motif);

        int humeur = generateurAleatoire.nextInt(10);
        boolean decision = (humeur + joueur.getReputation()) > 12;

        if (decision) 
        {
            System.out.println("Arbitre: Après vérification, la décision est en faveur du joueur.");
        } 
        else 
        {
            System.out.println("Arbitre: La décision initiale est maintenue.");
        }

        return decision;
    }

    // Pour afficher les infos de l'arbitre.
    @Override
    public String toString() 
    {
        return "Arbitre: " + getPrenom() + " " + getNomCourant();
    }
}