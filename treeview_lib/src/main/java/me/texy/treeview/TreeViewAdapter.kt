/*
 * Copyright 2016 - 2017 ShineM (Xinyuan)
 * Modified 2020 RNies
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import me.texy.treeview.base.BaseNodeViewBinder
import me.texy.treeview.base.BaseNodeViewFactory
import me.texy.treeview.base.CheckableNodeViewBinder
import me.texy.treeview.helper.TreeHelper
import java.util.*

class TreeViewAdapter internal constructor(private val context: Context, private val root: TreeNode?,
                                           private val baseNodeViewFactory: BaseNodeViewFactory) : RecyclerView.Adapter<ViewHolder>() {
    private val expandedNodeList: MutableList<TreeNode>?
    private var treeView: TreeView? = null

    private fun buildExpandedNodeList() {
        expandedNodeList?.clear()

        root?.let {
            for (child in it.getChildren()) {
                insertNode(expandedNodeList, child)
            }
        }
    }

    private fun insertNode(nodeList: MutableList<TreeNode>?, treeNode: TreeNode) {
        nodeList?.add(treeNode)

        if (treeNode.hasChild() && treeNode.isExpanded) {
            for (child in treeNode.getChildren()) {
                insertNode(nodeList, child)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        // return expandedNodeList.get(position).getLevel(); // this old code row used to always return the level
        val treeNode = expandedNodeList?.let {
            expandedNodeList[position]
        }

        return baseNodeViewFactory.getViewType(treeNode)
    }

    override fun onCreateViewHolder(parent: ViewGroup, level: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(baseNodeViewFactory.getLayoutId(level), parent, false)
        val nodeViewBinder = baseNodeViewFactory.getViewBinder(view, level)
        nodeViewBinder.setTreeView(treeView)
        return nodeViewBinder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nodeView = holder.itemView

        expandedNodeList?.let {
            val treeNode = it[position]
            val viewBinder = holder as BaseNodeViewBinder

            if (treeNode.isItemClickEnable) {
                nodeView.setOnClickListener {
                    onNodeToggled(treeNode)
                    viewBinder.onNodeToggled(treeNode, treeNode.isExpanded)
                }
            }

            if (viewBinder is CheckableNodeViewBinder) {
                setupCheckableItem(nodeView, treeNode, viewBinder)
            }

            viewBinder.bindView(treeNode)
        }

    }

    private fun setupCheckableItem(nodeView: View,
                                   treeNode: TreeNode,
                                   viewBinder: CheckableNodeViewBinder) {
        val view = nodeView.findViewById<View>(viewBinder.layoutId)
        if (view is Checkable) {
            val checkableView = view as Checkable
            checkableView.isChecked = treeNode.isSelected
            view.setOnClickListener {
                val checked = checkableView.isChecked
                selectNode(checked, treeNode)
                viewBinder.onNodeChanged(treeNode, checked)
            }
        } else {
            throw ClassCastException("getCheckableViewId() must return a CheckBox's id")
        }
    }

    fun selectNode(checked: Boolean, treeNode: TreeNode) {
        treeNode.isSelected = checked
    }

    private fun selectChildren(treeNode: TreeNode, checked: Boolean) {
        val impactedChildren = TreeHelper.selectNodeAndChild(treeNode, checked)
        val index = expandedNodeList?.indexOf(treeNode) ?: -1
        if (index != -1 && impactedChildren.isNotEmpty()) {
            notifyItemRangeChanged(index, impactedChildren.size + 1)
        }
    }

    private fun selectParentIfNeed(treeNode: TreeNode, checked: Boolean) {
        val impactedParents = TreeHelper.selectParentIfNeedWhenNodeSelected(treeNode, checked)
        if (impactedParents.isNotEmpty()) {
            for (parent in impactedParents) {
                val position = expandedNodeList?.indexOf(parent) ?: -1
                if (position != -1) notifyItemChanged(position)
            }
        }
    }

    private fun onNodeToggled(treeNode: TreeNode) {
        treeNode.isExpanded = !treeNode.isExpanded
        if (treeNode.isExpanded) {
            expandNode(treeNode)
        } else {
            collapseNode(treeNode)
        }
    }

    override fun getItemCount(): Int {
        return expandedNodeList?.size ?: 0
    }

    /**
     * Refresh all,this operation is only used for refreshing list when a large of nodes have
     * changed value or structure because it take much calculation.
     */
    fun refreshView() {
        buildExpandedNodeList()
        notifyDataSetChanged()
    }

    // Insert a node list after index.
    private fun insertNodesAtIndex(index: Int, additionNodes: List<TreeNode>?) {
        expandedNodeList?.apply {
            if (index < 0 || index > this.size - 1 || additionNodes == null) {
                return
            }

            this.addAll(index + 1, additionNodes)
            notifyItemRangeInserted(index + 1, additionNodes.size)
        }
    }

    //Remove a node list after index.
    private fun removeNodesAtIndex(index: Int, removedNodes: List<TreeNode>?) {
        expandedNodeList?.apply {
            if (index < 0 || index > this.size - 1 || removedNodes == null) {
                return
            }
            this.removeAll(removedNodes)
            notifyItemRangeRemoved(index + 1, removedNodes.size)
        }
    }

    /**
     * Expand node. This operation will keep the structure of children(not expand children)
     */
    fun expandNode(treeNode: TreeNode?) {
        if (treeNode != null) {
            val additionNodes = TreeHelper.expandNode(treeNode, false)
            val index = expandedNodeList?.indexOf(treeNode) ?: -1
            insertNodesAtIndex(index, additionNodes)
        }
    }

    /**
     * Collapse node. This operation will keep the structure of children(not collapse children)
     */
    fun collapseNode(treeNode: TreeNode?) {
        if (treeNode != null) {
            val removedNodes = TreeHelper.collapseNode(treeNode, false)
            val index = expandedNodeList?.indexOf(treeNode) ?: -1
            removeNodesAtIndex(index, removedNodes)
        }
    }

    /**
     * Delete a node from list.This operation will also delete its children.
     */
    fun deleteNode(node: TreeNode?) {
        if (node?.parent != null) {
            val allNodes = TreeHelper.getAllNodes(root)
            if (allNodes.indexOf(node) != -1) {
                node.parent?.removeChild(node)
            }

            //remove children form list before delete
            collapseNode(node)
            val index = expandedNodeList?.indexOf(node) ?: -1
            if (index != -1) {
                expandedNodeList?.remove(node)
            }
            notifyItemRemoved(index)
        }

    }

    fun setView(treeView: TreeView?) {
        this.treeView = treeView
    }

    init {
        expandedNodeList = ArrayList()
        buildExpandedNodeList()
    }
}