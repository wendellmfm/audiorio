package br.great.excursaopajeu.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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
            view.setBackgroundResource(R.drawable.rounded_button);
            buttonTextView.setTextColor(ContextCompat.getColor(context, R.color.games_menu_buttons_color));
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            view.setBackgroundResource(R.drawable.rounded_edit_text);
            buttonTextView.setTextColor(ContextCompat.getColor(context, R.color.background));
        }
        return false;
    }
}