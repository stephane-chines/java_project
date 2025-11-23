package tennis.jeu;

import java.util.List;
import java.util.Random;
import tennis.personnages.Arbitre;
import tennis.personnages.Joueur;
import tennis.personnages.Spectateur;
import tennis.stats.StatistiquesMatch;

/**
 * Tie-break: service une fois pour le serveur initial puis alternance toutes les deux balles.
 * Victoire à ≥ 7 points avec 2 points d'écart. Met à jour les statistiques.
 */
public class JeuDecisif 
{
    private final Joueur joueur1;
    private final Joueur joueur2;
    private final Arbitre arbitre;
    private final Joueur serveurInitial;
    private final StatistiquesMatch statsJoueur1;
    private final StatistiquesMatch statsJoueur2;
    private final List<Spectateur> spectateurs;
    private final Random generateur = new Random();

    private int pointsJoueur1;
    private int pointsJoueur2;
    private Joueur vainqueur;

    public JeuDecisif(Joueur joueur1, Joueur joueur2, Joueur serveurInitial, Arbitre arbitre,
            StatistiquesMatch statsJoueur1, StatistiquesMatch statsJoueur2, List<Spectateur> spectateurs) 
    {
        // Petit contrôle: mieux vaut bloquer tôt que réparer tard
        if (joueur1 == null || joueur2 == null || arbitre == null || serveurInitial == null
                || statsJoueur1 == null || statsJoueur2 == null) {
            throw new IllegalArgumentException("Arguments null interdits");
        }
        if (joueur1 == joueur2) {
            throw new IllegalArgumentException("Les deux joueurs du tie-break doivent être distincts");
        }
        if (serveurInitial != joueur1 && serveurInitial != joueur2) {
            throw new IllegalArgumentException("Le serveur initial doit être joueur1 ou joueur2");
        }
        this.joueur1 = joueur1;
        this.joueur2 = joueur2;
        this.arbitre = arbitre;
        this.serveurInitial = serveurInitial;
        this.statsJoueur1 = statsJoueur1;
        this.statsJoueur2 = statsJoueur2;
        this.spectateurs = spectateurs;
    }

    // Joue le tie-break jusqu'à trouver un gagnant. 
    public void jouer(ModeJeu mode, boolean afficherDetails) 
    {
        if (mode == ModeJeu.MANUEL || afficherDetails)
        {
            System.out.println("--- Début du Jeu Décisif (Tie-Break) ---");
            // Petite touche d'ambiance: l'arbitre annonce aussi le début du tie-break
            arbitre.annoncerDebutTieBreak(joueur1, joueur2);
        }

        Joueur serveurActuel = serveurInitial;
        int pointsDisputes = 0;

        while (vainqueur == null) 
        {
            if (pointsDisputes > 0 && pointsDisputes % 2 == 1)
            {
                serveurActuel = (serveurActuel == joueur1) ? joueur2 : joueur1;
            }

            Joueur receveur = (serveurActuel == joueur1) ? joueur2 : joueur1;
            StatistiquesMatch statsServeur = getStatsPour(serveurActuel);
            Joueur gagnantPoint;

            if (mode == ModeJeu.MANUEL) 
            {
                Echange echange = new Echange(serveurActuel, receveur, arbitre, spectateurs);
                Echange.Resultat resultat = echange.jouerPoint();
                enregistrerServices(statsServeur, resultat);
                if (resultat.doubleFaute)
                {
                    gagnantPoint = receveur;
                }
                else
                {
                    gagnantPoint = resultat.vainqueur;
                    if (resultat.ace)
                    {
                        statsServeur.incrementerAces();
                    }
                }
            }
            else 
            {
                gagnantPoint = simulerPointAutomatique(serveurActuel, receveur, statsServeur);
            }

            if (gagnantPoint == joueur1)
            {
                pointsJoueur1++;
            }
            else
            {
                pointsJoueur2++;
            }

            getStatsPour(gagnantPoint).incrementerPoints();

            if (mode == ModeJeu.MANUEL || afficherDetails)
            {
                arbitre.annoncerScore("Tie-break : " + pointsJoueur1 + " - " + pointsJoueur2);
            }

            verifierVainqueur();
            pointsDisputes++;
        }
    }

    private void verifierVainqueur() 
    {
        if (pointsJoueur1 >= 7 && pointsJoueur1 >= pointsJoueur2 + 2)
        {
            vainqueur = joueur1;
        }
        else if (pointsJoueur2 >= 7 && pointsJoueur2 >= pointsJoueur1 + 2)
        {
            vainqueur = joueur2;
        }
    }

    private StatistiquesMatch getStatsPour(Joueur joueur)
    {
        return (joueur == joueur1) ? statsJoueur1 : statsJoueur2;
    }

    private void enregistrerServices(StatistiquesMatch statsServeur, Echange.Resultat resultat)
    {
        if (resultat.premierServiceJoue)
        {
            double vitessePremier = resultat.premierServiceReussi ? genererVitessePremierService() : 0;
            statsServeur.enregistrerPremierService(resultat.premierServiceReussi, vitessePremier);
        }
        if (resultat.secondServiceJoue)
        {
            double vitesseSecond = resultat.secondServiceReussi ? genererVitesseSecondService() : 0;
            statsServeur.enregistrerSecondService(resultat.secondServiceReussi, vitesseSecond);
        }
        if (resultat.doubleFaute)
        {
            statsServeur.incrementerDoubleFautes();
        }
    }

    private Joueur simulerPointAutomatique(Joueur serveurActuel, Joueur receveur, StatistiquesMatch statsServeur)
    {
        boolean premierServiceReussi = generateur.nextDouble() < 0.62;

        if (premierServiceReussi)
        {
            statsServeur.enregistrerPremierService(true, genererVitessePremierService());
        }
        else
        {
            statsServeur.enregistrerPremierService(false, 0);
            boolean secondServiceReussi = generateur.nextDouble() < 0.8;
            statsServeur.enregistrerSecondService(secondServiceReussi,
                    secondServiceReussi ? genererVitesseSecondService() : 0);
            if (!secondServiceReussi)
            {
                statsServeur.incrementerDoubleFautes();
                receveur.sEncourager();
                return receveur;
            }
        }

        double probServeur = premierServiceReussi ? 0.65 : 0.55;
        Joueur gagnantPoint = (generateur.nextDouble() < probServeur) ? serveurActuel : receveur;

        if (gagnantPoint == serveurActuel && premierServiceReussi && generateur.nextDouble() < 0.12)
        {
            statsServeur.incrementerAces();
        }

        if (gagnantPoint == serveurActuel)
        {
            serveurActuel.sEncourager();
        }
        else
        {
            receveur.sEncourager();
        }

        return gagnantPoint;
    }

    private double genererVitessePremierService()
    {
        return 180 + generateur.nextDouble() * 40;
    }

    private double genererVitesseSecondService()
    {
        return 150 + generateur.nextDouble() * 30;
    }

    // Retourne celui qui a gagné ce tie-break (null si non joué).
    public Joueur getVainqueur() 
    {
        return vainqueur;
    }

    // Points du joueur1 dans le tie-break.
    public int getPointsJoueur1() {
        return pointsJoueur1;
    }

    // Points du joueur2 dans le tie-break.
    public int getPointsJoueur2() {
        return pointsJoueur2;
    }

    // Score lisible du tie-break, ex: "7–4".
    public String getScoreString() {
        return pointsJoueur1 + "–" + pointsJoueur2;
    }
}

