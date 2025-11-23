package tennis.tournoi;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tennis.erreurs.SaisieInvalideException;
import tennis.jeu.CategorieMatch;
import tennis.jeu.Match;
import tennis.jeu.ModeJeu;
import tennis.personnages.Arbitre;
import tennis.personnages.Joueur;
import tennis.personnages.Personne;
import tennis.personnages.Spectateur;
import tennis.stats.StatistiquesMatch;

/**
 * Gère un tournoi du Grand Chelem avec deux tableaux (simple hommes et simple femmes).
 * <p>
 * - Maintient les participants (H/F), prépare les tours (jusqu'à 128 joueurs par catégorie)
 * - Planifie les matchs d'un tour, attribue arbitres et spectateurs
 * - Joue les matchs (manuel/auto), enregistre résultats et statistiques
 * - Suit l'avancement (matchs à venir/joués), détermine les champions
 * - Affiche une synthèse finale avec quelques agrégats
 */
public class Tournoi 
{
    private final String nom;
    private final int annee;
    private final TournoiType type;

    private final List<Joueur> participantsHommes = new ArrayList<>();
    private final List<Joueur> participantsFemmes = new ArrayList<>();
    private final List<Arbitre> arbitres = new ArrayList<>();
    // Petit réservoir pour des spectateurs ajoutés manuellement pendant le tournoi
    private final List<Spectateur> reservesSpectateurs = new ArrayList<>();

    private final List<Joueur> joueursEnLiceHommes = new ArrayList<>();
    private final List<Joueur> joueursEnLiceFemmes = new ArrayList<>();
    private final List<Match> matchsAVenir = new ArrayList<>();
    private final List<Match> matchsJoues = new ArrayList<>();

    private int numeroTourHommes = 0;
    private int numeroTourFemmes = 0;
    private boolean tableauHommesTermine = false;
    private boolean tableauFemmesTermine = false;
    private boolean tournoiTermine = false;
    private Joueur championHommes;
    private Joueur championFemmes;

	/**
	 * Crée un tournoi identifié par son nom, son année et son type (ville/surface, tie-break dernier set).
	 *
	 * @param nom libellé du tournoi (ex: "Roland-Garros")
	 * @param annee année de l'édition
	 * @param type type de tournoi (définit ville/surface/règle tie-break du dernier set)
	 */
    public Tournoi(String nom, int annee, TournoiType type) 
    {
        this.nom = nom;
        this.annee = annee;
        this.type = type;
    }

	/**
	 * Intitulé lisible du tournoi incluant l'année.
	 *
	 * @return ex: "Roland-Garros 2025"
	 */
    public String getNom() 
    {
        return nom + " " + annee;
    }

	/** Type de tournoi (ville, surface, règle tie-break dernier set). */
    public TournoiType getType()
    {
        return type;
    }

	/** Ville hôte (issue du type de tournoi). */
    public String getVille()
    {
        return type.getVille();
    }

	/** Surface de l'édition (issue du type de tournoi). */
    public String getSurface()
    {
        return type.getSurface();
    }

	/** Indique si le tournoi est terminé (les deux tableaux H/F ont un champion). */
    public boolean estTermine() 
    {
        return tournoiTermine;
    }

	/** Matchs à venir pour le tour en cours (copie non modifiable). */
    public List<Match> getMatchsAVenir() 
    {
        return Collections.unmodifiableList(matchsAVenir);
    }

	/** Matchs déjà joués depuis le début du tournoi (copie non modifiable). */
    public List<Match> getMatchsJoues() 
    {
        return Collections.unmodifiableList(matchsJoues);
    }

    // Liste complète de tous les participants (hommes + femmes)
    public List<Joueur> getParticipants()
    {
        List<Joueur> tous = new ArrayList<>();
        tous.addAll(participantsHommes);
        tous.addAll(participantsFemmes);
        return Collections.unmodifiableList(tous);
    }

    // Liste de tous les arbitres du tournoi
    public List<Arbitre> getArbitres()
    {
        return Collections.unmodifiableList(arbitres);
    }

    // Liste de tous les spectateurs (réserve manuelle + ceux générés pour les matchs)
    public List<Spectateur> getSpectateurs()
    {
        List<Spectateur> tous = new ArrayList<>(reservesSpectateurs);
        // Ajouter aussi tous les spectateurs présents dans les matchs déjà joués
        for (Match match : matchsJoues)
        {
            tous.addAll(match.getSpectateurs());
        }
        // Et ceux des matchs à venir
        for (Match match : matchsAVenir)
        {
            tous.addAll(match.getSpectateurs());
        }
        return Collections.unmodifiableList(tous);
    }

    // Affiche les stats d'un joueur pour chacun de ses matchs joués dans le tournoi
    public void afficherStatsPourJoueur(Joueur joueur)
    {
        if (joueur == null)
        {
            System.out.println("Joueur non valide.");
            return;
        }
        boolean trouve = false;
        for (Match match : matchsJoues)
        {
            if (match.getJoueur1() == joueur || match.getJoueur2() == joueur)
            {
                trouve = true;
                StatistiquesMatch s = match.getStatistiques(joueur);
                System.out.println(match + " -> " + match.getScoreFinal());
                System.out.println("  " + joueur.getPrenom() + " - Aces: " + s.getAces()
                        + " | DF: " + s.getDoubleFautes()
                        + " | 1er serv: " + s.getPremiersServicesReussis() + "/" + s.getPremiersServicesJoues()
                        + " (" + pourcentage(s.getPremiersServicesReussis(), s.getPremiersServicesJoues()) + "%)"
                        + " | 2e serv: " + s.getSecondsServicesReussis() + "/" + s.getSecondsServicesJoues()
                        + " (" + pourcentage(s.getSecondsServicesReussis(), s.getSecondsServicesJoues()) + "%)"
                        + " | Breaks: " + s.getBallesDeBreakConverties() + "/" + s.getBallesDeBreakObtenues()
                        + " | Points gagnés: " + s.getTotalPointsRemportes());
            }
        }
        if (!trouve)
        {
            System.out.println("Aucune statistique: le joueur n'a pas encore joué de match dans ce tournoi.");
        }
    }
	/** Ajoute un arbitre pendant le tournoi (utilisé pour les prochains matchs). */
    public void ajouterArbitre(Arbitre arbitre)
    {
        if (arbitre != null && !arbitres.contains(arbitre))
        {
            arbitres.add(arbitre);
            System.out.println("Arbitre ajouté au tournoi: " + arbitre);
        }
    }

	/** Ajoute un spectateur manuel dans une réserve intégrée aux prochains matchs. */
    public void ajouterSpectateur(Spectateur spectateur)
    {
        if (spectateur != null)
        {
            reservesSpectateurs.add(spectateur);
            System.out.println("Spectateur ajouté au réservoir: " + spectateur);
        }
    }

	/**
	 * Inscrit un participant dans la catégorie correspondant à son genre (limite 128).
	 *
	 * @param joueur joueur à inscrire
	 */
    public void inscrireParticipant(Joueur joueur)
    {
        List<Joueur> participants = (joueur.getGenre() == Personne.Genre.HOMME) ? participantsHommes : participantsFemmes;

        if (participants.contains(joueur))
        {
            System.out.println(joueur + " est déjà inscrit au tournoi.");
            return;
        }

        if (participants.size() >= 128)
        {
            System.out.println("Il y a déjà 128 joueurs inscrits dans cette catégorie. " + joueur + " ne peut pas être ajouté.");
            return;
        }

        participants.add(joueur);
        joueur.getStatsCarriere().ajouterParticipationTournoi();
    }

	/**
	 * Prépare le tournoi: réinitialisation, complétion automatique des tableaux à 128,
	 * et ajout d'arbitres si nécessaire.
	 */
	public void creerParticipantsAutomatiquement() 
    {
        joueursEnLiceHommes.clear();
        joueursEnLiceFemmes.clear();
        matchsAVenir.clear();
        matchsJoues.clear();
        numeroTourHommes = 0;
        numeroTourFemmes = 0;
        tableauHommesTermine = false;
        tableauFemmesTermine = false;
        tournoiTermine = false;
        championHommes = null;
        championFemmes = null;

        ajusterParticipants(participantsHommes, Personne.Genre.HOMME, "AutoH");
        ajusterParticipants(participantsFemmes, Personne.Genre.FEMME, "AutoF");
        completerArbitres();

        System.out.println("Le tableau messieurs compte " + participantsHommes.size() + " joueurs.");
        System.out.println("Le tableau dames compte " + participantsFemmes.size() + " joueuses.");
        System.out.println(arbitres.size() + " arbitres disponibles.");
    }

	/** Prépare le prochain tour et crée les matchs à venir s'il y a assez de joueurs. */
	public void lancerProchainTour() 
    {
        if (tournoiTermine) 
        {
			System.out.println("Le tournoi est déjà terminé.");
			// on s'emballe pas, si tout est fini on n'ajoute pas de nouveaux matchs
            return;
        }

        if (!matchsAVenir.isEmpty()) 
        {
            System.out.println("Terminez d'abord les matchs du tour en cours.");
            return;
        }

        boolean aCreeDesMatchs = false;

        if (!tableauHommesTermine)
        {
            if (numeroTourHommes == 0)
            {
                joueursEnLiceHommes.addAll(participantsHommes);
            }
            if (joueursEnLiceHommes.size() < 2)
            {
                tableauHommesTermine = true;
                if (!joueursEnLiceHommes.isEmpty())
                {
                    championHommes = joueursEnLiceHommes.get(0);
                }
            }
            else
            {
                numeroTourHommes++;
                System.out.println("Préparation du tour messieurs " + numeroTourHommes + " (" + joueursEnLiceHommes.size() + " joueurs).");
                aCreeDesMatchs |= preparerMatchesCategorie(joueursEnLiceHommes, CategorieMatch.SIMPLE_HOMMES, numeroTourHommes);
            }
        }

        if (!tableauFemmesTermine)
        {
            if (numeroTourFemmes == 0)
            {
                joueursEnLiceFemmes.addAll(participantsFemmes);
            }
            if (joueursEnLiceFemmes.size() < 2)
            {
                tableauFemmesTermine = true;
                if (!joueursEnLiceFemmes.isEmpty())
                {
                    championFemmes = joueursEnLiceFemmes.get(0);
                }
            }
            else
            {
                numeroTourFemmes++;
                System.out.println("Préparation du tour dames " + numeroTourFemmes + " (" + joueursEnLiceFemmes.size() + " joueuses).");
                aCreeDesMatchs |= preparerMatchesCategorie(joueursEnLiceFemmes, CategorieMatch.SIMPLE_FEMMES, numeroTourFemmes);
            }
        }

        if (!aCreeDesMatchs)
        {
            tournoiTermine = true;
            afficherChampions();
        }
    }

	public void jouerMatch(Match match, ModeJeu mode, boolean afficherDetails) throws SaisieInvalideException
    {
        if (!matchsAVenir.contains(match)) 
        {
            throw new SaisieInvalideException("Le match sélectionné n'est pas prévu dans le tour en cours.");
        }

		match.jouerMatch(mode, afficherDetails);
        matchsAVenir.remove(match);
        matchsJoues.add(match);

        CategorieMatch categorie = match.getCategorie();
        double multiplicateurTour = (categorie == CategorieMatch.SIMPLE_HOMMES) ? numeroTourHommes : numeroTourFemmes;
        double gainVainqueur = 10000.0 * Math.max(1, multiplicateurTour);
        double gainPerdant = 5000.0 * Math.max(1, multiplicateurTour);

        match.getVainqueur().getStatsCarriere().enregistrerMatch(true, gainVainqueur);
        match.getPerdant().getStatsCarriere().enregistrerMatch(false, gainPerdant);
        match.getStatistiquesParJoueur().forEach((joueur, stats) -> joueur.getStatsCarriere().enregistrerStatsMatch(stats));

		// Petit ajustement du classement: le vainqueur monte (classement numérique baisse), le perdant descend
		Joueur vainqueur = match.getVainqueur();
		Joueur perdant = match.getPerdant();
		if (vainqueur != null && perdant != null)
		{
			int classementVainqueur = vainqueur.getClassement();
			int classementPerdant = perdant.getClassement();
			vainqueur.setClassement(Math.max(1, classementVainqueur - 1));
			perdant.setClassement(classementPerdant + 1);
		}

        if (categorie == CategorieMatch.SIMPLE_HOMMES)
        {
            joueursEnLiceHommes.remove(match.getPerdant());
            if (!resteMatchPourCategorie(CategorieMatch.SIMPLE_HOMMES) && joueursEnLiceHommes.size() == 1)
            {
                tableauHommesTermine = true;
                championHommes = joueursEnLiceHommes.get(0);
            }
        }
        else
        {
            joueursEnLiceFemmes.remove(match.getPerdant());
            if (!resteMatchPourCategorie(CategorieMatch.SIMPLE_FEMMES) && joueursEnLiceFemmes.size() == 1)
            {
                tableauFemmesTermine = true;
                championFemmes = joueursEnLiceFemmes.get(0);
            }
        }

        if (tableauHommesTermine && tableauFemmesTermine)
        {
            tournoiTermine = true;
            afficherChampions();
        }
    }
       
    public void jouerTourComplet(tennis.jeu.ModeJeu mode, boolean afficherDetails) throws SaisieInvalideException {
        if (estTermine()) {
            System.out.println("Le tournoi est déjà terminé.");
            return;
        }

    // Boucle jusqu'à ce que le tournoi soit terminé ou qu'aucun match ne puisse être préparé.
        while (true) {
            // Copie de la liste des matchs à venir pour éviter ConcurrentModificationException
            List<Match> aJouer = new ArrayList<>(getMatchsAVenir());

        
            if (aJouer.isEmpty()) {
            
                if (estTermine()) {
                   break;
                }

                System.out.println("Fin du tour en cours — préparation du tour suivant...");
                lancerProchainTour();

            
                if (getMatchsAVenir().isEmpty()) {
                
                    if (estTermine()) break;
                
                    System.out.println("Aucun match préparé pour le prochain tour.");
                    break;
                } else {
                
                    continue;
                }
            }

        
            System.out.println("Joue automatiquement " + aJouer.size() + " match(es) du tour...");
            for (Match match : aJouer) {
            
                if (!getMatchsAVenir().contains(match)) {
                    continue;
                }
            
                jouerMatch(match, mode, afficherDetails);

            
                if (estTermine()) {
                    break;
                }
            }

        
            if (getMatchsAVenir().isEmpty()) {
           
                if (!estTermine()) {
                    System.out.println("Tour terminé — préparation du tour suivant...");
                    lancerProchainTour();
                
                    continue;
                } else {
                    break;
                }
            } else {
            
                continue;
            }
        }

        if (estTermine()) {
            System.out.println("Tournoi terminé.");
        } else {
            System.out.println("Fin de l'exécution automatique (pas de matchs disponibles).");
        }
    }
	/** Affiche la liste des matchs à venir (tour en cours). */
	public void afficherMatchsAVenir() 
    {
        if (matchsAVenir.isEmpty()) 
        {
            System.out.println("Aucun match à venir pour le moment.");
            return;
        }

        for (int i = 0; i < matchsAVenir.size(); i++) 
        {
            System.out.println((i + 1) + ". " + matchsAVenir.get(i));
        }
    }

	/** Affiche les matchs joués et un résumé de statistiques par joueur. */
	public void afficherMatchsJoues() 
    {
        if (matchsJoues.isEmpty()) 
        {
            System.out.println("Aucun match joué pour le moment.");
            return;
        }

		for (Match match : matchsJoues)
		{
			System.out.println(match + " -> " + match.getScoreFinal());

			// Petit zoom statistiques par joueur pour mieux lire la physionomie du match
			// ici on a décidé d'afficher les deux lignes détaillées, c'est plus lisible pendant la démo
			Joueur j1 = match.getJoueur1();
			Joueur j2 = match.getJoueur2();
			StatistiquesMatch s1 = match.getStatistiques(j1);
			StatistiquesMatch s2 = match.getStatistiques(j2);

			System.out.println("  " + j1.getPrenom() + " - Aces: " + s1.getAces()
					+ " | DF: " + s1.getDoubleFautes()
					+ " | 1er serv: " + s1.getPremiersServicesReussis() + "/" + s1.getPremiersServicesJoues()
					+ " (" + pourcentage(s1.getPremiersServicesReussis(), s1.getPremiersServicesJoues()) + "%)"
					+ " | 2e serv: " + s1.getSecondsServicesReussis() + "/" + s1.getSecondsServicesJoues()
					+ " (" + pourcentage(s1.getSecondsServicesReussis(), s1.getSecondsServicesJoues()) + "%)"
					+ " | Breaks: " + s1.getBallesDeBreakConverties() + "/" + s1.getBallesDeBreakObtenues()
					+ " | Points gagnés: " + s1.getTotalPointsRemportes());

			System.out.println("  " + j2.getPrenom() + " - Aces: " + s2.getAces()
					+ " | DF: " + s2.getDoubleFautes()
					+ " | 1er serv: " + s2.getPremiersServicesReussis() + "/" + s2.getPremiersServicesJoues()
					+ " (" + pourcentage(s2.getPremiersServicesReussis(), s2.getPremiersServicesJoues()) + "%)"
					+ " | 2e serv: " + s2.getSecondsServicesReussis() + "/" + s2.getSecondsServicesJoues()
					+ " (" + pourcentage(s2.getSecondsServicesReussis(), s2.getSecondsServicesJoues()) + "%)"
					+ " | Breaks: " + s2.getBallesDeBreakConverties() + "/" + s2.getBallesDeBreakObtenues()
					+ " | Points gagnés: " + s2.getTotalPointsRemportes());
		}
    }

    private void ajusterParticipants(List<Joueur> participants, Personne.Genre genre, String prefix)
    {
        while (participants.size() > 128)
        {
            participants.remove(participants.size() - 1);
        }

        int indexAuto = participants.size() + 1;
		String[] prenomsH = {"Arthur","Lucas","Noah","Ethan","Adam","Liam","Hugo","Paul","Nathan","Enzo"};
		String[] prenomsF = {"Emma","Jade","Lou","Alice","Lina","Mia","Lea","Chloe","Anna","Rose"};
		String[] noms = {"Martin","Bernard","Petit","Durand","Dubois","Moreau","Laurent","Simon","Michel","Lefebvre"};
		String[] sponsors = {"Babolat","Wilson","Head","Yonex","Lacoste"};
		String[] coachs = {"Coach A","Coach B","Coach C","Coach D"};
		String[] couleurs = {"Blanc","Bleu","Rouge","Vert","Noir"};

		while (participants.size() < 128)
		{
			String prenom = (genre == Personne.Genre.HOMME)
					? prenomsH[(indexAuto - 1) % prenomsH.length]
					: prenomsF[(indexAuto - 1) % prenomsF.length];
			String nom = noms[(indexAuto - 1) % noms.length];
			LocalDate naissance = LocalDate.of(1980 + ((indexAuto * 7) % 20), 1 + ((indexAuto * 5) % 12), 1 + ((indexAuto * 3) % 28));
			String sponsor = sponsors[(indexAuto - 1) % sponsors.length];
			String coach = coachs[(indexAuto - 1) % coachs.length];
			String couleur = couleurs[(indexAuto - 1) % couleurs.length];

			Joueur joueur = new Joueur(nom, prenom, genre, naissance, getVille(),
					Joueur.MainDeJeu.DROITIER, sponsor, coach, couleur, 5);
            joueur.getStatsCarriere().ajouterParticipationTournoi();
            participants.add(joueur);
            indexAuto++;
        }
    }

    private void completerArbitres()
    {
        while (arbitres.size() < 10)
        {
            int idx = arbitres.size() + 1;
			String[] prenoms = {"Pierre","Marie","Jean","Sophie","Andre","Luc","Nina","Paul","Laura","Yves"};
			String[] noms = {"Durant","Morel","Garnier","Rousseau","Fontaine","Chevalier","Baron","Robin","Giraud","Menard"};
			String prenom = prenoms[(idx - 1) % prenoms.length];
			String nom = noms[(idx - 1) % noms.length];
			LocalDate naissance = LocalDate.of(1970 + (idx % 20), 1 + (idx % 12), 1 + (idx % 28));
			Personne.Genre genre = (idx % 2 == 0) ? Personne.Genre.HOMME : Personne.Genre.FEMME;
			arbitres.add(new Arbitre(nom, prenom, genre, naissance, getVille()));
        }
    }

    private boolean preparerMatchesCategorie(List<Joueur> joueursEnLice, CategorieMatch categorie, int numeroTour)
    {
        if (joueursEnLice.size() < 2)
        {
            return false;
        }

        if (arbitres.isEmpty())
        {
            System.out.println("Aucun arbitre disponible pour organiser les matchs.");
            tournoiTermine = true;
            return false;
        }

        Collections.shuffle(joueursEnLice);

        for (int i = 0; i < joueursEnLice.size(); i += 2) 
        {
            Joueur j1 = joueursEnLice.get(i);
            Joueur j2 = joueursEnLice.get(i + 1);
            Arbitre arbitre = arbitres.get((matchsAVenir.size() + i / 2) % arbitres.size());
            List<Spectateur> spectateurs = genererSpectateursPourMatch(categorie);
            Match match = new Match(j1, j2, arbitre, categorie,
                    (categorie == CategorieMatch.SIMPLE_HOMMES ? "Tour messieurs " : "Tour dames ") + numeroTour,
                    10, type.isTieBreakDernierSetAutorise(), spectateurs);
            matchsAVenir.add(match);
        }
        return true;
    }

    private List<Spectateur> genererSpectateursPourMatch(CategorieMatch categorie)
    {
        List<Spectateur> liste = new ArrayList<>();
        int nombreSpectateurs = 120;
        for (int i = 0; i < nombreSpectateurs; i++)
        {
            Personne.Genre genre = (i % 2 == 0) ? Personne.Genre.HOMME : Personne.Genre.FEMME;
            LocalDate naissance = LocalDate.of(1970 + (i % 30), 1 + (i % 12), 1 + (i % 28));
            Spectateur spectateur = new Spectateur("Spec" + categorie.name() + i, "Fan" + i, genre,
                    naissance, type.getVille(), 40 + (i % 20), "Tribune " + (char)('A' + (i % 4)), 1 + i);
            liste.add(spectateur);
        }
        // Si des spectateurs manuels sont disponibles, en intégrer une petite poignée pour l'ambiance
        int aAjouter = Math.min(20, reservesSpectateurs.size());
        for (int i = 0; i < aAjouter; i++)
        {
            liste.add(reservesSpectateurs.get(i));
        }
        return liste;
    }

    private boolean resteMatchPourCategorie(CategorieMatch categorie)
    {
        return matchsAVenir.stream().anyMatch(match -> match.getCategorie() == categorie);
    }

    private void afficherChampions()
    {
        if (championHommes != null)
        {
            System.out.println();
            System.out.println("**************************************************");
            System.out.println("*                   FELICITATIONS !            *");
            System.out.println("*                                                *");
            System.out.printf("*  Le champion messieurs de %s est :%n", getNom());
            System.out.printf("*               %s%n", championHommes);
            System.out.println("*                                                *");
            System.out.println("**************************************************");
            System.out.println();
        }
        if (championFemmes != null)
        {
             System.out.println();
            System.out.println("**************************************************");
            System.out.println("*                  BRAVO !                   *");
            System.out.println("*                                                *");
            System.out.printf("*  La championne dames de %s est :%n", getNom());
            System.out.printf("*               %s%n", championFemmes);
            System.out.println("*                                                *");
            System.out.println("**************************************************");
            System.out.println();
        }
        if (championHommes != null)
        {
            championHommes.getStatsCarriere().afficher();
        }
        if (championFemmes != null)
        {
            championFemmes.getStatsCarriere().afficher();
        }
        afficherSynthese();
    }

    private void afficherSynthese()
    {
        if (matchsJoues.isEmpty())
        {
            System.out.println("Aucun match joué, pas de synthèse disponible.");
            return;
        }

        int totalSpectateurs = matchsJoues.stream()
                .mapToInt(match -> match.getSpectateurs().size())
                .sum();
        double moyenneSpectateurs = totalSpectateurs / (double) matchsJoues.size();

        int totalPoints = matchsJoues.stream()
                .mapToInt(match -> match.getStatistiquesParJoueur().values().stream()
                        .mapToInt(StatistiquesMatch::getTotalPointsRemportes)
                        .sum())
                .sum();
        int ballesUtilisees = totalPoints; // approximation : 1 balle par échange

        long spectateursHommes = matchsJoues.stream()
                .flatMap(match -> match.getSpectateurs().stream())
                .filter(spectateur -> spectateur.getGenre() == Personne.Genre.HOMME)
                .count();
        long spectatricesFemmes = matchsJoues.stream()
                .flatMap(match -> match.getSpectateurs().stream())
                .filter(spectateur -> spectateur.getGenre() == Personne.Genre.FEMME)
                .count();

        System.out.println("\n--- Synthèse du tournoi ---");
        System.out.printf("Matches disputés : %d%n", matchsJoues.size());
        System.out.printf("Spectateurs totaux : %d (moyenne %.1f par match)%n", totalSpectateurs, moyenneSpectateurs);
        System.out.printf("Nombre total de points joués : %d%n", totalPoints);
        System.out.printf("Estimation des balles utilisées : %d%n", ballesUtilisees);
        System.out.printf("Public : %d spectateurs, %d spectatrices%n", spectateursHommes, spectatricesFemmes);
    }

	// Petit calcul rapide de pourcentage pour l'affichage des stats
	private static String pourcentage(int valeur, int total)
	{
		if (total <= 0)
		{
			return "0.0";
		}
		double pct = (100.0 * valeur) / total;
		return String.format("%.1f", pct);
	}
}

