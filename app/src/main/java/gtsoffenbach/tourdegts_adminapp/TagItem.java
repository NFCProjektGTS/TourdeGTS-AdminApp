package gtsoffenbach.tourdegts_adminapp;

import android.nfc.NdefRecord;

import java.math.BigInteger;

/**
 * Created by Marlon on 05.07.2014.
 */
public class TagItem  {

    public TagItem(String name,String beschreibung, int id){
        this.name = name;
        this.beschreibung = beschreibung;
        this.id = id;
    }
    public TagItem(int id){
        this("Name unbekannt","Beschreibung unbekannt",id);
    }

    private String name;
    private int id;
    private String beschreibung;






    public String getName(){
        return this.name;
    }

    public int getId() {
        return  this.id;
    }

    public String getBeschreibung(){
        return  this.beschreibung;
    }


    public short getNfcTnf(){
        return NdefRecord.TNF_WELL_KNOWN;
    }

    public byte[] getNfcType(){

        return nfcManager.intToByteArray(this.getId());
    }




    @Override
    public String toString() {
        return "  "+ getId()+" | "+getName();
    }
}
