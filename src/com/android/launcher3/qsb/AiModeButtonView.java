/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.launcher3.qsb;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.launcher3.R;
import com.android.launcher3.Utilities;

public class AiModeButtonView extends ImageView {
    private static final String TAG = "AiModeButtonView";

    public AiModeButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ScaleType.CENTER);
        setOnClickListener(view -> launchAiActivity(context));
    }

    public AiModeButtonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setScaleType(ScaleType.CENTER);
        setOnClickListener(view -> launchAiActivity(context));
    }

    private void launchAiActivity(Context context) {
        if (Utilities.isAiMusicSearchEnabled(context)) {
            launchMusicSearch(context);
            return;
        }

        String[] aiActivities = {
            "com.google.android.googlequicksearchbox.GeminiGatewayActivity",
            "com.google.android.googlequicksearchbox.SearchActivity",
            "com.google.android.googlequicksearchbox.VoiceSearchActivity",
            "com.google.android.googlequicksearchbox.OneSearchActivity",
        };

        for (String activityName : aiActivities) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setComponent(new ComponentName(Utilities.GSA_PACKAGE, activityName))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                return;
            } catch (ActivityNotFoundException | SecurityException e) {
                Log.d(TAG, "Activity not available: " + activityName);
            }
        }

        String[] aiIntents = {
            "android.intent.action.ASSIST",
            "android.intent.action.VOICE_ASSIST",
        };

        for (String intentAction : aiIntents) {
            try {
                Intent intent = new Intent(intentAction)
                        .setPackage(Utilities.GSA_PACKAGE)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                return;
            } catch (ActivityNotFoundException | SecurityException e) {
                Log.d(TAG, "Intent action not available: " + intentAction);
            }
        }

        // Fallback: try voice command via search package
        String searchPackage = QsbContainerView.getSearchWidgetPackageName(context);
        if (searchPackage != null) {
            try {
                Intent intent = new Intent(Intent.ACTION_VOICE_COMMAND)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .setPackage(searchPackage);
                context.startActivity(intent);
            } catch (ActivityNotFoundException | SecurityException e) {
                Log.e(TAG, "No AI or voice command activities found");
                Toast.makeText(context, R.string.ai_mode_not_available, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, R.string.ai_mode_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void launchMusicSearch(Context context) {
        String searchPackage = QsbContainerView.getSearchWidgetPackageName(context);
        if (searchPackage == null) {
            Toast.makeText(context, R.string.ai_mode_not_available, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .setAction("com.google.android.googlequicksearchbox.MUSIC_SEARCH")
                    .setPackage(searchPackage);
            context.startActivity(intent);
        } catch (ActivityNotFoundException | SecurityException e) {
            Log.d(TAG, "Music search not available, falling back to voice command");
            try {
                Intent intent = new Intent(Intent.ACTION_VOICE_COMMAND)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .setPackage(searchPackage);
                context.startActivity(intent);
            } catch (ActivityNotFoundException | SecurityException e2) {
                Toast.makeText(context, R.string.ai_mode_not_available, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
