/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.launcher3

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test for icon size scaling.
 */
@SmallTest
@RunWith(AndroidJUnit4::class)
class IconScaleTest : AbstractDeviceProfileTest() {

    @Before
    fun setup() {
        initializeVarsForPhone(deviceSpecs["phone"]!!)
    }

    @Test
    fun testIconScaleApplied() {
        val inv = InvariantDeviceProfile.INSTANCE.get(context)

        // Default scale 100%
        context.appComponent.launcherPrefs.putSync(LauncherPrefs.ICON_SIZE_SCALE.to(100))
        val dpDefault = inv.getDeviceProfile(context)
        val defaultIconSize = dpDefault.workspaceIconProfile.iconSizePx

        // Scale to 120%
        context.appComponent.launcherPrefs.putSync(LauncherPrefs.ICON_SIZE_SCALE.to(120))
        val dpLarge = inv.getDeviceProfile(context)
        val largeIconSize = dpLarge.workspaceIconProfile.iconSizePx

        assertThat(largeIconSize).isGreaterThan(defaultIconSize)
        assertThat(largeIconSize.toFloat()).isWithin(1f).of(defaultIconSize * 1.2f)

        // Scale to 80%
        context.appComponent.launcherPrefs.putSync(LauncherPrefs.ICON_SIZE_SCALE.to(80))
        val dpSmall = inv.getDeviceProfile(context)
        val smallIconSize = dpSmall.workspaceIconProfile.iconSizePx

        assertThat(smallIconSize).isLessThan(defaultIconSize)
        assertThat(smallIconSize.toFloat()).isWithin(1f).of(defaultIconSize * 0.8f)
    }

    @Test
    fun testAllAppsIconScaleApplied() {
        val inv = InvariantDeviceProfile.INSTANCE.get(context)

        context.appComponent.launcherPrefs.putSync(LauncherPrefs.ICON_SIZE_SCALE.to(100))
        val dpDefault = inv.getDeviceProfile(context)
        val defaultIconSize = dpDefault.allAppsProfile.iconSizePx

        context.appComponent.launcherPrefs.putSync(LauncherPrefs.ICON_SIZE_SCALE.to(120))
        val dpLarge = inv.getDeviceProfile(context)
        val largeIconSize = dpLarge.allAppsProfile.iconSizePx

        assertThat(largeIconSize).isGreaterThan(defaultIconSize)
        // Note: AllApps icon size calculation might be more complex due to grid constraints,
        // but it should still be larger.
    }

    @Test
    fun testTaskbarIconScaleUnaffected() {
        initializeVarsForTablet(deviceSpecs["tablet"]!!)
        val inv = InvariantDeviceProfile.INSTANCE.get(context)

        context.appComponent.launcherPrefs.putSync(LauncherPrefs.ICON_SIZE_SCALE.to(100))
        val dpDefault = inv.getDeviceProfile(context)
        val defaultTaskbarIconSize = dpDefault.taskbarProfile.iconSize

        context.appComponent.launcherPrefs.putSync(LauncherPrefs.ICON_SIZE_SCALE.to(120))
        val dpLarge = inv.getDeviceProfile(context)

        assertThat(dpLarge.taskbarProfile.iconSize).isEqualTo(defaultTaskbarIconSize)
    }
}
