package directory.tripin.com.tripindirectory.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.appinvite.AppInviteInvitation;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.helper.Logger;

/**
 * @author Ravishankar
 * @version 1.0
 * @since 18-03-2018
 */

public class AppUtils {
    private Context mContext;

    public AppUtils(Context context) {
        this.mContext = context;
    }

    public void shareApp() {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "Indian Logistic Network");
            String sAux = "\nLet me recommend you this application\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=directory.tripin.com.tripindirectory \n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            mContext.startActivity(Intent.createChooser(i, "Share ILN"));
        } catch (Exception e) {
            //e.toString();
        }
    }

    public void sendFeedback() {
        try {
            Intent Email = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "ravishankar.ahirwar@tripin.co.in", null));
            Email.putExtra(Intent.EXTRA_SUBJECT, "Send Feedback/Feature Request/Bug Report for INL");
            Email.putExtra(Intent.EXTRA_TEXT, "Dear ..., \n Please let us know how to improve ?" + "");
            mContext.startActivity(Intent.createChooser(Email, "Send Feedback:"));
        } catch (ActivityNotFoundException e) {
            Logger.e("There is no app to send feedback for the app");
        }
    }

    public Intent getInviteIntent() {
        Intent inviteIntent = new AppInviteInvitation.IntentBuilder(mContext.getString(R.string.invitation_title))
                .setMessage(mContext.getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(mContext.getString(R.string.invitation_deep_link)))
                .setCustomImage(Uri.parse(mContext.getString(R.string.invitation_custom_image)))
                .setCallToActionText(mContext.getString(R.string.invitation_cta))
                .build();
        return inviteIntent;
    }

}