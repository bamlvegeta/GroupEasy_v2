package example.com.groupeasy.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import example.com.groupeasy.R;
import example.com.groupeasy.adapters.UsersSelectAdapter;
import example.com.groupeasy.pojo.new_groups;
import example.com.groupeasy.pojo.users_list;

public class chooseUserActivity extends AppCompatActivity {

    RecyclerView mUserRecyclerView;
    Toolbar mToolbar;
    //adapter
    //list
    private UsersSelectAdapter mUserAdapter;
    private List<users_list> mLstGroups;

    private Context context;
    //firebase

    private TextView emptyView;

    /** Firebase db init*/
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = database.getReference();

    final DatabaseReference userRef = myRef.child("members");
    final DatabaseReference groupRef = myRef.child("groups").child("");

    final String group_id = groupRef.push().getKey();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);

        mToolbar = (Toolbar) findViewById(R.id.choose_users_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add members");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initElementsWithIds();
        createListView();
        initElementsWithListeners();

        mLstGroups = new ArrayList<>();
        // initialize adapter to our List of <group>
        mUserAdapter = new UsersSelectAdapter(mLstGroups);

        mUserRecyclerView = (RecyclerView) findViewById(R.id.choose_users_recyclerview);
        mUserRecyclerView.setHasFixedSize(true);

        mUserRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //a RecyclerView needs an adapter to access its data
        mUserRecyclerView.setAdapter(mUserAdapter);
        mUserRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }



    private void initElementsWithListeners()
    {


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_users_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final String groupName = getIntent().getStringExtra("groupName");

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_choose_users_done) {


            String data = "";
            List<users_list> uList = mUserAdapter
                    .getUserId();

            final HashMap <String, Boolean> myMap = new HashMap<>();


            for (int i = 0; i < uList.size(); i++) {
                users_list singleUser = uList.get(i);
                if (singleUser.isSelected()) {

//                    data = data + "\n" + singleUser.getId();
                    myMap.put(singleUser.getId(),true);

                    userRef.child(singleUser.getId()).child("groupsIn").child(groupName).setValue(true);

      /*
       * Toast.makeText( CardViewActivity.this, " " +
       * singleStudent.getName() + " " +
       * singleStudent.getEmailId() + " " +
       * singleStudent.isSelected(),
       * Toast.LENGTH_SHORT).show();
       */
                }

            }

            Toast.makeText(chooseUserActivity.this,
                    "Selected Students: \n" + data, Toast.LENGTH_LONG)
                    .show();

            //code to push data to firebase



            final DatabaseReference msgRef = myRef.child("messages").child("");

//            groupName = getIntent().getStringExtra("groupName");
            final String imagePic = getIntent().getStringExtra("imagePic");

//            Toast.makeText(this, groupName,Toast.LENGTH_SHORT).show();

            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
            final String uid = current_user.getUid();

            myRef.child("members").child(uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final String admin = dataSnapshot.getValue().toString();
                    String members = "";
                    final String icon = "";
                    final String last_msg = "You have no messages in the group";

                    new_groups newGroups = new new_groups(admin,imagePic,last_msg,groupName,group_id);

                    groupRef.child(group_id).setValue(newGroups);

                    msgRef.child(group_id).setValue(true);

                    groupRef.child(group_id).child("members").child("").setValue(myMap);


                    Toast.makeText(chooseUserActivity.this, "Group has been created ",Toast.LENGTH_LONG).show();

                    Intent i = new Intent(getApplicationContext(),DashboardActivity.class);
                    startActivity(i);
//                    finish();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



            //end code push



        }

        return super.onOptionsItemSelected(item);
    }

    private void initElementsWithIds() {

    emptyView = (TextView) findViewById(R.id.empty_view_users);

    }

    private void createListView() {

//        userRef.keepSynced(true);

        userRef.child("").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mLstGroups.removeAll(mLstGroups);

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    users_list usersList = snapshot.getValue(users_list.class);
                    mLstGroups.add(usersList);
                }

                mUserAdapter.notifyDataSetChanged();

//                if(mLstGroups.isEmpty()){
//                    emptyView.setVisibility(View.VISIBLE);
//                    mUserRecyclerView.setVisibility(View.GONE);
//                }
//                else{
//
//                    emptyView.setVisibility(View.GONE);
//                    mUserRecyclerView.setVisibility(View.VISIBLE);
//
//                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();
    }
//
//    public void RecyclerListViewClicked(View v, int position){
//
//
//
//
//    }

}