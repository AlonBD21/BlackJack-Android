package il.co.alonbd.blackjack;

import android.content.Context;
import android.util.AttributeSet;

public class TokensDisplay extends android.support.v7.widget.AppCompatTextView {
    private int amount;

    public TokensDisplay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAmount(int amount){
        this.amount = amount;
        setText(amount+"");
    }

    public int getAmount(){
        return amount;
    }
}
