package dragon.bakuman.iu.firestoreexampleprojectcif;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    //Constants keys
    //When we save values in firestore database we also need keys for them. (Key-Value pair)
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";

    //Two variables for editTextFields
    private EditText mEditTextTitle;
    private EditText mEditTextDescription;

    private TextView mTextViewData;

    //One variable to keep a reference to our Firestore Database
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DocumentReference noteRef = db.document("Notebook/My First Note");

//    private ListenerRegistration noteListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign the editText variables
        mEditTextTitle = findViewById(R.id.edit_text_title);
        mEditTextDescription = findViewById(R.id.edit_text_description);

        mTextViewData = findViewById(R.id.text_view_data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //addSnapshotListener returns the ListenerRegistration
        /*noteListener*/ noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if (e != null){
                    Toast.makeText(MainActivity.this, "Error while loading", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onEvent: " + e.toString());
                    return; //leave this method if there wasn't exception
                }

                if (documentSnapshot.exists()) {

                    String title = documentSnapshot.getString(KEY_TITLE);
                    String description = documentSnapshot.getString(KEY_DESCRIPTION);

                    mTextViewData.setText("Title: " + title + "\n" + "Description: " + description);


                }
            }
        });
    }

   /* @Override
    protected void onStop() {
        super.onStop();
        noteListener.remove();
    }*/

    //method for saveNote
    //make it public because it is in XML (prob cause of onClick there in XML)
    public void saveNote(View view) {

        //Extracting from editText
        //toString because without it, Its an editable and we need to convert to store in String variable
        String title = mEditTextTitle.getText().toString();
        String description = mEditTextDescription.getText().toString();

        //Define Map<type of the key, type of the value> to pass
        //Object because we can pass different types
        //Map is just an interface. HashMap is the specific implementation of the Map interface
        Map<String, Object> note = new HashMap<>();

        //this will appear in our document in firestore database
        note.put(KEY_TITLE, title);
        note.put(KEY_DESCRIPTION, description);
        //now we simply pass this note Map to our firestore database and set it as the value for our first document

        //we take our db variable which is a reference to our firestore database and call .collection which will be a reference to our first collection and then call .document which will be a reference to a document in our "notebook" collection

        noteRef.set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(MainActivity.this, "note saved", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());

            }
        });

    }


    public void loadNote(View view) {

        //to get value out of this get Request we call onSuccessListener and onFailureListener
        noteRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                    //this documentSnapshot contains all our data as long as the document exists
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.exists()) {

                            String title = documentSnapshot.getString(KEY_TITLE);
                            String description = documentSnapshot.getString(KEY_DESCRIPTION);

                            mTextViewData.setText("Title: " + title + "\n" + "Description: " + description);


                        } else {

                            Toast.makeText(MainActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());

            }
        });

    }

    public void updateDescription(View view) {

        //input from our EditText field
        String description = mEditTextDescription.getText().toString();

       /* Map<String, Object> note = new HashMap<>();
        note.put(KEY_DESCRIPTION, description);

        noteRef.set(note, SetOptions.merge());*/
       noteRef.update(KEY_DESCRIPTION, description);


    }
}










