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
                    case 6 -> afficherDescriptionPersonnage();
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
        System.out.println("6. Afficher la description complète d'une personne");
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
		
		// Date de naissance
		System.out.print("Année de naissance (ex: 1995) : ");
		int annee = lireEntier(1950, 2010, 1995);
		System.out.print("Mois de naissance (1-12) : ");
		int mois = lireEntier(1, 12, 6);
		System.out.print("Jour de naissance (1-31) : ");
		int jour = lireEntier(1, 31, 15);
		LocalDate dateNaissance = LocalDate.of(annee, mois, jour);
		
		// Lieu de naissance
		System.out.print("Lieu de naissance : ");
		String lieuNaissance = scanner.nextLine();
		if (lieuNaissance.trim().isEmpty()) lieuNaissance = "Inconnu";
		
		// Main de jeu
		System.out.print("Main de jeu (1: Droitier, 2: Gaucher) : ");
		String choixMain = scanner.nextLine();
		Joueur.MainDeJeu mainDeJeu = "2".equals(choixMain) ? Joueur.MainDeJeu.GAUCHER : Joueur.MainDeJeu.DROITIER;
		
		// Sponsor
		System.out.print("Sponsor : ");
		String sponsor = scanner.nextLine();
		if (sponsor.trim().isEmpty()) sponsor = "Aucun sponsor";
		
		// Entraîneur
		System.out.print("Entraîneur : ");
		String entraineur = scanner.nextLine();
		if (entraineur.trim().isEmpty()) entraineur = "Aucun entraîneur";
		
		// Couleur favorite
		System.out.print("Couleur favorite : ");
		String couleur = scanner.nextLine();
		if (couleur.trim().isEmpty()) couleur = "Blanc";
		
		// Niveau (réputation)
		System.out.print("Niveau/Réputation (1-10) : ");
		int niveau = lireEntier(1, 10, 5);

		// Création du joueur avec le constructeur existant
		Joueur joueur = new Joueur(nom, prenom, genre, dateNaissance, lieuNaissance,
				mainDeJeu, sponsor, entraineur, couleur, niveau);
		
		// Ajout des informations complémentaires via setters
		System.out.print("Nationalité (optionnel, Entrée pour passer) : ");
		String nationalite = scanner.nextLine();
		if (!nationalite.trim().isEmpty()) {
			joueur.setNationalite(nationalite);
		}
		
		System.out.print("Taille en cm (ex: 180, Entrée pour passer) : ");
		String tailleStr = scanner.nextLine();
		if (!tailleStr.trim().isEmpty()) {
			try {
				joueur.setTailleCm(Integer.parseInt(tailleStr));
			} catch (Exception e) {
				System.out.println("Taille invalide, ignorée.");
			}
		}
		
		System.out.print("Poids en kg (ex: 75, Entrée pour passer) : ");
		String poidsStr = scanner.nextLine();
		if (!poidsStr.trim().isEmpty()) {
			try {
				joueur.setPoidsKg(Integer.parseInt(poidsStr));
			} catch (Exception e) {
				System.out.println("Poids invalide, ignoré.");
			}
		}
		
		System.out.print("Surnom (optionnel, Entrée pour passer) : ");
		String surnom = scanner.nextLine();
		if (!surnom.trim().isEmpty()) {
			joueur.setSurnom(surnom);
		}
		
		// État matrimonial
		System.out.print("Est marié(e) ? (o/n) : ");
		String mariageReponse = scanner.nextLine();
		if (mariageReponse.equalsIgnoreCase("o")) {
			System.out.print("Nom du conjoint : ");
			String nomConjoint = scanner.nextLine();
			if (!nomConjoint.trim().isEmpty()) {
				joueur.seMarie(nomConjoint);
			}
		}
		
		joueursCrees.add(joueur);
		System.out.println("Joueur " + joueur + " créé avec succès.");
	}		private void creerArbitre()
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
		
		// Date de naissance
		System.out.print("Année de naissance (ex: 1985) : ");
		int annee = lireEntier(1950, 2010, 1985);
		System.out.print("Mois de naissance (1-12) : ");
		int mois = lireEntier(1, 12, 6);
		System.out.print("Jour de naissance (1-31) : ");
		int jour = lireEntier(1, 31, 15);
		LocalDate dateNaissance = LocalDate.of(annee, mois, jour);
		
		// Lieu de naissance
		System.out.print("Lieu de naissance : ");
		String lieuNaissance = scanner.nextLine();
		if (lieuNaissance.trim().isEmpty()) lieuNaissance = "Non spécifié";
		
		System.out.println("Choisissez le type de billet :");
		System.out.println("1. Latérale - 30€ (Tribune B)");
		System.out.println("2. Centre court - 75€ (Tribune A)");
		System.out.println("3. VIP - 150€ (Tribune VIP)");
		System.out.print("Votre choix : ");
		String choixBillet = scanner.nextLine();
		
		double prix;
		String nomTribune;
		switch (choixBillet) {
			case "2" -> {
				prix = 75.0;
				nomTribune = "Tribune A";
			}
			case "3" -> {
				prix = 150.0;
				nomTribune = "Tribune VIP";
			}
			default -> {
				prix = 30.0;
				nomTribune = "Tribune B";
			}
		}
		
		System.out.print("Numéro de place : ");
		int numeroPlace = lireEntier(1, 500, 1);
		
		tennis.personnages.Spectateur spectateur = new tennis.personnages.Spectateur(
				nom, prenom, genre, dateNaissance, lieuNaissance,
				prix, nomTribune, numeroPlace);
		
		// Informations optionnelles
		System.out.print("Nationalité (optionnel, Entrée pour passer) : ");
		String nationalite = scanner.nextLine();
		if (!nationalite.trim().isEmpty()) {
			spectateur.setNationalite(nationalite);
		}
		
		System.out.print("Taille en cm (ex: 170, Entrée pour passer) : ");
		String tailleStr = scanner.nextLine();
		if (!tailleStr.trim().isEmpty()) {
			try {
				spectateur.setTailleCm(Integer.parseInt(tailleStr));
			} catch (Exception e) {
				System.out.println("Taille invalide, ignorée.");
			}
		}
		
		System.out.print("Poids en kg (ex: 65, Entrée pour passer) : ");
		String poidsStr = scanner.nextLine();
		if (!poidsStr.trim().isEmpty()) {
			try {
				spectateur.setPoidsKg(Integer.parseInt(poidsStr));
			} catch (Exception e) {
				System.out.println("Poids invalide, ignoré.");
			}
		}
		
		System.out.print("Surnom (optionnel, Entrée pour passer) : ");
		String surnom = scanner.nextLine();
		if (!surnom.trim().isEmpty()) {
			spectateur.setSurnom(surnom);
		}
		
		// État matrimonial
		System.out.print("Est marié(e) ? (o/n) : ");
		String mariageReponse = scanner.nextLine();
		if (mariageReponse.equalsIgnoreCase("o")) {
			System.out.print("Nom du conjoint : ");
			String nomConjoint = scanner.nextLine();
			if (!nomConjoint.trim().isEmpty()) {
				spectateur.seMarie(nomConjoint);
			}
		}
		
		// Si c'est un homme, demander la couleur de chemise (seulement pour hommes)
		if (genre == Personne.Genre.HOMME)
		{
			System.out.print("Couleur de la chemise : ");
			String couleur = scanner.nextLine();
			if (!couleur.trim().isEmpty())
			{
				spectateur.changerCouleurChemise(couleur);
			}
		}
		// Les femmes ont automatiquement des lunettes (déjà initialisé dans le constructeur)
		
		spectateursCrees.add(spectateur);
		System.out.println("Spectateur " + spectateur + " créé avec succès.");
    }    // Création d'un tournoi et préparation du tableau.
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

			Match matchSelectionne = matchsAVenir.get(index);
			tournoiEnCours.jouerMatch(matchSelectionne, mode, afficherDetails);

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
        String[] sponsors = {"Adidas","Nike","Decathlon","Rolex","Lacoste"};
        String[] coachs = {"Jack Johnson","Emmanuel Macron","Raphael Nadal","Dwayne Johnson"};
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

    // Nouveau choix 9 : afficher description complète d'une personne avec âge
    private void afficherDescriptionPersonnage()
    {
        System.out.println("\n--- Description d'une personne ---");
        System.out.println("1. Décrire un joueur");
        System.out.println("2. Décrire un arbitre");
        System.out.println("3. Décrire un spectateur");
        System.out.print("Votre choix : ");
        String choix = scanner.nextLine();
        
        switch (choix)
        {
            case "1" -> afficherDescriptionJoueur();
            case "2" -> afficherDescriptionArbitre();
            case "3" -> afficherDescriptionSpectateur();
            default -> System.out.println("Choix invalide.");
        }
    }

    private void afficherDescriptionJoueur()
    {
        // Récupérer tous les joueurs (manuels + tournoi)
        List<Joueur> tousJoueurs = new ArrayList<>(joueursCrees);
        if (tournoiEnCours != null)
        {
            tousJoueurs.addAll(tournoiEnCours.getParticipants());
        }
        
        if (tousJoueurs.isEmpty())
        {
            System.out.println("Aucun joueur disponible. Créez-en un d'abord (option 1) ou créez un tournoi.");
            return;
        }
        
        System.out.println("\n--- Sélection d'un joueur ---");
        for (int i = 0; i < tousJoueurs.size(); i++)
        {
            System.out.println((i + 1) + ". " + tousJoueurs.get(i));
        }
        System.out.print("Votre choix : ");
        int index = demanderChoixUtilisateur() - 1;
        
        if (index < 0 || index >= tousJoueurs.size())
        {
            System.out.println("Choix invalide.");
            return;
        }
        
        Joueur joueur = tousJoueurs.get(index);
        System.out.println("\n=== DESCRIPTION COMPLÈTE DU JOUEUR ===");
        System.out.println("Nom complet : " + joueur.getPrenom() + " " + joueur.getNomCourant());
        System.out.println("Genre : " + joueur.getGenre());
        System.out.println("Âge : " + joueur.getAge() + " ans (" + joueur.getAgeEnJours() + " jours)");
        System.out.println("Date de naissance : " + joueur.getDateNaissance());
        System.out.println("Lieu de naissance : " + joueur.getLieuNaissance());
        System.out.println("Taille : " + joueur.getTailleCm() + " cm");
        System.out.println("Poids : " + joueur.getPoidsKg() + " kg");
        System.out.println("Main de jeu : " + joueur.getMainDeJeu());
        System.out.println("Sponsor : " + joueur.getSponsor());
        System.out.println("Entraîneur : " + joueur.getEntraineur());
        
        
        if (joueur.aUnSurnom())
        {
            System.out.println("Surnom : " + joueur.getSurnom());
        }
        if (joueur.estMariee())
        {
            System.out.println("Marié(e) avec : " + joueur.getNomDuConjoint());
        }
        System.out.println("\n--- Statistiques de carrière ---");
        joueur.getStatsCarriere().afficher();
        System.out.println("=====================================\n");
    }

    private void afficherDescriptionArbitre()
    {
        // Récupérer tous les arbitres (manuels + tournoi)
        List<Arbitre> tousArbitres = new ArrayList<>(arbitresCrees);
        if (tournoiEnCours != null)
        {
            tousArbitres.addAll(tournoiEnCours.getArbitres());
        }
        
        if (tousArbitres.isEmpty())
        {
            System.out.println("Aucun arbitre disponible. Créez-en un d'abord (option 1) ou créez un tournoi.");
            return;
        }
        
        System.out.println("\n--- Sélection d'un arbitre ---");
        for (int i = 0; i < tousArbitres.size(); i++)
        {
            System.out.println((i + 1) + ". " + tousArbitres.get(i));
        }
        System.out.print("Votre choix : ");
        int index = demanderChoixUtilisateur() - 1;
        
        if (index < 0 || index >= tousArbitres.size())
        {
            System.out.println("Choix invalide.");
            return;
        }
        
        Arbitre arbitre = tousArbitres.get(index);
        System.out.println("\n=== DESCRIPTION COMPLÈTE DE L'ARBITRE ===");
        System.out.println("Nom complet : " + arbitre.getPrenom() + " " + arbitre.getNomCourant());
        System.out.println("Genre : " + arbitre.getGenre());
        System.out.println("Âge : " + arbitre.getAge() + " ans (" + arbitre.getAgeEnJours() + " jours)");
        System.out.println("Date de naissance : " + arbitre.getDateNaissance());
        System.out.println("Lieu de naissance : " + arbitre.getLieuNaissance());
        if (arbitre.getTailleCm() > 0)
        {
            System.out.println("Taille : " + arbitre.getTailleCm() + " cm");
        }
        if (arbitre.getPoidsKg() > 0)
        {
            System.out.println("Poids : " + arbitre.getPoidsKg() + " kg");
        }
        if (arbitre.aUnSurnom())
        {
            System.out.println("Surnom : " + arbitre.getSurnom());
        }
        if (arbitre.estMariee())
        {
            System.out.println("Marié(e) avec : " + arbitre.getNomDuConjoint());
        }
        System.out.println("=========================================\n");
    }

    private void afficherDescriptionSpectateur()
    {
        // Récupérer tous les spectateurs (manuels + tournoi)
        List<tennis.personnages.Spectateur> tousSpectateurs = new ArrayList<>(spectateursCrees);
        if (tournoiEnCours != null)
        {
            tousSpectateurs.addAll(tournoiEnCours.getSpectateurs());
        }
        
        if (tousSpectateurs.isEmpty())
        {
            System.out.println("Aucun spectateur disponible. Créez-en un d'abord (option 1) ou créez un tournoi.");
            return;
        }
        
        System.out.println("\n--- Sélection d'un spectateur ---");
        for (int i = 0; i < tousSpectateurs.size(); i++)
        {
            System.out.println((i + 1) + ". " + tousSpectateurs.get(i));
        }
        System.out.print("Votre choix : ");
        int index = demanderChoixUtilisateur() - 1;
        
        if (index < 0 || index >= tousSpectateurs.size())
        {
            System.out.println("Choix invalide.");
            return;
        }
        
        tennis.personnages.Spectateur spectateur = tousSpectateurs.get(index);
        System.out.println("\n=== DESCRIPTION COMPLÈTE DU SPECTATEUR ===");
        System.out.println("Nom complet : " + spectateur.getPrenom() + " " + spectateur.getNomCourant());
        System.out.println("Genre : " + spectateur.getGenre());
        System.out.println("Place : " + spectateur.getPlace());
        System.out.println("Prix du billet : " + spectateur.getPrixBillet() + " €");
        
        // Affichage des caractéristiques spécifiques au genre
        if (spectateur.getGenre() == Personne.Genre.HOMME)
        {
            System.out.println("Couleur de chemise : " + spectateur.getCouleurChemise());
        }
        else
        {
            System.out.println("Porte des lunettes : " + (spectateur.porteLunettes() ? "Oui" : "Non"));
        }
        
        if (spectateur.aUnSurnom())
        {
            System.out.println("Surnom : " + spectateur.getSurnom());
        }
        if (spectateur.estMariee())
        {
            System.out.println("Marié(e) avec : " + spectateur.getNomDuConjoint());
        }
        System.out.println("==========================================\n");
    }
    
    // Méthode utilitaire pour lire un entier avec validation et valeur par défaut
    private int lireEntier(int min, int max, int defaut)
    {
        String saisie = scanner.nextLine();
        if (saisie.trim().isEmpty())
        {
            return defaut;
        }
        try
        {
            int valeur = Integer.parseInt(saisie);
            if (valeur < min || valeur > max)
            {
                System.out.println("Valeur hors limites [" + min + "-" + max + "], utilisation de " + defaut);
                return defaut;
            }
            return valeur;
        }
        catch (NumberFormatException e)
        {
            System.out.println("Entrée invalide, utilisation de " + defaut);
            return defaut;
        }
    }
}

