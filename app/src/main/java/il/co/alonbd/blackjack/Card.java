package il.co.alonbd.blackjack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Random;

public class Card extends AppCompatImageView {
    private int number;
    private Shape shape;
    private Bitmap bitmap;
    static private HashMap<Shape, HashMap<Integer, Bitmap>> bitmapsMap;
    static private Bitmap back;
    static Thread loader;
    final static double CARD_RATIO = 1.52822;

    public Card(Context context) {
        super(context);
        //Card Properties
        Random r = new Random();
        number = r.nextInt(13) + 1;
        shape = Shape.values()[r.nextInt(4)];
        //Set Correct Bitmap

        bitmap = bitmapsMap.get(shape).get(number);
        this.setImageBitmap(bitmap);
    }

    public Card(Context context, Shape shape, int number) {
        this(context);
        this.number = number;
        this.shape = shape;
        bitmap = bitmapsMap.get(shape).get(number);
        this.setImageBitmap(bitmap);
    }

    public int getNumber() {
        return number;
    }

    public Shape getShape() {
        return shape;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void ofDealer(boolean apply) {
        if (apply) {
            bitmap = back;
            setImageBitmap(back);
        }
    }

    enum Shape {
        Diamonds, Clubs, Hearts, Spades;
    }

    public static void threadCards(final Context context){
        loader = new Thread(new Runnable() {
            @Override
            public void run() {
                if (bitmapsMap == null) {
                    bitmapsMap = loadBitmaps(context);
                }
            }
        });
        loader.run();
    }

    public static void loadCards(final Context context) {
        if (bitmapsMap == null) {
            if (loader != null) {
                if (loader.isAlive()) {
                    try {
                        loader.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    threadCards(context);
                    try {
                        loader.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                threadCards(context);
                try {
                    loader.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static HashMap<Shape, HashMap<Integer, Bitmap>> loadBitmaps(Context context) {
        HashMap<Shape, HashMap<Integer, Bitmap>> mainMap = new HashMap<>();

        HashMap<Integer, Bitmap> d = new HashMap<>();
        d.put(1, BitmapFactory.decodeResource(context.getResources(), R.drawable.d1));
        d.put(2, BitmapFactory.decodeResource(context.getResources(), R.drawable.d2));
        d.put(3, BitmapFactory.decodeResource(context.getResources(), R.drawable.d3));
        d.put(4, BitmapFactory.decodeResource(context.getResources(), R.drawable.d4));
        d.put(5, BitmapFactory.decodeResource(context.getResources(), R.drawable.d5));
        d.put(6, BitmapFactory.decodeResource(context.getResources(), R.drawable.d6));
        d.put(7, BitmapFactory.decodeResource(context.getResources(), R.drawable.d7));
        d.put(8, BitmapFactory.decodeResource(context.getResources(), R.drawable.d8));
        d.put(9, BitmapFactory.decodeResource(context.getResources(), R.drawable.d9));
        d.put(10, BitmapFactory.decodeResource(context.getResources(), R.drawable.d10));
        d.put(11, BitmapFactory.decodeResource(context.getResources(), R.drawable.d11));
        d.put(12, BitmapFactory.decodeResource(context.getResources(), R.drawable.d12));
        d.put(13, BitmapFactory.decodeResource(context.getResources(), R.drawable.d13));


        HashMap<Integer, Bitmap> s = new HashMap<>();
        s.put(1, BitmapFactory.decodeResource(context.getResources(), R.drawable.s1));
        s.put(2, BitmapFactory.decodeResource(context.getResources(), R.drawable.s2));
        s.put(3, BitmapFactory.decodeResource(context.getResources(), R.drawable.s3));
        s.put(4, BitmapFactory.decodeResource(context.getResources(), R.drawable.s4));
        s.put(5, BitmapFactory.decodeResource(context.getResources(), R.drawable.s5));
        s.put(6, BitmapFactory.decodeResource(context.getResources(), R.drawable.s6));
        s.put(7, BitmapFactory.decodeResource(context.getResources(), R.drawable.s7));
        s.put(8, BitmapFactory.decodeResource(context.getResources(), R.drawable.s8));
        s.put(9, BitmapFactory.decodeResource(context.getResources(), R.drawable.s9));
        s.put(10, BitmapFactory.decodeResource(context.getResources(), R.drawable.s10));
        s.put(11, BitmapFactory.decodeResource(context.getResources(), R.drawable.s11));
        s.put(12, BitmapFactory.decodeResource(context.getResources(), R.drawable.s12));
        s.put(13, BitmapFactory.decodeResource(context.getResources(), R.drawable.s13));


        HashMap<Integer, Bitmap> c = new HashMap<>();
        c.put(1, BitmapFactory.decodeResource(context.getResources(), R.drawable.c1));
        c.put(2, BitmapFactory.decodeResource(context.getResources(), R.drawable.c2));
        c.put(3, BitmapFactory.decodeResource(context.getResources(), R.drawable.c3));
        c.put(4, BitmapFactory.decodeResource(context.getResources(), R.drawable.c4));
        c.put(5, BitmapFactory.decodeResource(context.getResources(), R.drawable.c5));
        c.put(6, BitmapFactory.decodeResource(context.getResources(), R.drawable.c6));
        c.put(7, BitmapFactory.decodeResource(context.getResources(), R.drawable.c7));
        c.put(8, BitmapFactory.decodeResource(context.getResources(), R.drawable.c8));
        c.put(9, BitmapFactory.decodeResource(context.getResources(), R.drawable.c9));
        c.put(10, BitmapFactory.decodeResource(context.getResources(), R.drawable.c10));
        c.put(11, BitmapFactory.decodeResource(context.getResources(), R.drawable.c11));
        c.put(12, BitmapFactory.decodeResource(context.getResources(), R.drawable.c12));
        c.put(13, BitmapFactory.decodeResource(context.getResources(), R.drawable.c13));

        HashMap<Integer, Bitmap> h = new HashMap<>();
        h.put(1, BitmapFactory.decodeResource(context.getResources(), R.drawable.h1));
        h.put(2, BitmapFactory.decodeResource(context.getResources(), R.drawable.h2));
        h.put(3, BitmapFactory.decodeResource(context.getResources(), R.drawable.h3));
        h.put(4, BitmapFactory.decodeResource(context.getResources(), R.drawable.h4));
        h.put(5, BitmapFactory.decodeResource(context.getResources(), R.drawable.h5));
        h.put(6, BitmapFactory.decodeResource(context.getResources(), R.drawable.h6));
        h.put(7, BitmapFactory.decodeResource(context.getResources(), R.drawable.h7));
        h.put(8, BitmapFactory.decodeResource(context.getResources(), R.drawable.h8));
        h.put(9, BitmapFactory.decodeResource(context.getResources(), R.drawable.h9));
        h.put(10, BitmapFactory.decodeResource(context.getResources(), R.drawable.h10));
        h.put(11, BitmapFactory.decodeResource(context.getResources(), R.drawable.h11));
        h.put(12, BitmapFactory.decodeResource(context.getResources(), R.drawable.h12));
        h.put(13, BitmapFactory.decodeResource(context.getResources(), R.drawable.h13));

        mainMap.put(Shape.Diamonds, d);
        mainMap.put(Shape.Clubs, c);
        mainMap.put(Shape.Hearts, h);
        mainMap.put(Shape.Spades, s);
        back = BitmapFactory.decodeResource(context.getResources(), R.drawable.purple_back);

        bitmapsMap = mainMap;
        return mainMap;
    }
    public void bindBitmap(){
        bitmap = bitmapsMap.get(shape).get(number);
        setImageBitmap(bitmap);
    }
}

