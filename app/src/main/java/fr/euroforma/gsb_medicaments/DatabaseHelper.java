package fr.euroforma.gsb_medicaments;




import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;


public class DatabaseHelper extends SQLiteOpenHelper {


    private final Context mycontext;


    private static final String NOM = "nom";
    private static final String PRENOM = "prenom";
    private static final String DATABASE_NAME = "medicaments.db";
    private static final int DATABASE_VERSION = 1;
    private String DATABASE_PATH;
    private static final String LOG_FILE_NAME = "application_log.txt";
    private static final String LOG_FILE_PATH = "logs/";
    private static final String KEY_USER_STATUS = "userStatus";
    private static final String PREF_NAME = "UserPrefs";
    private static final String PREMIERE_VOIE = "Séléctionner une voie d'administration";
    private static DatabaseHelper sInstance;
    public String logEntry, nb;
    private Integer nbResultat;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) { sInstance = new DatabaseHelper(context); }
        return sInstance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO: Define the tables and necessary structures
        // Note: You should execute the appropriate CREATE TABLE queries here
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion){
            //Log.d("debug", "onUpgrade() : oldVersion=" + oldVersion + ",newVersion=" + newVersion);
            mycontext.deleteDatabase(DATABASE_NAME);
            copydatabase();
        }
    } // onUpgrade

    // TODO: Implement methods to interact with the database, such as fetching distinct Voies_dadministration
    // and searching for medicaments based on criteria

    public List<String> getDistinctVoiesAdmin() {
        List<String> voiesAdminList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT Voies_dadministration FROM CIS_bdpm WHERE Voies_dadministration NOT LIKE '%;%' ORDER BY Voies_dadministration", null);
        voiesAdminList.add(PREMIERE_VOIE);
        if (cursor.moveToFirst()) {
            do {

                String voieAdmin = cursor.getString(0).toString();
                voiesAdminList.add(voieAdmin);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return voiesAdminList;
    }

    private boolean checkdatabase() {
        // retourne true/false si la bdd existe dans le dossier de l'app
        File dbfile = new File(DATABASE_PATH + DATABASE_NAME);

        return dbfile.exists();
    }

    public List<Medicament> searchMedicaments(String denomination, String formePharmaceutique, String titulaires, String denominationSubstance, String voiesAdmin
    ) {
        List<Medicament> medicamentList = new ArrayList<>();
        ArrayList<String> selectionArgs = new ArrayList<>();
        selectionArgs.add("%" + denomination + "%");
        selectionArgs.add("%" + formePharmaceutique + "%");
        selectionArgs.add("%" + titulaires + "%");
        selectionArgs.add("%" + denominationSubstance + "%");


        SQLiteDatabase db = this.getReadableDatabase();
        String finSQL= "";
        if (!voiesAdmin.equals(PREMIERE_VOIE)){
            finSQL="AND Voies_dadministration = ? ";
            selectionArgs.add(voiesAdmin);
        }

        String SQLSubstance = "SELECT CODE_CIS FROM CIS_COMPO_bdpm WHERE replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(upper(Denomination_substance), 'Â','A'),'Ä','A'),'À','A'),'É','E'),'Á','A'),'Ï','I'), 'Ê','E'),'È','E'),'Ô','O'),'Ü','U'), 'Ç','C' ) LIKE ?" ;
        //String SQLSubstance = "SELECT CODE_CIS FROM CIS_COMPO_bdpm WHERE Denomination_substance COLLATE latin1_general_cs_ai LIKE ?" ;

        // La requête SQL de recherche
        String query = "SELECT *, (select count(*) from CIS_COMPO_bdpm c where c.Code_CIS=m.Code_CIS) as nb_molecules FROM CIS_bdpm m WHERE " +
                "Denomination_du_medicament LIKE ? AND " +
                "Forme_pharmaceutique LIKE ? AND " +
                "Titulaires LIKE ? AND " +
                "Code_CIS IN (" +SQLSubstance+ ")" +
                finSQL;

            // Les valeurs à remplacer dans la requête;

        Cursor cursor = db.rawQuery(query, selectionArgs.toArray(new String[0]));
        Log.d("SQL", "searchMedicaments: ");

        if (cursor.moveToFirst()) {
            do {
                // Récupérer les valeurs de la ligne actuelle
                int codeCIS = cursor.getInt(cursor.getColumnIndexOrThrow("Code_CIS"));
                String denominationMedicament = cursor.getString(cursor.getColumnIndexOrThrow("Denomination_du_medicament"));
                String formePharmaceutiqueMedicament = cursor.getString(cursor.getColumnIndexOrThrow("Forme_pharmaceutique"));
                String voiesAdminMedicament = cursor.getString(cursor.getColumnIndexOrThrow("Voies_dadministration"));
                String titulairesMedicament = cursor.getString(cursor.getColumnIndexOrThrow("Titulaires"));
                String statutAdmiMedicament = cursor.getString(cursor.getColumnIndexOrThrow("Statut_administratif_de_lAMM"));
                String CountMolecules = cursor.getString(cursor.getColumnIndexOrThrow("nb_molecules"));

                // Créer un objet Medicament avec les valeurs récupérées
                Medicament medicament = new Medicament();
                medicament.setCodeCIS(codeCIS);
                medicament.setDenomination(denominationMedicament);
                medicament.setFormePharmaceutique(formePharmaceutiqueMedicament);
                medicament.setVoiesAdmin(voiesAdminMedicament);
                medicament.setTitulaires(titulairesMedicament);
                medicament.setStatut(statutAdmiMedicament);
                medicament.setNb_molecules(CountMolecules.toString());

                // Ajouter l'objet Medicament à la liste
                medicamentList.add(medicament);
            } while (cursor.moveToNext());
            Integer nbResultat = cursor.getCount();
            String nb = nbResultat.toString();
            String logEntry = String.format(denomination +" - "+ formePharmaceutique +" - "+ titulaires +" - "+ denominationSubstance +" - "+ voiesAdmin +" -" + " " + "\n Nombre de résultats: "+ nb) ;
            writeToLogFile(logEntry);
        } else {
            Toast toast = Toast.makeText(mycontext,"aucun resultat", Toast.LENGTH_LONG);
            toast.show();

            String logEntry = String.format(denomination +" - "+ formePharmaceutique +" - "+ titulaires +" - "+ denominationSubstance +" - "+ voiesAdmin +" -" + " " + "\n Aucun résultat");
            writeToLogFile(logEntry);
        }

        cursor.close();
        db.close();

        return medicamentList;
    }

    public List<String> getCompositionMedicament(int codeCIS) {
        List<String> compositionList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM CIS_compo_bdpm WHERE Code_CIS = ?", new String[]{String.valueOf(codeCIS)});
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                i++;
                String substance = cursor.getString(cursor.getColumnIndex("Denomination_substance"));
                String dosage = cursor.getString(cursor.getColumnIndex("Dosage_substance"));
                compositionList.add(i+ ".  " + substance + " - (" + dosage + ")");
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return compositionList;
    }

    public List<String> getPresentationMedicament(int codeCIS) {
        List<String> presentationList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM CIS_CIP_bdpm WHERE Code_CIS = ?", new String[]{String.valueOf(codeCIS)});
        int i=0;
        if (cursor.moveToFirst()) {
            do {
                i++;
                String libellePresentation = cursor.getString(cursor.getColumnIndex("Libelle_presentation"));
                presentationList.add(i+":"+libellePresentation);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return presentationList;
    }



    private void copydatabase() {

        final String outFileName = DATABASE_PATH + DATABASE_NAME;

        //AssetManager assetManager = mycontext.getAssets();
        InputStream myInput;

        try {
            // Ouvre  le fichier de la bdd de 'assets' en lecture
            myInput = mycontext.getAssets().open(DATABASE_NAME);

            // dossier de destination
            File pathFile = new File(DATABASE_PATH);
            if(!pathFile.exists()) {
                if(!pathFile.mkdirs()) {
                    Toast.makeText(mycontext, "Erreur : copydatabase(), pathFile.mkdirs()", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Ouverture en écriture du fichier bdd de destination
            OutputStream myOutput = new FileOutputStream(outFileName);

            // transfert de inputfile vers outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            // Fermeture
            Log.d("APP", "BDD copiée");
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.d("erreur", "erreur copydatabase: ");
            Toast.makeText(mycontext, "Erreur : copydatabase()", Toast.LENGTH_SHORT).show();
        }

        // on greffe le numéro de version
        try{
            SQLiteDatabase checkdb = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
            checkdb.setVersion(DATABASE_VERSION);
        }
        catch(SQLiteException e) {
            // bdd n'existe pas
        }
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mycontext=context;
        String filesDir = context.getFilesDir().getPath(); // /data/data/com.package.nom/files/
        DATABASE_PATH = filesDir.substring(0, filesDir.lastIndexOf("/")) + "/databases/"; // /data/data/com.package.nom/databases/

        // Si la bdd n'existe pas dans le dossier de l'app
        if (!checkdatabase()) {
            // copy db de 'assets' vers DATABASE_PATH
            Log.d("APP", "BDD à copier");
            copydatabase();
        }
    }

    public void writeToLogFile(String logEntry) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String timestamp = sdf.format(new Date());

        // Ajouter le timestamp au début de l'entrée de log
        File logDirectory = new File(mycontext.getFilesDir(), LOG_FILE_PATH);
        if (!logDirectory.exists()) {
            logDirectory.mkdirs();
        }

        File logFile = new File(logDirectory, LOG_FILE_NAME);
        try {
            FileOutputStream outputStream = new FileOutputStream(logFile, true);
            outputStream.write((timestamp + " - " + logEntry + "\n").getBytes());
            outputStream.close();
        } catch (IOException e) {
            Log.e("LogWriter", "Error writing to log file", e);
        }
    }

    public List<Generique> searchGenerique(String denomination, String formePharmaceutique, String titulaires, String denominationSubstance, String voiesAdmin
    ) {
        List<Generique> generiqueList = new ArrayList<>();
        ArrayList<String> selectionArgs = new ArrayList<>();
        selectionArgs.add("%" + denomination + "%");
        selectionArgs.add("%" + formePharmaceutique + "%");
        selectionArgs.add("%" + titulaires + "%");
        selectionArgs.add("%" + denominationSubstance + "%");


        SQLiteDatabase db = this.getReadableDatabase();
        String finSQL= "";
        if (!voiesAdmin.equals(PREMIERE_VOIE)){
            finSQL="AND Voies_dadministration = ? ";
            selectionArgs.add(voiesAdmin);
        }

        String SQLSubstance = "SELECT CODE_CIS FROM CIS_COMPO_bdpm WHERE replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(replace(upper(Denomination_substance), 'Â','A'),'Ä','A'),'À','A'),'É','E'),'Á','A'),'Ï','I'), 'Ê','E'),'È','E'),'Ô','O'),'Ü','U'), 'Ç','C' ) LIKE ?" ;
        //String SQLSubstance = "SELECT CODE_CIS FROM CIS_COMPO_bdpm WHERE Denomination_substance COLLATE latin1_general_cs_ai LIKE ?" ;

        // La requête SQL de recherche
        String query = "SELECT p.Code_CIS, p.libelle_generic " +
                "from  CIS_bdpm m  inner join CIS_GENER_bdpm p on m.Code_CIS = p.Code_CIS  " +
                "WHERE Statut_administratif_de_lAMM = 'Autorisation active' AND " +
                "Denomination_du_medicament LIKE ? AND " +
                "Forme_pharmaceutique LIKE ? AND " +
                "Titulaires LIKE ? AND " +
                "m.Code_CIS IN (" +SQLSubstance+ ")" +
                finSQL;

        // Les valeurs à remplacer dans la requête;

        Cursor cursor = db.rawQuery(query, selectionArgs.toArray(new String[0]));
        Log.d("SQL", "searchGenerique: ");

        if (cursor.moveToFirst()) {
            do {
                // Récupérer les valeurs de la ligne actuelle
                int codeCIS = cursor.getInt(cursor.getColumnIndexOrThrow("Code_CIS"));
                String libelleGeneric = cursor.getString(cursor.getColumnIndexOrThrow("Libelle_generic"));


                // Créer un objet Medicament avec les valeurs récupérées
                Generique generique = new Generique();
                generique.setCodeCIS(codeCIS);
                generique.setGenerique(libelleGeneric);

                // Ajouter l'objet Medicament à la liste
                generiqueList.add(generique);
            } while (cursor.moveToNext());
        } else {
            Toast toast = Toast.makeText(mycontext,"aucun resultat", Toast.LENGTH_LONG);
            toast.show();


        }

        cursor.close();
        db.close();

        return generiqueList;
    }

    public Cursor performQuery(String cisCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT gener.id _id, m.Code_CIS,Libelle_generic from CIS_bdpm m inner JOIN CIS_GENER_bdpm gener on m.Code_CIS = gener.Code_CIS WHERE Statut_administratif_de_lAMM='Autorisation active' AND gener.Code_CIS= ?";
        return db.rawQuery(query, new String[]{cisCode});
    }

    public String nameMed(String cisCode) {
        String Name = "aucun générique actif pour ce médicament" ;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT m.Denomination_du_medicament from  CIS_GENER_bdpm gener inner JOIN CIS_bdpm m on m.Code_CIS = gener.Code_CIS WHERE Statut_administratif_de_lAMM='Autorisation active' AND gener.Code_CIS= ?";
        Cursor cursor = db.rawQuery(query, new String[]{cisCode});
        if (cursor.moveToFirst()) {
            Name = cursor.getString(0);
        }return Name;
    }




}


