package gtsoffenbach.tourdegts_adminapp;

/**
 * Copied by Kern on 21.07.2014.
 */

    import android.app.Activity;
    import android.app.AlertDialog;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.animation.Animation;
    import android.view.animation.TranslateAnimation;
    import android.widget.Button;
    import android.widget.ImageView;

    import java.util.ArrayList;

    public class AlertDialogAnimation extends AlertDialog implements Button.OnClickListener{
        private Animation animation;
        private ArrayList<ImageView> images = new ArrayList<ImageView>();
        private View view;
        private Button btnAbbrechen;
        private nfcManager nfcManager;

        AlertDialogAnimation(Activity caller, int THEME, nfcManager nfcManager) {
            super(caller, THEME);
            this.nfcManager = nfcManager;

            animation = new TranslateAnimation(0, -230, 0, 0);
            animation.setDuration(1800);
            animation.setRepeatCount(Animation.INFINITE);
            LayoutInflater inflater = (LayoutInflater) caller.getSystemService(caller.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.dialog_wanimation, null);
            btnAbbrechen = (Button) view.findViewById(R.id.btnAbbrechen);
            btnAbbrechen.setOnClickListener(this);
            setTitle("NFC Tag beschreiben");
            setView(view);
            setCancelable(false);
            show();
            //setContentView(view);
        }

        public void loadNFCAnimation() {
            images.clear();
            ImageView tag = (ImageView) view.findViewById(R.id.imgViewTag);
            images.add(tag);
            ImageView handy = (ImageView) view.findViewById(R.id.imgViewHandy);
            images.add(handy);
        }

        public void startAnimation() {
            images.get(0).setVisibility(View.VISIBLE);
            images.get(1).setVisibility(View.VISIBLE);
            images.get(1).startAnimation(animation);

            //img.setImageResource(R.drawable.handy);
            //ObjectAnimator animation = ObjectAnimator.ofFloat(img,"x", 200);
            //img.setImageResource(R.drawable.bild_b);
        }

        public void closeDialog() {
            animation.cancel();
            images.clear();
            this.dismiss();
        }

        @Override
        public void onClick(View v) {
          this.nfcManager.cancelWrite();
        }
    }


