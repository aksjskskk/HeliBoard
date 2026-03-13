package helium314.keyboard.latin;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserBannedWordsActivity extends Activity {

    private ArrayList<String> allWords;
    private ArrayList<String> displayedWords;
    private BlockedWordsAdapter adapter;
    private TextView textActiveCount;
    private View emptyStateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_banned_words);

        // Header Back Button
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());

        // Initialize Views
        textActiveCount = findViewById(R.id.text_active_count);
        emptyStateView = findViewById(R.id.empty_state_view);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_words);
        EditText inputSearch = findViewById(R.id.input_search);
        EditText inputNewWord = findViewById(R.id.input_new_word);
        View btnAddWord = findViewById(R.id.btn_add_word);

        // Setup List
        allWords = new ArrayList<>();
        displayedWords = new ArrayList<>();
        adapter = new BlockedWordsAdapter(displayedWords);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Initial Data Load
        refreshData();
        filter(""); // Show all initially

        // Search Logic
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Add Word Logic
        btnAddWord.setOnClickListener(v -> {
            String txt = inputNewWord.getText().toString();
            if (!txt.trim().isEmpty()) {
                BlacklistManager.addUserWord(this, txt);
                inputNewWord.setText("");
                refreshData();
                filter(inputSearch.getText().toString()); // Re-apply current filter
                Toast.makeText(this, "Word blocked successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshData() {
        Set<String> set = BlacklistManager.getUserBannedWords(this);
        allWords.clear();
        allWords.addAll(set);
    }

    private void filter(String query) {
        displayedWords.clear();
        if (query == null || query.isEmpty()) {
            displayedWords.addAll(allWords);
        } else {
            String lowerQuery = query.toLowerCase();
            for (String word : allWords) {
                if (word.toLowerCase().contains(lowerQuery)) {
                    displayedWords.add(word);
                }
            }
        }
        adapter.notifyDataSetChanged();
        updateUI();
    }

    private void updateUI() {
        textActiveCount.setText(displayedWords.size() + " Active"); // Show count of filtered items
        if (displayedWords.isEmpty()) {
            emptyStateView.setVisibility(View.VISIBLE);
        } else {
            emptyStateView.setVisibility(View.GONE);
        }
    }

    // RecyclerView Adapter
    private class BlockedWordsAdapter extends RecyclerView.Adapter<BlockedWordsAdapter.ViewHolder> {

        private List<String> words;

        BlockedWordsAdapter(List<String> words) {
            this.words = words;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_blocked_word, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String word = words.get(position);
            holder.textWord.setText(word);
            holder.textDate.setText("Permanent Block"); // Static text for now

            holder.btnDelete.setOnClickListener(v -> {
                Toast.makeText(UserBannedWordsActivity.this, "Cannot delete global blocks", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return words.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textWord;
            TextView textDate;
            View btnDelete;

            ViewHolder(View itemView) {
                super(itemView);
                textWord = itemView.findViewById(R.id.text_word);
                textDate = itemView.findViewById(R.id.text_date);
                btnDelete = itemView.findViewById(R.id.btn_delete_item);
            }
        }
    }
}
