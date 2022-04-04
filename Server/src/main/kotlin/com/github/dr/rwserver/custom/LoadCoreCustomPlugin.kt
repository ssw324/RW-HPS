/*
 * Copyright 2020-2022 RW-HPS Team and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/RW-HPS/RW-HPS/blob/master/LICENSE
 */

package com.github.dr.rwserver.custom

import com.github.dr.rwserver.data.plugin.PluginManage
import com.github.dr.rwserver.plugin.beta.gamepanel.GamePanel
import com.github.dr.rwserver.plugin.beta.uplist.UpList

class LoadCoreCustomPlugin {
    val core = "[Core Plugin]"
    val coreEx = "[Core Plugin Extend]"
    init {
        PluginManage.addPluginClass("UpList","Dr","$core UpList","1.0", UpList(),false)
        PluginManage.addPluginClass("GamePanel","Dr","$coreEx GamePanel","1.0", GamePanel(),false)
    }
}