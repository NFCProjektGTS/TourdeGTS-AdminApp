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
        tagItems.add(new TagItem("Info/Empfang Tag","Empfang am Hauteingang mit Informationen über die Schule.",1));
        tagItems.add(new TagItem("DSFinfo Tag","Hier gibt es Informationen zur DSF",2));
        tagItems.add(new TagItem("Mechatronik Tag","Kompetenzcheck zur Arbeit eines Mechatronikers mit Beispielen aus der Messtechnik und der Steuerungstechnik sollen die Schüler Einblicke in mechatronische Systeme bekommen.",3));
        tagItems.add(new TagItem("Schaltungen Tag","Elektrische Schaltungen werden Präsentiert von Herr Höhm.",4));
        tagItems.add(new TagItem("MatheImBG Tag","Vorstellung der Themen die in der Oberstufe unterrichtet werden. Mit unteranderem einem kleinen Kenntnis Test im Bereich Mathe und der Vorstellung von Geogebra.",5));
        tagItems.add(new TagItem("TaschenrechnerTag","Programmierung eines Taschenrechners in JavaScript der z.B. Berechnungen, wie Addieren oder Nullstellenfindung, im Browser ausführt. ",6));
        tagItems.add(new TagItem("Mechaniker Tag","Industriemechanik an der GTS wird vorgestellt.",7));
        tagItems.add(new TagItem("Sport Tag","Aufgebaute Slackline und Schüler Präsentationen über den Ski- und Snowboardkurs.",8));
        tagItems.add(new TagItem("Präsentation Tag","Präsentationen des Informatik Leistungskurses",9));
        tagItems.add(new TagItem("MatheMinigame Tag","Das Mathe Minigame.",10));    //hat das Minigame auch einen Tag?

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
