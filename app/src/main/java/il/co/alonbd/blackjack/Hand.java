package il.co.alonbd.blackjack;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

public class Hand extends RelativeLayout {

    LayoutParams cardLp;
    final double CARD_RATIO = 0.65436;
    Point size;
    int cardHeight;

    private TextView valueTv;
    private ArrayList<Integer> values;
    private int maxValue;
    private boolean isLost, twentyOne, bj, visible; boolean regDrawOrder;

    //CONSTRUCTION SITE
    private void constSetup(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        size = new Point();
        wm.getDefaultDisplay().getSize(size);
        cardHeight = size.y / 4;
        cardLp = new LayoutParams((int) (cardHeight * CARD_RATIO), cardHeight);
        cardLp.addRule(CENTER_HORIZONTAL);
        cardLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        cardLp.bottomMargin = cardHeight / 10;
        setPadding(cardHeight / 8, cardHeight / 8, cardHeight / 8, cardHeight / 8);
        regDrawOrder = false;
        setChildrenDrawingOrderEnabled(true);
    }


    public Hand(Context context) {
        super(context);
        constSetup(context);
        addView(new Card(context), new LayoutParams(cardLp));
        addView(new Card(context), new LayoutParams(cardLp));
        hideCards();
        generateInfo();
    }

    public Hand(Context context, AttributeSet attrs) {
        super(context, attrs);
        constSetup(context);
        addView(new Card(context), new LayoutParams(cardLp));
        addView(new Card(context), new LayoutParams(cardLp));
        hideCards();
        generateInfo();
    }

    private void addCard(final Animator.EndListener endListener) {
        Animator.handAnimation(this, true, new Animator.EndListener() {
            @Override
            public void onEnd() {
                addView(new Card(getContext()), 0, new LayoutParams(cardLp));
                generateInfo();
                Animator.handAnimation(Hand.this, false, endListener);
            }
        });
    }

    public void hitHand(Animator.EndListener endListener) {
        addCard(endListener);
    }

    public void resetHand(final Animator.EndListener endListener) {
        Animator.handInOut(this, false, new Animator.EndListener() {
            @Override
            public void onEnd() {
                removeAllViews();
                addView(new Card(getContext()), new LayoutParams(cardLp));
                addView(new Card(getContext()), new LayoutParams(cardLp));
                hideCards();
                generateInfo();
                Animator.handInOut(Hand.this, true, endListener);
            }
        });
    }

    private void hideCards() {
        //hide without animation
        for (int i = 0; i < getChildCount(); i++) {
            Card c = (Card) getChildAt(i);
            c.ofDealer(true);
            visible = false;
            //regDrawOrder = false;
        }
    }

    public void revealAll(boolean dealerInitial, final TextView tv) {
        if (dealerInitial) {
            Animator.flipCard((Card) getChildAt(getChildCount() - 1),  null);
        } else {
            for (int i = 0; i < getChildCount(); i++) {
                Card c = (Card) getChildAt(i);
                Animator.flipAllHand(this, new Animator.EndListener() {
                    @Override
                    public void onEnd() {
                        visible = true;
                        valueTv = tv;
                        generateInfo();
                    }
                });

            }
        }
    }
    private boolean isDealerPlay(){
        boolean hit = true;
        for (int v : values) {
            if (v >= 17) hit = false;
        }
        if (isLost) hit = false;
        return hit;
    }
    private void addDealerCards(final Animator.EndListener endListener){
        if (isDealerPlay()) {
            addCard(new Animator.EndListener() {
                @Override
                public void onEnd() {
                    addDealerCards(endListener);
                }
            });
        }else{
            generateInfo();
            endListener.onEnd();

        }
    }
    public void dealerPlay(final TextView tv, Animator.EndListener endListener) {
        revealAll(false, tv);
        addDealerCards(endListener);
    }


    public boolean isLost() {
        return isLost;
    }

    public boolean isBj() {
        return bj;
    }

    public boolean isTwentyOne() {
        return twentyOne;
    }

    public int getMaxValue() {
        return maxValue;
    }

    private void generateInfo() {
        maxValue = 0;
        isLost = false;
        twentyOne = false;
        bj = false;

        int aces = 0;
        int noAcesValue = 0;

        for (int i = 0; i < this.getChildCount(); i++) {
            Card card = (Card) this.getChildAt(i);

            if (card.getNumber() == 1) aces++;
            else {
                if (card.getNumber() >= 10) noAcesValue += 10;
                else noAcesValue += card.getNumber();
            }
        }

        ArrayList<Integer> acesValues = new ArrayList<>();
        ArrayList<Integer> values = new ArrayList<>();

        acesValues.add(aces);
        for (int i = 1; i <= aces; i++) {
            int currentValue = i * 11 + aces - i;
            acesValues.add(currentValue);
        }

        for (int i : acesValues) {
            int currentValue = i + noAcesValue;
            if (currentValue <= 21) {
                values.add(currentValue);

                if (currentValue == 21) {
                    twentyOne = true;
                    maxValue = 21;
                    if (getChildCount() == 2) bj = true;
                } else if (currentValue > maxValue) {
                    maxValue = currentValue;
                }
            }

        }
        if (values.size() == 0) {
            isLost = true;
            maxValue = 0;
            twentyOne = false;
        }

        //UpdateTV
        this.values = values;
        if (valueTv != null && visible) {
            String valuesString = "";
            for (int i = 0; i < values.size(); i++) {
                valuesString += values.get(i);
                if (i < values.size() - 1) {
                    valuesString += " / ";
                }
            }
            valueTv.setText(valuesString);
        }
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
       if (regDrawOrder){
            return childCount - i - 1;
       }
        return i;
    }
}
