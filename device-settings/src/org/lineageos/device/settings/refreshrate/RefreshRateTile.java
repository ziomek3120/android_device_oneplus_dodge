/*
 * SPDX-FileCopyrightText: 2025 AlphaDroid
 * SPDX-License-Identifier: Apache-2.0
 */
package org.lineageos.device.settings.refreshrate;

import android.app.Service;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.content.Intent;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import androidx.annotation.Nullable;

import org.lineageos.device.settings.Constants;
import org.lineageos.device.settings.R;

public class RefreshRateTile extends TileService {
    private static final String TAG = "RefreshRateTile";

    private static final int[] RATES = {
            RefreshRateController.REFRESH_RATE_AUTO,
            RefreshRateController.REFRESH_RATE_60,
            RefreshRateController.REFRESH_RATE_90,
            RefreshRateController.REFRESH_RATE_120
    };

    /**
     * External action that other components (Activity) can send to request an immediate tile update.
     * We implement onStartCommand to react to this action and call updateTile().
     */
    public static final String ACTION_REQUEST_TILE_UPDATE =
            "org.lineageos.device.settings.action.REFRESH_RATE_TILE_UPDATE";

    private RefreshRateController mController;

    @Override
    public void onCreate() {
        super.onCreate();
        mController = RefreshRateController.getInstance(getApplicationContext());
        if (Constants.DEBUG) Log.i(TAG, "RefreshRateTile created");
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();

        // Short click: cycle through rates
        int current = mController.getGlobalRefreshRate();
        int next = getNextRate(current);

        if (Constants.DEBUG) Log.i(TAG, "Tile tapped. Current: " + current + ", Next: " + next);

        mController.setGlobalRefreshRate(next);

        // Update the tile immediately after changing the value.
        updateTile();
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        if (Constants.DEBUG) Log.i(TAG, "Tile added to Quick Settings");
        updateTile();
    }

    private int getNextRate(int current) {
        for (int i = 0; i < RATES.length; i++) {
            if (RATES[i] == current) {
                return RATES[(i + 1) % RATES.length];
            }
        }
        return RefreshRateController.REFRESH_RATE_AUTO;
    }

    private void updateTile() {
        Tile tile = getQsTile();
        if (tile == null) return;

        int current = mController.getGlobalRefreshRate();
        String label = getString(R.string.refresh_rate_title);
        String sub = formatRateLabel(current);

        tile.setState(Tile.STATE_ACTIVE);
        tile.setLabel(label);
        tile.setSubtitle(sub);
        tile.setContentDescription(label + " - " + sub);
        tile.setIcon(getIconForRate(current));
        tile.updateTile();
    }

    private String formatRateLabel(int rate) {
        if (rate == RefreshRateController.REFRESH_RATE_AUTO) {
            return getString(R.string.refresh_rate_auto);
        } else if (rate == RefreshRateController.REFRESH_RATE_60) {
            return getString(R.string.refresh_rate_60hz).trim();
        } else if (rate == RefreshRateController.REFRESH_RATE_90) {
            return getString(R.string.refresh_rate_90hz).trim();
        } else if (rate == RefreshRateController.REFRESH_RATE_120) {
            return getString(R.string.refresh_rate_120hz).trim();
        }
        return getString(R.string.refresh_rate_auto);
    }

    private Icon getIconForRate(int rate) {
        // try {
        //     if (rate == RefreshRateController.REFRESH_RATE_AUTO) {
        //         return Icon.createWithResource(this, R.drawable.ic_refresh_rate_auto);
        //     } else if (rate == RefreshRateController.REFRESH_RATE_60) {
        //         return Icon.createWithResource(this, R.drawable.ic_refresh_rate_60);
        //     } else if (rate == RefreshRateController.REFRESH_RATE_90) {
        //         return Icon.createWithResource(this, R.drawable.ic_refresh_rate_90);
        //     } else if (rate == RefreshRateController.REFRESH_RATE_120) {
        //         return Icon.createWithResource(this, R.drawable.ic_refresh_rate_120);
        //     }
        // } catch (Exception e) {
        //     if (Constants.DEBUG) Log.w(TAG, "Specific RR icon missing, using fallback", e);
        // }
        return Icon.createWithResource(this, R.drawable.ic_refresh_rate);
    }
}
