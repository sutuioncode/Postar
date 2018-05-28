package mz.co.example.nhane.postar;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static mz.co.example.nhane.postar.FeedFragment.POST;
import static mz.co.example.nhane.postar.FeedFragment.QUALQUER_UM;

/**
 * Created by SUtui on 5/20/2018.
 */
public class ContactFragment extends Fragment {
    public static final int ANONIMO = 0;
    public static final int PUBLICO = 1;
    public static final int PRIVADO = 2;
    public static final String POST_PRIVADO = "Privado";
    public static final String POST_PUBLICO = "Publico";
    public static final String POST_ANONIMO = "Anonimo";
    public static final String USUARIO = "Usuarios";
    private static int PICK_IMAGE_REQUEST = 1;
    Uri imageUri;
    private ImageView image;
    private ImageButton lerImagemButton;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference postDatabase;
    private DatabaseReference usuarioDatabase;
    private String[] tipoMessagem;
    private ListView listView;
    private ArrayList<Uri> uris;
    private int position;
    private String usuario = "null";
    private ImageButton postarButton;
    private File file;
    private EditText text;
    private android.support.v4.widget.ContentLoadingProgressBar progressBar;

    private StorageReference mStorageRef;
    private SQLiteDatabase database;
    private Messagem messagem;

    public ContactFragment() {
        // Required empty public constructor
    }

    public static ContactFragment getFragmentInstance(int position) {
        ContactFragment contactFragment = new ContactFragment();
        Bundle arg = new Bundle();
        arg.putInt("POSITION", position);
        contactFragment.setArguments(arg);
        return contactFragment;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        progressBar = view.findViewById(R.id.progress_bar_upload_image);
        text = view.findViewById(R.id.contact_input);
        image = view.findViewById(R.id.contact_image_view);

        lerImagemButton = (ImageButton) view.findViewById(R.id.contact_ler_imagem);
        postarButton = view.findViewById(R.id.contact_postar_button);

        if (!LoginActivity.currentUser.isAnonymous()) {
            tipoMessagem = new String[]{"Anonimo", "Publico", "Privado"};
        } else tipoMessagem = new String[]{"Anonimo"};

        final Spinner spinner = view.findViewById(R.id.contact_spinner);
        spinner.setSelection(1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1, tipoMessagem);
        spinner.setAdapter(adapter);

        MensagemOpenHelper mensagemOpenHelper = new MensagemOpenHelper(getActivity());
        database = mensagemOpenHelper.getWritableDatabase();

        UsuarioAdapter usuarioAdapter = new UsuarioAdapter(getActivity(), getCursor(), 0);

        listView = view.findViewById(R.id.contact_list_view);
        listView.setAdapter(usuarioAdapter);

        mStorageRef = FirebaseStorage.getInstance().getReference("Imagem");


        usuarioDatabase = FirebaseDatabase.getInstance().getReference(USUARIO);
        usuarioDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    Pessoa pessoa = data.getValue(Pessoa.class);
                    addToDatabase(pessoa);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        postarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postarMessagem(usuario, spinner.getSelectedItemPosition());
                spinner.setSelection(0);


            }
        });


        lerImagemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lerImagem();

            }
            //Snackbar.make(lerImagemButton, "Clicked: " +createDialog(), Snackbar.LENGTH_SHORT).show();
        });

        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setSelector(android.R.color.holo_blue_light);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String username = cursor.getString(cursor.getColumnIndex(MensagemOpenHelper.COLUMN_USERNAME_PES));
                ImageView image = view.findViewById(R.id.check_usuario_item);
                image.setVisibility(View.VISIBLE);
                if (username != null) {
                    usuario = username;
                }

            }


        });

        return view;
    }

    private void lerImagem() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);


    }

    @Override
    public void onStart() {
        super.onStart();

        firebaseDatabase = FirebaseDatabase.getInstance();
        postDatabase = firebaseDatabase.getReference(POST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && data != null && resultCode == RESULT_OK) {
            imageUri = data.getData();
            Picasso.with(getContext()).load(imageUri).into(image);
        }
    }


    void postarMessagem(String receptor, int tipo) {
        String emissor;
        if (!LoginActivity.currentUser.isAnonymous()) {
            emissor = LoginActivity.pessoa.getUsername();
        } else {
            emissor = QUALQUER_UM;
        }
        messagem = new Messagem(text.getEditableText().toString(), emissor, receptor, System.currentTimeMillis());

        if (imageUri != null) {
            armazenarImage(receptor, tipo);

        } else {

            mandaMensagem(tipo, receptor);
        }


    }

    private String getFileExtention(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    void armazenarImage(final String receptor, final int tipo) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(messagem.getData()));

        progressBar.setVisibility(View.VISIBLE);

        StorageReference imagemRef = mStorageRef.child("IMG" +
                calendar.get(Calendar.YEAR) +
                calendar.get(Calendar.MONTH) +
                calendar.get(Calendar.DAY_OF_MONTH) +
                calendar.get(Calendar.HOUR) +
                calendar.get(Calendar.MINUTE) +
                calendar.get(Calendar.SECOND) +
                calendar.get(Calendar.MILLISECOND) +
                getFileExtention(imageUri));
        imagemRef.putFile(imageUri)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Retorna o link da imagem no servidor
                        progressBar.setVisibility(View.INVISIBLE);
                        progressBar.setProgress(0);
                        messagem.setFotoUri(taskSnapshot.getDownloadUrl().toString());
                        messagem.setExt(getFileExtention(imageUri));
                        mandaMensagem(tipo, receptor);
                        imageUri = null;


                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressBar.setVisibility(View.GONE);
                        progressBar.setProgress(0);
                        imageUri = null;
                        Toast.makeText(getActivity(), "Upload sem sucesso", Toast.LENGTH_SHORT).show();
                        exception.printStackTrace();
                        // ...
                    }
                }).addOnProgressListener(getActivity(), new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressBar.setProgress((int) progress);

            }
        });


    }

    void mandaMensagem(int tipo, String receptor) {
        messagem.setTexto(text.getEditableText().toString());
        switch (tipo) {
            case ANONIMO:
                messagem.setTipo(Messagem.TIPO_ANONIMO);
                messagem.setReceptor(QUALQUER_UM);
                messagem.setEmissor("Anonimo");
                postDatabase.child(new Date(messagem.getData()).toString()).setValue(messagem);
                messagem = null;
                text.getEditableText().clear();

                image.setImageBitmap(null);
                break;

            case PRIVADO:
                if (!receptor.equals("null")) {
                    messagem.setTipo(Messagem.TIPO_PRIVADO);
                    postDatabase.child(new Date(messagem.getData()).toString()).setValue(messagem);
                    messagem = null;
                    text.getEditableText().clear();

                    image.setImageBitmap(null);
                } else
                    Toast.makeText(getActivity(), "Escolha o nome do receptor", Toast.LENGTH_SHORT).show();
                break;

            case PUBLICO:
                messagem.setTipo(Messagem.TIPO_PUBLICO);
                messagem.setReceptor(QUALQUER_UM);
                postDatabase.child(new Date(messagem.getData()).toString()).setValue(messagem);
                messagem = null;
                text.getEditableText().clear();

                image.setImageBitmap(null);
                break;
        }
        text.getEditableText().clear();

        image.setImageBitmap(null);

    }

    public Cursor getCursor() {


        return database.query(MensagemOpenHelper.TABLE_PESSOA,
                null,
                null,
                null,
                null,
                null,
                MensagemOpenHelper.COLUMN_USERNAME_PES + " ASC");
    }

    private void addToDatabase(final Pessoa messagem) {


        ContentValues cv = new ContentValues();
        cv.put(MensagemOpenHelper.COLUMN_FOTO_URL_PES, " ");
        cv.put(MensagemOpenHelper.COLUMN_USERNAME_PES, messagem.getUsername());
        cv.put(MensagemOpenHelper._COLUMN_FIREBASE_ID_PES, messagem.getFirebaserId());
        try {
            database.insertOrThrow(MensagemOpenHelper.TABLE_PESSOA, null, cv);

        } catch (SQLException e) {
            Log.d("Database-insertOrThrow:", e + "");
        }


          /*else {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(messagem.getData()));

            StorageReference imagemRef = mStorageRef.child("Imagem").child(messagem.getFotoUri().getPath());

            try {
                File localFile = new File("img" + calendar.get(Calendar.YEAR) + calendar.get(Calendar.MONTH) + calendar.get(Calendar.DAY_OF_MONTH) + calendar.get(Calendar.HOUR) + calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND) + ".jpg");
                localFile.createNewFile();
                imagemRef.getFile(localFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                ContentValues cv = new ContentValues();
                                cv.put(MensagemOpenHelper.COLUMN_DATA_MENS, messagem.getData());
                                cv.put(MensagemOpenHelper.COLUMN_EMISSOR_MENS, messagem.getEmissor());
                                cv.put(MensagemOpenHelper._COLUMN_ID_MENS, messagem.getEmissor() + String.valueOf(messagem.getData()));
                                cv.put(MensagemOpenHelper.COLUMN_TEXTO_MENS, messagem.getTexto());
                                cv.put(MensagemOpenHelper.COLUMN_IMAGEM_URI_MENS, messagem.getFotoUri().getPath());
                                try {
                                    database.insertOrThrow(MensagemOpenHelper.TABLE_MENSAGEM, null, cv);
                                    us.swapCursor(getCursor());
                                } catch (SQLException e) {
                                    Log.d("Database-insertOrThrow:", e + "");
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getActivity(),"Nao foi possivel Baixar mensagem",Toast.LENGTH_SHORT).show();
                        // ...
                    }
                });
            } catch (IOException e) {
                Toast.makeText(getActivity(), "download sem successo", Toast.LENGTH_SHORT).show();
            }


        }*/

    }
}