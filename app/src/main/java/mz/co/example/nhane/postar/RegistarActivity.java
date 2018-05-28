package mz.co.example.nhane.postar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistarActivity extends AppCompatActivity {
    private static final String TAG = "RegistarActivity";
    private FirebaseAuth mAuth;
    private Button registar;
    private EditText name, email, password, confirmarPassword;
    private SharedPreferences preferences;
    private DatabaseReference usuarioRef;
    private android.support.v4.widget.ContentLoadingProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar);

        progressBar = findViewById(R.id.progress_bar_registar);

        usuarioRef = FirebaseDatabase.getInstance().getReference("Usuarios");

        mAuth = FirebaseAuth.getInstance();

        preferences = getSharedPreferences("CREDENCIAL", MODE_PRIVATE);

        name = findViewById(R.id.username_registar);
        email = findViewById(R.id.email_register);
        password = findViewById(R.id.password_registar);
        confirmarPassword = findViewById(R.id.password_confirmar_registar);
        registar = findViewById(R.id.button_registar);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    public void efectuarRegisto(View view) {

        if (name.getText().toString().trim().isEmpty() || email.getText().toString().trim().isEmpty() || password.getText().toString().trim().isEmpty() || confirmarPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
        } else if (!password.getText().toString().equals(confirmarPassword.getText().toString())) {
            Toast.makeText(this, "As senhas devem ser iguais", Toast.LENGTH_SHORT).show();
        } else {

            //Cria a conta de Email e password
            registar(email.getEditableText().toString(), password.getEditableText().toString());

            //Adicionar o usuario ao memoria do Dispositivo
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString("NAME", name.getEditableText().toString());
            edit.putString("EMAIL", email.getEditableText().toString());
            edit.putString("PASSWORD", password.getEditableText().toString());
            edit.apply();

        }
    }

    private void registar(final String email, final String password) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Se Sign in for um successo,
                            Log.d(TAG, "c");

                            //Ler usuario do firebase
                            FirebaseUser user = mAuth.getCurrentUser();


                            //Adicionar Usuario para a Base de Dados
                            Pessoa usuario = new Pessoa(user.getUid(), name.getEditableText().toString(), email, password);
                            usuarioRef.child(user.getUid()).setValue(usuario);

                            irParaAplicacao();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistarActivity.this, "Cadastro sem sucesso.",
                                    Toast.LENGTH_SHORT).show();

                        }
                        progressBar.setVisibility(View.GONE);

                        // ...
                    }
                });

    }

    public void irParaAplicacao() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}
