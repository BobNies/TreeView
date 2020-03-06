/*
 * Copyright 2016 - 2017 ShineM (Xinyuan)
 * * Modified 2020 RNies
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under.
 */
package me.texy.treeview.base

import android.view.View
import me.texy.treeview.TreeNode

abstract class CheckableNodeViewBinder(itemView: View?) : BaseNodeViewBinder(itemView) {

    /**
     * Get the checkable view id. MUST BE A Checkable type！
     *
     * @return
     */
    abstract val checkableViewId: Int

    /**
     * Do something when a node select or deselect（only triggered by clicked）
     *
     * @param treeNode
     * @param selected
     */
    abstract fun onNodeSelectedChanged(treeNode: TreeNode?, selected: Boolean)
}