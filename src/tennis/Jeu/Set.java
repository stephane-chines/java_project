package tennis.jeu;

import tennis.personnages.Arbitre;
import tennis.personnages.Joueur;
import tennis.stats.StatistiquesMatch;

/**
 * Orchestration d'un set: enchaîne les jeux, déclenche le tie-break à 6–6 si autorisé,
 * calcule le vainqueur, et détermine le premier serveur du set suivant selon les règles.
 */
public class Set 
{
	private final Joueur joueur1;
	private final Joueur joueur2;
	private final Arbitre arbitre;
    
    private int scoreJeuxJoueur1 = 0;
    private int scoreJeuxJoueur2 = 0;
    
    private Joueur vainqueur;
    private Joueur serveurActuel;
    private final boolean tieBreakAutorise;
    private final StatistiquesMatch statsJoueur1;
    private final StatistiquesMatch statsJoueur2;

	// Petit repère: qui a commencé ce set, qui a commencé le tie-break,
	// et qui devra servir au début du prochain set. Ça évite les ambiguïtés à la fin.
	private final Joueur premierServeurDuSet; // ← final (assigné au ctor)
	private Joueur premierServeurTieBreak;
	private Joueur premierServeurProchainSet;

	// Pour un affichage sympa du score en cas de 7–6: "7–6(7–x)"
	private Integer tbPointsJ1 = null;
	private Integer tbPointsJ2 = null;

    public Set(Joueur joueur1, Joueur joueur2, Joueur premierServeur, Arbitre arbitre, boolean tieBreakAutorise,
            StatistiquesMatch statsJoueur1, StatistiquesMatch statsJoueur2) 
    {
		// Petit filet de sécurité: éviter les paramètres bizarres qui cassent la logique
		if (joueur1 == null || joueur2 == null || arbitre == null || premierServeur == null)
			throw new IllegalArgumentException("Arguments nulls interdits");
		if (joueur1 == joueur2)
			throw new IllegalArgumentException("Les deux joueurs doivent être distincts");
		if (premierServeur != joueur1 && premierServeur != joueur2)
			throw new IllegalArgumentException("Le premier serveur doit être joueur1 ou joueur2");

		this.joueur1 = joueur1;
		this.joueur2 = joueur2;
		this.arbitre = arbitre;
		this.serveurActuel = premierServeur;
        this.tieBreakAutorise = tieBreakAutorise;
        this.statsJoueur1 = statsJoueur1;
        this.statsJoueur2 = statsJoueur2;

		// Mémorisation douce pour bien chaîner le service entre les sets
		this.premierServeurDuSet = premierServeur;
		this.premierServeurProchainSet = null; // défini en fin de set selon la règle
    }

    // On joue le set jusqu'à ce qu'il y ait un gagnant.
    public void jouerSet(ModeJeu mode, boolean afficherDetails) 
    {
        while (vainqueur == null) 
        {
            if (tieBreakAutorise && scoreJeuxJoueur1 == 6 && scoreJeuxJoueur2 == 6) 
            {
				// ici on a préféré mémoriser qui sert au début du TB pour décider le serveur du prochain set
				this.premierServeurTieBreak = serveurActuel;
				JeuDecisif tieBreak = new JeuDecisif(joueur1, joueur2, serveurActuel, arbitre, statsJoueur1, statsJoueur2);
                tieBreak.jouer(mode, afficherDetails);
                vainqueur = tieBreak.getVainqueur();
				// maintenant que les getters existent, on mémorise le détail du TB
				this.tbPointsJ1 = tieBreak.getPointsJoueur1();
				this.tbPointsJ2 = tieBreak.getPointsJoueur2();
                if (vainqueur == joueur1) 
                {
                    scoreJeuxJoueur1 = 7;
                } 
                else 
                {
                    scoreJeuxJoueur2 = 7;
                }
                if (mode == ModeJeu.MANUEL || afficherDetails)
                {
                    arbitre.annoncerFinJeu("Tie-break", scoreJeuxJoueur1, scoreJeuxJoueur2);
                }
				// Règle officielle: celui qui a servi en premier le TB reçoit au tout début du set suivant
				this.premierServeurProchainSet = (premierServeurTieBreak == joueur1) ? joueur2 : joueur1;
                break;
            }

            Jeu jeu = new Jeu(joueur1, joueur2, serveurActuel, arbitre, statsJoueur1, statsJoueur2);
            jeu.jouerJeu(mode, afficherDetails);

            Joueur gagnantDuJeu = jeu.getVainqueur();
            if (gagnantDuJeu == joueur1) 
            {
                scoreJeuxJoueur1++;
            } 
            else 
            {
                scoreJeuxJoueur2++;
            }

            if (mode == ModeJeu.MANUEL || afficherDetails)
            {
                arbitre.annoncerFinJeu(gagnantDuJeu.getPrenom(), scoreJeuxJoueur1, scoreJeuxJoueur2);
            }

            verifierVainqueur();

			// Petite subtilité: on n'alterne le serveur QUE si le set continue
			if (vainqueur == null) 
			{
				changerServeur();
			}
			else
			{
				// Hors tie-break, le premier serveur du prochain set est l'inverse de celui qui a démarré ce set
				if (premierServeurProchainSet == null)
				{
					this.premierServeurProchainSet = (premierServeurDuSet == joueur1) ? joueur2 : joueur1;
				}
			}
        }
    }

    private void verifierVainqueur() 
    {
        if (tieBreakAutorise && scoreJeuxJoueur1 == 7 && scoreJeuxJoueur2 == 6) 
        {
            vainqueur = joueur1;
        } 
        else if (tieBreakAutorise && scoreJeuxJoueur2 == 7 && scoreJeuxJoueur1 == 6) 
        {
            vainqueur = joueur2;
        }
        else if (scoreJeuxJoueur1 >= 6 && scoreJeuxJoueur1 >= scoreJeuxJoueur2 + 2) 
        {
            vainqueur = joueur1;
        } 
        else if (scoreJeuxJoueur2 >= 6 && scoreJeuxJoueur2 >= scoreJeuxJoueur1 + 2) 
        {
            vainqueur = joueur2;
        }
    }

    private void changerServeur() 
    {
        serveurActuel = (serveurActuel == joueur1) ? joueur2 : joueur1;
    }

    // Renvoie le vainqueur du set.
    public Joueur getVainqueur() 
    {
        return vainqueur;
    }

    public int getScoreJeuxJoueur1()
    {
        return scoreJeuxJoueur1;
    }

    public int getScoreJeuxJoueur2()
    {
        return scoreJeuxJoueur2;
    }

	// Petit raccourci lisible pour afficher le score style tennis
	public String getScore()
	{
		// Si le set est 7–6 et que les points du TB sont connus, afficher la parenthèse
		if (vainqueur != null && scoreJeuxJoueur1 == 7 && scoreJeuxJoueur2 == 6
				&& tbPointsJ1 != null && tbPointsJ2 != null)
		{
			return "7–6(" + tbPointsJ1 + "–" + tbPointsJ2 + ")";
		}
		if (vainqueur != null && scoreJeuxJoueur2 == 7 && scoreJeuxJoueur1 == 6
				&& tbPointsJ1 != null && tbPointsJ2 != null)
		{
			return "6–7(" + tbPointsJ1 + "–" + tbPointsJ2 + ")";
		}
		return scoreJeuxJoueur1 + "–" + scoreJeuxJoueur2;
	}

	// Point d'appui clair pour chaîner les sets sans deviner depuis serveurActuel
	public Joueur getPremierServeurProchainSet()
	{
		// Tant que le set n'est pas fini, on laisse null pour éviter l'ambiguïté (surtout avec TB)
		return (vainqueur == null) ? null : premierServeurProchainSet;
	}
}

