package fr.euroforma.gsb_medicaments;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editTextDenomination, editTextFormePharmaceutique, editTextTitulaires, editTextDenominationSubstance;
    private Spinner spinnerVoiesAdmin;
    private Button btnSearch;
    private ListView listViewResults;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        editTextDenomination = findViewById(R.id.editTextDenomination);
        editTextFormePharmaceutique = findViewById(R.id.editTextFormePharmaceutique);
        editTextTitulaires = findViewById(R.id.editTextTitulaires);
        editTextDenominationSubstance = findViewById(R.id.editTextDenominationSubstance);
        spinnerVoiesAdmin = findViewById(R.id.spinnerVoiesAdmin);
        btnSearch = findViewById(R.id.btnSearch);
        listViewResults = findViewById(R.id.listViewResults);

        // Initialize the database helper
        dbHelper = new DatabaseHelper(this);

        // Set up the spinner with Voies_dadministration data
        setupVoiesAdminSpinner();

        // Set up the click listener for the search button
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Perform the search and update the ListView
                performSearch();
            }
        });
    }

    private void setupVoiesAdminSpinner() {
        // Fetch distinct Voies_dadministration data from the database and populate the spinner
        List<String> voiesAdminList = dbHelper.getDistinctVoiesAdmin();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, voiesAdminList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVoiesAdmin.setAdapter(spinnerAdapter);
    }


    private void performSearch() {
        // TODO: Implement the search logic using the entered criteria and update the ListView
        String denomination = editTextDenomination.getText().toString().trim();
        String formePharmaceutique = editTextFormePharmaceutique.getText().toString().trim();
        String titulaires = editTextTitulaires.getText().toString().trim();
        String denominationSubstance = editTextDenominationSubstance.getText().toString().trim();
        String voiesAdmin = spinnerVoiesAdmin.getSelectedItem().toString();

        // TODO: Use dbHelper to fetch search results and update the ListView
        List<Medicament> searchResults = dbHelper.searchMedicaments(denomination, formePharmaceutique, titulaires, denominationSubstance, voiesAdmin);

        // TODO: Create and set an adapter for the ListView to display search results
        MedicamentAdapter adapter = new MedicamentAdapter(this, searchResults);
        listViewResults.setAdapter(adapter);
    }
}