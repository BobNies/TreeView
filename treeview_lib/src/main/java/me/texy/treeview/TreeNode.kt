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
package me.texy.treeview

import me.texy.treeview.helper.TreeHelper
import java.util.*

class TreeNode(var value: Any?) {

    private var children: MutableList<TreeNode>?

    var level = 0
    var parent: TreeNode? = null
    var index = 0
    var isExpanded = false
    var isSelected = false
    var isItemClickEnable = true

    constructor(value: Any?, level: Int) : this(value) {
        this.level = level
    }

    fun addChild(treeNode: TreeNode?) {
        if (treeNode == null) {
            return
        }
        children?.add(treeNode)
        treeNode.index = getChildren().size
        treeNode.parent = this
    }

    fun removeChild(treeNode: TreeNode?) {
        if (treeNode == null || getChildren().size < 1) {
            return
        }
        if (getChildren().indexOf(treeNode) != -1) {
            getChildren().remove(treeNode)
        }
    }

    fun removeAll(treeNode: TreeNode?) {
        treeNode?.let {
            children?.clear()
        }
    }

    val isLastChild: Boolean
        get() {
            if (parent == null) {
                return false
            }
            val children: List<TreeNode> = parent!!.getChildren()
            return children.size > 0 && children.indexOf(this) == children.size - 1
        }

    val isRoot: Boolean
        get() = parent == null

    fun getChildren(): MutableList<TreeNode> {
        return (if (children == null) {
            mutableListOf()
        } else {
            children
        }) as MutableList<TreeNode>

    }

    val selectedChildren: List<TreeNode>
        get() {
            val selectedChildren: MutableList<TreeNode> = ArrayList()
            for (child in getChildren()) {
                if (child.isSelected) {
                    selectedChildren.add(child)
                }
            }
            return selectedChildren
        }

    fun setChildren(children: List<TreeNode?>?) {
        if (children == null) {
            return
        }
        this.children = ArrayList()
        for (child in children) {
            addChild(child)
        }
    }

    /**
     * Updating the list of children while maintaining the tree structure
     */
    fun updateChildren(children: MutableList<TreeNode>?) {
        val expands: MutableList<Boolean> = ArrayList()
        val allNodesPre = TreeHelper.getAllNodes(this)
        for (node in allNodesPre) {
            expands.add(node.isExpanded)
        }
        this.children = children
        val allNodes = TreeHelper.getAllNodes(this)
        if (allNodes.size == expands.size) {
            for (i in allNodes.indices) {
                allNodes[i].isExpanded = expands[i]
            }
        }
    }

    fun hasChild(): Boolean {
        return children!!.size > 0
    }

    val id: String
        get() = "$level,$index"

    companion object {
        @JvmStatic
        fun root(): TreeNode {
            return TreeNode(null)
        }
    }

    init {
        children = ArrayList()
    }
}