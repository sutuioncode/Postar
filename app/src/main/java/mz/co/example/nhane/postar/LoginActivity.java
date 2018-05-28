package mz.co.example.nhane.postar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static mz.co.example.nhane.postar.ContactFragment.USUARIO;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    public static FirebaseUser currentUser;
    public static Pessoa pessoa;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private Button login, registar;
    private EditText email, password;
    private FirebaseAuth mAuth;
    private AlertDialog dialog;
    private android.support.v4.widget.ContentLoadingProgressBar progressBar;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        login = findViewById(R.id.entrar_login);
        registar = findViewById(R.id.registar_login);
        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);
        progressBar = findViewById(R.id.progress_bar_login);


        databaseReference = FirebaseDatabase.getInstance().getReference(USUARIO);


        //login(preferences.getString("EMAIL","null"),preferences.getString("PASSWORD","null"));
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    public void irParaRegisto(View view) {
        Intent intent = new Intent(this, RegistarActivity.class);
        startActivity(intent);
    }

    public void fazerLogin(View view) {
        String emailTxt = email.getText().toString();
        String passwordTxt = password.getText().toString();
        if (emailTxt.isEmpty() || passwordTxt.isEmpty()) {
            Toast.makeText(this, "Preencha os campos de cadastro", Toast.LENGTH_SHORT).show();

        } else {
            login(emailTxt, passwordTxt);
            //Adicionar o usuario ao memoria do Dispositivo


        }

    }

    private void login(final String email, final String password) {
        progressBar.setVisibility(View.VISIBLE);
        if (!email.equals("null") || !password.equals("null")) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        currentUser = mAuth.getCurrentUser();

                        databaseReference.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    System.out.println("data = " + data);
                                }
                                System.out.println("dataSnapshot = " + dataSnapshot);

                                pessoa = dataSnapshot.getValue(Pessoa.class);

                                System.out.println("pessoa = " + pessoa);
                                progressBar.setVisibility(View.GONE);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        //updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Autenticacao sem Sucesso.",
                                Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        // updateUI(null);
                    }

                    // ...

                }
            });
        }


    }

    public void irParaLoginAnonimo(View view) {
        mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                currentUser = mAuth.getCurrentUser();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
