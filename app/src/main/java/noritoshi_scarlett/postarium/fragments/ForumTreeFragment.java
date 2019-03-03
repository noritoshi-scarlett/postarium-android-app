package noritoshi_scarlett.postarium.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import noritoshi_scarlett.postarium.Postarium;
import noritoshi_scarlett.postarium.R;
import noritoshi_scarlett.postarium.adapters.ForumOnlineAdapter;
import noritoshi_scarlett.postarium.jsonPojo.JsonForumOnline;
import noritoshi_scarlett.postarium.jsonPojo.JsonForumTree;
import noritoshi_scarlett.postarium.libraries.forum_tree.ForumTreeBinder;
import noritoshi_scarlett.postarium.libraries.forum_tree.Subforum;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewAdapter;
import tellh.com.recyclertreeview_lib.TreeViewBinder;

public class ForumTreeFragment extends Fragment {

    private Context context;
    private RecyclerView recyclerView;

    private TreeViewAdapter adapter;
    private ArrayList<JsonForumTree.Subforum> treeList;


    public ForumTreeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.fragment_forum_tree, container, false);

        context = getActivity().getApplicationContext();
        recyclerView = v.findViewById(R.id.treeRecyclerView);

        getTree();

        return v;
    }

    public void getTree() {

        Postarium.WebRequests.get("forum/viewTree/" + Postarium.getPostariData().getPickedForum().getForum_name() + ".html", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jResponse = response.body().string();

                    JsonForumTree treeParser = new JsonForumTree(jResponse);
                    treeList = treeParser.getForumTree().getRoot();

                    if (treeList != null) {
                        final List<TreeNode> nodes = new ArrayList<>();
                        TreeNode<Subforum> cat = null;
                        TreeNode<?> current = null;
                        TreeNode<Subforum> temp = null;
                        int level = 0;

                        for (JsonForumTree.Subforum elem : treeList) {
                            if (elem.isCat()) {
                                cat = new TreeNode<>(new Subforum(elem.getId(), elem.getTitle(), true));
                                nodes.add(cat);
                                current = cat;
                                level = 1;
                            } else {
                                if (current != null) {
                                    temp = new TreeNode<>(new Subforum(elem.getId(), elem.getTitle(), false));
                                    if ((level + 1) > elem.getBranch()) {
                                        for (int i = level; i >= elem.getBranch() ; i--) {
                                            current = current.getParent();
                                        }
                                    }
                                    level = elem.getBranch();
                                    current.addChild(temp);
                                    current = temp;
                                }
                            }
                        }


                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {

                                    recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                    adapter = new TreeViewAdapter(nodes, Arrays.asList(new ForumTreeBinder()));
                                    recyclerView.setAdapter(adapter);

                                    adapter.setOnTreeNodeListener(new TreeViewAdapter.OnTreeNodeListener() {
                                        @Override
                                        public boolean onClick(TreeNode node, RecyclerView.ViewHolder holder) {
                                            if (!node.isLeaf()) {
                                                //Update and toggle the node.
                                                onToggle(!node.isExpand(), holder);
                                            }
                                            return false;
                                        }

                                        @Override
                                        public void onToggle(boolean isExpand, RecyclerView.ViewHolder holder) {
                                            ForumTreeBinder.ViewHolder SubforumViewHolder = (ForumTreeBinder.ViewHolder) holder;
                                            final ImageView ivArrow = SubforumViewHolder.getSubforumArrow();
                                            int rotateDegree = isExpand ? 90 : 0;
                                            ivArrow.animate().rotation(rotateDegree).start();
                                        }
                                    });


                                }
                            });
                        }
                    }
                }
            }
        });

    }

}
