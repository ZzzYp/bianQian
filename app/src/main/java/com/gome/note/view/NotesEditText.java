package com.gome.note.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

public class NotesEditText extends android.support.v7.widget.AppCompatEditText {
    private static final String TAG = "NotesEditText";

    private BackKeyListener mBackKeyListener;

    public NotesEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public NotesEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotesEditText(Context context) {
        super(context);
    }

    public void setBackKeyListener(BackKeyListener backKeyListener){
        mBackKeyListener = backKeyListener;
    }


    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new NotesInputConnection(super.onCreateInputConnection(outAttrs),
                true);
    }

    private class NotesInputConnection extends InputConnectionWrapper {

        public NotesInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                if(null != mBackKeyListener){
                    mBackKeyListener.onBackPressedDown();
                }
                // Un-comment if you wish to cancel the backspace:
                // return false;
            }
            return super.sendKeyEvent(event);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
            if (beforeLength == 1 && afterLength == 0) {
                // backspace
                return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                        && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }

            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }

    public interface BackKeyListener{
        void onBackPressedDown();
    }

}
