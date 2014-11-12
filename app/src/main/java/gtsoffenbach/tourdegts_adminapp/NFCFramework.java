package gtsoffenbach.tourdegts_adminapp;

import android.app.Activity;
import android.app.AlertDialog;
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
import java.math.BigInteger;

/**
 * Created by Kern on 21.07.2014.
 */

    public class NFCFramework {

        protected NfcAdapter mNfcAdapter;
        protected Activity caller;
        protected Tag wTAG;
        protected boolean WriteMode = false;
        protected boolean enabled = false;
        protected IntentFilter[] mTagFilters;
        protected NdefMessage[] mCurrentNdef;
        protected NdefMessage[] mWriteNdef;
        private String payload = "";

        NFCFramework(Activity caller) {
            this.caller = caller;
            this.mNfcAdapter = NfcAdapter.getDefaultAdapter(caller);
            IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            this.mTagFilters = new IntentFilter[]{tagDetected};

        }

        public boolean isEnabled() {
            return enabled;
        }

        public String getPayload() {
            return payload;
        }

        public void setPayload(String payload) {
            this.payload = payload;
        }

        public void installService() {
            if (enabled) {

                Intent activityIntent = new Intent(caller, caller.getClass());
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent intent = PendingIntent.getActivity(caller, 0,
                        activityIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                mNfcAdapter.enableForegroundDispatch(caller, intent, mTagFilters, null);
            }
        }

        public void uninstallService() {
            if (enabled) {
                mNfcAdapter.disableForegroundDispatch(caller);
            }
        }


        public boolean checkNFC() {
            if (mNfcAdapter != null) {
                if (!mNfcAdapter.isEnabled()) {
                    new Dialog(caller, 0);
                }
                if (mNfcAdapter.isEnabled()) {
                    this.enabled = true;
                    return true;
                }
            } else {
               System.out.println("NO NFC HARDWARE");
            }
            this.enabled = false;
            return false;
        }

        public void resolveIntent(Intent intent) {
            String action = intent.getAction();
            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                    || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

                Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                NdefMessage[] msgs;
                if (!WriteMode) {
                    /*this.wai.printDebugInfo("Reading");
                    if (rawMsgs != null) {
                        msgs = new NdefMessage[rawMsgs.length];
                        for (int i = 0; i < rawMsgs.length; i++) {
                            msgs[i] = (NdefMessage) rawMsgs[i];
                        }
                    } else {
                        msgs = RawNDEFContent(intent);

                    }
                    mCurrentNdef = msgs;
                    operate(mCurrentNdef);
                    printTag(mCurrentNdef);*/
                } else {

                    wTAG = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    if (wTAG != null && mWriteNdef[0] != null) {
                        int code = writeTag(wTAG, mWriteNdef[0]);
                        //System.out.println(OnTagWriteListener.onTagWrite(code));
                        if (MainActivity.dialogAnimation != null) {
                            MainActivity.dialogAnimation.closeDialog();
                        }
                    }
                }
            }

        }


       /* private byte[] rawTagData(Parcelable parc) {
            StringBuilder s = new StringBuilder();
            Tag tag = (Tag) parc;
            byte[] id = tag.getId();
            s.append("UID In Hex: ").append(Utils.convertByteArrayToHexString(id)).append("\n");
            s.append("UID In Dec: ").append(Utils.convertByteArrayToDecimal(id)).append("\n\n");

            String prefix = "android.nfc.tech.";
            s.append("Technologies: ");
            for (String tech : tag.getTechList()) {
                s.append(tech.substring(prefix.length()));
                s.append(", ");
            }
            s.delete(s.length() - 2, s.length());
            for (String tech : tag.getTechList()) {
                if (tech.equals(MifareClassic.class.getName())) {
                    s.append('\n');
                    MifareClassic mifareTag = MifareClassic.get(tag);
                    String type = "Unknown";
                    switch (mifareTag.getType()) {
                        case MifareClassic.TYPE_CLASSIC:
                            type = "Classic";
                            break;
                        case MifareClassic.TYPE_PLUS:
                            type = "Plus";
                            break;
                        case MifareClassic.TYPE_PRO:
                            type = "Pro";
                            break;
                    }
                    s.append("Mifare Classic type: ").append(type).append('\n');
                    s.append("Mifare size: ").append(mifareTag.getSize() + " bytes").append('\n');
                    s.append("Mifare sectors: ").append(mifareTag.getSectorCount()).append('\n');
                    s.append("Mifare blocks: ").append(mifareTag.getBlockCount());
                }

                if (tech.equals(MifareUltralight.class.getName())) {
                    s.append('\n');
                    MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                    String type = "Unknown";
                    switch (mifareUlTag.getType()) {
                        case MifareUltralight.TYPE_ULTRALIGHT:
                            type = "Ultralight";
                            break;
                        case MifareUltralight.TYPE_ULTRALIGHT_C:
                            type = "Ultralight C";
                            break;
                    }
                    s.append("Mifare Ultralight type: ").append(type);
                }
            }

            return s.toString().getBytes();
        } //Only reading

        private NdefMessage[] RawNDEFContent(Intent intent) {
            byte[] empty = new byte[0];
            byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] payload = rawTagData(tag);
            NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
            NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
            return new NdefMessage[]{msg};
        } //Only reading

        private void printTag(NdefMessage[] msgs) {
            for (NdefMessage msg : msgs) {
                for (NdefRecord rec : msg.getRecords()) {
                    byte[] payload = rec.getPayload();
                    String content = new String(payload);
                    this.wai.printDebugInfo("Message: " + msg.toString());
                    this.wai.printDebugInfo("Record: " + rec.toString());
                    this.wai.printDebugInfo("Content: " + content);
                }
            }
            //wai.printDebugInfo(msgs.toString());
            //System.out.println(msgs);
        }*/

        public int writeTag(Tag tag, NdefMessage message) {
            try {
                int size = message.toByteArray().length;
                Ndef ndef = Ndef.get(tag);
                if (ndef != null) {
                    ndef.connect();
                    if (!ndef.isWritable()) {
                        System.out.println("READONLY");
                        disableWrite();
                       // return OnTagWriteListener.WRITE_ERROR_READ_ONLY;
                    }
                    if (ndef.getMaxSize() < size) {
                        System.out.println("Tag capacity is " + ndef.getMaxSize() + " bytes, message is " +
                                size + " bytes.");
                        disableWrite();
                      //  return OnTagWriteListener.WRITE_ERROR_CAPACITY;
                    }

                    ndef.writeNdefMessage(message);
                    disableWrite();
                   // return OnTagWriteListener.WRITE_OK;
                } else {
                    NdefFormatable format = NdefFormatable.get(tag);
                    if (format != null) {
                        try {
                            format.connect();
                            format.format(message);
                            disableWrite();
                     //       return OnTagWriteListener.WRITE_OK;
                        } catch (IOException e) {
                            disableWrite();
                       //     return OnTagWriteListener.WRITE_ERROR_IO_EXCEPTION;
                        }
                    } else {
                        disableWrite();
                      //  return OnTagWriteListener.WRITE_ERROR_BAD_FORMAT;
                    }
                }
            } catch (TagLostException e) {
                disableWrite();
                System.out.println("Failed to write. Tag out of range.");
           //     return OnTagWriteListener.WRITE_ERROR_TAG_LOST;
            } catch (IOException e) {
                disableWrite();
                System.out.println("Failed to write. I/O Error");
           //     return OnTagWriteListener.WRITE_ERROR_IO_EXCEPTION;
            } catch (FormatException e) {
                disableWrite();
                System.out.println("Failed to write. Tag unformatable!");
            //    return OnTagWriteListener.WRITE_ERROR_BAD_FORMAT;
            }
            return 0;
        }

        public void enableWrite() {
            //allow write for next NFC intent
            if (enabled) {
                if (this.mWriteNdef != null) {
                    this.WriteMode = true;

                    MainActivity.dialogAnimation = new AlertDialogAnimation(caller, AlertDialog.THEME_HOLO_LIGHT, null);
                    MainActivity.dialogAnimation.loadNFCAnimation();
                    MainActivity.dialogAnimation.startAnimation();
                } else {
                    System.out.println("no data");
                }
            }
        }

        public void disableWrite() {
            if (enabled) {
                this.wTAG = null;
                this.mWriteNdef = null;
                this.WriteMode = false;
                this.payload = "";
            }
        }



    public static NdefMessage NdefFromId(int id) {
        try {
            final BigInteger bi = BigInteger.valueOf(id);
            final byte[] opc = bi.toByteArray();
            NdefRecord record = new NdefRecord(
                    NdefRecord.TNF_WELL_KNOWN, opc, new byte[0], new byte[0]);
            NdefRecord[] records = new NdefRecord[]{record};
            return new NdefMessage(records);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getEmptyNdef();
    }
    public static NdefMessage NdefFromApp(String appname) {
        try {
            NdefMessage msg;
            NdefRecord rec = NdefRecord.createApplicationRecord(appname);
            msg = new NdefMessage(rec);
            System.out.println(appname);
            return msg;
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


        public void createWriteNdef(NdefMessage message) {
            NdefMessage[] temp = {message};
            this.mWriteNdef = temp;
        }
        public void createWriteNdef(NdefMessage[] messages) {
            this.mWriteNdef = messages;
        }


    }

