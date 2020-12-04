package me.texy.treeviewdemo;

import android.view.View;

import org.jetbrains.annotations.NotNull;

import me.texy.treeview.base.BaseNodeViewBinder;
import me.texy.treeview.base.BaseNodeViewFactory;


/**
 * Created by zxy on 17/4/23.
 */

public class MyNodeViewFactory extends BaseNodeViewFactory {

    @NotNull
    @Override
    public BaseNodeViewBinder getViewBinder(View view, int level) {
        switch (level) {
            case 1:
                return new SecondLevelNodeViewBinder(view);
            case 2:
                return new ThirdLevelNodeViewBinder(view);
            case 0:
            default:
                return new FirstLevelNodeViewBinder(view);
        }
    }

    @Override
    public int getLayoutId(int level) {
        switch (level) {
            case 0:
                return R.layout.item_first_level;
            case 1:
                return R.layout.item_second_level;
            case 2:
                return R.layout.item_third_level;
            default:
                return 0;
        }
    }
}
