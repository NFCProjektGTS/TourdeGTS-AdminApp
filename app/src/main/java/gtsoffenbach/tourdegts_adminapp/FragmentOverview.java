package gtsoffenbach.tourdegts_adminapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Marlon on 08.07.2014.
 */
public class FragmentOverview extends Fragment implements  ListView.OnItemClickListener, nfcManager.StatusChangeListener {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private TagItemMagazine tagItemMagazine;
    private ListView listViewTagItems;
    private TagItem selectedTagItem = null;

    private nfcManager nfcManager = null;


    public FragmentOverview() {
    }


    public static FragmentOverview newInstance(int sectionNumber) {
        FragmentOverview fragment = new FragmentOverview();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);

        listViewTagItems = (ListView) rootView.findViewById(R.id.listView_TagItems);
        listViewTagItems.setOnItemClickListener(this);
        this.fillTagItemListView();


        this.nfcManager = ((MainActivity) this.getActivity()).getNfcManager();
        nfcManager.setStatusChangedListener(this);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.tagItemMagazine = ((MainActivity) activity).getTagItemMagazine();
    }

   
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        this.selectedTagItem = ((TagItem) listViewTagItems.getItemAtPosition(position));

        PreviewTagItemDialog previewTagItemDialog = new PreviewTagItemDialog(this.getActivity(), AlertDialog.THEME_HOLO_LIGHT,this.selectedTagItem);

    }


    private void fillTagItemListView() {
        ArrayAdapter<TagItem> listAdapter = new ArrayAdapter<TagItem>(getActivity(), R.layout.tagitemlist_item, this.tagItemMagazine.getAllTagItems());
        this.listViewTagItems.setAdapter(listAdapter);
    }


    @Override
    public void onStatusChanged(nfcManager.Status newStatus) {
       

        if(newStatus == gtsoffenbach.tourdegts_adminapp.nfcManager.Status.bereit_zum_schreiben){
            try{
                MainActivity.dialogAnimation = new AlertDialogAnimation(this.getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                MainActivity.dialogAnimation.loadNFCAnimation();
                MainActivity.dialogAnimation.startAnimation();
            }catch(Exception e){
                System.out.println(e.getCause());
            }

        }
        if(newStatus == gtsoffenbach.tourdegts_adminapp.nfcManager.Status.ready){
            if(MainActivity.dialogAnimation!=null){
                MainActivity.dialogAnimation.closeDialog();
            }
        }
    }
}
