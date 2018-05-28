package mz.co.example.nhane.postar;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by SUtui on 5/19/2018.
 */


@IgnoreExtraProperties
public class Messagem {
    public static final String TIPO_PRIVADO = "Privado";
    public static final String TIPO_ANONIMO = "Anonimo";
    public static final String TIPO_PUBLICO = "Publico";
    private String fotoUri;
    private String texto;
    private String emissor, receptor;
    private String tipo = TIPO_PUBLICO;
    private String ext;
    private long data;
    private int totalRate = 0;
    private int numRate = 0;


    public Messagem(String texto, String emissor, String receptor, String tipo, long data) {
        this.texto = texto;
        this.emissor = emissor;
        this.receptor = receptor;
        this.tipo = tipo;
        this.data = data;
    }

    public Messagem(String texto, String emissor, String receptor, long data) {
        this.texto = texto;
        this.emissor = emissor;
        this.receptor = receptor;
        this.data = data;
    }

    public Messagem(String emissor, String texto) {
        this.texto = texto;
        this.emissor = emissor;

    }

    public Messagem() {
    }

    public String getFotoUri() {
        return fotoUri;
    }

    public void setFotoUri(String fotoUri) {
        this.fotoUri = fotoUri;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getEmissor() {
        return emissor;
    }

    public void setEmissor(String emissor) {
        this.emissor = emissor;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public int getNumRate() {
        return numRate;
    }

    public void setNumRate(int numRate) {
        this.numRate = numRate;
    }

    public void addRate(int rate) {
        this.numRate += rate;
        this.totalRate++;

    }

    public int getTotalRate() {
        return totalRate;
    }

    public void setTotalRate(int totalRate) {
        this.totalRate = totalRate;
    }

    @Exclude
    public int getTotalAccRate() {
        return totalRate * 5;
    }

    @Exclude
    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    @Override
    public String toString() {
        return "Messagem{" +
                "fotoUri='" + fotoUri + '\'' +
                ", texto='" + texto + '\'' +
                ", emissor='" + emissor + '\'' +
                ", ext='" + ext + '\'' +
                ", data=" + data +
                ", totalRate=" + totalRate +
                ", numRate=" + numRate +
                '}';
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }
}
