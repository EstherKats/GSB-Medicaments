package fr.euroforma.gsb_medicaments;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private DatabaseHelper databaseHelper;
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("fr.euroforma.gsb_medicaments", appContext.getPackageName());

    }
    public void checkDatabase() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("fr.euroforma.gsb_medicaments", appContext.getPackageName());
    }
    @Test
    public void checkMedicament() {
        // Context of the app under test.
        Medicament medicament = new Medicament();
        medicament.setCodeCIS(1234);
        medicament.setDenomination("denominationMedicament");
        medicament.setFormePharmaceutique("formePharmaceutiqueMedicament");
        medicament.setVoiesAdmin("voiesAdminMedicament");
        medicament.setTitulaires("titulairesMedicament");
        medicament.setStatut("statutAdministratif");
        medicament.setNb_molecules("NbMolecule");
        assertEquals("denominationMedicament", medicament.getDenomination());

    }




    @Test
    public void testGetDistinctVoiesAdmin() {
        List<String> voiesAdminList = databaseHelper.getDistinctVoiesAdmin();

        // Vérifier que la liste n'est pas vide
        assertFalse(voiesAdminList.isEmpty());

        // Vérifier que la première entrée est "Séléctionnez une voie d'administration"
        assertTrue(voiesAdminList.get(0).equals("Séléctionner une voie d'administration"));

        // Vous pouvez ajouter d'autres assertions pour vérifier le contenu de la liste
    }
    @Test
    public void testsearchMedicaments() {
        // Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        List<Medicament> maliste = databaseHelper.searchMedicaments("Aspirine","","UPSA","","orale");

        // Vérifier que la liste n'est pas vide
        assertFalse(maliste.isEmpty());

        // Vérifier que la première entrée est "Séléctionnez une voie d'administration"
        assertTrue(maliste.size()==4);

        // Vous pouvez ajouter d'autres assertions pour vérifier le contenu de la liste
    }
    @Before
    public void setUp() {
        // Initialiser DatabaseHelper avant chaque test
        Context context = ApplicationProvider.getApplicationContext();
        databaseHelper = DatabaseHelper.getInstance(context);
    }

    @After
    public void tearDown() {
        // Fermer la base de données après chaque test
        databaseHelper.close();
    }

}