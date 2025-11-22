package tennis.tournoi;

/**
 * Types de tournois du Grand Chelem.
 * Définit la ville, la surface, et si un tie-break est autorisé dans le dernier set.
 */
public enum TournoiType 
{
    AUSTRALIE("Melbourne", "Plexicushion", false),
    ROLAND_GARROS("Paris", "Terre battue", false),
    WIMBLEDON("Londres", "Gazon", false),
    US_OPEN("New York", "Decoturf", true);

    private final String ville;
    private final String surface;
    private final boolean tieBreakDernierSetAutorise;

    TournoiType(String ville, String surface, boolean tieBreakDernierSetAutorise) 
    {
        this.ville = ville;
        this.surface = surface;
        this.tieBreakDernierSetAutorise = tieBreakDernierSetAutorise;
    }

    public String getVille() 
    {
        return ville;
    }

    public String getSurface() 
    {
        return surface;
    }

    public boolean isTieBreakDernierSetAutorise() 
    {
        return tieBreakDernierSetAutorise;
    }
}

