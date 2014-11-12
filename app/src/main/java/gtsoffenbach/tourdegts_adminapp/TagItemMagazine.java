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


        tagItems.add(new StartTagItem());
        tagItems.add(new TagItem("Test01 Tag","Dies ist ein Test Tag(1).",1));
        tagItems.add(new TagItem("Test02 Tag","Dies ist ein Test Tag(2).",2));
        tagItems.add(new TagItem("Test03 Tag","Dies ist ein Test Tag(3).",3));
        tagItems.add(new TagItem("Test04 Tag","Dies ist ein Test Tag(4).",4));
        tagItems.add(new TagItem("Test05 Tag","Dies ist ein Test Tag(5).",5));
        tagItems.add(new TagItem("Test06 Tag","Dies ist ein Test Tag(6).",6));
        tagItems.add(new TagItem("Test07 Tag","Dies ist ein Test Tag(7).",7));
        tagItems.add(new TagItem("Test08 Tag","Dies ist ein Test Tag(8).",8));
        tagItems.add(new TagItem("Test09 Tag","Dies ist ein Test Tag(9).",9));
        tagItems.add(new TagItem("Test10 Tag","Dies ist ein Test Tag(10).",10));

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

    public TagItem findTagItemByID(int ID){
       for(int i = 0; i<tagItems.size();i++){
           if(tagItems.get(i).getId()==ID){
               return tagItems.get(i);
           }
       }
       return null;
    }
}
