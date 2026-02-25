package helium314.keyboard.latin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class UserBannedWordsActivity extends Activity {

    private ArrayList<String> wordsList;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private View emptyStateLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_banned_words);

        // Setup Header Back Button
        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        listView = findViewById(R.id.list_user_words);
        emptyStateLayout = findViewById(R.id.empty_state_layout);
        ImageButton btnAdd = findViewById(R.id.btn_add_float);

        refreshList();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialog();
            }
        });
    }

    private void refreshList() {
        Set<String> set = BlacklistManager.getUserBannedWords(this);
        wordsList = new ArrayList<>(set);

        if (wordsList.isEmpty()) {
            listView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
            // Using simple_list_item_1 for now as per "simple as now" request
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wordsList);
            listView.setAdapter(adapter);
        }
    }

    private void showAddDialog() {
        final EditText input = new EditText(this);
        input.setHint("Type word here...");

        // Add some padding to the input view in the dialog
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(padding, padding, padding, padding);

        new AlertDialog.Builder(this)
                .setTitle("Add Block Word")
                .setMessage("⚠️ WARNING: Once added, this word CANNOT be deleted. Are you sure?")
                .setView(input)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String txt = input.getText().toString();
                        if (!txt.trim().isEmpty()) {
                            BlacklistManager.addUserWord(UserBannedWordsActivity.this, txt);
                            refreshList(); 
                            Toast.makeText(getApplicationContext(), "Word locked permanently 🔒", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
