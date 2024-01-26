package fr.euroforma.gsb_medicaments;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;

public class Authentification extends AppCompatActivity {

    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_USER_STATUS = "userStatus";

    private void setUserStatus(String status) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_STATUS, status);
        editor.apply();
    }

    private EditText codeVisiteur,  saisieCode;

    private Button buttonValider, buttonOK;

    private TextView codeAleatoire;



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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentification);


        codeVisiteur = findViewById(R.id.codeVisiteur);
        buttonValider = findViewById(R.id.buttonValider);
        codeAleatoire = findViewById(R.id.codeAleatoire);
        saisieCode = findViewById(R.id.saisieCode);
        buttonOK = findViewById(R.id.buttonOk);

    }

    public void clickValider(View v){
        findViewById(R.id.partieDeux).setVisibility(View.VISIBLE);
        //findViewById(R.id.partieDeux).isVisible(true);
        String CA= generateRandomCode();
        codeAleatoire.setText(CA);

    }


    public void clickOk(View v){
        String str1 = codeAleatoire.getText().toString();
        String str2 = saisieCode.getText().toString();
        if (str1.equals(str2)){
            String status1 = "Authentifié";
            setUserStatus(status1);
            //Log.d("COMPARE", "OK");
            Toast toast = Toast.makeText(this,"Authentification réussie", Toast.LENGTH_LONG);
            toast.show();

        } else {
            Toast toast = Toast.makeText(this,"identifiant ou code incorrecte", Toast.LENGTH_LONG);
            toast.show();
        }
    }


    }
