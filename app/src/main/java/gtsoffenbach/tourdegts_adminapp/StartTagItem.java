package gtsoffenbach.tourdegts_adminapp;

/**
 * Created by Marlon on 05.07.2014.
 */
public class StartTagItem extends TagItem {
    private String address="gtsoffenbach.nfcgamespieler_appprototype";


    public StartTagItem() {
        super("Start Tag", "Dies ist der Start Tag‏",0);
    }

    public String getAddress(){
        return address;
    }


}
