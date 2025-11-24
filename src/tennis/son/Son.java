package tennis.son;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.*;

/**
 * Utilitaire pour jouer un fichier WAV une seule fois.
 * - Essaie plusieurs emplacements usuels (classpath et quelques fallbacks disque).
 * - Ne logge rien en production (silent fail si fichier introuvable ou format non supporté).
 * - Possibilité d'activer/désactiver globalement le son via setActiverSon(false).
 */
public final class Son {

    private Son() {}

    // Flag pour activer/désactiver les sons (utile pour exécutions batch)
    private static volatile boolean activerSon = true;

    public static void setActiverSon(boolean actif) {
        activerSon = actif;
    }

    public static boolean isActiverSon() {
        return activerSon;
    }

    public static void playStart() {
        
        playOnce("/tennis/son/externe/trompette.wav");
    }

    public static void playEnd() {
        playOnce("/tennis/son/externe/victoire.wav");
    }

    // le path est normalemnent du style "/tennis/son/externe/trompette.wav"
    public static void playOnce(String resourcePath) {
        if (!activerSon || resourcePath == null) return;

        AudioInputStream ais = null;

        
        String[] classpathCandidates = new String[] {
            resourcePath,
            
            resourcePath.startsWith("/") ? "/tennis" + resourcePath : "/tennis/" + resourcePath
        };

        InputStream isStream = null;
        for (String candidate : classpathCandidates) {
            try {
                isStream = Son.class.getResourceAsStream(candidate);
                if (isStream != null) {
                    ais = AudioSystem.getAudioInputStream(new BufferedInputStream(isStream));
                    break;
                }
            } catch (Exception e) {
                
                try { if (isStream != null) isStream.close(); } catch (IOException ignored) {}
                isStream = null;
                ais = null;
            }
        }

        //permet de chercher les fichiers sur une plus grande variété de chemins disques
        if (ais == null) {
            String[] diskBases = new String[] {
                "tennis/resources",
                "tennis/tennis/resources",
                "resources",
                "" 
            };
            File found = null;
            for (String base : diskBases) {
                String candidatePath = base + resourcePath;
                File f = new File(candidatePath);
                if (f.exists()) { found = f; break; }
                if (resourcePath.startsWith("/")) {
                    String c2 = base + resourcePath.substring(1);
                    f = new File(c2);
                    if (f.exists()) { found = f; break; }
                }
            }
            if (found != null) {
                try {
                    ais = AudioSystem.getAudioInputStream(found);
                } catch (Exception e) {
                    try { if (ais != null) ais.close(); } catch (IOException ignored) {}
                    ais = null;
                }
            }
        }

        if (ais == null) {
            // silent fail : ne pas spammer la console en production
            return;
        }

        
        try {
            final AudioInputStream aisForThread = ais;
            final Clip clip = AudioSystem.getClip();
            clip.open(aisForThread);
            clip.start();

            Thread worker = new Thread(() -> {
                try {
                    while (clip.isRunning()) {
                        try { Thread.sleep(50); } catch (InterruptedException ignored) {}
                    }
                } finally {
                    try { clip.close(); } catch (Exception ignored) {}
                    try { aisForThread.close(); } catch (IOException ignored) {}
                }
            }, "Son-Worker");
            worker.setDaemon(true);
            worker.start();
        } catch (LineUnavailableException | IOException e) {
            try { if (ais != null) ais.close(); } catch (IOException ignored) {}
            
        }
    }
}