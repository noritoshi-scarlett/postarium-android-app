package noritoshi_scarlett.postarium.libraries.forum_tree;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import noritoshi_scarlett.postarium.R;
import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewBinder;

public class ForumTreeBinder extends TreeViewBinder<ForumTreeBinder.ViewHolder> {

    @Override
    public ViewHolder provideViewHolder(View itemView) {
        return new ViewHolder(itemView);
    }

    @Override
    public void bindView(ViewHolder holder, int position, final TreeNode node) {
        Subforum subforumNode = (Subforum) node.getContent();
        if (node.isLeaf()) {
            holder.subforumArrow.setVisibility(View.INVISIBLE);
            holder.subforumGoBtn.setVisibility(View.VISIBLE);
        } else {
            holder.subforumArrow.setVisibility(View.VISIBLE);
        }
        if (((Subforum) node.getContent()).isCat) {
            holder.subforumGoBtn.setVisibility(View.INVISIBLE);
        } else {
            holder.subforumGoBtn.setVisibility(View.VISIBLE);
        }
        holder.subforumName.setText(subforumNode.title);
        holder.subforumGoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO -> listener
            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.item_forum_tree_subforum;
    }

    public class ViewHolder extends TreeViewBinder.ViewHolder {
        public TextView subforumName;
        public ImageView subforumArrow;
        public ImageButton subforumGoBtn;

        public ViewHolder(View rootView) {
            super(rootView);
            this.subforumName = rootView.findViewById(R.id.subforumName);
            this.subforumArrow = rootView.findViewById(R.id.subforumArrow);
            this.subforumGoBtn = rootView.findViewById(R.id.subforumGoBtn);
        }

        public ImageView getSubforumArrow() {
            return subforumArrow;
        }
        public ImageView getSubforumGoBtn() {
            return subforumGoBtn;
        }

        public void setSubforumArrow(ImageView subforumArrow) {
            this.subforumArrow = subforumArrow;
        }
    }
}