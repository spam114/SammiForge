package com.symbol.sammiforge.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.symbol.sammiforge.R;
import com.symbol.sammiforge.databinding.RowRegisterRepairPhotoBinding;
import com.symbol.sammiforge.model.object.EquipmentRepair;
import com.symbol.sammiforge.view.activity.mold.RegRepairPhotoActivity;
import com.symbol.sammiforge.view.activity.mold.RegRepairPhotoDetailActivity;
import com.symbol.sammiforge.viewmodel.CommonViewModel;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.adapter.rxjava2.Result;

public class RegRepairPhotoAdapter extends RecyclerView.Adapter<RegRepairPhotoAdapter.ViewHolder> implements Filterable {

    /*
     LinearLayout layoutTop;
     String contractNo;
     String fromDate;*/
    Context context;
    private ArrayList<EquipmentRepair> items = new ArrayList<>();
    boolean confirmFlag;
    ActivityResultLauncher<Intent> resultLauncher;

    ArrayList<EquipmentRepair> unFilteredlist;//for filter
    ArrayList<EquipmentRepair> filteredList;//for filter
    CommonViewModel commonViewModel;

    public RegRepairPhotoAdapter(ArrayList<EquipmentRepair> items, Context context, ActivityResultLauncher<Intent> resultLauncher, CommonViewModel commonViewModel) {
        this.context = context;
        this.items = items;
        this.resultLauncher = resultLauncher;
        this.unFilteredlist = items;
        this.filteredList = items;
        this.commonViewModel = commonViewModel;
    }


   /* public ProgressFloorReturnViewAdapter(Context context, LinearLayout layoutTop, String contractNo, String fromDate) {
        super();
        this.context = context;
        this.layoutTop = layoutTop;
        this.contractNo = contractNo;
        this.fromDate = fromDate;
    }*/

    public void updateAdapter(ArrayList<EquipmentRepair> newCountries) {
        items.clear();
        items.addAll(newCountries);
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        RowRegisterRepairPhotoBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_register_repair_photo, viewGroup, false);
        return new RegRepairPhotoAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        //EquipmentRepair item = items.get(position);
        EquipmentRepair item = filteredList.get(position);//for filter
        viewHolder.setItem(item, position);
    }

    @Override
    public int getItemCount() {
        //return items.size();
        return filteredList.size();//for filter

    }

    //for filter
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    filteredList = unFilteredlist;
                } else {
                    ArrayList<EquipmentRepair> filteringList = new ArrayList<>();
                    for (EquipmentRepair equipmentRepair : unFilteredlist) {
                        if (equipmentRepair.CustomerName.toLowerCase().contains(charString.toLowerCase())) {
                            filteringList.add(equipmentRepair);
                        }
                    }
                    filteredList = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (ArrayList<EquipmentRepair>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public Filter getFilter2() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    filteredList = unFilteredlist;
                } else {
                    ArrayList<EquipmentRepair> filteringList = new ArrayList<>();
                    for (EquipmentRepair equipmentRepair : unFilteredlist) {
                        if (equipmentRepair.EquipmentNo.toLowerCase().contains(charString.toLowerCase())) {
                            filteringList.add(equipmentRepair);
                        }
                    }
                    filteredList = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (ArrayList<EquipmentRepair>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    //보통은 ViewHolder를 Static 으로 쓴다.
    //범용성을 위해서, 나는 제거함
    class ViewHolder extends RecyclerView.ViewHolder {
        RowRegisterRepairPhotoBinding binding;
        //View row;

        public ViewHolder(RowRegisterRepairPhotoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setItem(EquipmentRepair item, int position) {
            DecimalFormat numFormatter = new DecimalFormat("###,###");
            binding.tvCustomerName.setText(item.CustomerName);
            binding.tvEquipmentName.setText(item.EquipmentName);
            binding.tvEquipmentNo.setText(item.EquipmentNo);
            binding.tvRepairNo.setText(item.RepairNo);
            if(!item.RepairTypeName.equals(""))
                binding.tvRepairTypeName.setText("["+item.RepairTypeName+"]");
            else
                binding.tvRepairTypeName.setText("");
            binding.tvRepairDescription.setText(item.RepairDescription);
            //textViewExYearMonth.setText(((Dong) data.get(position)).ExProgressDate);
            /*binding.tvPartSpec.setText(item.PartSpec);
            binding.tvReceivedQty.setText(numFormatter.format(item.ReceivedQty));*/


            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, RegRepairPhotoDetailActivity.class);
                    intent.putExtra("repairNo", item.RepairNo);
                    intent.putExtra("seqNo", item.SeqNo);
                    resultLauncher.launch(intent);

                    /*if (item.SaleOrderNo.equals("")) return;

                    SearchCondition sc = new SearchCondition();
                    sc.IConvetDivision = 1;
                    sc.Barcode = "S2-"+item.SaleOrderNo;
                    //commonViewModel.cData = sc.Barcode;
                    commonViewModel.Get2("GetNumConvertData", sc);*/
                    /*if(confirmFlag)
                        GoActivity1100(item.WorderLot);*/
                    //Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
   /* private void GoActivity1100(String result){
        Intent intent = new Intent(context, Activity1100.class);
        intent.putExtra("result", result);
        resultLauncher.launch(intent);
    }*/

    public void addItem(EquipmentRepair item) {
        items.add(item);
    }

    public void setItems(ArrayList<EquipmentRepair> items) {
        this.items = items;
    }

    public EquipmentRepair getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, EquipmentRepair item) {
        items.set(position, item);
    }

    public void setConfirmFlag(boolean confirmFlag) {
        this.confirmFlag = confirmFlag;
    }
}
