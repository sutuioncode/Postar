package mz.co.example.nhane.postar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SUtui on 5/24/2018.
 */

public class MensagemOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "postar.db";
    public static final int DATABASE_VERSION = 10;

    public static final String TABLE_MENSAGEM = "mensagem";
    public static final String COLUMN_IMAGEM_URI_MENS = "imagem_uri";
    public static final String COLUMN_TEXTO_MENS = "texto";
    public static final String COLUMN_EMISSOR_MENS = "emissor";
    public static final String COLUMN_DATA_MENS = "data";
    public static final String COLUMN__TOTAL_RATE_MENS = "total_rating";
    public static final String COLUMN_RATE_SUM_MENS = "rate_sum";
    public static final String COLUMN_EXT_MENS = "ext";
    public static final String _COLUMN_ID_MENS = "_id_mens";
    public static final String COLUMN_TIPO_MENS = "tipo";
    public static final String COLUMN_RECEPTOR_MENS = "receptor";


    public static final String TABLE_PESSOA = "pessoa";
    public static final String _COLUMN_FIREBASE_ID_PES = "_id";
    public static final String COLUMN_USERNAME_PES = "username";
    public static final String COLUMN_EMAIL_PES = "email";
    public static final String COLUMN_PASSWORD_PES = "password";
    public static final String COLUMN_FOTO_URL_PES = "foto";


    public MensagemOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_MENSAGEM = "CREATE TABLE " + TABLE_MENSAGEM + " ( "
                + _COLUMN_ID_MENS + " VARCHAR (100) PRIMARY KEY, "
                + COLUMN_DATA_MENS + " INTEGER, "
                + COLUMN_IMAGEM_URI_MENS + " VARCHAR(500), "
                + COLUMN_TEXTO_MENS + " VARCHAR(100), "
                + COLUMN_RATE_SUM_MENS + " INTEGER, "
                + COLUMN__TOTAL_RATE_MENS + " INTEGER, "
                + COLUMN_EXT_MENS + " VARCHAR(5), "
                + COLUMN_EMISSOR_MENS + " VARCHAR(100),"
                + COLUMN_RECEPTOR_MENS + " VARCHAR(100),"
                + COLUMN_TIPO_MENS + " VARCHAR(15)"
                + ");";

        String CREATE_TABLE_PESSOA = "CREATE TABLE " + TABLE_PESSOA + " ( "
                + _COLUMN_FIREBASE_ID_PES + " VARCHAR (50) PRIMARY KEY, "
                + COLUMN_EMAIL_PES + "  VARCHAR(100), "
                + COLUMN_USERNAME_PES + " VARCHAR(150),"
                + COLUMN_PASSWORD_PES + " VARCHAR(100),"
                + COLUMN_FOTO_URL_PES +
                " VARCHAR(500)"
                + ");";

        db.execSQL(CREATE_TABLE_MENSAGEM);
        db.execSQL(CREATE_TABLE_PESSOA);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_PESSOA);
        db.execSQL("DROP TABLE " + TABLE_MENSAGEM);

        onCreate(db);

    }


}
