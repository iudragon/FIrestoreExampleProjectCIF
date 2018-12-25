package dragon.bakuman.iu.firestoreexampleprojectcif;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";

    private EditText editTextTitle;
    private EditText editTextDescription;
    private TextView textViewData;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference notebookRef = db.collection("Notebook");

    private DocumentReference noteRef = db.document("Notebook/My First Note");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        textViewData = findViewById(R.id.text_view_data);
    }

    @Override
    protected void onStart() {
        super.onStart();

        notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null){

                    return; //due to return we dont execute whatever comes below it
                }

                String data = "";
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    Note note = documentSnapshot.toObject(Note.class);
                    note.setDocumentId(documentSnapshot.getId());

                    String documentId = note.getDocumentId();

                    String title = note.getTitle();
                    String description = note.getDescription();

                    data += "ID: " + documentId + "\nTItle: " + title + "\nDescription: " + description + "\n\n";

                }

                textViewData.setText(data);

            }
        });

    }

    public void addNote(View v) {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        Note note = new Note(title, description);

        notebookRef.add(note);
    }


    public void loadNotes(View v) {

        notebookRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        String data = "";

                        //this QuerySmapshot contains over multiple documentSnapshots
                        //iterate throught this QuerySnapshots
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            Note note = documentSnapshot.toObject(Note.class);
                            note.setDocumentId(documentSnapshot.getId());

                            String documentId = note.getDocumentId();

                            String title = note.getTitle();
                            String description = note.getDescription();

                            data += "ID: " + documentId + "\nTItle: " + title + "\nDescription: " + description + "\n\n";

                        }
                        textViewData.setText(data);

                    }
                });

    }
}