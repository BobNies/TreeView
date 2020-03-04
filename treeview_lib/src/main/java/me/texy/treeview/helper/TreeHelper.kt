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
package me.texy.treeview.helper

import me.texy.treeview.TreeNode
import java.util.*

/**
 * Created by xinyuanzhong on 2017/4/27.
 */
object TreeHelper {
    fun expandAll(node: TreeNode?) {
        if (node == null) {
            return
        }
        expandNode(node, true)
    }

    /**
     * Expand node and calculate the visible addition nodes.
     *
     * @param treeNode     target node to expand
     * @param includeChild should expand child
     * @return the visible addition nodes
     */
    fun expandNode(treeNode: TreeNode?, includeChild: Boolean): List<TreeNode> {
        val expandChildren: MutableList<TreeNode> = ArrayList()
        if (treeNode == null) {
            return expandChildren
        }
        treeNode.isExpanded = true
        if (!treeNode.hasChild()) {
            return expandChildren
        }
        for (child in treeNode.getChildren()) {
            expandChildren.add(child)
            if (includeChild || child.isExpanded) {
                expandChildren.addAll(expandNode(child, includeChild))
            }
        }
        return expandChildren
    }

    /**
     * Expand the same deep(level) nodes.
     *
     * @param root  the tree root
     * @param level the level to expand
     */
    fun expandLevel(root: TreeNode?, level: Int) {
        if (root != null) {
            for (child in root.getChildren()) {
                if (child.level == level) {
                    expandNode(child, false)
                } else {
                    expandLevel(child, level)
                }
            }
        }
    }

    fun collapseAll(node: TreeNode?) {
        if (node != null) {
            for (child in node.getChildren()) {
                performCollapseNode(child, true)
            }
        }
    }

    /**
     * Collapse node and calculate the visible removed nodes.
     *
     * @param node         target node to collapse
     * @param includeChild should collapse child
     * @return the visible addition nodes before remove
     */
    fun collapseNode(node: TreeNode, includeChild: Boolean): List<TreeNode> {
        val treeNodes = performCollapseNode(node, includeChild)
        node.isExpanded = false
        return treeNodes
    }

    private fun performCollapseNode(node: TreeNode?, includeChild: Boolean): List<TreeNode> {
        val collapseChildren: MutableList<TreeNode> = ArrayList()
        if (node == null) {
            return collapseChildren
        }
        if (includeChild) {
            node.isExpanded = false
        }
        for (child in node.getChildren()) {
            collapseChildren.add(child)
            if (child.isExpanded) {
                collapseChildren.addAll(performCollapseNode(child, includeChild))
            } else if (includeChild) {
                performCollapseNodeInner(child)
            }
        }
        return collapseChildren
    }

    /**
     * Collapse all children node recursive
     *
     * @param node target node to collapse
     */
    private fun performCollapseNodeInner(node: TreeNode?) {
        if (node != null) {
            node.isExpanded = false
            for (child in node.getChildren()) {
                performCollapseNodeInner(child)
            }
        }
    }

    fun collapseLevel(root: TreeNode?, level: Int) {
        if (root != null) {
            for (child in root.getChildren()) {
                if (child.level == level) {
                    collapseNode(child, false)
                } else {
                    collapseLevel(child, level)
                }
            }
        }
    }

    fun getAllNodes(root: TreeNode?): List<TreeNode> {
        val allNodes: MutableList<TreeNode> = ArrayList()
        root?.apply {
            fillNodeList(allNodes, this)
        }

        allNodes.remove(root)
        return allNodes
    }

    private fun fillNodeList(treeNodes: MutableList<TreeNode>, treeNode: TreeNode) {
        treeNodes.add(treeNode)
        if (treeNode.hasChild()) {
            for (child in treeNode.getChildren()) {
                fillNodeList(treeNodes, child)
            }
        }
    }

    /**
     * Select the node and node's children,return the visible nodes
     */
    fun selectNodeAndChild(treeNode: TreeNode?, select: Boolean): List<TreeNode> {
        val expandChildren: MutableList<TreeNode> = ArrayList()
        if (treeNode == null) {
            return expandChildren
        }
        treeNode.isSelected = select
        if (!treeNode.hasChild()) {
            return expandChildren
        }
        if (treeNode.isExpanded) {
            for (child in treeNode.getChildren()) {
                expandChildren.add(child)
                if (child.isExpanded) {
                    expandChildren.addAll(selectNodeAndChild(child, select))
                } else {
                    selectNodeInner(child, select)
                }
            }
        } else {
            selectNodeInner(treeNode, select)
        }
        return expandChildren
    }

    private fun selectNodeInner(treeNode: TreeNode?, select: Boolean) {
        if (treeNode != null) {
            treeNode.isSelected = select
            if (treeNode.hasChild()) {
                for (child in treeNode.getChildren()) {
                    selectNodeInner(child, select)
                }
            }
        }

    }

    /**
     * Select parent when all the brothers have been selected, otherwise deselect parent,
     * and check the grand parent recursive.
     */
    fun selectParentIfNeedWhenNodeSelected(treeNode: TreeNode?, select: Boolean): List<TreeNode> {
        val impactedParents: MutableList<TreeNode> = ArrayList()
        if (treeNode == null) {
            return impactedParents
        }

        //ensure that the node's level is bigger than 1(first level is 1)
        val parent = treeNode.parent
        if (parent?.parent == null) {
            return impactedParents
        }
        val brothers: List<TreeNode> = parent.getChildren()
        var selectedBrotherCount = 0
        for (brother in brothers) {
            if (brother.isSelected) selectedBrotherCount++
        }
        if (select && selectedBrotherCount == brothers.size) {
            parent.isSelected = true
            impactedParents.add(parent)
            impactedParents.addAll(selectParentIfNeedWhenNodeSelected(parent, true))
        } else if (!select && selectedBrotherCount == brothers.size - 1) {
            // only the condition that the size of selected's brothers
            // is one less than total count can trigger the deselect
            parent.isSelected = false
            impactedParents.add(parent)
            impactedParents.addAll(selectParentIfNeedWhenNodeSelected(parent, false))
        }
        return impactedParents
    }

    /**
     * Get the selected nodes under current node, include itself
     */
    fun getSelectedNodes(treeNode: TreeNode?): List<TreeNode> {
        val selectedNodes: MutableList<TreeNode> = ArrayList()
        if (treeNode == null) {
            return selectedNodes
        }
        if (treeNode.isSelected && treeNode.parent != null) selectedNodes.add(treeNode)
        for (child in treeNode.getChildren()) {
            selectedNodes.addAll(getSelectedNodes(child))
        }
        return selectedNodes
    }

    /**
     * Return true when the node has one selected child(recurse all children) at least,
     * otherwise return false
     */
    fun hasOneSelectedNodeAtLeast(treeNode: TreeNode?): Boolean {
        if (treeNode == null || treeNode.getChildren().size == 0) {
            return false
        }
        val children: List<TreeNode> = treeNode.getChildren()
        for (child in children) {
            if (child.isSelected || hasOneSelectedNodeAtLeast(child)) {
                return true
            }
        }
        return false
    }
}