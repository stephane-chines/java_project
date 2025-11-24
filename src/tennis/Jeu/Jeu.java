package tennis.jeu;

import java.util.List;
import java.util.Random;
import tennis.personnages.Arbitre;
import tennis.personnages.Joueur;
import tennis.personnages.Spectateur;
import tennis.stats.StatistiquesMatch;

/**
 * Simulation d'un jeu (succession de points) en mode manuel ou automatique.
 * Gère les règles 0/15/30/40, égalité/avantage, et met à jour les statistiques.
 */
public class Jeu 
{
    private final Joueur joueur1;
    private final Joueur joueur2;
    private Joueur serveur;
    private final Arbitre arbitre;
    private final StatistiquesMatch statsJoueur1;
    private final StatistiquesMatch statsJoueur2;
    private final List<Spectateur> spectateurs;
    private final Random generateur = new Random();

    private int pointsJoueur1;
    private int pointsJoueur2;
    private Joueur vainqueur;

    public Jeu(Joueur joueur1, Joueur joueur2, Joueur serveur, Arbitre arbitre,
            StatistiquesMatch statsJoueur1, StatistiquesMatch statsJoueur2, List<Spectateur> spectateurs) 
    {
        this.joueur1 = joueur1;
        this.joueur2 = joueur2;
        this.serveur = serveur;
        this.arbitre = arbitre;
        this.statsJoueur1 = statsJoueur1;
        this.statsJoueur2 = statsJoueur2;
        this.spectateurs = spectateurs;
        this.pointsJoueur1 = 0;
        this.pointsJoueur2 = 0;
        this.vainqueur = null;
    }

    // Joue un jeu complet selon le mode choisi.
    public void jouerJeu(ModeJeu mode, boolean afficherDetails) 
    {
        while (vainqueur == null) 
        {
            Joueur receveur = (serveur == joueur1) ? joueur2 : joueur1;
            Joueur gagnantPoint;
            StatistiquesMatch statsServeur = getStatsPour(serveur);
            boolean balleDeBreak = estBalleDeBreak(serveur);

            if (mode == ModeJeu.MANUEL) 
            {
                Echange echange = new Echange(serveur, receveur, arbitre, spectateurs);
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
                gagnantPoint = simulerPointAutomatique(statsServeur);
            }

            marquerPoint(gagnantPoint);
            getStatsPour(gagnantPoint).incrementerPoints();
            if (balleDeBreak)
            {
                getStatsPour(receveur).enregistrerBalleDeBreak(gagnantPoint == receveur);
            }

            if (mode == ModeJeu.MANUEL || afficherDetails) 
            {
                arbitre.annoncerScore(getScorePourAffichage());
            }

            verifierVainqueur();
        }
    }

    private void marquerPoint(Joueur joueur) 
    {
        if (pointsJoueur1 >= 3 && pointsJoueur2 >= 3) 
        {
            if (pointsJoueur1 == pointsJoueur2)
            {
                if (joueur == joueur1) 
                {
                    pointsJoueur1++;
                } 
                else 
                {
                    pointsJoueur2++;
                }
            } 
            else if (pointsJoueur1 > pointsJoueur2) 
            {
                if (joueur == joueur1) 
                {
                    pointsJoueur1++;
                } 
                else 
                {
                    pointsJoueur1--;
                }
            } 
            else 
            {
                if (joueur == joueur2) 
                {
                    pointsJoueur2++;
                } 
                else 
                {
                    pointsJoueur2--;
                }
            }
        } 
        else 
        {
            if (joueur == joueur1) 
            {
                pointsJoueur1++;
            } 
            else 
            {
                pointsJoueur2++;
            }
        }
    }

    private void verifierVainqueur() 
    {
        if (pointsJoueur1 >= 4 && pointsJoueur1 >= pointsJoueur2 + 2) 
        {
            this.vainqueur = joueur1;
        } 
        else if (pointsJoueur2 >= 4 && pointsJoueur2 >= pointsJoueur1 + 2) 
        {
            this.vainqueur = joueur2;
        }
    }

    private String getScorePourAffichage() 
    {
        if (vainqueur != null) 
        {
            return "Jeu remporté par " + vainqueur.getPrenom();
        }

        int pointsServeur = (serveur == joueur1) ? pointsJoueur1 : pointsJoueur2;
        int pointsReceveur = (serveur == joueur1) ? pointsJoueur2 : pointsJoueur1;
        Joueur receveur = (serveur == joueur1) ? joueur2 : joueur1;

        if (pointsServeur >= 3 && pointsServeur == pointsReceveur) 
        {
            return "Egalité";
        }
        if (pointsServeur > 3 && pointsServeur > pointsReceveur) 
        {
            return "Avantage " + serveur.getPrenom();
        }
        if (pointsReceveur > 3 && pointsReceveur > pointsServeur) 
        {
            return "Avantage " + receveur.getPrenom();
        }

        return serveur.getPrenom() + " " + convertirPoint(pointsServeur) + " - " + convertirPoint(pointsReceveur) + " " + receveur.getPrenom();
    }
    
    private String convertirPoint(int point) 
    {
        switch (point) 
        {
            case 0: return "0";
            case 1: return "15";
            case 2: return "30";
            case 3: return "40";
            default: return "";
        }
    }

    public Joueur getVainqueur() 
    {
        return vainqueur;
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

    private Joueur simulerPointAutomatique(StatistiquesMatch statsServeur)
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
                return (serveur == joueur1) ? joueur2 : joueur1;
            }
        }

        Joueur receveur = (serveur == joueur1) ? joueur2 : joueur1;
        StatistiquesMatch statsReceveur = getStatsPour(receveur);

        double probServeur = premierServiceReussi ? 0.65 : 0.55;
        Joueur gagnantPoint = (generateur.nextDouble() < probServeur) ? serveur : receveur;

        if (gagnantPoint == serveur && premierServiceReussi && generateur.nextDouble() < 0.12)
        {
            statsServeur.incrementerAces();
        }

        if (gagnantPoint == serveur)
        {
            serveur.sEncourager();
        }
        else
        {
            receveur.sEncourager();
        }
        
        // Occasionnellement en automatique, SEUL le perdant du point conteste la décision (5% de chance)
        Joueur perdantPoint = (gagnantPoint == serveur) ? receveur : serveur;
        if (generateur.nextDouble() < 0.05)
        {
            String[] motifs = {
                "La balle était dehors !",
                "Je conteste cette décision !",
                "La balle a touché la ligne !",
                "Ce n'était pas une faute !"
            };
            String motif = motifs[generateur.nextInt(motifs.length)];
            perdantPoint.appelerArbitre(arbitre, motif);
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

    private boolean estBalleDeBreak(Joueur serveurActuel)
    {
        Joueur receveur = (serveurActuel == joueur1) ? joueur2 : joueur1;
        int pointsServeur = getPointsPour(serveurActuel);
        int pointsReceveur = getPointsPour(receveur);

        if (pointsReceveur < 3)
        {
            return false;
        }
        if (pointsReceveur == 3 && pointsServeur < 3)
        {
            return true;
        }
        return pointsReceveur >= 4 && pointsReceveur == pointsServeur + 1;
    }

    private int getPointsPour(Joueur joueur)
    {
        return (joueur == joueur1) ? pointsJoueur1 : pointsJoueur2;
    }
}

