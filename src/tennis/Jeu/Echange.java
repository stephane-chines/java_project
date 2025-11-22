package tennis.jeu;

import java.util.Scanner;
import tennis.personnages.Arbitre;
import tennis.personnages.Joueur;

/**
 * Déroulé d'un  echange entre serveur et receveur.
 * En mode MANUEL: saisie clavier des événements (let, faute, ace, etc.).
 * Retourne un {@link Resultat} décrivant le point joué.
 */
public class Echange 
{
    // Petit paquet de données pour suivre ce qui s'est passé pendant l'échange.
    public static class Resultat
    {
        public final Joueur vainqueur;
        public final boolean premierServiceJoue;
        public final boolean premierServiceReussi;
        public final boolean secondServiceJoue;
        public final boolean secondServiceReussi;
        public final boolean doubleFaute;
        public final boolean ace;

        public Resultat(Joueur vainqueur, boolean premierServiceJoue, boolean premierServiceReussi,
                boolean secondServiceJoue, boolean secondServiceReussi, boolean doubleFaute, boolean ace)
        {
            this.vainqueur = vainqueur;
            this.premierServiceJoue = premierServiceJoue;
            this.premierServiceReussi = premierServiceReussi;
            this.secondServiceJoue = secondServiceJoue;
            this.secondServiceReussi = secondServiceReussi;
            this.doubleFaute = doubleFaute;
            this.ace = ace;
        }
    }

    private final Joueur serveur;
    private final Joueur receveur;
    private final Arbitre arbitre;

    public Echange(Joueur serveur, Joueur receveur, Arbitre arbitre) 
    {
        this.serveur = serveur;
        this.receveur = receveur;
        this.arbitre = arbitre;
    }

    // Gestion interactive de point via la console.
    public Resultat jouerPoint() 
    {
        Scanner scanner = new Scanner(System.in);
        Joueur vainqueurDuPoint = null;
        int tentatives = 0;
        boolean premierServiceJoue = false;
        boolean premierServiceReussi = false;
        boolean secondServiceJoue = false;
        boolean secondServiceReussi = false;
        boolean doubleFaute = false;
        boolean ace = false;

        while (tentatives < 2 && vainqueurDuPoint == null) 
        {
            boolean estPremierService = (tentatives == 0);
            serveur.servir();
            System.out.println("Service de " + serveur.getPrenom() + " (" + (estPremierService ? "1er" : "2nd") + " service)...");
            System.out.print("Résultat du service (1: Réussi, 2: Filet (Let), 3: Faute) : ");

            int choix = scanner.nextInt();

            if (choix == 2)
            {
                System.out.println("Balle 'Let', le service est à rejouer.");
                continue;
            }

            tentatives++;

            switch (choix) 
            {
                case 1:
                    if (estPremierService)
                    {
                        premierServiceJoue = true;
                        premierServiceReussi = true;
                        System.out.print("Le service est-il un ace ? (o/n) : ");
                        String reponse = scanner.next();
                        ace = reponse.equalsIgnoreCase("o");
                    }
                    else
                    {
                        secondServiceJoue = true;
                        secondServiceReussi = true;
                    }
                    receveur.retournerService();
                    serveur.renvoyerBalle();
                    System.out.print("Qui gagne l'échange ? (1: " + serveur.getPrenom() + ", 2: " + receveur.getPrenom() + ") : ");
                    int gagnant = scanner.nextInt();
                    vainqueurDuPoint = (gagnant == 1) ? serveur : receveur;
                    if (vainqueurDuPoint == serveur) 
                    {
                        serveur.sEncourager();
                    } 
                    else 
                    {
                        receveur.sEncourager();
                    }
                    break;
                case 3:
                    if (estPremierService) 
                    {
                        premierServiceJoue = true;
                        premierServiceReussi = false;
                        arbitre.annoncerFaute();
                        serveur.faireFauteDirecte();
                        System.out.println("Deuxième service à suivre.");
                    } 
                    else 
                    {
                        secondServiceJoue = true;
                        secondServiceReussi = false;
                        doubleFaute = true;
                        System.out.println("Double faute !");
                        serveur.faireFauteDirecte();
                        receveur.sEncourager();
                        vainqueurDuPoint = receveur;
                    }
                    break;
                default:
                    System.out.println("Choix invalide.");
                    tentatives--;
                    break;
            }
        }

        if (vainqueurDuPoint == null)
        {
            vainqueurDuPoint = receveur;
        }

        return new Resultat(vainqueurDuPoint, premierServiceJoue, premierServiceReussi,
                secondServiceJoue, secondServiceReussi, doubleFaute, ace);
    }
}

