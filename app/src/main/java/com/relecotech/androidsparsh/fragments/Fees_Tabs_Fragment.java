package com.relecotech.androidsparsh.fragments;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.adapters.FeesAdapterAdapter;
import com.relecotech.androidsparsh.adapters.FeesListDialogAdapterAdapter;
import com.relecotech.androidsparsh.controllers.FeesDialogListData;
import com.relecotech.androidsparsh.controllers.FeesListdata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amey on 10/19/2016.
 */
public class Fees_Tabs_Fragment extends Fragment {
    private ListView feesListView;
    private List<FeesListdata> getFeesList;
    private FeesAdapterAdapter feesAdapterAdapter;
    private ListView fee_dialogListView;
    private TextView fee_dialogTextView1;
    private TextView fee_dialogTextView2;
    private TextView fee_dialogTextView3;
    private TextView fee_dialogTextView4;
    private ArrayList<FeesDialogListData> fees_Dialog_List;
    private FeesDialogListData feesDialogListData;
    private FeesListDialogAdapterAdapter feesListDialogAdapterAdapter;
    private TextView feeNodataTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fees_tabs_fragment, container, false);
        feesListView = (ListView) rootView.findViewById(R.id.fees_listView);
        feeNodataTextView = (TextView) rootView.findViewById(R.id.feeNoDataTv);
        Bundle bundle = this.getArguments();
        getFeesList = (List<FeesListdata>) bundle.getSerializable("feesList");

        System.out.println(" getFeesList getFeesList " + getFeesList);
        //ystem.out.println(" getFeesList getFeesList size " + getFeesList.size());
        fees_Dialog_List = new ArrayList<>();

        //previous logic...had some issues
//        if (getFeesList != null || !getFeesList.isEmpty()) {
//            feesAdapterAdapter = new FeesAdapterAdapter(getActivity(), getFeesList);
//            feesListView.setAdapter(feesAdapterAdapter);
//
//        } else {
//            System.out.println("Fees List  list is NULLL");
//
//
//        }

        if (getFeesList == null) {

            System.out.println("Fees List  list is NULLL");
            feeNodataTextView.setVisibility(View.VISIBLE);
            feesListView.setVisibility(View.INVISIBLE);
        } else {

            feesAdapterAdapter = new FeesAdapterAdapter(getActivity(), getFeesList);
            feesListView.setAdapter(feesAdapterAdapter);
            feeNodataTextView.setVisibility(View.INVISIBLE);
            feesListView.setVisibility(View.VISIBLE);

        }


        feesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d("list clicked", "academic clicked");
                FeesListdata selectedfeesListdata = getFeesList.get(position);
                Fees_List_Dialog(position, selectedfeesListdata);
            }
        });


        return rootView;

    }


    public void Fees_List_Dialog(final int itemPosition, FeesListdata selectedfeesListdata) {

        LayoutInflater flater = getActivity().getLayoutInflater();

        View view = flater.inflate(R.layout.feescustom_dialoge, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle("Fees Details");
        fee_dialogListView = (ListView) view.findViewById(R.id.dialog_listView);
        fee_dialogTextView1 = (TextView) view.findViewById(R.id.ttlFeeVal);
        fee_dialogTextView2 = (TextView) view.findViewById(R.id.textView39);
        fee_dialogTextView3 = (TextView) view.findViewById(R.id.payment_status_textView);
        fee_dialogTextView4 = (TextView) view.findViewById(R.id.textView6);

        fee_dialogTextView1.setText(selectedfeesListdata.getDuedate());
        fee_dialogTextView2.setText("Rs." + selectedfeesListdata.getFeesamount());

        if (selectedfeesListdata.getFee_Type().equals("Academic")) {
            fee_dialogTextView4.setVisibility(View.GONE);
            fee_dialogTextView1.setText("Total Fees ");

        } else if (selectedfeesListdata.getFee_Type().equals("Extracurricular")) {

            fee_dialogTextView4.setVisibility(View.GONE);
            fee_dialogTextView1.setText(selectedfeesListdata.getFee_Comment());
            fee_dialogListView.setVisibility(View.GONE);
        }
        if (selectedfeesListdata.getStauts().equals("Unpaid")) {
            fee_dialogTextView3.setText(selectedfeesListdata.getStauts());
            fee_dialogTextView3.setBackgroundColor(Color.parseColor("#ff4d4d"));

        } else if (selectedfeesListdata.getStauts().equals("Paid")) {

            System.out.println("SelectedfeesListdata.getPaid_Date()   " + selectedfeesListdata.getPaid_Date());
            if (!selectedfeesListdata.getPaid_Date().equals("null")) {

                fee_dialogTextView3.setText(selectedfeesListdata.getStauts() + " " + "on " + selectedfeesListdata.getPaid_Date());
            }
            fee_dialogTextView3.setBackgroundColor(Color.parseColor("#00c200"));
        }
        fees_Dialog_List.clear();

        if (selectedfeesListdata.getAmount_Split1().equals("null") && selectedfeesListdata.getAmount_split_comment_1().equals("null")) {

            System.out.println("1 .when data is not present");

        } else {
            feesDialogListData = new FeesDialogListData(selectedfeesListdata.getAmount_split_comment_1(), selectedfeesListdata.getAmount_Split1());
            fees_Dialog_List.add(feesDialogListData);

            System.out.println("1 .else when data is not preesent");
        }
        if (selectedfeesListdata.getAmount_Split2().equals("null") && selectedfeesListdata.getAmount_split_comment_2().equals("null")) {

            System.out.println("2 .when data is not present");

        } else {

            feesDialogListData = new FeesDialogListData(selectedfeesListdata.getAmount_split_comment_2(), selectedfeesListdata.getAmount_Split2());
            fees_Dialog_List.add(feesDialogListData);
            System.out.println("1 .else when data is not present");
        }
        if (selectedfeesListdata.getAmount_Split3().equals("null") && selectedfeesListdata.getAmount_split_comment_3().equals("null")) {

            System.out.println("3 .when data is not present");

        } else {

            feesDialogListData = new FeesDialogListData(selectedfeesListdata.getAmount_split_comment_3(), selectedfeesListdata.getAmount_Split3());
            fees_Dialog_List.add(feesDialogListData);
            System.out.println("1 .else when data is not present");
        }
        if (selectedfeesListdata.getAmount_Split4().equals("null") && selectedfeesListdata.getAmount_split_comment_4().equals("null")) {

            System.out.println("4 . when data is not present");

        } else {

            feesDialogListData = new FeesDialogListData(selectedfeesListdata.getAmount_split_comment_4(), selectedfeesListdata.getAmount_Split4());
            fees_Dialog_List.add(feesDialogListData);
            System.out.println("1 .else when data is not present");
        }

        if (selectedfeesListdata.getAmount_Split5().equals("null") && selectedfeesListdata.getAmount_split_comment_5().equals("null")) {

            System.out.println("5 .when data is not present");

        } else {

            feesDialogListData = new FeesDialogListData(selectedfeesListdata.getAmount_split_comment_5(), selectedfeesListdata.getAmount_Split5());
            fees_Dialog_List.add(feesDialogListData);
            System.out.println("1 .else when data is not present");

        }
        if (selectedfeesListdata.getAmount_Split6().equals("null") && selectedfeesListdata.getAmount_split_comment_6().equals("null")) {

            System.out.println("5 .when data is not present");

        } else {

            feesDialogListData = new FeesDialogListData(selectedfeesListdata.getAmount_split_comment_6(), selectedfeesListdata.getAmount_Split6());
            fees_Dialog_List.add(feesDialogListData);
            System.out.println("1 .else when data is not present");

        }


        feesListDialogAdapterAdapter = new FeesListDialogAdapterAdapter(getActivity(), fees_Dialog_List);
        fee_dialogListView.setAdapter(feesListDialogAdapterAdapter);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
