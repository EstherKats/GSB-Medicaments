package fr.euroforma.gsb_medicaments;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Authentification extends AppCompatActivity {

    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_STATUS = "userStatus";
    private static final String SECURETOKEN = "BethElicheva5";

    private EditText codeVisiteur,  saisieCode, NomVisiteur, PrenomVisiteur;
    private static final String NOM = "nom";
    private static final String PRENOM = "prenom";

    private Button buttonValider, buttonOK;
    private String secureKey;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentification);
        setUserStatus("KO");

        NomVisiteur = findViewById(R.id.NomVisiteur);
        PrenomVisiteur = findViewById(R.id.PrenomVisiteur);
        codeVisiteur = findViewById(R.id.codeVisiteur);
        buttonValider = findViewById(R.id.buttonValider);
        saisieCode = findViewById(R.id.saisieCode);
        buttonOK = findViewById(R.id.buttonOk);

    }

    public void clickValider(View v){
        findViewById(R.id.partieDeux).setVisibility(View.VISIBLE);
        //findViewById(R.id.partieDeux).isVisible(true);

    String codeV = codeVisiteur.getText().toString();

    /*
     Vous pouvez maintenant utiliser la méthode sendKeyByEmail
     avec le codeV, secureKey, et token comme paramètres */
    secureKey = generateRandomCode();

        String token = SECURETOKEN;
    SendKeyTask sendEmail = new SendKeyTask(getApplicationContext());
    sendEmail.execute(codeV, secureKey, token);

    }


    public void clickOk(View v){
        //String str1 = secureKey;
        String str2 = saisieCode.getText().toString();
        if (secureKey.equals(str2)){
            String status1 = "Authentifié";
            setUserStatus(status1);
            //Log.d("COMPARE", "OK");
            Toast toast = Toast.makeText(this,"Authentification réussie", Toast.LENGTH_LONG);
            toast.show();
            String nom = NomVisiteur.getText().toString();
            String prenom = PrenomVisiteur.getText().toString();
            setNomPrenom(nom, prenom);
            Intent authIntent = new Intent(this, MainActivity.class);
            startActivity(authIntent);
            finish();

        } else {
            Toast toast = Toast.makeText(this,"identifiant ou code incorrecte", Toast.LENGTH_LONG);
            toast.show();
        }
    }
    private void setUserStatus(String status) {
        Date currentTime = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = dateFormat.format(currentTime);
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_STATUS, status);
        editor.putString("TIME", strDate);
        editor.apply();
    }

    private void setNomPrenom(String nom, String prenom) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(NOM, nom);
        editor.putString(PRENOM, prenom);
        editor.apply();
    }



    private String generateRandomCode() {
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
    public void clickQuitter(View v){
        finish();
    }

    }
