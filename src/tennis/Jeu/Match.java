package tennis.jeu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import tennis.personnages.Arbitre;
import tennis.personnages.Joueur;
import tennis.personnages.Spectateur;
import tennis.son.Son;
import tennis.stats.StatistiquesMatch;

/**
 * Représente un match de tennis entre deux joueurs géré par un arbitre.
 * - Gère la succession des sets, le tirage du premier serveur,
 * - Applique les règles de best-of selon la catégorie (H=3, F=2),
 * - Autorise ou non le tie-break dans le dernier set selon le tournoi,
 * - Maintient l'historique lisible des sets et met à jour les statistiques.
 */
public class Match 
{
    private final Joueur joueur1;
    private final Joueur joueur2;
    private final Arbitre arbitre;
    private final CategorieMatch categorie;
    private final String niveauTournoi;
    private final int pointsATPgagnes;
    private final boolean tieBreakDernierSetAutorise;
    private final List<Spectateur> spectateurs;
    private final Map<Joueur, StatistiquesMatch> statistiquesParJoueur = new HashMap<>();

    //  RNG injectable pour tests déterministes
    private final Random rng;

    // Historique des scores de sets, ex: ["6-4", "3-6", "7-6"]
    private final List<String> historiqueSets = new ArrayList<>();

    private Joueur vainqueur;
    private Joueur perdant;

    private final int nombreSetsGagnants;
    private int scoreSetsJoueur1;
    private int scoreSetsJoueur2;

	/** Constructeur confort (délègue en utilisant un RNG par défaut). */
    public Match(Joueur joueur1, Joueur joueur2, Arbitre arbitre, CategorieMatch categorie, String niveauTournoi,
            int pointsATPgagnes, boolean tieBreakDernierSetAutorise, List<Spectateur> spectateurs) 
    {
        this(joueur1, joueur2, arbitre, categorie, niveauTournoi, pointsATPgagnes, 
             tieBreakDernierSetAutorise, spectateurs, null);
    }

	/**
	 * Constructeur détaillé avec RNG injectable pour tests déterministes.
	 *
	 * @param joueur1 premier joueur
	 * @param joueur2 second joueur
	 * @param arbitre arbitre central
	 * @param categorie SIMPLE_HOMMES ou SIMPLE_FEMMES
	 * @param niveauTournoi libellé du tour ou niveau
	 * @param pointsATPgagnes points attribués au vainqueur
	 * @param tieBreakDernierSetAutorise règle du tie-break dans le dernier set
	 * @param spectateurs liste des spectateurs du match
	 * @param rng générateur pseudo-aléatoire (peut être null)
	 */
    public Match(Joueur joueur1, Joueur joueur2, Arbitre arbitre, CategorieMatch categorie, String niveauTournoi,
            int pointsATPgagnes, boolean tieBreakDernierSetAutorise, List<Spectateur> spectateurs, Random rng) 
    {
        this.joueur1 = joueur1;
        this.joueur2 = joueur2;
        this.arbitre = arbitre;
        this.categorie = categorie;
        this.niveauTournoi = niveauTournoi;
        this.pointsATPgagnes = pointsATPgagnes;
        this.tieBreakDernierSetAutorise = tieBreakDernierSetAutorise;

        // null-safety + liste immuable
        List<Spectateur> copie = (spectateurs != null) ? new ArrayList<>(spectateurs) : new ArrayList<>();
        this.spectateurs = Collections.unmodifiableList(copie);

        this.nombreSetsGagnants = (categorie == CategorieMatch.SIMPLE_HOMMES) ? 3 : 2;
        statistiquesParJoueur.put(joueur1, new StatistiquesMatch());
        statistiquesParJoueur.put(joueur2, new StatistiquesMatch());

        // RNG injectable
        this.rng = (rng != null) ? rng : new Random();
    }

	/**
	 * Joue le match jusqu'à désigner un vainqueur.
	 *
	 * @param mode MANUEL ou AUTOMATIQUE
	 * @param afficherDetails true pour afficher les annonces détaillées
	 */
    public void jouerMatch(ModeJeu mode, boolean afficherDetails) 
    {
		// ici on a préféré tirer au sort le premier serveur pour varier un peu les scénarios
        // Réinitialisation des compteurs au cas où le match serait relancé.
        scoreSetsJoueur1 = 0;
        scoreSetsJoueur2 = 0;
        vainqueur = null;
        perdant = null;
        
        // Vide l'historique au début du match
        historiqueSets.clear();

        if (afficherDetails || mode == ModeJeu.MANUEL)
        {
            System.out.println("Début du match (" + niveauTournoi + ") entre " + joueur1.getPrenom() + " et " + joueur2.getPrenom());
        }
        Son.playStart();
        // Utiliser rng au lieu de new Random()
		Joueur serveurActuel = rng.nextBoolean() ? joueur1 : joueur2;

        if (afficherDetails || mode == ModeJeu.MANUEL)
        {
            System.out.println(serveurActuel.getPrenom() + " servira en premier.");
        }

        while (vainqueur == null) 
        {
            boolean estSetPotentiellementDecisif = (scoreSetsJoueur1 == nombreSetsGagnants - 1)
                    && (scoreSetsJoueur2 == nombreSetsGagnants - 1);
            boolean autoriseTieBreakDansCeSet = !estSetPotentiellementDecisif || tieBreakDernierSetAutorise;

            StatistiquesMatch statsAvantJ1 = statistiquesParJoueur.get(joueur1);
            StatistiquesMatch statsAvantJ2 = statistiquesParJoueur.get(joueur2);
            int acesAvantJ1 = statsAvantJ1.getAces();
            int acesAvantJ2 = statsAvantJ2.getAces();
            int dfAvantJ1 = statsAvantJ1.getDoubleFautes();
            int dfAvantJ2 = statsAvantJ2.getDoubleFautes();
            int ptsAvantJ1 = statsAvantJ1.getTotalPointsRemportes();
            int ptsAvantJ2 = statsAvantJ2.getTotalPointsRemportes();

            Set set = new Set(joueur1, joueur2, serveurActuel, arbitre, autoriseTieBreakDansCeSet,
            statistiquesParJoueur.get(joueur1), statistiquesParJoueur.get(joueur2), spectateurs);
            set.jouerSet(mode, afficherDetails);
// on a décidé d'utiliser le score du set directement (inclut le détail du TB si dispo)
            Joueur gagnantDuSet = set.getVainqueur();

// Historique lisible (inclut éventuellement le détail du TB, ex: 7–6(7–4))
            historiqueSets.add(set.getScore());


            StatistiquesMatch statsApresJ1 = statistiquesParJoueur.get(joueur1);
            StatistiquesMatch statsApresJ2 = statistiquesParJoueur.get(joueur2);
            int acesDeltaJ1 = statsApresJ1.getAces() - acesAvantJ1;
            int acesDeltaJ2 = statsApresJ2.getAces() - acesAvantJ2;
            int dfDeltaJ1 = statsApresJ1.getDoubleFautes() - dfAvantJ1;
            int dfDeltaJ2 = statsApresJ2.getDoubleFautes() - dfAvantJ2;
            int ptsDeltaJ1 = statsApresJ1.getTotalPointsRemportes() - ptsAvantJ1;
            int ptsDeltaJ2 = statsApresJ2.getTotalPointsRemportes() - ptsAvantJ2;


            notifySpectatorsAfterSet(gagnantDuSet, joueur1, joueur2,
            acesDeltaJ1, acesDeltaJ2, dfDeltaJ1, dfDeltaJ2, ptsDeltaJ1, ptsDeltaJ2, set.getScore());

            if (gagnantDuSet == joueur1) 
            {
            scoreSetsJoueur1++;
            } 
            else 
            {
            scoreSetsJoueur2++;
            }

//Déterminer le prochain server
            Joueur prochainServeur = set.getPremierServeurProchainSet();
            if (prochainServeur != null)
            {
            serveurActuel = prochainServeur;
            }
            else
            {
    
            serveurActuel = (serveurActuel == joueur1) ? joueur2 : joueur1;
            }

            if (afficherDetails || mode == ModeJeu.MANUEL)
            {
                arbitre.annoncerFinSet(gagnantDuSet.getPrenom(), scoreSetsJoueur1, scoreSetsJoueur2);
            }

        verifierVainqueur();
        }
        arbitre.annoncerFinMatch(vainqueur.getPrenom());
        Son.playEnd();
        vainqueur.crierVictoire();
        if (perdant != null)
        {
            perdant.crierDefaite();
        }
        notifySpectatorsAfterMatch(vainqueur);
        // ajouter les points au vainqueur
        vainqueur.getStatsCarriere().ajouterPointsATP(pointsATPgagnes);
    }
        
        
    private void verifierVainqueur() 
    {
        if (scoreSetsJoueur1 == nombreSetsGagnants) 
        {
            this.vainqueur = joueur1;
            this.perdant = joueur2;
        } 
        else if (scoreSetsJoueur2 == nombreSetsGagnants) 
        {
            this.vainqueur = joueur2;
            this.perdant = joueur1;
        }
    }
    private void notifySpectatorsAfterSet(Joueur gagnantDuSet, Joueur j1, Joueur j2,
                                      int acesDeltaJ1, int acesDeltaJ2,
                                      int dfDeltaJ1, int dfDeltaJ2,
                                      int ptsDeltaJ1, int ptsDeltaJ2,
                                      String scoreSet)
    {
        // Calcul d'indicateurs simples
        int totalAces = Math.max(0, acesDeltaJ1) + Math.max(0, acesDeltaJ2);
        int totalDF = Math.max(0, dfDeltaJ1) + Math.max(0, dfDeltaJ2);
        int totalPoints = Math.max(0, ptsDeltaJ1) + Math.max(0, ptsDeltaJ2);
        boolean setSerré = scoreSet != null && (scoreSet.contains("7–6") || scoreSet.contains("7-6") || scoreSet.matches(".*7.5.*"));

    // Score d'ambiance (heuristique)
        int ambianceScore = 0;
        ambianceScore += totalAces * 5;         // beaux coups
        ambianceScore += Math.min(totalPoints / 5, 10); // long set -> plus d'ambiance
        ambianceScore -= totalDF * 3;           // double fautes = boos
        ambianceScore += setSerré ? 8 : 0;      // set serré = grosse tension

    // Normalisation / pondération selon la taille du public
        int publicSize = (spectateurs == null) ? 0 : spectateurs.size();
        if (publicSize > 0) {
            // plus le public est grand, plus l'impact est visible -> on laisse tel quel
        } else {
            // si pas de spectateurs listés, on prend une estimation minimale
            publicSize = 1;
        }

    
        String target = (gagnantDuSet != null) ? gagnantDuSet.getPrenom() + " " + gagnantDuSet.getNomCourant() : "le joueur";
        if (ambianceScore <= 0) {
            System.out.println("Le public reste relativement calme pendant ce set (" + scoreSet + ").");
        } else if (ambianceScore <= 8) {
        System.out.println("Quelques applaudissements pour " + target + " après ce set (" + scoreSet + ").");
        } else if (ambianceScore <= 18) {
            System.out.println("Le public applaudit chaleureusement après ce set (" + scoreSet + ") — belle intensité !");
        } else if (ambianceScore <= 30) {
            System.out.println("Le public se lève et ovationne " + target + " après ce set (" + scoreSet + ") !");
        } else {
            System.out.println("Ambiance de folie ! Standing ovation pour " + target + " après ce set (" + scoreSet + ") !!!");
        }

    }

    private void notifySpectatorsAfterMatch(Joueur vainqueur)
    {
        if (vainqueur == null) {
            System.out.println("Fin du match : aucun vainqueur identifié.");
            return;
        }

    // Calcul d'une appréciation finale simple en fonction des statistiques cumulées du match
        int publicSize = (spectateurs == null) ? 0 : spectateurs.size();
        String name = vainqueur.getPrenom() + " " + vainqueur.getNomCourant();

        if (publicSize <= 0) {
        // Aucun spectateur enregistré pour ce match
            System.out.println("Le match est terminé. " + name + " remporte le match.");
        } else if (publicSize < 20) {
            System.out.println("Le public salue la performance de " + name + " et l'applaudit chaleureusement.");
        } else if (publicSize < 60) {
            System.out.println("Le public ovationne " + name + " — belle victoire !");
        } else {
            System.out.println("Le stade est en feu : " + name + " reçoit une ovation massive !");
        }

    }
    public Joueur getJoueur1()
    {
        return joueur1;
    }

    public Joueur getJoueur2()
    {
        return joueur2;
    }

    public Joueur getPerdant()
    {
        return perdant;
    }

	
    public CategorieMatch getCategorie()
    {
        return categorie;
    }

    public Joueur getVainqueur() 
    {
        return vainqueur;
    }
    
	
    public String getScoreFinal()
    {
        if (vainqueur == null) {
            return "Match non terminé.";
        }
        
        String sets = historiqueSets.stream().collect(Collectors.joining(" "));
        return "Score: " + sets + " (" + scoreSetsJoueur1 + " - " + scoreSetsJoueur2 + ")";
    }

    public List<Spectateur> getSpectateurs()
    {
        return spectateurs;
    }

	/** Statistiques cumulées du match pour un joueur donné. */
    public StatistiquesMatch getStatistiques(Joueur joueur)
    {
        return statistiquesParJoueur.get(joueur);
    }

	/** Map des statistiques pour les deux joueurs ( non modifiable). */
    public Map<Joueur, StatistiquesMatch> getStatistiquesParJoueur()
    {
        return Collections.unmodifiableMap(statistiquesParJoueur);
    }

    @Override
    public String toString() 
    {
        return categorie + " - " + joueur1.getPrenom() + " vs " + joueur2.getPrenom() + " (" + niveauTournoi + ")";
    }
}

