package mz.co.example.nhane.postar;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by SUtui on 5/25/2018.
 */

public class UsuarioAdapter extends CursorAdapter {
    LayoutInflater inflater;

    public UsuarioAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return inflater.inflate(R.layout.usuario_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        android.support.v7.widget.AppCompatTextView textView = view.findViewById(R.id.username_usuario_item);
        textView.setText(cursor.getString(cursor.getColumnIndex(MensagemOpenHelper.COLUMN_USERNAME_PES)));
    }
}
