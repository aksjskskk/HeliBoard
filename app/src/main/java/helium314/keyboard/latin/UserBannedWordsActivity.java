package helium314.keyboard.latin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Set;

public class UserBannedWordsActivity extends Activity {

    private ArrayList<String> wordsList;
    private ArrayAdapter<String> adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_banned_words);

        listView = findViewById(R.id.list_user_words);
        Button btnAdd = findViewById(R.id.btn_add_float);

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
            wordsList.add("No custom words added yet.");
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wordsList);
        listView.setAdapter(adapter);
    }

    private void showAddDialog() {
        final EditText input = new EditText(this);
        input.setHint("Type word here...");
        input.setTextColor(android.graphics.Color.BLACK); 

        new AlertDialog.Builder(this)
                .setTitle("Add Block Word")
                .setMessage("⚠️ WARNING: Once added, this word CANNOT be deleted. Are you sure?")
                .setView(input)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String txt = input.getText().toString();
                        if (!txt.trim().isEmpty()) {
                            // إضافة الكلمة
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
