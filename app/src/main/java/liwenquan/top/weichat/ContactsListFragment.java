package liwenquan.top.weichat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactsListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsListFragment extends android.support.v4.app.ListFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    int[] image = {
            R.drawable.people,
            R.drawable.people
    };
    String[] name = {"1@qq.com", "2@qq.com"};
    // TODO: Rename and change types of parameters
    private SimpleAdapter adapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView mlist;

    public ContactsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactsListFragment newInstance(String param1, String param2) {
        ContactsListFragment fragment = new ContactsListFragment();
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
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < name.length; i++) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("image", image[i]);
            listItem.put("name", name[i]);
            listItems.add(listItem);
        }
        adapter = new SimpleAdapter(getActivity(), listItems, R.layout.contact_item, new String[]{"image", "name",}, new int[]{R.id.contact_image, R.id.contact_name});
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts_list, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        System.out.println(l.getChildAt(position));
        HashMap<String, Object> view = (HashMap<String, Object>) l.getItemAtPosition(position);
        //Toast.makeText(getActivity(), "" + l.getItemIdAtPosition(position), Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getActivity(), MainActivity.class);

        i.putExtra("people",id);
        startActivityForResult(i, 0);
    }


}
