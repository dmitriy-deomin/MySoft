package dmitriy.deomin.mysoft;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

/**
 * Created by Admin on 31.10.2016.
 */

public class Fon_item extends CardView {

    public Fon_item(Context context) {
        super(context);
        this.setCardBackgroundColor(Main.COLOR_ITEM);
    }

    public Fon_item(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setCardBackgroundColor(Main.COLOR_ITEM);
    }

    public Fon_item(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setCardBackgroundColor(Main.COLOR_ITEM);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.setCardBackgroundColor(Main.COLOR_ITEM);

    }
}
