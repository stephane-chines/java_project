package tennis.personnages;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

/**
 * Données d'état civil communes aux personnages (joueurs, arbitres, spectateurs).
 * Certains champs sont immuables (nom de naissance, genre, date/lieu de naissance).
 * Fournit des utilitaires (âge, anniversaire) et gère mariage/divorce.
 */
public class Personne {

    public enum Genre {
        HOMME,
        FEMME
    }

    // Informations qui ne peuvent pas etre modifiées.
    private final String nomNaissance;
    private final String prenom;
    private final Genre genre;
    private final LocalDate dateNaissance;
    private final String lieuNaissance;

    // Informations qui peuvent changer au cours du jeu.
    private String nomCourant;
    private String surnom;
    private LocalDate dateDeces;
    private String nationalite;
    private int tailleCm;
    private int poidsKg;
    private boolean estMariee;
    private String nomDuConjoint;

    // Constructeur de base avec les informations essentielles.
    public Personne(String nomNaissance, String prenom, Genre genre, LocalDate dateNaissance, String lieuNaissance) {
        this.nomNaissance = nomNaissance;
        this.prenom = prenom;
        this.genre = genre;
        this.dateNaissance = dateNaissance;
        this.lieuNaissance = lieuNaissance;
    }

    // Constructeur plus complet pour initialiser également des détails optionnels.
    public Personne(String nomNaissance, String prenom, Genre genre, LocalDate dateNaissance, String lieuNaissance,
            String nationalite, int tailleCm, int poidsKg, String surnom) {
        this(nomNaissance, prenom, genre, dateNaissance, lieuNaissance);
        this.nationalite = nationalite;
        setTailleCm(tailleCm);
        setPoidsKg(poidsKg);
        this.surnom = surnom;
    }

    public String getNomNaissance() {
        return nomNaissance;
    }

    // Retourne le nom courant si disponible, sinon renvoie le nom de naissance.
    public String getNomCourant() {
        if (nomCourant != null && !nomCourant.isEmpty()) {
            return nomCourant;
        } else {
            return nomNaissance;
        }
    }

    // Setter privé pour maîtriser les changements de nom.
    private void setNomCourant(String nomCourant) {
        this.nomCourant = nomCourant;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getSurnom() {
        return surnom;
    }

    public void setSurnom(String surnom) {
        this.surnom = surnom;
    }

    // Vérifie si la personne possède un surnom actif.
    public boolean aUnSurnom() {
        return surnom != null && !surnom.isEmpty();
    }

    public Genre getGenre() {
        return genre;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public String getLieuNaissance() {
        return lieuNaissance;
    }

    public LocalDate getDateDeces() {
        return dateDeces;
    }

    // La date de décès ne peut être enregistrée qu’une seule fois pour limiter les erreurs.
    public void setDateDeces(LocalDate dateDeces) {
        if (this.dateDeces == null && dateDeces != null && !dateDeces.isBefore(dateNaissance)) {
            this.dateDeces = dateDeces;
        }
    }

    public String getNationalite() {
        return nationalite;
    }

    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }

    public int getTailleCm() {
        return tailleCm;
    }

    public void setTailleCm(int tailleCm) {
        if (tailleCm <= 0) {
            throw new IllegalArgumentException("La taille doit être un entier positif (en cm).");
        }
        this.tailleCm = tailleCm;
    }

    public int getPoidsKg() {
        return poidsKg;
    }

    public void setPoidsKg(int poidsKg) {
        if (poidsKg <= 0) {
            throw new IllegalArgumentException("Le poids doit être un entier positif (en kg).");
        }
        this.poidsKg = poidsKg;
    }

    public boolean estMariee() {
        return estMariee;
    }

    public String getNomDuConjoint() {
        return nomDuConjoint;
    }

    // Lors d’un mariage, une femme adopte automatiquement le nom du conjoint.
    public void seMarie(String nomConjoint) {
        if (!estMariee && nomConjoint != null && !nomConjoint.isEmpty()) {
            estMariee = true;
            nomDuConjoint = nomConjoint;

            if (genre == Genre.FEMME) {
                setNomCourant(nomConjoint);
            }
        }
    }

    // Annule le mariage et remet les informations associées à zéro.
    public void divorcer() {
        if (estMariee) {
            estMariee = false;
            nomDuConjoint = null;
            if (genre == Genre.FEMME) {
                setNomCourant(null);
            }
        }
    }

    // Indique si la personne est considérée comme décédée.
    public boolean estDecede() {
        return dateDeces != null;
    }

    // Calcule l’âge en tenant compte de la date de décès si elle est renseignée.
    public int getAge() {
        LocalDate fin = (dateDeces == null) ? LocalDate.now() : dateDeces;
        return Period.between(dateNaissance, fin).getYears();
    }

    // Donne l'age en jours pour etre plus précis.
    public long getAgeEnJours() {
        LocalDate fin = (dateDeces == null) ? LocalDate.now() : dateDeces;
        return ChronoUnit.DAYS.between(dateNaissance, fin);
    }

    // Vérifie si aujourd hui correspond à la date d anniversaire.
    public boolean aSonAnniversaireAujourdHui() {
        LocalDate aujourdHui = LocalDate.now();
        return aujourdHui.getMonth() == dateNaissance.getMonth()
                && aujourdHui.getDayOfMonth() == dateNaissance.getDayOfMonth();
    }

    @Override
    public String toString() {
        return prenom + " " + getNomCourant();
    }
}
