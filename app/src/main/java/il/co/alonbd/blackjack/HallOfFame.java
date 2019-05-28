package il.co.alonbd.blackjack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class HallOfFame extends AppCompatActivity {
    private static ArrayList<Entry> entries;
    private final static String FILE = "entries";
    public static class Entry implements Serializable, Comparable<Entry>{
        @Override
        public int compareTo(Entry o) {
            return  o.score - score;
        }
        private int score;
        private String name;

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Entry(int score, String name) {
            this.score = score;
            this.name = name;
        }
    }
    public static void addEntry(int score,String name, Context context){
        Entry entry = new Entry(score,name);
        readEntries(context);
        entries.add(entry);
        writeEntries(context);
    }
    public static void clearEntries(Context context){
        entries = new ArrayList<>();
        writeEntries(context);
    }

    private static void readEntries(Context context){
        try {
            FileInputStream fis = context.openFileInput(FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            entries = (ArrayList<Entry>) ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            entries = new ArrayList<>();
            writeEntries(context);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Collections.sort(entries);
    }

    private static void writeEntries(Context context){
        try {
            FileOutputStream fos = context.openFileOutput(FILE,MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(entries);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.sort(entries);
    }
    RecyclerView recycler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall_of_fame);
        readEntries(this);
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recycler.setAdapter(new EntryAdapter(entries));
    }
}
