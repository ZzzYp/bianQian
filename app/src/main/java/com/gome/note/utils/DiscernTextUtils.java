package com.gome.note.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gome.note.R;
import com.gome.note.ui.create.presenter.NoteCreatePresenter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ProjectName:MyPocket
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2017/7/11
 * DESCRIBE: check text  is  have  URL  or Phone   and  can click URL and call phone
 */


public class DiscernTextUtils {
    private Context mContext;

    public DiscernTextUtils(Context context) {

        mContext = context;
    }


    //
    public void setTouchListener(TextView tv) {

        tv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                boolean ret = false;
                CharSequence text = ((TextView) v).getText();
                Spannable stext = Spannable.Factory.getInstance().newSpannable(
                        text);
                TextView widget = (TextView) v;
                int action = event.getAction();
                //judge whether in spannable object by onclicklistener
                if (action == MotionEvent.ACTION_UP
                        || action == MotionEvent.ACTION_DOWN) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    x -= widget.getTotalPaddingLeft();
                    y -= widget.getTotalPaddingTop();

                    x += widget.getScrollX();
                    y += widget.getScrollY();

                    Layout layout = widget.getLayout();
                    int line = layout.getLineForVertical(y);
                    int off = layout.getOffsetForHorizontal(line, x);

                    ClickableSpan[] link = stext.getSpans(off, off,
                            ClickableSpan.class);

                    if (link.length != 0) {
                        if (action == MotionEvent.ACTION_UP) {
                            link[0].onClick(widget);
                        }
                        ret = true;
                    }
                }
                return ret;
            }
        });


    }


    //
    public void checkMobilePhoneText(SpannableString sp, String text, TextView tv) {
        Pattern pattern = Pattern
                .compile("[1][3587]\\d{9}");


        Matcher matcher = pattern.matcher(text);
        int start = 0;
        //match accord with rule
        while (matcher.find(start)) {
            start = matcher.end();
            sp.setSpan(new MyPhoneSpan(matcher.group()), matcher.start(),
                    matcher.end(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            if (start >= text.length()) {
                break;
            }
        }
        tv.setText(sp);

    }

    public void checkPhoneText(SpannableString spp, String text, TextView tv) {
        Pattern pattern = Pattern
                .compile("([0-9]|[-*()#+]){0,29}");

        SpannableString sp = new SpannableString(text);

        Matcher matcher = pattern.matcher(text);
        int start = 0;
        //match accord with rule
        while (matcher.find(start)) {
            start = matcher.end();
            sp.setSpan(new MyPhoneSpan(matcher.group()), matcher.start(),
                    matcher.end(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            if (start >= text.length()) {
                break;
            }
        }
        tv.setText(sp);

    }


    public void checkWebUrlText(TextView tv) {
        CharSequence text = tv.getText();
        int end = text.length();
        SpannableString sp = new SpannableString(text);
        URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);

        SpannableStringBuilder style = new SpannableStringBuilder(text);
        for (URLSpan url : urls) {
            MyWebURLSpan myURLSpan = new MyWebURLSpan(url.getURL());
            style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable
                    .SPAN_EXCLUSIVE_INCLUSIVE);
        }
        tv.setText(style);

    }

    class MyPhoneSpan extends ClickableSpan {
        private String phone;
        private NoteCreatePresenter presenter;

        MyPhoneSpan(String url) {
            phone = url;
        }


        //click the underline of link to pop-up the dialog or hint the tele and the message
        @Override
        public void onClick(View widget) {
            final String urlTemp = phone.replace(" ", "");
            // Toast.makeText(mContext, urlTemp, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
            ds.setColor(mContext.getResources().getColor(R.color.font_blue_1));
        }
    }

    private class MyWebURLSpan extends ClickableSpan {

        private String mUrl;

        MyWebURLSpan(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {
            //pop-up the dialog
            Toast.makeText(mContext, mUrl, Toast.LENGTH_SHORT).show();
            widget.setBackgroundColor(Color.parseColor("#00000000"));
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
            ds.setColor(mContext.getResources().getColor(R.color.font_blue_1));
        }
    }

}
