package mz.co.example.nhane.postar;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by SUtui on 5/19/2018.
 */

public class MessagemRecyclerAdapter extends RecyclerView.Adapter<MessagemRecyclerAdapter.MessagemViewHolder> {
    private OnItemClickListner onItemClickListner;
    private LayoutInflater inflater;
    private Cursor cursor;
    private ArrayList<Messagem> messagens = new ArrayList<>();

    public MessagemRecyclerAdapter(Context context, Cursor cursor) {
        this.inflater = LayoutInflater.from(context);
        this.cursor = cursor;
    }

    public void addicionaMessagem(Messagem messagem) {
        messagens.add(messagem);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessagemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.cardview_item, parent, false);
        return new MessagemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagemViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            holder.texto.setText(cursor.getString(cursor.getColumnIndex(MensagemOpenHelper.COLUMN_TEXTO_MENS)));
            Picasso.with(inflater.getContext()).load(Uri.parse(cursor.getString(cursor.getColumnIndex(MensagemOpenHelper.COLUMN_IMAGEM_URI_MENS)))).into(holder.foto);
            holder.emissor.setText(cursor.getString(cursor.getColumnIndex((MensagemOpenHelper.COLUMN_EMISSOR_MENS))));
            double numRate = cursor.getInt(cursor.getColumnIndex((MensagemOpenHelper.COLUMN_RATE_SUM_MENS)));
            double rateTotal = cursor.getInt(cursor.getColumnIndex((MensagemOpenHelper.COLUMN__TOTAL_RATE_MENS)));

            if (numRate != 0 || rateTotal != 0) {
                holder.rating.setNumStars((int) ((5 * numRate) / (5 * rateTotal)));
            } else holder.rating.setNumStars(0);


        }
    }


    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public void swapCursor(Cursor novoCursor) {
        if (cursor != null) {
            cursor.close();
        }

        cursor = novoCursor;

        if (novoCursor != null) {
            notifyDataSetChanged();
        }
    }

    public void setOnItemClickListner(OnItemClickListner onItemClickListner) {
        this.onItemClickListner = onItemClickListner;
    }

    public interface OnItemClickListner {
        void addRate(int index, RatingBar ratingBar, float rating, String key);

        void downloadPicture(int index, String downloadLink);

    }

    public class MessagemViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, RatingBar.OnRatingBarChangeListener {
        RatingBar rating;
        ImageView foto;
        TextView emissor, texto;

        MessagemViewHolder(View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.foto_messagem);
            texto = itemView.findViewById(R.id.texto_messagem);
            emissor = itemView.findViewById(R.id.emissor_messagem);
            rating = itemView.findViewById(R.id.rating_bar);


            foto.setOnLongClickListener(this);
            rating.setOnRatingBarChangeListener(this);


        }

        @Override
        public boolean onLongClick(View v) {
            if (onItemClickListner != null) {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    if (v.getId() == foto.getId()) {
                        if (cursor.moveToPosition(adapterPosition)) {
                            onItemClickListner.downloadPicture(adapterPosition, cursor.getString(cursor.getColumnIndex(MensagemOpenHelper.COLUMN_IMAGEM_URI_MENS)));

                            return true;
                        }
                    }
                    return false;


                }
                return false;
            }
            return false;
        }


        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            if (onItemClickListner != null) {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {

                    if (cursor.moveToPosition(adapterPosition)) {
                        onItemClickListner.addRate(adapterPosition, ratingBar, rating, cursor.getString(cursor.getColumnIndex(MensagemOpenHelper._COLUMN_ID_MENS)));
                    }

                }
            }
        }
    }


}
