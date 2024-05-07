package fr.euroforma.gsb_medicaments;

import static org.junit.Assert.*;
import org.junit.Test;



public class MedicamentTest  {
    @Test
    public void checkMedicament() {
        // Context of the app under test.
        Medicament medicament = new Medicament();
        medicament.setCodeCIS(1234);
        medicament.setDenomination("Dolipran");
        medicament.setFormePharmaceutique("Fake formePharmaceutiqueMedicament");
        medicament.setVoiesAdmin("Fake voiesAdminMedicament");
        medicament.setTitulaires("Fake titulairesMedicament");
        medicament.setStatut("Fake statutAdministratif");
        medicament.setNb_molecules("Fake NbMolecule");
        assertEquals("Dolipran", medicament.getDenomination());

    }
}