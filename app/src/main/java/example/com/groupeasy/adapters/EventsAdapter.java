package example.com.groupeasy.adapters;

import android.content.Context;
import android.graphics.Color;
import android.renderscript.Sampler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.security.Key;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import example.com.groupeasy.R;
import example.com.groupeasy.pojo.Participants;
import example.com.groupeasy.pojo.list_details;
import example.com.groupeasy.pojo.list_primary;

/**
 * Created by Harsh on 11-09-2017.
 */

public class EventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static List<list_primary> mListl;
//    static List<Participants> mList2;
    Context mContext;
//    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    final DatabaseReference groupRef = database.getReference().child("Events").child("lists");

    private static final int VIEW_TYPE_EMPTY_LIST_PLACEHOLDER = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;

   public EventsAdapter(){

   }

    public EventsAdapter(List<list_primary> mLstGroups)
    {
        this.mListl = mLstGroups;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_view, parent, false);

        switch(viewType) {
            case VIEW_TYPE_EMPTY_LIST_PLACEHOLDER:
                Toast.makeText(mContext, "Empty",Toast.LENGTH_LONG).show();
                break;
            case VIEW_TYPE_OBJECT_VIEW:

                return new EventViewHolder(rootView);
        }
        return new EventViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        final EventViewHolder viewHolder = (EventViewHolder) holder;

        viewHolder.eventName.setText(mListl.get(position).getName());
        viewHolder.admin.setText(mListl.get(position).getAdmin());

        Map<String,String> myMap = new HashMap<>();
//        myMap.put(mListl.get(position).getParticipants().getName(), mListl.get(position).getParticipants().getValue());

//        String test = mListl.get(position).getParticipants().getParticipants();


//       for (Map.Entry<String,String> entry: myMap.entrySet()){
//
//            Log.w(entry.getKey(),"keyis");
//            Log.w(entry.getValue(),"valueis");
//
//        }


        //if no location has been provided dont show
    try{
        if((mListl.get(position).getLocation())!= null) {
            viewHolder.locationText.setVisibility(View.VISIBLE);
            viewHolder.locationText.setText(mListl.get(position).getLocation());
            viewHolder.locationImage.setVisibility(View.VISIBLE);
               }

            }
    catch (NullPointerException e){
        e.printStackTrace();
        }

        viewHolder.eventID.setText(mListl.get(position).getId());

        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("Events").child("lists");
        final String key = eventRef.getKey();


        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String key1 = ds.getKey();

//                    viewHolder.eventName.setText(key1);

//                    list_primary list = ds.getValue(list_primary.class);
//                    list.setMessageid(ds.getKey());

//                        Log.d("This_key",list.toString());

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        String uid = eventRef.getKey();
        Log.i("uid", uid);

        //what does this do?
//        eventRef.addListenerForSingleValueEvent(eventListener);

        viewHolder.userImageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String image = dataSnapshot.child("image").getValue().toString();
                Picasso.with(mContext)
                        .load(image)
                        .placeholder(R.drawable.ic_default_groups)
                        .resize(50,50)
                        .into(((EventsAdapter.EventViewHolder) holder).userImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//        if(!location.isEmpty()){
//        viewHolder.locationText.setVisibility(View.VISIBLE);
//        viewHolder.locationText.setText(mList2.get(position).getLocation());
//            viewHolder.locationImage.setVisibility(View.VISIBLE);
//        }

        //try to get participants
        eventRef.child("-Kx8REumOYknDMHjHEsq").child("-Kx8T3J3ddUyfOivXc-x").child("participants").child("").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String test = dataSnapshot.getValue().toString();
                                    Log.w(test,"sfsfasd");


//                for(DataSnapshot snapshot : dataSnapshot.getValue()){
//
//                    Participants participants = snapshot.getValue(Participants.class);
//
//                    Log.w(participants.toString(),"Participantz");
//                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    @Override
    public int getItemCount() {
        return mListl.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView eventName;
        private TextView eventID;
        private TextView admin;
        private TextView addMe;
        private TextView locationText;
        private ImageView locationImage;
        private ImageView userImage;
        private RecyclerView recyclerView;


        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = current_user.getUid();
        DatabaseReference userImageRef = FirebaseDatabase.getInstance().getReference().child("members").child(uid);
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child("Events").child("lists");


        public EventViewHolder(View itemView) {
            super(itemView);

            eventName = (TextView) itemView.findViewById(R.id.event_name);
            admin = (TextView) itemView.findViewById(R.id.author_of_event);
            locationText = (TextView) itemView.findViewById(R.id.location_text);
            locationImage = (ImageView) itemView.findViewById(R.id.location_image);
            userImage = (ImageView) itemView.findViewById(R.id.user_dp);
            addMe = (TextView) itemView.findViewById(R.id.add_Me);
            eventID = (TextView) itemView.findViewById(R.id.event_key);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recycler_view);

            eventName.setOnClickListener(this);
            admin.setOnClickListener(this);
            addMe.setOnClickListener(this);

        }

        @Override
        public void onClick(final View v) {

            if (v.getId() == eventName.getId() ) {
//            clickListener.onItemClick(getAdapterPosition(),v);
                Toast.makeText(v.getContext(), "Something to do with name", Toast.LENGTH_SHORT).show();
            }
            else if (v.getId() == admin.getId()) {
                Toast.makeText(v.getContext(), "Admin", Toast.LENGTH_SHORT).show();
//            clickListener.onItemClick(getAdapterPosition(),v);
            }
            else if (v.getId() == addMe.getId()){

                final String key = eventRef.getKey();
                Log.w("type_this",key);

                String test = eventRef.child(key).getKey();
                Log.w("type_this_test",key);

                final DatabaseReference groupRef = eventRef.child("").child(key);

                userImageRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String uName = dataSnapshot.child("name").getValue().toString();
                        addMe.setText(uName);

//                        locationText.setText(mListl.get(position).getLocation());
                        int position = 0;

                        String event_id = eventID.getText().toString();

                        eventRef.child(event_id).child("members").child(uid).setValue(true);

                        Snackbar snackbar = Snackbar
                                .make(v, "You have been added to the event!", Snackbar.LENGTH_LONG)
                                .setAction("- Remove me", new View.OnClickListener() {
                                    @Override
                                    public void onClick(final View view) {

                                        Snackbar snackbar1 = Snackbar.make(v, "You have been removed from the event!", Snackbar.LENGTH_SHORT);
                                        View snackbarView = snackbar1.getView();
                                        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                                        textView.setTextColor(Color.YELLOW);
                                        textView.setTextSize(14);
                                        snackbar1.show();
                                    }
                                });

                        snackbar.setActionTextColor(Color.WHITE);
                        TextView snackbarActionTextView = (TextView) snackbar.getView().findViewById( android.support.design.R.id.snackbar_action );
                        snackbarActionTextView.setTextSize(10);

                        View snackbarView = snackbar.getView();
                        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                        textView.setTextSize(14);

                        snackbar.show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mListl.isEmpty()) {
            return VIEW_TYPE_EMPTY_LIST_PLACEHOLDER;
        } else {
            return VIEW_TYPE_OBJECT_VIEW;
        }
    }
}
