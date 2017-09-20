package br.great.excursaopajeu.util;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.ufc.great.arviewer.pajeu.R;

public class EfeitoClique implements View.OnTouchListener {
    Context context;

    public EfeitoClique(Context context) {
        this.context = context;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        ViewGroup vg = (ViewGroup) view;
        TextView buttonTextView = (TextView) vg.getChildAt(0);

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            view.setBackgroundColor(context.getResources().getColor(R.color.games_menu_buttons_effect_color));
            buttonTextView.setTextColor(context.getResources().getColor(R.color.games_menu_buttons_color));
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            view.setBackgroundColor(context.getResources().getColor(R.color.games_menu_buttons_color));
            buttonTextView.setTextColor(context.getResources().getColor(R.color.background));
        }
        return false;
    }
}