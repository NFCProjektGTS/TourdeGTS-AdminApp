package gtsoffenbach.tourdegts_adminapp;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {



    public interface ActivityLifeCycleListener{
        public void onActivityResume();
        public void onActivityPause();
        public void onActivityNewIntent(Intent intent);
        public NdefMessage[] NoNDEFContent(Intent intent);
        public byte[] rawTagData(Parcelable parc);
    }

    private ActivityLifeCycleListener activityLifeCycleListener = null;


    
   // public static NFCFramework framework;
    private nfcManager nfcManager;
    private CharSequence mTitle;
    private TagItemMagazine tagItemMagazine;
    public static AlertDialogAnimation dialogAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, FragmentOverview.newInstance(0))
                .commit();
        mTitle = getTitle();



        this.tagItemMagazine = new TagItemMagazine();
       // framework = new NFCFramework(this);
        nfcManager = new nfcManager(this);
    }

    public TagItemMagazine getTagItemMagazine(){
        return this.tagItemMagazine;
    }

    public nfcManager getNfcManager(){
        return nfcManager;
    }

   




    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    public void setActivityLifeCycleListener(ActivityLifeCycleListener value){
        this.activityLifeCycleListener = value;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onPause() {
        super.onPause();
       /* if (framework != null) {
            if (framework.checkNFC()) {
                framework.uninstallService();
            }
        }*/

        if(this.activityLifeCycleListener!=null){
            this.activityLifeCycleListener.onActivityPause();
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        /*if (framework != null) {
            framework.resolveIntent(getIntent());
        }*/

        if(this.activityLifeCycleListener!=null){
            this.activityLifeCycleListener.onActivityNewIntent(intent);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        setIntent(new Intent());

       /* if (framework != null) {
            if (framework.checkNFC()) {
                framework.installService();
            }
        } */

        if(this.activityLifeCycleListener!=null){
            this.activityLifeCycleListener.onActivityResume();
        }
    }


}
