package edu.cnm.deepdive.codebreaker.app.view;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;

public class SquareRadioButton extends AppCompatRadioButton {

  public SquareRadioButton(@NonNull Context context) {
    super(context);
  }

  public SquareRadioButton(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }

  public SquareRadioButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // Use the calculated width for both dimensions to ensure it's square.
    super.onMeasure(widthMeasureSpec, widthMeasureSpec);
  }

}
