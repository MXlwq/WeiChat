package liwenquan.top.weichat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.text.TextDirectionHeuristicCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AboutMyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AboutMyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutMyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView mEmail;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public AboutMyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AboutMyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutMyFragment newInstance(String param1, String param2) {
        AboutMyFragment fragment = new AboutMyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View root=inflater.inflate(R.layout.fragment_about_my, container, false);
        mEmail= (TextView) root.findViewById(R.id.userEmail);
        String Email=getActivity().getIntent().getStringExtra("UserName");
        mEmail.setText(Email);
        return root;
    }

}
