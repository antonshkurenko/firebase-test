package io.github.tonyshkurenko.firebasetest.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import io.github.tonyshkurenko.firebasetest.R;
import io.github.tonyshkurenko.firebasetest.pojos.Item;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

  @BindView(R.id.listView) ListView mListView;
  @BindView(R.id.todoText) EditText mTodoText;

  private final ChildEventListener mChildEventListener = new ChildEventListener() {
    @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {
      Timber.d("%s, String s = %s", dataSnapshot.toString(), s);
      mAdapter.add((String) dataSnapshot.child("title").getValue());
    }

    @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override public void onChildRemoved(DataSnapshot dataSnapshot) {
      mAdapter.remove((String) dataSnapshot.child("title").getValue());
    }

    @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override public void onCancelled(DatabaseError databaseError) {

    }
  };

  private FirebaseAuth mFirebaseAuth;
  private FirebaseDatabase mFirebaseDatabase;

  private String mUserId;
  private DatabaseReference mTitleReference;
  private ArrayAdapter<String> mAdapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    mFirebaseAuth = FirebaseAuth.getInstance();

    if (mFirebaseAuth.getCurrentUser() == null) {
      LoginActivity.logOut(this);
    }

    mFirebaseDatabase = FirebaseDatabase.getInstance();

    mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
    mListView.setAdapter(mAdapter);
  }

  @Override protected void onStart() {
    super.onStart();

    final FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
    if (currentUser != null) {
      mUserId = currentUser.getUid();
      mTitleReference = mFirebaseDatabase.getReference("users").child(mUserId).child("items");

      mTitleReference.addChildEventListener(mChildEventListener);
    }
  }

  @Override protected void onStop() {
    super.onStop();

    if (mTitleReference != null) {
      mTitleReference.removeEventListener(mChildEventListener);
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {

    getMenuInflater().inflate(R.menu.menu_main, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case R.id.action_logout:
        mFirebaseAuth.signOut();
        LoginActivity.logOut(this);
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @OnClick(R.id.addButton) public void onClick() {

    if (mUserId == null) {
      return;
    }

    final Item item = new Item(mTodoText.getText().toString());

    mTitleReference.push().setValue(item);
  }

  @OnItemClick(R.id.listView) void onItemClick(ListView listView, int position) {
    mTitleReference.orderByChild("title")
        .equalTo((String) listView.getItemAtPosition(position))
        .addListenerForSingleValueEvent(new ValueEventListener() {
          public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.hasChildren()) {
              DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
              firstChild.getRef().removeValue();
            }
          }

          public void onCancelled(DatabaseError e) {
            Timber.e(e.toString());
          }
        });
  }
}
