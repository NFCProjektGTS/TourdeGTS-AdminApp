package gtsoffenbach.tourdegts_adminapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Marlon on 23.07.2014.
 */
public class nfcManager implements MainActivity.ActivityLifeCycleListener {

    private static final String TAG = nfcManager.class.getSimpleName();

    public static final int READ_WRITE_OK = 0;
    public static final int WRITE_ERROR_READ_ONLY = 1;
    public static final int WRITE_ERROR_CAPACITY = 2;
    public static final int WRITE_ERROR_BAD_FORMAT = 3;
    public static final int WRITE_ERROR_IO_EXCEPTION = 4;
    public static final int WRITE_ERROR_TAG_LOST = 5;
    public static final int WRITE_UNKNOWN_ERROR = 6;

    public static final int READ_ERROR_IO_EXCEPTION = 10;
    public static final int READ_ERROR_TAG_LOST = 11;
    public static final int READ_UNKNOWN_ERROR = 12;
    public static final int READ_UNKNOWN_TAGDATA = 13;
    public static final int READ_ERROR_NO_NDEF_TAG = 14;




    private TagItem lastReadTagItem;

    public enum Status {
        none,
        NFC_Hardware_nicht_vorhanden,
        NFC_Hardware_nicht_eingeschaltet,
        bereit_zum_schreiben,   //hat daten zum schreiben und wartet auf ein Tag
        schreibt,
        liest,
        lesen_Beendet,
        ready
    }


    public interface StatusChangeListener {
        public void onStatusChanged(Status newStatus);

    }

    private Status status;
    private TagItem tagItem;
    private StatusChangeListener statusChangeListener;
    private NfcAdapter nfcAdapter;
    private Activity activity;

    private PendingIntent pendingIntent;
    private IntentFilter[] intentFilters;
    private String[] [] techLists;


    public nfcManager(MainActivity caller) {
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(caller);
        this.status = Status.none;
        this.activity = caller;
        checkStatus();
        this.prepareNFC();
        caller.setActivityLifeCycleListener(this);

    }

    private void prepareNFC(){

        Intent activityIntent = new Intent(this.activity, activity.getClass());
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        this.pendingIntent = PendingIntent.getActivity(this.activity, 0,
                activityIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        this.intentFilters = new IntentFilter[]{tagDetected};
    }


    public void setStatusChangedListener(StatusChangeListener value) {
        this.statusChangeListener = value;
    }


    /*
     *diese Methode wird aufgerufen, wenn das Handy etwas auf einen Tag schreiben soll.
     * Beim aufruf der Methode, wird das zu schreibende TagItem mit übergeben.
     * Ebenso wird der Status von "ready" auf "bereit zum schreiben" gesetzt.
     */
    public void write(TagItem tagItem) {
        if (this.status == Status.ready) {
            this.tagItem = tagItem;
            this.setStatus(Status.bereit_zum_schreiben);
        }
    }


    /*
     *die Methode cancelWrite wird aufgerufen, wenn das Handy aufhören soll, auf ein Tag zu
     * warten, auf welches das TagItem geschrieben werden sollte.
     * Ebenso wird der Status von "bereit zum schreiben" auf "ready" gesetzt.
     */
    public void cancelWrite() {
        if (this.status == Status.bereit_zum_schreiben) {
            this.setStatus(Status.ready);
            this.tagItem = null;
        }
    }

    private void endWrite(int writeResult){
        if (this.status == Status.schreibt){
            this.setStatus(Status.ready);
            this.tagItem = null;
        }
    }

    private void endRead(int readResult, TagItem readItem){
        if (this.status == Status.liest){
            if(readResult == READ_WRITE_OK){
                Log.d(TAG,"lesen Erfolgreich");
                this.lastReadTagItem = readItem;
                this.setStatus(Status.lesen_Beendet);
            }else{
                Log.d(TAG,"FEHLER: "+readResult);
                this.setStatus(Status.ready);

            }
        }
    }

    public void finishRead(){
        if(this.getStatus() == Status.lesen_Beendet){
            this.lastReadTagItem = null;
            this.setStatus(Status.ready);
            enableForegroundDispatch();
        }
    }

    public TagItem getLastReadTagItem(){
        return this.lastReadTagItem;
    }



    //todo: methode kommentieren
    /*
     * diese Methode wird aufgerufen, wenn man erfahren möchte,
     * welchen Status das Handy momentan hat. ( z.B.:"bereit zum schreiben")
     */
    public Status getStatus() {
        return this.status;
    }


    private void setStatus(Status value) {

        Status oldStatus = this.status;
        this.status = value;

        if (oldStatus != this.status) {
            if (this.statusChangeListener != null) {
                statusChangeListener.onStatusChanged(this.status);
            }
        }
    }

    private void checkStatus(){

        if((this.status == Status.none) ||
                (this.status == Status.NFC_Hardware_nicht_eingeschaltet) ||
                (this.status == Status.NFC_Hardware_nicht_vorhanden) ||
                (this.status == Status.ready)){


            this.setStatus(this.checkNfcHardware());

        }
    }


    private Status checkNfcHardware() {

        if (this.hasNfcHardware()) {
            if (this.isNfcHardwareEnabled()) {
                return Status.ready;
            } else {
                return Status.NFC_Hardware_nicht_eingeschaltet;
            }
        } else {
            return Status.NFC_Hardware_nicht_vorhanden;
        }
    }

    private boolean hasNfcHardware() {
        if (this.nfcAdapter == null) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isNfcHardwareEnabled() {
        return this.nfcAdapter.isEnabled();
    }

    private void enableForegroundDispatch(){
        if(this.getStatus()==Status.ready){
            this.nfcAdapter.enableForegroundDispatch(this.activity, this.pendingIntent,this.intentFilters,this.techLists);
        }else {
            int i=0;
        }
    }

    private void disableForegroundDispatch(){
        this.nfcAdapter.disableForegroundDispatch(this.activity);
    }



    @Override
    public void onActivityResume() {

        checkStatus();
        enableForegroundDispatch();
    }

    @Override
    public void onActivityPause() {
        disableForegroundDispatch();
    }

    @Override
    public void onActivityNewIntent(Intent intent) {
        this.resolveIntent(intent);

    }




    /*
    ==================
     *NFC Ndef Message
    ==================
     */
    public void resolveIntent(Intent intent) {
        Log.d("HALLO", "TEST");


        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                if (this.status == Status.bereit_zum_schreiben) {
                    this.setStatus(Status.schreibt);
                    writeTag(tag, this.createNdefMessage());
                }else if(this.status == Status.ready){
                    this.setStatus(Status.liest);
                    this.readTag(tag);
                }
            }
        }

    }

    private NdefMessage createNdefMessage(){
        try {
        NdefRecord newRecord = null;

        if(tagItem instanceof StartTagItem){

           String appname = ((StartTagItem) tagItem).getAddress();
           newRecord = NdefRecord.createApplicationRecord(appname);

        }else {

            newRecord = new NdefRecord(
                    this.tagItem.getNfcTnf(), this.tagItem.getNfcType(), new byte[0], new byte[0]);
        }
            NdefRecord[] records = new NdefRecord[]{newRecord};
            return new NdefMessage(records);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return getEmptyNdef();
    }

    private TagItem createTagItem(NdefMessage message)
    {
        TagItem tagitem = null;
        if(message!=null){
            NdefRecord[] records = message.getRecords();
            if(records.length>0){
                NdefRecord record = records[0];
                if(record.getTnf()== NdefRecord.TNF_WELL_KNOWN){
                    // es ist ein normales tagitem
                    byte[] tagItemIdAsArray = record.getType();
                    if(tagItemIdAsArray.length ==4){
                        tagitem = new TagItem(nfcManager.byteArrayToInt(tagItemIdAsArray));
                    }
                }
                else if(record.getTnf()== NdefRecord.TNF_EXTERNAL_TYPE) {
                    // es ist wohl unser starttagitem
                    StartTagItem test = new StartTagItem();
                    NdefRecord testRecord = NdefRecord.createApplicationRecord(test.getAddress());
                    if(Arrays.equals(testRecord.getType() , record.getType()) )
                    {
                        tagitem=test;
                    }
                }
            }
        }

        return tagitem;

    }

    public static final NdefMessage getEmptyNdef() {
        byte[] empty = new byte[0];
        NdefRecord[] records = new NdefRecord[1];
        records[0] = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, empty, empty, empty);
        return new NdefMessage(records);
    }

    private void writeTag(Tag tag, NdefMessage message) {
        try {
            int size = message.toByteArray().length;
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    this.endWrite(WRITE_ERROR_READ_ONLY);
                }
                else if (ndef.getMaxSize() < size) {
                    //Tag hat zu wenig Speicherplatz
                    this.endWrite(WRITE_ERROR_CAPACITY);
                }else {
                    ndef.writeNdefMessage(message);
                    this.endWrite(READ_WRITE_OK);
                }

            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        this.endWrite(READ_WRITE_OK);
                    } catch (IOException e) {
                        this.endWrite(WRITE_ERROR_IO_EXCEPTION);
                    }
                } else {
                    this.endWrite(WRITE_ERROR_BAD_FORMAT);
                }
            }
        } catch (TagLostException e) {
            this.endWrite(WRITE_ERROR_TAG_LOST);
            System.out.println("Failed to write. Tag out of range.");
        } catch (IOException e) {
            this.endWrite(WRITE_ERROR_IO_EXCEPTION);
            System.out.println("Failed to write. I/O Error");
        } catch (FormatException e) {
            this.endWrite(WRITE_ERROR_BAD_FORMAT);
            System.out.println("Failed to write. Tag unformatable!");
        }

    }

    private void readTag (Tag tag) {
        Ndef ndef = null;
        try {
            ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                NdefMessage readMessage =  ndef.getCachedNdefMessage();
                TagItem tagitem = this.createTagItem(readMessage);
                if(tagitem!=null){
                    endRead(READ_WRITE_OK,tagitem);
                }else{
                    endRead(READ_UNKNOWN_TAGDATA,null);
                }

            }else{
                endRead(READ_ERROR_NO_NDEF_TAG,null);
            }
        } catch (TagLostException e) {
            this.endRead(READ_ERROR_TAG_LOST,null);
            //System.out.println("Failed to write. Tag out of range.");
        } catch (IOException e) {
            this.endRead(READ_ERROR_IO_EXCEPTION,null);
            //System.out.println("Failed to write. I/O Error");
        }catch (Exception e) {
            this.endRead(READ_UNKNOWN_ERROR, null);
        }

        finally {
            if(ndef!=null){
                try {
                    ndef.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    /*
    *Diese Methode wandelt das übergebene ByteArray in einen Integer um.
    * Es werden die ersten 4 Bytes aus dem ByteArray verwendet.
    * Bei im ByteArray weniger als 4 Bytes enthalten sind gibt es ein Fehler.
    *
     */
    public static int byteArrayToInt(byte[] byteArray){
       int value = 0;
       for (int i = 0; i<4; i++){
           int shift = (3-i)*8;
           value += (byteArray[i]& 0x000000FF)<< shift;
       }

       return value;
    }

    public static byte[] intToByteArray(int a){

        byte[] ret = new byte[4];
        ret[3] = (byte) (a & 0xFF);
        ret[2] = (byte) ((a >> 8) & 0xFF);
        ret[1] = (byte) ((a >> 16) & 0xFF);
        ret[0] = (byte) ((a >> 24) & 0xFF);

        return ret;
    }

}
