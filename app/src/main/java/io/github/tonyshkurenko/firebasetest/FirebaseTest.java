package io.github.tonyshkurenko.firebasetest;

import android.app.Application;
import timber.log.Timber;

/**
 * Created by: Anton Shkurenko (tonyshkurenko)
 * Project: Firebasetest
 * Date: 5/30/16
 * Code style: SquareAndroid (https://github.com/square/java-code-styles)
 * Follow me: @tonyshkurenko
 */

/**
 * https://www.sitepoint.com/creating-a-cloud-backend-for-your-android-app-using-firebase/
 *
 * Adopted to the new firebase api
 */
public class FirebaseTest extends Application {

  @Override public void onCreate() {
    super.onCreate();

    Timber.plant(new Timber.DebugTree());
  }
}
