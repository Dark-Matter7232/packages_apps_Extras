/*
 *  Copyright (C) 2016 The Dirty Unicorns Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.extras.settings.fragments;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.Vibrator;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;

import com.android.settings.Utils;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import com.android.internal.util.omni.OmniSwitchConstants;
import com.android.internal.util.omni.PackageUtils;
import com.android.internal.util.omni.DeviceUtils;

import com.android.internal.logging.nano.MetricsProto;

import com.extras.settings.preferences.SystemSettingSwitchPreference;

import com.extras.settings.preferences.CustomSeekBarPreference;

public class ButtonSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener{

    private static final String KEYS_SHOW_NAVBAR_KEY = "navigation_bar_show";
    private static final String CATEGORY_KEYS = "button_keys"; 

    private SystemSettingSwitchPreference mEnableNavBar;

    //Keys
   
    private static final String KEY_BUTTON_BRIGHTNESS = "button_brightness";
    private static final String KEY_BACKLIGHT_TIMEOUT = "backlight_timeout";
   
    // category keys
    private static final String CATEGORY_HWKEY = "hardware_keys";
   
    private ListPreference mBacklightTimeout;
    private CustomSeekBarPreference mButtonBrightness;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.buttonsettings_category);

        final PreferenceScreen prefScreen = getPreferenceScreen();
        final PreferenceCategory keysCategory =
                (PreferenceCategory) prefScreen.findPreference(CATEGORY_KEYS);

        boolean showNavBarDefault = DeviceUtils.deviceSupportNavigationBar(getActivity());
        boolean showNavBar = Settings.System.getInt(resolver,
                Settings.System.OMNI_NAVIGATION_BAR_SHOW, showNavBarDefault ? 1 : 0) == 1;
        mEnableNavBar = (SystemSettingSwitchPreference) prefScreen.findPreference(KEYS_SHOW_NAVBAR_KEY);
        mEnableNavBar.setChecked(showNavBar);

        mBacklightTimeout =
                (ListPreference) findPreference(KEY_BACKLIGHT_TIMEOUT);
        mButtonBrightness =
                (CustomSeekBarPreference) findPreference(KEY_BUTTON_BRIGHTNESS);
        if (mBacklightTimeout != null) {
            mBacklightTimeout.setOnPreferenceChangeListener(this);
            int BacklightTimeout = Settings.System.getInt(getContentResolver(),
                    Settings.System.BUTTON_BACKLIGHT_TIMEOUT, 5000);
            mBacklightTimeout.setValue(Integer.toString(BacklightTimeout));
            mBacklightTimeout.setSummary(mBacklightTimeout.getEntry());
        }
        if (mButtonBrightness != null) {
            int ButtonBrightness = Settings.System.getInt(getContentResolver(),
                    Settings.System.BUTTON_BRIGHTNESS, 255);
            mButtonBrightness.setValue(ButtonBrightness / 1);
            mButtonBrightness.setOnPreferenceChangeListener(this);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mBacklightTimeout) {
            String BacklightTimeout = (String) newValue;
            int BacklightTimeoutValue = Integer.parseInt(BacklightTimeout);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.BUTTON_BACKLIGHT_TIMEOUT, BacklightTimeoutValue);
            int BacklightTimeoutIndex = mBacklightTimeout
                    .findIndexOfValue(BacklightTimeout);
            mBacklightTimeout
                    .setSummary(mBacklightTimeout.getEntries()[BacklightTimeoutIndex]);
            return true;
        } else if (preference == mButtonBrightness) {
            int value = (Integer) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.BUTTON_BRIGHTNESS, value * 1);
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.EXTRAS;
    }

}
