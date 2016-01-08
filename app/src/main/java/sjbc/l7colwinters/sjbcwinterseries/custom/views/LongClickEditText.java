package sjbc.l7colwinters.sjbcwinterseries.custom.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 * Created by L7ColWinters on 12/19/2015.
 */
public class LongClickEditText extends EditText implements View.OnLongClickListener {
    public LongClickEditText(Context context) {
        super(context);
        setup();
    }

    public LongClickEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public LongClickEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    @TargetApi(21)
    public LongClickEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup();
    }

    private void setup(){
        setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        requestFocus();
        return true;
    }
}
