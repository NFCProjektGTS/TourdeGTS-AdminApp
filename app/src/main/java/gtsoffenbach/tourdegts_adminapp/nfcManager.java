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

import java.io.IOException;

/**
 * Created by Marlon on 23.07.2014.
 */
public class nfcManager implements MainActivity.ActivityLifeCycleListener {



    public enum Status {
        none,
        NFC_Hardware_nicht_vorhanden,
        NFC_Hardware_nicht_eingeschaltet,
        bereit_zum_schreiben,   //hat daten zum schreiben und wartet auf ein Tag
        schreibt,
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

    //todo: methode kommentieren
    /*
     *diese Methode wird aufgerufen, wenn das Handy etwas auf einen Tag schrieben soll.
     * Beim aufruf der Methode, wird das zu schreibende TagItem mit übergeben.
     * Ebenso wird der Status von "ready" auf "bereit zum schreiben" gesetzt.
     */
    public void write(TagItem tagItem) {
        if (this.status == Status.ready) {
            this.tagItem = tagItem;
            this.setStatus(Status.bereit_zum_schreiben);
        }
    }

    //todo: methode kommentieren
    /*
     *die Methode cancelWrite wird aufgerufen, wenn das Handy aufhören soll, auf ein Tag zu
     * warten, auf welches das TagItem geschrieben werden sollte.
     * Ebenso wird der Status von "bereit zum schreiben" auf "gesetzt" gesetzt.
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

    private void readTag() {

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
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;
            if (this.status == Status.bereit_zum_schreiben) {

                Tag wTAG = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                if (wTAG != null) {
                    this.setStatus(Status.schreibt);
                    writeTag(wTAG, this.createNdefMessage());

                }
            }
        }

    }

    private NdefMessage createNdefMessage(){
        try {
        if(tagItem.getType()==1){


           String appname = ((StartTagItem) tagItem).getAddress();
           NdefMessage msg;
           NdefRecord rec = NdefRecord.createApplicationRecord(appname);
           msg = new NdefMessage(rec);
           System.out.println(appname);
           return msg;



        }else {


            NdefRecord record = new NdefRecord(
                    this.tagItem.getNfcTnf(), this.tagItem.getNfcType(), new byte[0], null);
            NdefRecord[] records = new NdefRecord[]{record};
            return new NdefMessage(records);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getEmptyNdef();
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
                    this.endWrite(OnTagWriteListener.WRITE_ERROR_READ_ONLY);
                }
                else if (ndef.getMaxSize() < size) {
                    //Tag hat zu wenig Speicherplatz
                    this.endWrite(OnTagWriteListener.WRITE_ERROR_CAPACITY);
                }else {
                    ndef.writeNdefMessage(message);
                    this.endWrite(OnTagWriteListener.WRITE_OK);
                }

            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        this.endWrite(OnTagWriteListener.WRITE_OK);
                    } catch (IOException e) {
                        this.endWrite(OnTagWriteListener.WRITE_ERROR_IO_EXCEPTION);
                    }
                } else {
                    this.endWrite(OnTagWriteListener.WRITE_ERROR_BAD_FORMAT);
                }
            }
        } catch (TagLostException e) {
            this.endWrite(OnTagWriteListener.WRITE_ERROR_TAG_LOST);
            System.out.println("Failed to write. Tag out of range.");
        } catch (IOException e) {
            this.endWrite(OnTagWriteListener.WRITE_ERROR_IO_EXCEPTION);
            System.out.println("Failed to write. I/O Error");
        } catch (FormatException e) {
            this.endWrite(OnTagWriteListener.WRITE_ERROR_BAD_FORMAT);
            System.out.println("Failed to write. Tag unformatable!");
        }

    }


}
