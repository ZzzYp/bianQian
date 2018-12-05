package com.gome.note.view;

import android.content.ClipboardManager;
import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;


import com.blankj.utilcode.util.KeyboardUtils;
import com.gome.note.ui.create.IEditTextPasteCallback;
import com.gome.note.utils.ShareHelper;

import java.util.Random;

/**
 * created on 2017-01-17
 *
 * @author pythoncat.cheng  http://stackoverflow.com/questions/4886858/android-edittext-deletebackspace-key-event"
 */
public class ZanyEditText extends android.support.v7.widget.AppCompatEditText {

    private Random r = new Random();
    private Context mContext;
    private IEditTextPasteCallback iEditTextPasteCallback = null;

    public ZanyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public ZanyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public ZanyEditText(Context context) {
        super(context);
        mContext = context;
    }

    public void setRandomBackgroundColor() {
//        setBackgroundColor(Color.rgb(r.nextInt(256), r.nextInt(256), r
//                .nextInt(256)));
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new ZanyInputConnection(super.onCreateInputConnection(outAttrs),
                true);
    }

    private class ZanyInputConnection extends InputConnectionWrapper {

        ZanyInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                ZanyEditText.this.setRandomBackgroundColor();
                // Un-comment if you wish to cancel the backspace:
                // return false;
            } else if (event.getAction() == KeyEvent.ACTION_UP
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

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

    //listener copy
    @Override
    public boolean onTextContextMenuItem(int id) {


        switch (id) {
            case android.R.id.paste:
                ClipboardManager clip = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);

//                if (mContext instanceof IEditTextPasteCallback) {
//                    ((IEditTextPasteCallback) mContext).onPaste(clip.getText());
//                }
                if (null != iEditTextPasteCallback) {
                    iEditTextPasteCallback.onPaste(clip.getText());
                }
                return true;
            case android.R.id.shareText:
                KeyboardUtils.hideSoftInput(this);
                Editable editable = getText();
                int start = getSelectionStart();
                int end = getSelectionEnd();
                String selectStr = editable.toString().substring(start, end);
                ShareHelper.shareText(mContext, selectStr);
                return true;


        }
        return super.onTextContextMenuItem(id);
    }

    public IEditTextPasteCallback getiEditTextPasteCallback() {
        return iEditTextPasteCallback;
    }

    public void setiEditTextPasteCallback(IEditTextPasteCallback iEditTextPasteCallback) {
        this.iEditTextPasteCallback = iEditTextPasteCallback;
    }
}