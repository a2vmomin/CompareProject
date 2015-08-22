package com.example.alif.compareproject2;

import android.content.Context;

import android.graphics.Bitmap;


import android.graphics.drawable.Drawable;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.view.ActionMode.Callback;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;


import java.util.Collections;
import java.util.List;

/**
 * Created by Alif on 15-Jul-15.
 */
public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.Holder> {

    private RecyclerView recyclerView;
    private Context context;
    private ActionMode mActionMode;
    private Bitmap bmp;
    private static boolean selected;
    List<Information> data = Collections.emptyList();
    public ResultsAdapter(Context context, List<Information> data, RecyclerView recyclerView)
    {
        this.context = context;
        this.data = data;
        this.recyclerView = recyclerView;
    }
    OnlineChecking onlineChecking;
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        onlineChecking = new OnlineChecking(context);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_results_row, parent, false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }



    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Information current = data.get(position);
        holder.resultText1.setText(current.resultText1);
        holder.resultText2.setText(current.resultText2);
        holder.winnerResult.setText("(" + String.valueOf(current.winnerTotal) + ")");
        holder.loserResult.setText("(" + String.valueOf(current.loserTotal) + ")");
        if(onlineChecking.isOnline())
        {
            Picasso.with(context).load(current.winnerImage).into(holder.winnerImageView);
            Picasso.with(context).load(current.loserImage).into(holder.loserImageView);
        }
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView resultText1, resultText2, winnerResult, loserResult;
        ImageView winnerImageView, loserImageView;

        public Holder(View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(mActionMode != null)
                    {
                        return false;
                    }
                    mActionMode = ((Results) context).startSupportActionMode(mActionModeCallback);
                    v.setSelected(true);
                    return true;
                }
            });

            resultText1 = (TextView) itemView.findViewById(R.id.resultText1);
            resultText2 = (TextView) itemView.findViewById(R.id.resultText2);
            winnerResult = (TextView) itemView.findViewById(R.id.winnerTotal);
            loserResult = (TextView) itemView.findViewById(R.id.loserTotal);
            winnerImageView = (ImageView) itemView.findViewById(R.id.winnerImage);
            loserImageView = (ImageView) itemView.findViewById(R.id.loserImage);
        }

        private Callback mActionModeCallback = new Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.long_click_app_bar, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                /*itemView.findViewById(R.id.linearLayoutInCardView).setBackgroundColor(context.getResources().getColor(R.color.primaryDarkColor));
                selected = true;*/
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_delete:
                        Information current = data.get(getPosition());
                        DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
                        databaseAdapter.deleteData(current.id);
                        data.remove(getPosition());
                        notifyItemRemoved(getPosition());
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                if(mActionMode != null) {
                    /*itemView.findViewById(R.id.linearLayoutInCardView).setBackgroundColor(context.getResources().getColor(R.color.whiteColor));
                    selected = false;*/
                    mActionMode = null;
                }
            }
        };
    }

    
}