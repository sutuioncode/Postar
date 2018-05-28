package mz.co.example.nhane.postar;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by SUtui on 5/20/2018.
 */
public class FeedFragment extends Fragment implements MessagemRecyclerAdapter.OnItemClickListner {
    public static final String POST = "Posts";
    public static final String QUALQUER_UM = "Qualquer";
    private StorageReference mStorageRef;
    private SQLiteDatabase database;
    private RecyclerView recyclerView;
    private MessagemRecyclerAdapter adapter;
    private DatabaseReference postDB;
    private String username;

    public FeedFragment() {
        // Required empty public constructor
    }

    public static FeedFragment getFragmentInstance(int position) {
        FeedFragment feedFragment = new FeedFragment();
        Bundle arg = new Bundle();
        arg.putInt("POSITION", position);
        feedFragment.setArguments(arg);
        return feedFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        SharedPreferences preferences = getActivity().getSharedPreferences("CREDENCIAL", Context.MODE_PRIVATE);
        username = preferences.getString("NAME", "Todos");

        postDB = FirebaseDatabase.getInstance().getReference(POST);
        mStorageRef = FirebaseStorage.getInstance().getReference("image");


        MensagemOpenHelper helper = new MensagemOpenHelper(getActivity());
        database = helper.getWritableDatabase();

        recyclerView = view.findViewById(R.id.feed_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        if (LoginActivity.currentUser.isAnonymous()) {
            adapter = new MessagemRecyclerAdapter(getActivity(), getCursorAnonimo());
        } else {
            adapter = new MessagemRecyclerAdapter(getActivity(), getCursorPrivado());
        }

        adapter.setOnItemClickListner(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        postDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messagem messagem = dataSnapshot.getValue(Messagem.class);
                addToDatabase(messagem, dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Messagem messagem = dataSnapshot.getValue(Messagem.class);
                addToDatabase(messagem, dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    private synchronized void addToDatabase(final Messagem messagem, String key) {


        ContentValues cv = new ContentValues();
        if (messagem.getFotoUri() == null || messagem.getFotoUri().trim().isEmpty()) {
            cv.put(MensagemOpenHelper.COLUMN_IMAGEM_URI_MENS, " ");
        } else
            cv.put(MensagemOpenHelper.COLUMN_IMAGEM_URI_MENS, messagem.getFotoUri());

        cv.put(MensagemOpenHelper.COLUMN_EXT_MENS, messagem.getExt());
        cv.put(MensagemOpenHelper.COLUMN_DATA_MENS, messagem.getData());
        cv.put(MensagemOpenHelper.COLUMN_EMISSOR_MENS, messagem.getEmissor());
        cv.put(MensagemOpenHelper._COLUMN_ID_MENS, key);
        cv.put(MensagemOpenHelper.COLUMN_TEXTO_MENS, messagem.getTexto());
        cv.put(MensagemOpenHelper.COLUMN__TOTAL_RATE_MENS, messagem.getTotalRate());
        cv.put(MensagemOpenHelper.COLUMN_RATE_SUM_MENS, messagem.getNumRate());
        cv.put(MensagemOpenHelper.COLUMN_TIPO_MENS, messagem.getTipo());
        cv.put(MensagemOpenHelper.COLUMN_RECEPTOR_MENS, messagem.getReceptor());


        try {
            database.insertWithOnConflict(MensagemOpenHelper.TABLE_MENSAGEM, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            if (LoginActivity.currentUser.isAnonymous()) {
                adapter.swapCursor(getCursorAnonimo());
            } else {
                adapter.swapCursor(getCursorPrivado());
            }
        } catch (SQLException e) {
            String whereCause = messagem.getEmissor() + String.valueOf(messagem.getData()) + " = ?";
            String[] whereArgs;
            //database.update(MensagemOpenHelper.TABLE_MENSAGEM,cv,whereCause, );
        }


    }

    public String baixarImagem(String path, String ext, long data) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(data));


        String subPath;
        if (ext == null || ext.trim().isEmpty()) {
            subPath = "Postar/FB-IMG-"
                    + calendar.get(Calendar.YEAR)
                    + calendar.get(Calendar.MONTH)
                    + calendar.get(Calendar.DAY_OF_MONTH)
                    + calendar.get(Calendar.HOUR)
                    + calendar.get(Calendar.MINUTE)
                    + calendar.get(Calendar.SECOND)
                    + "." + "png";
        } else {
            subPath = "Postar/FB-IMG-"
                    + calendar.get(Calendar.YEAR)
                    + calendar.get(Calendar.MONTH)
                    + calendar.get(Calendar.DAY_OF_MONTH)
                    + calendar.get(Calendar.HOUR)
                    + calendar.get(Calendar.MINUTE)
                    + calendar.get(Calendar.SECOND)
                    + "." + ext;
        }

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + subPath);
        if (file.exists()) {
            Toast.makeText(FeedFragment.this.getActivity(), file.getPath(), Toast.LENGTH_SHORT).show();
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + subPath;
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(path));

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, subPath);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); // to notify when download is complete
        request.allowScanningByMediaScanner();// if you want to be available from media players
        DownloadManager manager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);

        manager.enqueue(request);
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + subPath;
    }

    public Cursor getCursorAnonimo() {


        String[] args = {Messagem.TIPO_ANONIMO};
        return database.query(MensagemOpenHelper.TABLE_MENSAGEM,
                null,
                MensagemOpenHelper.COLUMN_TIPO_MENS + " = ? ",
                args,
                null,
                null,
                MensagemOpenHelper.COLUMN_DATA_MENS + " DESC");
    }

    public Cursor getCursorPrivado() {


        String[] args = {QUALQUER_UM, LoginActivity.pessoa.getUsername()};
        return database.query(MensagemOpenHelper.TABLE_MENSAGEM,
                null,
                MensagemOpenHelper.COLUMN_RECEPTOR_MENS + " in (?,?)",
                args,
                null,
                null,
                MensagemOpenHelper.COLUMN_DATA_MENS + " DESC");
    }


    @Override
    public void addRate(int index, RatingBar ratingBar, float rating, String key) {
        //postDB.child(key).updateChildren();


    }

    @Override
    public void downloadPicture(int index, String downloadLink) {
        Toast.makeText(getActivity(), "DOWNLOADIng PICTURE", Toast.LENGTH_SHORT).show();
        baixarImagem(downloadLink, "jpg", System.currentTimeMillis());

    }
}
