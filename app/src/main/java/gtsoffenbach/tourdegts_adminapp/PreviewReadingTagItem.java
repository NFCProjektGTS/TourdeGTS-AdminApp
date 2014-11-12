package gtsoffenbach.tourdegts_adminapp;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * Created by Marlon on 28.09.2014.
 */
public class PreviewReadingTagItem extends AlertDialog implements Button.OnClickListener{

    TagItem tagItemToDisplay;
    nfcManager nfcManager;


    private View view;
    private Button btnSchließen;
    public PreviewReadingTagItem(MainActivity caller, nfcManager nfcM){
        super(caller, AlertDialog.THEME_HOLO_DARK);
        this.nfcManager = nfcM;

        TagItem dummyTagItem = nfcManager.getLastReadTagItem();

        // das dummyTagItem enthält nur die korrekte Id
        // daher vom TagItemMagazine ein vollständiges TagItem holen
        tagItemToDisplay = caller.getTagItemMagazine().findTagItemByID(dummyTagItem.getId());

        // falls kein vollständiges tagitem gefunden dann besser das dummy nehmen als null zu haben
        if(tagItemToDisplay==null){
            tagItemToDisplay = dummyTagItem;
        }

            LayoutInflater inflater = (LayoutInflater) caller.getSystemService(caller.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.dialog_reading_tagitem, null);
            btnSchließen = (Button) view.findViewById(R.id.btnSchließen);
            btnSchließen.setOnClickListener(this);
            setTitle(tagItemToDisplay.getName());
            setMessage("ID: " + tagItemToDisplay.getId() + System.getProperty("line.separator") + tagItemToDisplay.getBeschreibung());
            setView(view);
            setCancelable(false);
            show();
            //setContentView(view);

    }

    @Override
    public void onClick(View v) {
        nfcManager.finishRead();
        this.dismiss();
    }
}
