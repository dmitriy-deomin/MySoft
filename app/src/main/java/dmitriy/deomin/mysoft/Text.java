package dmitriy.deomin.mysoft;

import android.content.Context;
import android.graphics.Canvas;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.widget.TextView;


public class Text extends TextView {

    public Text(Context context) {
        super(context);
        this.setTypeface(Main.face);
        this.setTextColor(Main.COLOR_TEXT);
    }

    public Text(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Main.face);
        this.setTextColor(Main.COLOR_TEXT);
    }

    public Text(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(Main.face);
        this.setTextColor(Main.COLOR_TEXT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.setTypeface(Main.face);
        this.setTextColor(Main.COLOR_TEXT);
        //форматирование текста если есть перенос
        if (this.getText().toString().contains("\n")) {
            //new UnderlineSpan() - подчеркнутый текст
            //new StyleSpan(Typeface.BOLD) - полужирный тектс
            //new StyleSpan(Typeface.ITALIC) - курсив
            //new ForegroundColorSpan(Color.GREEN) - цвет
            //new RelativeSizeSpan(1.5f) - размер текста
            Main.text = new SpannableString(this.getText().toString());
            Main.text.setSpan(new UnderlineSpan(), 0, this.getText().toString().indexOf("\n"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            Main.text.setSpan(new RelativeSizeSpan(0.8f), this.getText().toString().indexOf("\n"), this.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            this.setText(Main.text);
        }


    }
}
