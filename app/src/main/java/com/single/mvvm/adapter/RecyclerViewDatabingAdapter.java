package com.single.mvvm.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.single.mvvm.BR;

import java.util.List;

/**
 * Created by xiangcheng on 18/2/2.
 */

public abstract class RecyclerViewDatabingAdapter<T, B extends ViewDataBinding> extends RecyclerView.Adapter<RecyclerViewDatabingAdapter.BindingHolder> {

    private List<T> list;
    private B dataBing;

    public RecyclerViewDatabingAdapter(List<T> list) {
        this.list = list;
    }

    @Override
    public RecyclerViewDatabingAdapter.BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //同样会根据布局生成一个相应的binding
        dataBing = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), getItemLayout(), parent, false);
        //这里getRoot会返回布局的根view
        BindingHolder holder = new BindingHolder(dataBing.getRoot());
        holder.setBinding(dataBing);
        return holder;
    }

    protected abstract int getItemLayout();

    @Override
    public void onBindViewHolder(RecyclerViewDatabingAdapter.BindingHolder holder, int position) {
        //在绑定数据的时候是固定写法，就没什么好说的了
        holder.getBinding().setVariable(BR.item, list.get(position));
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return list.size();
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
}
