package tennis;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import tennis.erreurs.SaisieInvalideException;
import tennis.jeu.Match;
import tennis.jeu.ModeJeu;
import tennis.personnages.Arbitre;
import tennis.personnages.Joueur;
import tennis.personnages.Personne;
import tennis.tournoi.Tournoi;
import tennis.tournoi.TournoiType;

/**
 * Application console: menus de création, création/gestion du tournoi, et navigation.
 * Conçue pour piloter rapidement une édition complète en mode console.
 */
public class Application 
{
    private final Scanner scanner = new Scanner(System.in);
    private final List<Joueur> joueursCrees = new ArrayList<>();
		private final List<Arbitre> arbitresCrees = new ArrayList<>();
		private final List<tennis.personnages.Spectateur> spectateursCrees = new ArrayList<>();
    private Tournoi tournoiEnCours = null;

    // main pour lancer tout ça.
    public static void main(String[] args) 
    {
        Application app = new Application();
        app.lancer();
    }

    // Menu principal en boucle jusqu'à ce que l'utilisateur quitte.
    public void lancer() 
    {
		// phrase d'accueil
		System.out.println("Bienvenue dans le gestionnaire de tournoi de jeu de tennis. )");

        while (true) 
        {
            afficherMenuPrincipal();
            try 
            {
                int choix = demanderChoixUtilisateur();
                switch (choix) 
                {
                    case 1 -> creerPersonnage();
                    case 2 -> creerTournoi();
                    case 3 -> {
                        if (tournoiEnCours != null) 
                        {
                            gererTournoi();
                        } 
                        else 
                        {
                            System.out.println("Erreur : aucun tournoi n'a été créé pour le moment.");
                        }
                    }
                    case 4 -> afficherInfosJoueurs();
                    case 5 -> genererJoueursAutomatiques();
                    case 0 -> {
                        System.out.println("Merci d'avoir utilisé l'application. À bientôt !");
                        return;
                    }
                    default -> throw new SaisieInvalideException("Ce choix n'existe pas dans le menu.");
                }
            } 
            catch (InputMismatchException e) 
            {
                System.out.println("Erreur : veuillez entrer un nombre entier.");
                scanner.nextLine(); // nettoie le buffer
            } 
            catch (SaisieInvalideException e) 
            {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
    }

    private void afficherMenuPrincipal() 
    {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Créer un personnage (joueur)");
        System.out.println("2. Créer un nouveau tournoi");
        System.out.println("3. Gérer le tournoi en cours");
        System.out.println("4. Afficher les informations des joueurs créés");
        System.out.println("5. Générer automatiquement des joueurs (ajout à la liste)");
        System.out.println("0. Quitter");
		// si perdu, taper 0 pour revenir en arrière, ça evite de tout casser
    }

    private int demanderChoixUtilisateur() 
    {
        System.out.print("Votre choix : ");
        int choix = scanner.nextInt();
        scanner.nextLine(); // consomme le retour chariot
        return choix;
    }

		// Création d'un personnage: joueur, arbitre ou spectateur
    private void creerPersonnage() 
    {
			System.out.println("\n--- Création d'un personnage ---");
			System.out.println("1. Créer un joueur");
			System.out.println("2. Créer un arbitre");
			System.out.println("3. Créer un spectateur");
			System.out.print("Votre choix : ");
			String choix = scanner.nextLine();
			switch (choix)
			{
				case "1" -> creerJoueur();
				case "2" -> creerArbitre();
				case "3" -> creerSpectateur();
				default -> System.out.println("Choix invalide.");
			}
		}

		private void creerJoueur()
		{
			System.out.println("\n--- Création d'un joueur ---");
			System.out.print("Prénom : ");
			String prenom = scanner.nextLine();
			System.out.print("Nom : ");
			String nom = scanner.nextLine();
			Personne.Genre genre = demanderGenreJoueur();

			Joueur joueur = new Joueur(nom, prenom, genre, LocalDate.now(), "Inconnu",
					Joueur.MainDeJeu.DROITIER, "Sponsor", "Entraîneur", "Blanc", 5);
			joueursCrees.add(joueur);
			System.out.println("Joueur " + joueur + " créé avec succès.");
		}

		private void creerArbitre()
		{
			System.out.println("\n--- Création d'un arbitre ---");
			System.out.print("Prénom : ");
			String prenom = scanner.nextLine();
			System.out.print("Nom : ");
			String nom = scanner.nextLine();
			Personne.Genre genre = demanderGenreJoueur();
			Arbitre arbitre = new Arbitre(nom, prenom, genre, LocalDate.now(), "Inconnu");
			arbitresCrees.add(arbitre);
			System.out.println("Arbitre " + arbitre + " créé avec succès.");
		}

		private void creerSpectateur()
		{
			System.out.println("\n--- Création d'un spectateur ---");
			System.out.print("Prénom : ");
			String prenom = scanner.nextLine();
			System.out.print("Nom : ");
			String nom = scanner.nextLine();
			Personne.Genre genre = demanderGenreJoueur();
			System.out.print("Prix du billet (€) : ");
			double prix = 30.0;
			try { prix = Double.parseDouble(scanner.nextLine()); } catch (NumberFormatException ignored) {}
			tennis.personnages.Spectateur spectateur = new tennis.personnages.Spectateur(
					nom, prenom, genre, LocalDate.now(), "Inconnu",
					prix, "Tribune A", 1);
			spectateursCrees.add(spectateur);
			System.out.println("Spectateur " + spectateur + " créé avec succès.");
    }

    // Création d'un tournoi et préparation du tableau.
    private void creerTournoi() 
    {
        System.out.println("\n--- Création d'un tournoi ---");
        System.out.print("Nom du tournoi (ex: Roland-Garros) : ");
        String nom = scanner.nextLine();

        TournoiType type = demanderTypeTournoi();

        tournoiEnCours = new Tournoi(nom, LocalDate.now().getYear(), type);

        if (!joueursCrees.isEmpty())
        {
            System.out.println("Inscription des joueurs créés manuellement...");
            joueursCrees.forEach(tournoiEnCours::inscrireParticipant);
        }

			tournoiEnCours.creerParticipantsAutomatiquement();
			// Ajouter arbitres/spectateurs créés manuellement
			if (!arbitresCrees.isEmpty())
			{
				System.out.println("Ajout des arbitres créés manuellement au tournoi...");
				arbitresCrees.forEach(tournoiEnCours::ajouterArbitre);
			}
			if (!spectateursCrees.isEmpty())
			{
				System.out.println("Ajout des spectateurs créés manuellement (réserve)...");
				spectateursCrees.forEach(tournoiEnCours::ajouterSpectateur);
			}

		
		System.out.println("C'est partis pour un nouveau tournoi!!");
        tournoiEnCours.lancerProchainTour();

        System.out.println("Le tournoi " + tournoiEnCours.getNom() + " (" + tournoiEnCours.getVille() + ", surface "
                + tournoiEnCours.getSurface() + ") est prêt. Utilisez le menu \"Gérer le tournoi\" pour lancer les matchs.");
    }

    // Sous-menu pour piloter le tournoi déjà créé.
    private void gererTournoi() throws SaisieInvalideException 
    {
        while (true) 
        {
            System.out.println("\n--- Gestion du tournoi : " + tournoiEnCours.getNom() + " ---");
            System.out.println("1. Afficher les matchs à venir");
            System.out.println("2. Jouer un match");
            System.out.println("3. Afficher les matchs déjà joués");
            System.out.println("4. Lancer le tour suivant");
            System.out.println("5. Ajouter les arbitres créés au tournoi");
            System.out.println("6. Ajouter les spectateurs créés (réserve)");
            System.out.println("7. Afficher les stats d'un joueur");
            System.out.println("8. Jouer le tour complet (auto)");
            System.out.println("0. Retour au menu principal");

            int choix = demanderChoixUtilisateur();
            switch (choix) 
            {
                case 1 -> tournoiEnCours.afficherMatchsAVenir();
                case 2 -> jouerUnMatchDuTournoi();
                case 3 -> tournoiEnCours.afficherMatchsJoues();
                case 4 -> tournoiEnCours.lancerProchainTour();
                case 5 -> {
                    if (arbitresCrees.isEmpty()) { System.out.println("Aucun arbitre créé manuellement."); }
                    else { arbitresCrees.forEach(tournoiEnCours::ajouterArbitre); System.out.println("Arbitres ajoutés."); }
                }
                case 6 -> {
                    if (spectateursCrees.isEmpty()) { System.out.println("Aucun spectateur créé manuellement."); }
                    else { spectateursCrees.forEach(tournoiEnCours::ajouterSpectateur); System.out.println("Spectateurs ajoutés."); }
                }
                case 7 -> afficherStatsJoueurDepuisTournoi();
                case 8 -> jouerTourCompletDuTournoi();
                case 0 -> { return; }
                default -> throw new SaisieInvalideException("Choix invalide dans la gestion du tournoi.");
            }
        }
    }

    private void afficherStatsJoueurDepuisTournoi()
    {
        List<Joueur> participants = tournoiEnCours.getParticipants();
        if (participants.isEmpty())
        {
            System.out.println("Aucun participant disponible.");
            return;
        }
        System.out.println("\nSélectionner un joueur:");
        for (int i = 0; i < participants.size(); i++)
        {
            System.out.println((i + 1) + ". " + participants.get(i));
        }
        System.out.print("Votre choix : ");
        int index = demanderChoixUtilisateur() - 1;
        if (index < 0 || index >= participants.size())
        {
            System.out.println("Choix invalide.");
            return;
        }
        Joueur choisi = participants.get(index);
        tournoiEnCours.afficherStatsPourJoueur(choisi);
    }

    // Choisir un match et le jouer (manuel ou auto).
		private void jouerUnMatchDuTournoi() throws SaisieInvalideException 
    {
        List<Match> matchsAVenir = tournoiEnCours.getMatchsAVenir();
        if (matchsAVenir.isEmpty()) 
        {
            System.out.println("Aucun match disponible. Lancez le prochain tour.");
            return;
        }

        System.out.println("\nMatchs à jouer :");
        for (int i = 0; i < matchsAVenir.size(); i++) 
        {
            System.out.println((i + 1) + ". " + matchsAVenir.get(i));
        }

        System.out.print("Sélectionnez un match : ");
        int index = demanderChoixUtilisateur() - 1;
        if (index < 0 || index >= matchsAVenir.size()) 
        {
            throw new SaisieInvalideException("Numéro de match invalide.");
        }

        System.out.print("Mode de jeu (1: Manuel, 2: Automatique) : ");
        int choixMode = demanderChoixUtilisateur();
        if (choixMode != 1 && choixMode != 2)
        {
            throw new SaisieInvalideException("Mode de jeu inexistant.");
        }
        ModeJeu mode = (choixMode == 1) ? ModeJeu.MANUEL : ModeJeu.AUTOMATIQUE;

			boolean afficherDetails = true;
			if (mode == ModeJeu.AUTOMATIQUE)
			{
				System.out.print("Afficher le détail du match ? (1: oui, 2: non) : ");
				int choixDetails = demanderChoixUtilisateur();
				afficherDetails = (choixDetails == 1);
			}

			tournoiEnCours.jouerMatch(matchsAVenir.get(index), mode, afficherDetails);

        if (!tournoiEnCours.estTermine() && tournoiEnCours.getMatchsAVenir().isEmpty())
        {
            System.out.println("Fin du tour. Préparation du tour suivant...");
            tournoiEnCours.lancerProchainTour();
        }
    }

    // Petit menu pour choisir le type de tournoi.
    private TournoiType demanderTypeTournoi()
    {
        System.out.println("Choisissez le type de tournoi :");
        TournoiType[] types = TournoiType.values();
        for (int i = 0; i < types.length; i++)
        {
            System.out.println((i + 1) + ". " + types[i].name() + " (" + types[i].getVille() + " - "
                    + types[i].getSurface() + ")");
        }

        while (true)
        {
            System.out.print("Votre choix : ");
            String saisie = scanner.nextLine();
            try
            {
                int choix = Integer.parseInt(saisie);
                if (choix >= 1 && choix <= types.length)
                {
                    return types[choix - 1];
                }
            }
            catch (NumberFormatException e)
            {
                // boucle
            }
            System.out.println("Choix invalide, merci de recommencer.");
        }
    }
    private void jouerTourCompletDuTournoi() throws SaisieInvalideException {
        if (tournoiEnCours == null) {
            System.out.println("Aucun tournoi en cours.");
            return;
        }

    // Choix du mode
        System.out.print("Mode de jeu (1: Manuel, 2: Automatique) : ");
        int choixMode = demanderChoixUtilisateur();
        if (choixMode != 1 && choixMode != 2) {
            System.out.println("Mode invalide.");
            return;
        }
        ModeJeu mode = (choixMode == 1) ? ModeJeu.MANUEL : ModeJeu.AUTOMATIQUE;

        boolean afficherDetails = true;
        if (mode == ModeJeu.AUTOMATIQUE) {
            System.out.print("Afficher le détail du match ? (1: oui, 2: non) : ");
            int choixDetails = demanderChoixUtilisateur();
            afficherDetails = (choixDetails == 1);
        }
        tennis.personnages.Joueur.setAfficherEncouragements(false);
        try {
            tournoiEnCours.jouerTourComplet(mode, afficherDetails);
        } finally {
    
            tennis.personnages.Joueur.setAfficherEncouragements(true);
        }
    
        
    }
    // Retourne le genre suivant la saisie (H/F).
    private Personne.Genre demanderGenreJoueur()
    {
        while (true)
        {
            System.out.print("Genre (1: Homme, 2: Femme) : ");
            String saisie = scanner.nextLine();
            if ("1".equals(saisie))
            {
                return Personne.Genre.HOMME;
            }
            if ("2".equals(saisie))
            {
                return Personne.Genre.FEMME;
            }
            System.out.println("Saisie invalide, veillez recommencer.");
        }
    }

    // Petit listing des joueurs déjà créés.
    private void afficherInfosJoueurs() 
    {
        if (joueursCrees.isEmpty()) 
        {
            System.out.println("Aucun joueur créé pour le moment.");
            return;
        }

        System.out.println("\n--- Joueurs créés ---");
        joueursCrees.forEach(joueur -> {
            System.out.println(joueur);
            joueur.getStatsCarriere().afficher();
        });
    }

    // Génère N joueurs automatiques et les ajoute à la liste des joueurs créés
    private void genererJoueursAutomatiques()
    {
        System.out.print("Combien de joueurs générer automatiquement ? ");
        String saisie = scanner.nextLine();
        int n;
        try
        {
            n = Integer.parseInt(saisie);
        }
        catch (NumberFormatException e)
        {
            System.out.println("Nombre invalide.");
            return;
        }
        if (n <= 0)
        {
            System.out.println("Rien à générer.");
            return;
        }

        String[] prenomsH = {"Arthur","Lucas","Noah","Ethan","Adam","Liam","Hugo","Paul","Nathan","Enzo"};
        String[] prenomsF = {"Emma","Jade","Lou","Alice","Lina","Mia","Lea","Chloe","Anna","Rose"};
        String[] noms = {"Martin","Bernard","Petit","Durand","Dubois","Moreau","Laurent","Simon","Michel","Lefebvre"};
        String[] sponsors = {"Babolat","Wilson","Head","Yonex","Lacoste"};
        String[] coachs = {"Coach A","Coach B","Coach C","Coach D"};
        String[] couleurs = {"Blanc","Bleu","Rouge","Vert","Noir"};

        for (int i = 0; i < n; i++)
        {
            boolean homme = (i % 2 == 0);
            Personne.Genre genre = homme ? Personne.Genre.HOMME : Personne.Genre.FEMME;
            String prenom = (homme ? prenomsH : prenomsF)[i % 10];
            String nom = noms[i % noms.length];
            LocalDate naissance = LocalDate.of(1985 + (i % 15), 1 + (i % 12), 1 + (i % 28));
            String sponsor = sponsors[i % sponsors.length];
            String coach = coachs[i % coachs.length];
            String couleur = couleurs[i % couleurs.length];

            Joueur joueur = new Joueur(nom, prenom, genre, naissance, "Généré",
                    Joueur.MainDeJeu.DROITIER, sponsor, coach, couleur, 5);
            joueursCrees.add(joueur);
        }
        System.out.println(n + " joueur(s) ajoutés à la liste locale. Ils seront inscrits au prochain tournoi créé.");
    }
}

