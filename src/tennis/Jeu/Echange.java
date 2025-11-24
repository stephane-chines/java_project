package tennis.jeu;

import java.util.List;
import java.util.Scanner;
import tennis.personnages.Arbitre;
import tennis.personnages.Joueur;
import tennis.personnages.Spectateur;

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
    private final List<Spectateur> spectateurs;

    public Echange(Joueur serveur, Joueur receveur, Arbitre arbitre, List<Spectateur> spectateurs) 
    {
        this.serveur = serveur;
        this.receveur = receveur;
        this.arbitre = arbitre;
        this.spectateurs = spectateurs;
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
                        
                        if (ace)
                        {
                            // Un ace donne automatiquement le point au serveur
                            System.out.println("ACE ! Point pour " + serveur.getPrenom() + " !");
                            vainqueurDuPoint = serveur;
                            serveur.sEncourager();
                            proposerReactionSpectateurs(scanner);

                            
                            Joueur perdantDuPoint = receveur;
                            System.out.print(perdantDuPoint.getPrenom() + " conteste-t-il le point ? (o/n) : ");
                            String contestePoint = scanner.next();
                            scanner.nextLine(); 
                            if (contestePoint.equalsIgnoreCase("o"))
                            {
                                System.out.print("Motif de la contestation : ");
                                String motif = scanner.nextLine();
                                perdantDuPoint.appelerArbitre(arbitre, motif);
                            }
                        }
                        else
                        {
                            // Pas d'ace, échange normal
                            receveur.retournerService();
                            serveur.renvoyerBalle();
                            System.out.print("Qui gagne l'échange ? (1: " + serveur.getPrenom() + ", 2: " + receveur.getPrenom() + ") : ");
                            int gagnant = scanner.nextInt();
                            vainqueurDuPoint = (gagnant == 1) ? serveur : receveur;
                            Joueur perdantDuPoint = (vainqueurDuPoint == serveur) ? receveur : serveur;
                            
                            if (vainqueurDuPoint == serveur) 
                            {
                                serveur.sEncourager();
                            } 
                            else 
                            {
                                receveur.sEncourager();
                            }
                            
                            // Proposer aux spectateurs de réagir
                            proposerReactionSpectateurs(scanner);
                            
                            // Seul le perdant du point peut contester
                            System.out.print(perdantDuPoint.getPrenom() + " conteste-t-il le point ? (o/n) : ");
                            String contestePoint = scanner.next();
                            scanner.nextLine(); // consommer le retour à la ligne
                            if (contestePoint.equalsIgnoreCase("o"))
                            {
                                System.out.print("Motif de la contestation : ");
                                String motif = scanner.nextLine();
                                perdantDuPoint.appelerArbitre(arbitre, motif);
                            }
                        }
                    }
                    else
                    {
                        secondServiceJoue = true;
                        secondServiceReussi = true;
                        // Second service, échange normal
                        receveur.retournerService();
                        serveur.renvoyerBalle();
                        System.out.print("Qui gagne l'échange ? (1: " + serveur.getPrenom() + ", 2: " + receveur.getPrenom() + ") : ");
                        int gagnant = scanner.nextInt();
                        vainqueurDuPoint = (gagnant == 1) ? serveur : receveur;
                        Joueur perdantDuPoint = (vainqueurDuPoint == serveur) ? receveur : serveur;
                        
                        if (vainqueurDuPoint == serveur) 
                        {
                            serveur.sEncourager();
                        } 
                        else 
                        {
                            receveur.sEncourager();
                        }
                        
                        // Proposer aux spectateurs de réagir
                        proposerReactionSpectateurs(scanner);
                        
                        // Seul le perdant du point peut contester
                        System.out.print(perdantDuPoint.getPrenom() + " conteste-t-il le point ? (o/n) : ");
                        String contestePoint = scanner.next();
                        scanner.nextLine(); // consommer le retour à la ligne
                        if (contestePoint.equalsIgnoreCase("o"))
                        {
                            System.out.print("Motif de la contestation : ");
                            String motif = scanner.nextLine();
                            perdantDuPoint.appelerArbitre(arbitre, motif);
                        }
                    }
                    break;
                case 3:
                    if (estPremierService) 
                    {
                        premierServiceJoue = true;
                        premierServiceReussi = false;
                        arbitre.annoncerFaute();
                        serveur.faireFauteDirecte();
                        
                        // Possibilité de contester la faute appelée
                        System.out.print(serveur.getPrenom() + " conteste-t-il la faute ? (o/n) : ");
                        String contesteFaute = scanner.next();
                        scanner.nextLine(); // consommer le retour à la ligne
                        if (contesteFaute.equalsIgnoreCase("o"))
                        {
                            System.out.print("Motif de la contestation : ");
                            String motif = scanner.nextLine();
                            serveur.appelerArbitre(arbitre, motif);
                        }
                        
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
                        
                        // Proposer aux spectateurs de réagir
                        proposerReactionSpectateurs(scanner);
                        
                        // Le serveur (perdant) peut contester la double faute
                        System.out.print(serveur.getPrenom() + " conteste-t-il la double faute ? (o/n) : ");
                        String contesteDF = scanner.next();
                        scanner.nextLine(); // consommer le retour à la ligne
                        if (contesteDF.equalsIgnoreCase("o"))
                        {
                            System.out.print("Motif de la contestation : ");
                            String motif = scanner.nextLine();
                            serveur.appelerArbitre(arbitre, motif);
                        }
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
    
    // Proposer aux spectateurs de réagir après un point
    private void proposerReactionSpectateurs(Scanner scanner)
    {
        if (spectateurs == null || spectateurs.isEmpty())
        {
            return;
        }
        
        System.out.print("Un spectateur réagit-il ? (o/n) : ");
        String reponse = scanner.next();
        scanner.nextLine(); // consommer le retour à la ligne
        
        if (reponse.equalsIgnoreCase("o"))
        {
            System.out.println("\n--- Sélection d'un spectateur ---");
            for (int i = 0; i < spectateurs.size(); i++)
            {
                System.out.println((i + 1) + ". " + spectateurs.get(i));
            }
            System.out.print("Votre choix : ");
            int index = scanner.nextInt() - 1;
            scanner.nextLine(); // consommer le retour à la ligne
            
            if (index >= 0 && index < spectateurs.size())
            {
                Spectateur spectateur = spectateurs.get(index);
                System.out.println("\n--- Actions disponibles pour " + spectateur.getPrenom() + " ---");
                System.out.println("1. Applaudir");
                System.out.println("2. Crier");
                System.out.println("3. Huer");
                System.out.println("4. Dormir");
                System.out.print("Votre choix : ");
                int action = scanner.nextInt();
                scanner.nextLine(); // consommer le retour à la ligne
                
                switch (action)
                {
                    case 1 -> spectateur.applaudir();
                    case 2 -> spectateur.crier();
                    case 3 -> spectateur.huer();
                    case 4 -> spectateur.dormir();
                    default -> System.out.println("Action invalide.");
                }
            }
        }
    }
}

