package com.single.mvvm.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.single.mvvm.BR;
import com.single.mvvm.suspension.ISuspensionInterface;

import java.util.List;

/**
 * Created by xiangcheng on 18/2/2.
 */

public abstract class RecyclerViewDatabingAdapter<T extends ISuspensionInterface, B extends ViewDataBinding, O extends ViewDataBinding> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = RecyclerViewDatabingAdapter.class.getSimpleName();
    private List<T> list;
    private B dataBing;
    private O otherBing;
    private static final int title = 0;
    private static final int listItem = 1;

    public RecyclerViewDatabingAdapter(List<T> list) {
        this.list = list;
    }

    public void refreshData(List<T> list) {
        if (this.list == null) {
            this.list = list;
        } else {
            this.list.clear();
            this.list.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).isShowSuspension() ? title : listItem;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //同样会根据布局生成一个相应的binding
        RecyclerView.ViewHolder viewHolder;
        if (viewType == title) {
            otherBing = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()), getTitleLayout(), parent, false);
            viewHolder = new OtherBindingHolder(otherBing.getRoot());
            ((OtherBindingHolder) viewHolder).setBinding(otherBing);
        } else {
            dataBing = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()), getItemLayout(), parent, false);
            viewHolder = new BindingHolder(dataBing.getRoot());
            ((BindingHolder) viewHolder).setBinding(dataBing);

        }
        //这里getRoot会返回布局的根view

        return viewHolder;
    }

    protected abstract int getTitleLayout();

    protected abstract int getItemLayout();

    protected abstract void itemClick(T item);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //在绑定数据的时候是固定写法，就没什么好说的了
        if (holder instanceof RecyclerViewDatabingAdapter.BindingHolder) {
            ((RecyclerViewDatabingAdapter.BindingHolder) holder).getBinding().setVariable(BR.item, list.get(position));
            ((RecyclerViewDatabingAdapter.BindingHolder) holder).getBinding().executePendingBindings();
            ((RecyclerViewDatabingAdapter.BindingHolder) holder).getBinding().getRoot().setOnClickListener(v -> {
                itemClick(list.get(position));
            });
        } else {
            Log.d(TAG, "getSuspensionTag:" + list.get(position).getSuspensionTag());
            ((RecyclerViewDatabingAdapter.OtherBindingHolder) holder).getBinding().setVariable(BR.listtitle_item, list.get(position));
            ((RecyclerViewDatabingAdapter.OtherBindingHolder) holder).getBinding().executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void loadMore(List<T> storiesBeans) {
        if (this.list == null || this.list.size() <= 0) {
            throw new RuntimeException("当前没有数据，请加载上一页");
        } else {
            int startIndex = this.list.size();
            this.list.addAll(storiesBeans);
            notifyItemInserted(startIndex);
            notifyItemRangeChanged(startIndex, this.list.size() - startIndex);
        }
    }

    public class BindingHolder extends RecyclerView.ViewHolder {

        //这里在holder里面，传入一个binding对象就ok了
        private B binding;

        public BindingHolder(View itemView) {
            super(itemView);
        }

        public B getBinding() {
            return binding;
        }

        public void setBinding(B binding) {
            this.binding = binding;
        }
    }

    public class OtherBindingHolder extends RecyclerView.ViewHolder {

        //这里在holder里面，传入一个binding对象就ok了
        private O binding;

        public OtherBindingHolder(View itemView) {
            super(itemView);
        }

        public O getBinding() {
            return binding;
        }

        public void setBinding(O binding) {
            this.binding = binding;
        }
    }
}
