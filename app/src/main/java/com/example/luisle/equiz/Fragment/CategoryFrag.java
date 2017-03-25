package com.example.luisle.equiz.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.luisle.equiz.R;

public class CategoryFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String PUT_EXTRA_VALUE_GRAMMAR = "Grammar";
    private String PUT_EXTRA_VALUE_VOLCABUARY = "Volcabuary";
    private String PUT_EXTRA_VALUE_COMMUNICATION = "Communication";
    private String PUT_EXTRA_VALUE_READUNDERSTAND = "ReadUnderstand";
    private String PUT_EXTRA_VALUE_SYNTHETICEXAM = "SyntheticExam";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Fragment Layout
    CardView cardVGrammar, cardVVolcabuary, cardVCommunication, cardVReadUnderstand, cardSyntheticExam;

    private OnDataPass dataPass;

    public CategoryFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryFrag newInstance(String param1, String param2) {
        CategoryFrag fragment = new CategoryFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPass = (OnDataPass) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frag_category, container, false);
        mappingLayout(view);
        onSendCategoryCode();
        return view;
    }

    private void mappingLayout(View view) {
        cardVGrammar = (CardView) view.findViewById(R.id.cardVCategory_Grammar);
        cardVVolcabuary = (CardView) view.findViewById(R.id.cardVCategory_Volcabuary);
        cardVCommunication = (CardView) view.findViewById(R.id.cardVCategory_Communication);
        cardVReadUnderstand = (CardView) view.findViewById(R.id.cardVCategory_ReadUnderstand);
        cardSyntheticExam = (CardView) view.findViewById(R.id.cardVCategory_SyntheticExam);
    }

    private void onSendCategoryCode() {
        cardVGrammar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataPass.onDataPass(PUT_EXTRA_VALUE_GRAMMAR);
            }
        });
        cardVVolcabuary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataPass.onDataPass(PUT_EXTRA_VALUE_VOLCABUARY);
            }
        });
        cardVCommunication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataPass.onDataPass(PUT_EXTRA_VALUE_COMMUNICATION);
            }
        });
        cardVReadUnderstand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataPass.onDataPass(PUT_EXTRA_VALUE_READUNDERSTAND);
            }
        });
        cardSyntheticExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataPass.onDataPass(PUT_EXTRA_VALUE_SYNTHETICEXAM);
            }
        });
    }

    public interface OnDataPass {
        public void onDataPass(String catgegory);
    }
}
