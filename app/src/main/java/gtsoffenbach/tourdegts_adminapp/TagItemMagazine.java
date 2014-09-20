package gtsoffenbach.tourdegts_adminapp;

import java.util.ArrayList;

/**
 * Created by Marlon on 05.07.2014.
 *
 * Diese Klasse verwaltet alle zur Verfügung stehenden tagItems
 * Es darf nur ein Objekt dieser Klasse geben
 */
public class TagItemMagazine {

    ArrayList<TagItem> tagItems;

    /*
    im Konstruktor werden aus allen Assets TagItems erstellt und der ArrayList vom Typen <TagItem> hinzugefügt.
    Wenn Links vorhanden sind, würden in diesem Konstruktor auch LinkTagItems erstellt werden und der tagItem_ArrayList hinzugefügt werden.
     */
    public TagItemMagazine(){
        tagItems = new ArrayList<TagItem>();


        tagItems.add(new StartTagItem("Start Tag","Dies ist der Start Tag‏","gtsoffenbach.nfcgamespieler_appprototype"));
        tagItems.add(new TagItem("Test01 Tag","Dies ist ein Test Tag."));
        tagItems.add(new TagItem("Test02 Tag","Dies ist ein Test Tag."));
        tagItems.add(new TagItem("Test03 Tag","Dies ist ein Test Tag."));
        tagItems.add(new TagItem("Test04 Tag","Dies ist ein Test Tag."));

    }



    public void addTagItem(TagItem tagItem){
        tagItems.add(tagItem);

    }
    public TagItem getItemAt(int position){
        return tagItems.get(position);
    }

    /*
    diese Methode erstellt ein neues Array in dem alle TagItems gespeichert werden
    dieses Array wird dann zurückgegeben.
     */
    public ArrayList<TagItem> getAllTagItems(){
        return tagItems;
    }
}
