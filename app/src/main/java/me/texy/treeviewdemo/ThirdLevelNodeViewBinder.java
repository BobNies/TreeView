package me.texy.treeviewdemo;

import android.view.View;
import android.widget.TextView;

import org.jetbrains.annotations.Nullable;

import me.texy.treeview.TreeNode;
import me.texy.treeview.base.CheckableNodeViewBinder;

/**
 * Created by zxy on 17/4/23.
 */

public class ThirdLevelNodeViewBinder extends CheckableNodeViewBinder {
    private TextView textView;

    public ThirdLevelNodeViewBinder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.node_name_view);
    }

    @Override
    public int getLayoutId() {
        return R.id.checkBox;
    }

    @Override
    public void bindView(TreeNode treeNode) {
        Object node = treeNode.getValue();
        if (node != null) {
            textView.setText(node.toString());
        }
    }

    @Override
    public void onNodeChanged(@Nullable TreeNode treeNode, boolean selected) {

    }
}
