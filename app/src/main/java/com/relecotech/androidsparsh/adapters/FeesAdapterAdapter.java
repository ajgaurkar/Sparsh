package com.relecotech.androidsparsh.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.controllers.FeesListdata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by amey on 10/16/2015.
 */
public class FeesAdapterAdapter extends BaseAdapter {

    protected List<FeesListdata> feesList;
    Context context;
    LayoutInflater inflater;
    private String check_fees_dueDate;
    private Date fees_dueDate;
    private boolean getDueDateResult;
    private String check_fees_status;

    public FeesAdapterAdapter(Context context, List<FeesListdata> feesList) {
        this.feesList = feesList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return feesList.size();
    }

    public FeesListdata getItem(int position) {
        return feesList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.fees_list_item, parent, false);


            holder.feesListItemLayout = (RelativeLayout) convertView.findViewById(R.id.fees_list_item_layout);
            holder.txtPeriod = (TextView) convertView.findViewById(R.id.period);
            holder.txtInstallment = (TextView) convertView.findViewById(R.id.sr_installment);
            holder.txtPaid_unPaid_Date = (TextView) convertView.findViewById(R.id.paid_unpaid_date);
            holder.txtAmounts = (TextView) convertView.findViewById(R.id.fees_amount);
            holder.txtFee_Stauts = (TextView) convertView.findViewById(R.id.fee_status);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // AlertListData alertListData = alertList.get(position);
        FeesListdata fees_Acadmics_Listdata = feesList.get(position);

        if (fees_Acadmics_Listdata.getFee_Type().equals("Extracurricular")) {
            holder.txtPeriod.setText(fees_Acadmics_Listdata.getFee_Comment());
        } else if (fees_Acadmics_Listdata.getFee_Type().equals("Academic")) {
            holder.txtPeriod.setText(fees_Acadmics_Listdata.getMonths());
        }

        holder.txtInstallment.setText("" + fees_Acadmics_Listdata.getInstallment());
        holder.txtPaid_unPaid_Date.setText(fees_Acadmics_Listdata.getDuedate());
        holder.txtAmounts.setText("Rs." + fees_Acadmics_Listdata.getFeesamount());
        String checkstauts = fees_Acadmics_Listdata.getStauts();
        check_fees_dueDate = fees_Acadmics_Listdata.getDuedate();
        check_fees_status = fees_Acadmics_Listdata.getStauts();

        System.out.println(" check_fees_status check_fees_status check_fees_status " + check_fees_status);


        if (CheckDueDate(check_fees_dueDate)&&checkstauts.equals("Unpaid")) {
            holder.feesListItemLayout.setBackgroundColor(Color.parseColor("#FFF4C7C3"));
        }
        else {
            holder.feesListItemLayout.setBackgroundColor(Color.parseColor("#00030303"));
        }

        /*
                below code used to checked fees status  .
               if its ' Unpaid ' so need to changed fees ststus text color RED .

         */
        String unpaid = "Unpaid";
        if (checkstauts.equals(unpaid)) {
            holder.txtFee_Stauts.setTextColor(Color.parseColor("#ff0000"));
            holder.txtFee_Stauts.setText(fees_Acadmics_Listdata.getStauts());
        } else {
            holder.txtFee_Stauts.setTextColor(Color.parseColor("#00c200"));
            holder.txtFee_Stauts.setText(fees_Acadmics_Listdata.getStauts());
        }


        return convertView;
    }

    /*
                below method used to  checked due date above todays date .
                if due date is above todays date so we need change color of fees item layout.
         */
    public boolean CheckDueDate(String check_fees_dueDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy", Locale.getDefault());

        try {
            fees_dueDate = dateFormat.parse(this.check_fees_dueDate);
            int get_due_Date_Compare_Result = fees_dueDate.compareTo(new Date());
            switch (get_due_Date_Compare_Result) {
                case -1:
                    return true;
                case 1:
                    return false;
                case 0:
                    return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;

    }

    private class ViewHolder {
        TextView txtPeriod;
        TextView txtPaid_unPaid_Date;
        TextView txtAmounts;
        TextView txtFee_Stauts;
        TextView txtInstallment;
        RelativeLayout feesListItemLayout;

    }

}
