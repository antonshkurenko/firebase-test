package io.github.tonyshkurenko.firebasetest.activities;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import io.github.tonyshkurenko.firebasetest.R;

public class SignUpActivity extends AppCompatActivity {

  @BindView(R.id.emailField) EditText mEmailField;
  @BindView(R.id.passwordField) EditText mPasswordField;

  private FirebaseAuth mFirebaseAuth;
  private FirebaseDatabase mFirebaseDatabase;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_up);
    ButterKnife.bind(this);

    mFirebaseAuth = FirebaseAuth.getInstance();
    mFirebaseDatabase = FirebaseDatabase.getInstance();
  }

  @OnClick(R.id.signupButton) public void onClick() {
    final String password = mPasswordField.getText().toString().trim();
    final String email = mEmailField.getText().toString().trim();

    if (password.isEmpty() || email.isEmpty()) {
      final AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
      builder.setMessage(R.string.signup_error_message)
          .setTitle(R.string.signup_error_title)
          .setPositiveButton(android.R.string.ok, null);
      builder.create().show();
    } else {

      mFirebaseAuth.createUserWithEmailAndPassword(email, password)
          .addOnSuccessListener(this, authResult -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
            builder.setMessage(R.string.signup_success)
                .setPositiveButton(R.string.login_button_label, (dialogInterface, i) -> {
                  LoginActivity.logOut(SignUpActivity.this);
                });
            builder.create().show();
          })
          .addOnFailureListener(this, e -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
            builder.setMessage(e.getMessage())
                .setTitle(R.string.signup_error_title)
                .setPositiveButton(android.R.string.ok, null);
            builder.create().show();
          });
    }
  }
}
