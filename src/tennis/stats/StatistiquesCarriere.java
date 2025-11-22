package tennis.stats;

/**
 * Statistiques de carrière d'un joueur: matchs, tournois, gains, points
 * et agrégations basées sur les {@link tennis.stats.StatistiquesMatch}.
 */
public class StatistiquesCarriere 
{
    private int matchsJoues = 0;
    private int matchsRemportes = 0;
    private int tournoisParticipes = 0;
    private double totalGains = 0.0;

    private int pointsTotaux = 0;
    private int pointsATP = 0;
    private int acesTotaux = 0;
    private int doubleFautesTotales = 0;
    private int ballesDeBreakObtenues = 0;
    private int ballesDeBreakConverties = 0;

    public void ajouterParticipationTournoi() { this.tournoisParticipes++; }

    public void enregistrerMatch(boolean victoire, double gain) 
    {
        this.matchsJoues++;
        if (victoire) 
        {
            this.matchsRemportes++;
        }
        this.totalGains += gain;
    }

    public void enregistrerStatsMatch(StatistiquesMatch stats)
    {
        this.pointsTotaux += stats.getTotalPointsRemportes();
        this.acesTotaux += stats.getAces();
        this.doubleFautesTotales += stats.getDoubleFautes();
        this.ballesDeBreakObtenues += stats.getBallesDeBreakObtenues();
        this.ballesDeBreakConverties += stats.getBallesDeBreakConverties();
    }

    // Ajoute des points ATP au total
    public void ajouterPointsATP(int points)
    {
        this.pointsATP += points;
    }

    public int getMatchsJoues() { return matchsJoues; }
    public int getMatchsRemportes() { return matchsRemportes; }
    public int getTournoisParticipes() { return tournoisParticipes; }
    public double getTotalGains() { return totalGains; }

    public void afficher() 
    {
        System.out.println("Carrière: " + matchsRemportes + " victoires sur " + matchsJoues + " matchs. Gains: " + totalGains + "€");
        System.out.println("Total points gagnés: " + pointsTotaux + " | Aces: " + acesTotaux + " | Doubles fautes: " + doubleFautesTotales);
        System.out.println("Balles de break converties: " + ballesDeBreakConverties + "/" + ballesDeBreakObtenues);
    }
}

