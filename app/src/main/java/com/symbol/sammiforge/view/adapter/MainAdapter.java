package com.symbol.sammiforge.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.symbol.sammiforge.R;
import com.symbol.sammiforge.databinding.RowMenuBinding;
import com.symbol.sammiforge.model.object.MainMenuItem;
import com.symbol.sammiforge.view.activity.mold.RegRepairPhotoActivity;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    /*
     LinearLayout layoutTop;
     String contractNo;
     String fromDate;*/
    Context context;
    private ArrayList<MainMenuItem> items = new ArrayList<>();
    boolean confirmFlag;

    public MainAdapter(ArrayList<MainMenuItem> items, Context context) {
        this.context = context;
        this.items = items;
    }


   /* public ProgressFloorReturnViewAdapter(Context context, LinearLayout layoutTop, String contractNo, String fromDate) {
        super();
        this.context = context;
        this.layoutTop = layoutTop;
        this.contractNo = contractNo;
        this.fromDate = fromDate;
    }*/

    public void updateAdapter(ArrayList<MainMenuItem> newCountries) {
        items.clear();
        items.addAll(newCountries);
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        RowMenuBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_menu, viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        MainMenuItem item = items.get(position);
        viewHolder.setItem(item, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //보통은 ViewHolder를 Static 으로 쓴다.
    //범용성을 위해서, 나는 제거함
    class ViewHolder extends RecyclerView.ViewHolder {
        RowMenuBinding binding;
        //View row;

        public ViewHolder(RowMenuBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setItem(MainMenuItem item, int position) {

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            if (item.menuType == 1) {//각 그룹의 첫항목 이미지 없애기, 글자 크게
                binding.imvIcon.setVisibility(View.GONE);
                binding.txtMenuName.setTextSize(19);
                binding.txtMenuName.setTypeface(null, Typeface.BOLD); //only text style(only bold)



                DisplayMetrics dm = context.getResources().getDisplayMetrics();

                param.leftMargin = (int) (15 * dm.density);
                binding.txtMenuName.setLayoutParams(param);
                binding.getRoot().setEnabled(false);
            } else {
                binding.imvIcon.setVisibility(View.VISIBLE);
                //binding.imvIcon.setImageDrawable(context.getResources().getDrawable(item.imageID));
                binding.imvIcon.setImageDrawable(context.getDrawable(item.imageID));
                binding.txtMenuName.setTextSize(17);
                binding.txtMenuName.setTypeface(null, Typeface.NORMAL); //only text style(only bold)

                param.leftMargin = 0;
                binding.txtMenuName.setLayoutParams(param);
                binding.getRoot().setEnabled(true);
            }
            FrameLayout.LayoutParams param2 = new FrameLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            if (item.lastItem) {//그릅의 마지막 항목 줄 띄우기

                param2.bottomMargin = 40;
            }
            else{
                param2.bottomMargin = 0;
            }
            binding.cardView.setLayoutParams(param);
           /* if(item.menuType==1){//선택불가
                binding.getRoot().setEnabled(false);
            }
            else{
                binding.getRoot().setEnabled(true);
            }*/
            binding.txtMenuName.setText(item.menuName);
            //binding.getRoot().setOn

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.menuName.equals(context.getString(R.string.menu2))||
                            item.menuName.equals(context.getString(R.string.menu2_eng))) {
                        Intent intent = new Intent(context, RegRepairPhotoActivity.class);
                        context.startActivity(intent);
                    }/*else if (item.menuName.equals(context.getString(R.string.menu3))||
                            item.menuName.equals(context.getString(R.string.menu3_eng))) {
                        Intent intent = new Intent(context, Activity1000.class);
                        context.startActivity(intent);
                    } else if (item.menuName.equals(context.getString(R.string.menu4))||
                            item.menuName.equals(context.getString(R.string.menu4_eng))) {

                    }*/
                }
            });
        }
    }

    public void removeItem(int position){
        items.remove(position);
    }


    public void addItem(MainMenuItem item) {
        items.add(item);
    }

    public void setItems(ArrayList<MainMenuItem> items) {
        this.items = items;
    }

    public MainMenuItem getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, MainMenuItem item) {
        items.set(position, item);
    }

    public void setConfirmFlag(boolean confirmFlag) {
        this.confirmFlag = confirmFlag;
    }
}


