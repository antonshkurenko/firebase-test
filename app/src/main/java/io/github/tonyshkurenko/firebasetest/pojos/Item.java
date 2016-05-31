package io.github.tonyshkurenko.firebasetest.pojos;

/**
 * Created by: Anton Shkurenko (tonyshkurenko)
 * Project: Firebasetest
 * Date: 5/31/16
 * Code style: SquareAndroid (https://github.com/square/java-code-styles)
 * Follow me: @tonyshkurenko
 */
public class Item {

  private String mTitle;

  public Item() {

  }

  public Item(String title) {
    this.mTitle = title;
  }

  public String getTitle() {
    return mTitle;
  }

  public void setTitle(String title) {
    this.mTitle = title;
  }
}
