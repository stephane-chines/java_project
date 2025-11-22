package tennis.stats;

/**
 * Statistiques détaillées sur un match pour un joueur (points, services, breaks).
 * Exposées via accesseurs simples et quelques moyennes utiles.
 */
public class StatistiquesMatch 
{
    private int totalPointsRemportes = 0;
    private int aces = 0;
    private int doubleFautes = 0;

    private int premiersServicesJoues = 0;
    private int premiersServicesReussis = 0;
    private double sommeVitessesPremierService = 0.0;

    private int secondsServicesJoues = 0;
    private int secondsServicesReussis = 0;
    private double sommeVitessesSecondService = 0.0;

    private int ballesDeBreakObtenues = 0;
    private int ballesDeBreakConverties = 0;

    public int getTotalPointsRemportes() { return totalPointsRemportes; }
    public int getAces() { return aces; }
    public int getDoubleFautes() { return doubleFautes; }
    public int getPremiersServicesJoues() { return premiersServicesJoues; }
    public int getPremiersServicesReussis() { return premiersServicesReussis; }
    public int getSecondsServicesJoues() { return secondsServicesJoues; }
    public int getSecondsServicesReussis() { return secondsServicesReussis; }
    public int getBallesDeBreakObtenues() { return ballesDeBreakObtenues; }
    public int getBallesDeBreakConverties() { return ballesDeBreakConverties; }

    public void incrementerPoints() { this.totalPointsRemportes++; }
    public void incrementerAces() { this.aces++; }
    public void incrementerDoubleFautes() { this.doubleFautes++; }

    public void enregistrerPremierService(boolean reussi, double vitesse)
    {
        premiersServicesJoues++;
        if (reussi)
        {
            premiersServicesReussis++;
            sommeVitessesPremierService += vitesse;
        }
    }

    public void enregistrerSecondService(boolean reussi, double vitesse)
    {
        secondsServicesJoues++;
        if (reussi)
        {
            secondsServicesReussis++;
            sommeVitessesSecondService += vitesse;
        }
    }

    public void enregistrerBalleDeBreak(boolean convertie)
    {
        ballesDeBreakObtenues++;
        if (convertie)
        {
            ballesDeBreakConverties++;
        }
    }

    public double getVitesseMoyennePremierService()
    {
        return premiersServicesReussis == 0 ? 0.0 : sommeVitessesPremierService / premiersServicesReussis;
    }

    public double getVitesseMoyenneSecondService()
    {
        return secondsServicesReussis == 0 ? 0.0 : sommeVitessesSecondService / secondsServicesReussis;
    }

    public void afficher() 
    {
        System.out.printf("Points gagnés: %d | Aces: %d | Doubles fautes: %d%n", totalPointsRemportes, aces, doubleFautes);
        System.out.printf("1er service: %d/%d (%.1f%%) Vitesse moyenne: %.1f km/h%n",
                premiersServicesReussis, premiersServicesJoues,
                pourcentage(premiersServicesReussis, premiersServicesJoues), getVitesseMoyennePremierService());
        System.out.printf("2nd service: %d/%d (%.1f%%) Vitesse moyenne: %.1f km/h%n",
                secondsServicesReussis, secondsServicesJoues,
                pourcentage(secondsServicesReussis, secondsServicesJoues), getVitesseMoyenneSecondService());
        System.out.printf("Balles de break: %d/%d%n", ballesDeBreakConverties, ballesDeBreakObtenues);
    }

    private double pourcentage(int valeur, int total)
    {
        return total == 0 ? 0.0 : (100.0 * valeur) / total;
    }
}

