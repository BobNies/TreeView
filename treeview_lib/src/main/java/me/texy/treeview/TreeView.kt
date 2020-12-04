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

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import me.texy.treeview.base.BaseNodeViewFactory
import me.texy.treeview.base.TreeAction
import me.texy.treeview.helper.TreeHelper


class TreeView(root: TreeNode, context: Context, baseNodeViewFactory: BaseNodeViewFactory) : TreeAction {
    private val root: TreeNode?
    private val context: Context
    private val baseNodeViewFactory: BaseNodeViewFactory
    private var rootView: RecyclerView? = null
    private var adapter: TreeViewAdapter? = null

    val view: View?
        get() {
            if (rootView == null) {
                rootView = buildRootView()
            }
            return rootView
        }

    override val selectedNodes: List<TreeNode?> = TreeHelper.getSelectedNodes(root)
    override val allNodes: List<TreeNode?>? = TreeHelper.getAllNodes(root)

    init {
        this.root = root
        this.context = context
        this.baseNodeViewFactory = baseNodeViewFactory
    }
    private fun buildRootView(): RecyclerView {
        val recyclerView = RecyclerView(context)
        /**
         * disable multi touch event to prevent terrible data set error when calculate list.
         */
        recyclerView.isMotionEventSplittingEnabled = false
        (recyclerView.itemAnimator as SimpleItemAnimator?)?.supportsChangeAnimations = false
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = TreeViewAdapter(context, root, baseNodeViewFactory)
        adapter?.setView(this)
        recyclerView.adapter = adapter
        return recyclerView
    }

    override fun expandAll() {
        if (root == null) {
            return
        }
        TreeHelper.expandAll(root)
        refreshTreeView()
    }

    fun refreshTreeView() {
        rootView?.let {
            (it.adapter as TreeViewAdapter?)?.refreshView()
        }
    }

    fun updateTreeView() {
        rootView?.let {
            it.adapter?.notifyDataSetChanged()
        }
    }

    override fun expandNode(treeNode: TreeNode?) {
        adapter?.expandNode(treeNode)
    }

    override fun expandLevel(level: Int) {
        TreeHelper.expandLevel(root, level)
        refreshTreeView()
    }

    override fun collapseAll() {
        root?.apply {
            TreeHelper.collapseAll(this)
            refreshTreeView()
        }
    }

    override fun collapseNode(treeNode: TreeNode?) {
        adapter?.collapseNode(treeNode)
    }

    override fun collapseLevel(level: Int) {
        TreeHelper.collapseLevel(root, level)
        refreshTreeView()
    }

    override fun toggleNode(treeNode: TreeNode?) {
        treeNode?.let {
            if (it.isExpanded) {
                collapseNode(it)
            } else {
                expandNode(it)
            }
        }
    }

    override fun deleteNode(node: TreeNode?) {
        adapter?.deleteNode(node)
    }

    override fun addNode(parent: TreeNode?, treeNode: TreeNode?) {
        parent?.addChild(treeNode)
        refreshTreeView()
    }

    override fun selectNode(treeNode: TreeNode) {
        adapter?.selectNode(true, treeNode)
    }

    override fun deselectNode(treeNode: TreeNode) {
        adapter?.selectNode(false, treeNode)
    }

    override fun selectAll() {
        TreeHelper.selectNodeAndChild(root, true)
        refreshTreeView()
    }

    override fun deselectAll() {
        TreeHelper.selectNodeAndChild(root, false)
        refreshTreeView()
    }

}