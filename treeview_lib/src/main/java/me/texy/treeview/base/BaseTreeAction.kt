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

import me.texy.treeview.TreeNode


interface BaseTreeAction {

    fun expandAll()
    fun expandNode(treeNode: TreeNode?)
    fun expandLevel(level: Int)
    fun collapseAll()
    fun collapseNode(treeNode: TreeNode?)
    fun collapseLevel(level: Int)
    fun toggleNode(treeNode: TreeNode?)
    fun deleteNode(node: TreeNode?)
    fun addNode(parent: TreeNode?, treeNode: TreeNode?)

    // TODO: 17/4/30
    val allNodes: List<TreeNode?>?
    // 1.add node at position
    // 2.add slide delete or other operations
}