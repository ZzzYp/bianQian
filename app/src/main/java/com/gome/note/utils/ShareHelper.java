package com.gome.note.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;


import com.gome.note.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2016/12/20.
 *
 * @author pythoncat.cheng
 * @apiNote intent share imsge or text
 */

public class ShareHelper {

    public static void shareText2(@NonNull Context context, @NonNull String extraText) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, extraText);
        if (share.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(share, context.getString(R.string
                    .share_title)));
        } else {
            ToastHelper.show(context, "no receiver app......");
        }
    }

    /**
     * 分享功能
     *
     * @param context       context
     * @param activityTitle the name of activity
     * @param imgPath       the file route of image ,will show null if do not share image
     */
    public static void shareMsg2(@NonNull Context context,
                                 String activityTitle,
                                 String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // plain text
        } else {
            File f = new File(imgPath);
            if (f.exists() && f.isFile()) {
                intent.setType("image/*");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, activityTitle));
    }

    public static void shareMsg(@NonNull Context context,
                                String activityTitle,
                                String imgPath) {
        if (TextUtils.isEmpty(imgPath)) {
            ToastHelper.show(context, context.getString(R.string.nothing_to_share));
            return;
        }
        File f = new File(imgPath);
        if (f == null || !f.exists() || !f.isFile()) {
            ToastHelper.show(context, context.getString(R.string.nothing_to_share));
            return;
        }
        try {
            String pkg = context.getPackageName();
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setPackage(pkg);
            sharingIntent.setType("image/*");
            Uri u = Uri.fromFile(f);

            List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(
                    sharingIntent, PackageManager.MATCH_DEFAULT_ONLY);
            ArrayList<ComponentName> excludeLists = new ArrayList<>();
            if (resInfo != null && !resInfo.isEmpty()) {
                for (ResolveInfo info : resInfo) {
                    if (info != null) {
                        ActivityInfo activityInfo = info.activityInfo;
                        if (activityInfo != null && activityInfo.packageName.equals(context.getPackageName())) {
                            excludeLists.add(new ComponentName(activityInfo.packageName, activityInfo.name));
                        }
                    }
                }
            }
            sharingIntent.setPackage(null);
            sharingIntent.removeExtra(Intent.EXTRA_STREAM);
            sharingIntent.putExtra(Intent.EXTRA_STREAM, u);
            Intent chooserIntent = Intent.createChooser(sharingIntent, activityTitle);
            if (chooserIntent == null) {
                return;
            }
            if (excludeLists != null && !excludeLists.isEmpty()) {
                chooserIntent.putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, excludeLists.toArray(new Parcelable[]{}));
            }
            context.startActivity(chooserIntent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void shareText(Context context, String extraText) {
        if (extraText != null && !extraText.isEmpty()) {
            try {
                String pkg = context.getPackageName();
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.setPackage(pkg);
                List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(
                        sharingIntent, PackageManager.MATCH_DEFAULT_ONLY);
                ArrayList<ComponentName> excludeLists = new ArrayList<>();
                if (resInfo != null && !resInfo.isEmpty()) {
                    for (ResolveInfo info : resInfo) {
                        if (info != null) {
                            ActivityInfo activityInfo = info.activityInfo;
                            if (activityInfo != null && activityInfo.packageName.equals(context.getPackageName())) {
                                excludeLists.add(new ComponentName(activityInfo.packageName, activityInfo.name));
                            }
                        }
                    }
                }
                sharingIntent.setPackage(null);
                sharingIntent.removeExtra(Intent.EXTRA_TEXT);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, extraText);
                Intent chooserIntent = Intent.createChooser(sharingIntent, null);
                if (chooserIntent == null) {
                    return;
                }
                if (excludeLists != null && !excludeLists.isEmpty()) {
                    chooserIntent.putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, excludeLists.toArray(new Parcelable[]{}));
                }
                context.startActivity(chooserIntent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
