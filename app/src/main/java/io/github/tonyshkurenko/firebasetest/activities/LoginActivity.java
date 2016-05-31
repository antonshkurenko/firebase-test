package io.github.tonyshkurenko.firebasetest.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import io.github.tonyshkurenko.firebasetest.R;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

public class LoginActivity extends AppCompatActivity {

  @BindView(R.id.emailField) EditText mEmailField;
  @BindView(R.id.passwordField) EditText mPasswordField;

  private FirebaseAuth mFirebaseAuth;
  private FirebaseDatabase mFirebaseDatabase;

  private final AuthStateListener mAuthStateListener = firebaseAuth -> {
    final FirebaseUser user = firebaseAuth.getCurrentUser();
    if (user != null) {
      // User is signed in
      Timber.d("onAuthStateChanged:signed in:" + user.getUid());
    } else {
      // User is signed out
      Timber.d("onAuthStateChanged:signed out");
    }
  };

  public static void logOut(Context ctx) {
    final Intent intent = new Intent(ctx, LoginActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    ctx.startActivity(intent);
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);

    mFirebaseAuth = FirebaseAuth.getInstance();
    mFirebaseDatabase = FirebaseDatabase.getInstance();
  }

  @Override public void onStart() {
    super.onStart();
    mFirebaseAuth.addAuthStateListener(mAuthStateListener);
  }

  @Override public void onStop() {
    super.onStop();
    if (mAuthStateListener != null) {
      mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }
  }

  @OnClick({ R.id.loginButton, R.id.signUpText }) public void onClick(View view) {
    switch (view.getId()) {
      case R.id.loginButton:
        logIn();
        break;
      case R.id.signUpText:
        final Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
        break;
    }
  }

  private void logIn() {
    final String email = mEmailField.getText().toString().trim();
    final String password = mPasswordField.getText().toString().trim();

    if (email.isEmpty() || password.isEmpty()) {
      final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
      builder.setMessage(R.string.login_error_message)
          .setTitle(R.string.login_error_title)
          .setPositiveButton(android.R.string.ok, null);
      builder.create().show();
    } else {

      mFirebaseAuth.signInWithEmailAndPassword(email, password)
          .addOnSuccessListener(this, authResult -> {

            final FirebaseUser user = authResult.getUser();

            final Map<String, Object> map = new HashMap<>();

            map.put("email", email);

            mFirebaseDatabase.getReference("users").child(user.getUid()).updateChildren(map);

            final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
          })
          .addOnFailureListener(this, e -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(e.getMessage())
                .setTitle(R.string.login_error_title)
                .setPositiveButton(android.R.string.ok, null);
            builder.create().show();
          });
    }
  }
}

