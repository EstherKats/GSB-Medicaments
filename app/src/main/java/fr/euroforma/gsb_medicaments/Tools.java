package fr.euroforma.gsb_medicaments;

import android.content.Context;
import android.content.SharedPreferences;
import java.security.SecureRandom;

public class Tools {

    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_STATUS = "AuthStatus";

    // Fonction pour générer un code aléatoire de 12 caractères
    public static String generateRandomCode() {
        // Caractères possibles dans le code
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        // Longueur du code souhaitée
        int codeLength = 12;

        // Utilisation de SecureRandom pour une génération sécurisée
        SecureRandom random = new SecureRandom();

        // StringBuilder pour construire le code
        StringBuilder codeBuilder = new StringBuilder(codeLength);

        // Boucle pour construire le code caractère par caractère
        for (int i = 0; i < codeLength; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            codeBuilder.append(randomChar);
        }

        // Retourne le code généré
        return codeBuilder.toString();
    }

    // Fonction pour stocker le statut de l'utilisateur
    public static void setUserStatus(Context context, String status) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_STATUS, status);
        editor.apply();
    }

    // Fonction pour récupérer le statut de l'utilisateur
    public static String getUserStatus(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_STATUS, "");
    }
}

