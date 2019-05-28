package il.co.alonbd.blackjack;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Animator {
    final static public int WIN = 0;
    final static public int BURNED = 1;
    final static public int BJ = 2;
    final static public int PUSH = 3;
    final static public int OWM = 3;
    final static public int LOST = 5;
    public interface EndListener {
        void onEnd();
    }
    public static void finishAnim(final FrameLayout fl, int TAG, final EndListener endListener){
        LinearLayout tvs = (LinearLayout) ((Activity)fl.getContext()).getLayoutInflater().inflate(R.layout.win_lose_bj,null,false);
        final TextView tv = (TextView)tvs.getChildAt(TAG);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        tvs.removeView(tv);
        fl.addView(tv,lp);
        TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,-1,Animation.RELATIVE_TO_PARENT,0);
        anim.setDuration(400);
        anim.setFillAfter(true);
        setEndListener(anim, new EndListener() {
            @Override
            public void onEnd() {
                TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,1);
                anim.setDuration(400);
                anim.setStartOffset(700);
                anim.setFillAfter(true);
                setEndListener(anim, new EndListener() {
                    @Override
                    public void onEnd() {
                        fl.removeView(tv);
                        if (endListener != null) endListener.onEnd();
                    }
                });
                tv.startAnimation(anim);
            }
        });
        tv.startAnimation(anim);

    }

    private static void setEndListener(Animation animation, final EndListener endListener) {
        if (endListener != null) {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    endListener.onEnd();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    private static void setEndListener(ObjectAnimator animator, final EndListener endListener) {
        if (endListener != null) {
            animator.addListener(new android.animation.Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(android.animation.Animator animation) {

                }

                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    endListener.onEnd();
                }

                @Override
                public void onAnimationCancel(android.animation.Animator animation) {

                }

                @Override
                public void onAnimationRepeat(android.animation.Animator animation) {

                }
            });
        }
    }

    public static void handInOut(Hand hand,  boolean in,EndListener endListener){
        float fromX = -1;
        float toX = 0;
        if (!in){
            fromX = toX;
            toX = 1;
        }
        TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, fromX, Animation.RELATIVE_TO_PARENT,toX,Animation.RELATIVE_TO_PARENT,0,Animation.RELATIVE_TO_PARENT,0);
        setEndListener(anim,endListener);
        anim.setDuration(1000);
        anim.setFillAfter(true);
        hand.startAnimation(anim);
    }

    public static void flipAllHand(Hand hand, EndListener endListener){
        for (int i = 0 ; i<hand.getChildCount() ; i++) {
            Card c  =(Card)hand.getChildAt(i);
            if (i != hand.getChildCount() -1){
                flipCard(c,null);
            }else{
                flipCard(c,endListener);
            }
        }
    }

    public static void flipCard(final Card toFlip, final EndListener endListener){
        ObjectAnimator oa = ObjectAnimator.ofFloat(toFlip,"rotationY",0, 90);
        oa.setDuration(150);
        setEndListener(oa, new EndListener() {
            @Override
            public void onEnd() {
                toFlip.bindBitmap();
                ObjectAnimator oa = ObjectAnimator.ofFloat(toFlip,"rotationY",-90, 0);
                oa.setDuration(150);
                setEndListener(oa,endListener);
                oa.start();
            }
        });
        oa.start();
    }

    public static void handAnimation(Hand hand, boolean reverse, EndListener endListener) {
        int count = hand.getChildCount();

        for (int i = 0; i < count; i++) {
            Card card = (Card) hand.getChildAt(i);
            AnimationSet set = new AnimationSet(true);
            set.addAnimation(handTranslate(i, count, reverse));
            set.addAnimation(handRotate(i, count, reverse));
            set.setFillAfter(true);
            if (i == count - 1) setEndListener(set, endListener);
            card.startAnimation(set);
        }
    }

    private static TranslateAnimation handTranslate(int index, int count, boolean reverse) {
        TranslateAnimation out;

        float step = (float) 0.1;
        float ini = (float) (-(count - 1) * 0.5 * step);

        if (!reverse)
            out = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, (step * index) + ini, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        else
            out = new TranslateAnimation(Animation.RELATIVE_TO_SELF, (step * index) + ini, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);

        out.setDuration(500);

        if (!reverse)
            out.setStartOffset(0);
        else out.setStartOffset(300);

        return out;
    }

    private static RotateAnimation handRotate(int index, int count, boolean reverse) {
        RotateAnimation out;

        float step = 5;
        float ini = (float) (-(count - 1) * 0.5 * step);

        if (!reverse)
            out = new RotateAnimation(0, (step * index) + ini, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.95);
        else
            out = new RotateAnimation((step * index) + ini, 0, Animation.RELATIVE_TO_SELF, (float) 0.5, Animation.RELATIVE_TO_SELF, (float) 0.95);

        out.setDuration(500);

        if (!reverse)
            out.setStartOffset(300);
        else out.setStartOffset(0);

        return out;
    }

}
