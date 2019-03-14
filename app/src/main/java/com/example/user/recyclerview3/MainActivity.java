package com.example.user.recyclerview3;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.user.recyclerview3.adapter.HotelAdapter;
import com.example.user.recyclerview3.model.Hotel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements HotelAdapter.IHotelAdapter {
    public static final String HOTEL = "hotel";
    public static final int REQUEST_CODE_ADD = 88;
    public static final int REQUEST_CODE_EDIT = 99;
    ArrayList<Hotel> mList = new
            ArrayList<>();
    HotelAdapter mAdapter;
    int itemPos;
    ArrayList<Hotel> mListAll = new ArrayList<>();
    boolean isFiltered;
    ArrayList<Integer> mListMapFilter = new ArrayList<>();
    String mQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new HotelAdapter(this, mList);
        recyclerView.setAdapter(mAdapter);

        fillData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view)
            {
                goAdd();
            }



        });




    }

    private void fillData() {

        Resources resources = getResources();
        String[] arJudul = resources.getStringArray(R.array.places);
        String[]
                arDeskripsi = resources.getStringArray(R.array.place_desc);
        String[]
                arDetail = resources.getStringArray(R.array.place_details);
        String[]
                arLokasi = resources.getStringArray(R.array.place_locations);
        TypedArray a = resources.obtainTypedArray(R.array.places_picture);
        String[] arFoto = new String[a.length()];
        for (int i = 0; i <
                arFoto.length; i++) {
            int id = a.getResourceId(i, 0);
            arFoto[i] = ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                    + resources.getResourcePackageName(id) + '/'
                    + resources.getResourceTypeName(id) + '/'
                    + resources.getResourceEntryName(id);
        }
        a.recycle();
        for (int i = 0; i < arJudul.length; i++) {
            mList.add(new Hotel(arJudul[i], arDeskripsi[i],
                    arDetail[i], arLokasi[i], arFoto[i]));
        }
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void doClick(int pos) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(HOTEL, mList.get(pos));
        startActivity(intent);
    }

    @Override

    public void doEdit(int pos) {
        itemPos = pos;
        Intent intent = new Intent(this, InputActivity.class);
        intent.putExtra(HOTEL, mList.get(pos));
        startActivityForResult(intent,REQUEST_CODE_EDIT);


    }

    @Override

    public void doDelete(int pos) {
        itemPos = pos;
        final Hotel hotel = mList.get(pos);
        mList.remove(itemPos);
        if (isFiltered)
            mListAll.remove(mListMapFilter.get(itemPos).intValue());
        mAdapter.notifyDataSetChanged();
        Snackbar.make(findViewById(R.id.fab), hotel.judul + " Wah Terhapus",
                Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener()

        {
            @Override

            public void onClick(View v) {
                mList.add(itemPos, hotel);
                if (isFiltered) mListAll.add(mListMapFilter.get(itemPos),
                        hotel);
                mAdapter.notifyDataSetChanged();
            }
        }).show();


    }


        protected void onActivityResult(int requestCode, int resultCode, Intent data)
        {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD && resultCode == RESULT_OK) {
            Hotel hotel = (Hotel) data.getSerializableExtra(HOTEL);
            mList.add(hotel);
            if (isFiltered) mListAll.add(hotel);
            doFilter(mQuery);
            mAdapter.notifyDataSetChanged();}

                else if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
                Hotel Hotel = (Hotel) data.getSerializableExtra(HOTEL);
                mList.remove(itemPos);
                if (isFiltered)
                    mListAll.remove(mListMapFilter.get(itemPos).intValue());
                mList.add(itemPos, Hotel);

                if (isFiltered) mListAll.add(mListMapFilter.get(itemPos),
                        Hotel);
                mAdapter.notifyDataSetChanged();

            }}



    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
//	Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)
                MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText)
            { mQuery = newText.toLowerCase();
            doFilter(mQuery);
                return true;

            }

        });

        return true;

    }





    @Override
    public void doFav(int pos) {

    }

    @Override
    public void doShare(int pos) {

    }

    private void doFilter(String mQuery)
    {
        if (!isFiltered) {
            mListAll.clear();
            mListAll.addAll(mList);
            isFiltered = true;
        }
        mList.clear();
        if (mQuery == null || mQuery.isEmpty()) {
            mList.addAll(mListAll);
            isFiltered = false;
        } else { mListMapFilter.clear();
            for (int i = 0; i < mListAll.size(); i++) { Hotel hotel = mListAll.get(i);
                if (hotel.judul.toLowerCase().contains(mQuery) ||
                        hotel.deskripsi.toLowerCase().contains(mQuery) ||
                        hotel.lokasi.toLowerCase().contains(mQuery)) {
                    mList.add(hotel);
                    mListMapFilter.add(i);
                }
            }}
        mAdapter.notifyDataSetChanged();
    }
    private void goAdd()
    {
        startActivityForResult(new Intent(this, InputActivity.class), REQUEST_CODE_ADD);
    }

}

