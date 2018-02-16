package com.amarmh.customedittext;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;


import java.util.regex.Pattern;

/**
 * Created by inficare on 12/05/2017.
 */

public class CustomEditText extends LinearLayout {

    private static final String TAG = "CustomEditText";
    private final ImageView imageView;
    private final TextInputEditText editText;
    private final TextInputLayout inputLayout;
    public boolean animationStarted = false;
    private Bitmap defaultDrawable, successDrawable, failedDrawable;
    private boolean isValid = false;
    private String hint = "";
    private String errorMessage = "Empty Field";
    private Boolean isPassword = false;
    private Boolean showDefaultIndicator = false;

    public CustomEditText(Context context) {
        this(context, null);
    }

    public CustomEditText(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    //Filter add for numberPassword input in editText  Amar 02/07/2017
    InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; ++i) {
                if (!Pattern.compile("[1234567890]*").matcher(String.valueOf(source.charAt(i))).matches()) {
                    return "";
                }
            }

            return null;
        }
    };


    //Filter add for textLimit input in editText  Amar 02/07/2017
    InputFilter textFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; ++i) {
                if (!Pattern.compile("[A-Za-z0-9 .]*").matcher(String.valueOf(source.charAt(i))).matches()) {
                    return "";
                }
            }

            return null;
        }
    };


    public CustomEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.customEdit,
                0, 0);
        int inputType = 1;
//<enum name="email" value="1"/>
//            <enum name="password" value="2"/>
//            <enum name="normal" value="3"/>
//            <enum name="phone" value="4"/>

        boolean clickAble = false;
        try {
            clickAble = a.getBoolean(R.styleable.customEdit_clickAble, false);
            inputType = a.getInteger(R.styleable.customEdit_inputType, -1);
            hint = a.getString(R.styleable.customEdit_hint);
            errorMessage = a.getString(R.styleable.customEdit_errorMessage);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }


        inputLayout = new TextInputLayout(context);
        inputLayout.setPasswordVisibilityToggleEnabled(false);
        inputLayout.setError(null);
        editText = new TextInputEditText(context);
        imageView = new ImageView(context);

        editText.setFocusable(!clickAble);
        editText.setCursorVisible(!clickAble);


        if (clickAble) {
            editText.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.action_drop_down), null);
        }


        if (inputType == 1) {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        } else if (inputType == 2) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            inputLayout.setPasswordVisibilityToggleEnabled(true);
            showDefaultIndicator = true;
        } else if (inputType == 3) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
//            editText.setFilters(new InputFilter[]{textFilter, new InputFilter.LengthFilter(50)});
            editText.setFilters(new InputFilter[]{
                    new InputFilter() {
                        public CharSequence filter(CharSequence src, int start,
                                                   int end, Spanned dst, int dstart, int dend) {
                            if (src.equals("")) { // for backspace
                                return src;
                            }
                            if (src.toString().matches("[a-zA-Z .]+")) {
                                return src;
                            }
                            return "";
                        }
                    }
            });
        } else if (inputType == 4) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (inputType == 5) {  //for numberPassword Amar 02/07/2017
            editText.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4)});
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            inputLayout.setPasswordVisibilityToggleEnabled(true);
        } else if (inputType == 6) {
//            editText.setFilters(new InputFilter[]{textFilter, new InputFilter.LengthFilter(50)});
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        } else if (inputType == 7) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setFilters(new InputFilter[]{textFilter, new InputFilter.LengthFilter(50)});
            editText.setFilters(new InputFilter[]{
                    new InputFilter() {
                        public CharSequence filter(CharSequence src, int start,
                                                   int end, Spanned dst, int dstart, int dend) {
                            CharSequence returnVal = "";
                            if (src.equals("")) { // for backspace
                                return src;
                            }
                            if (src.toString().matches("[a-zA-Z0-9 .]+")) {
                                return src;
                            }

                            try {
                                returnVal = src.subSequence(start, end - 1);
                            } catch (Exception ignored) {

                            }

                            return returnVal;
                        }
                    }
            });
        } else if (inputType == 8) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setFilters(new InputFilter[]{textFilter, new InputFilter.LengthFilter(50)});
            editText.setFilters(new InputFilter[]{
                    new InputFilter() {
                        public CharSequence filter(CharSequence src, int start,
                                                   int end, Spanned dst, int dstart, int dend) {
                            CharSequence returnVal = "";
                            if (src.equals("")) { // for backspace
                                return src;
                            }
                            if (src.toString().matches("[a-zA-Z[-,0-9] .]+")) {
                                return src;
                            }

                            try {
                                returnVal = src.subSequence(start, end - 1);
                            } catch (Exception ignored) {

                            }

                            return returnVal;
                        }
                    }
            });
        }

//        else if (inputType ==-1){
//            editText.setFilters(new InputFilter[]{textFilter, new InputFilter.LengthFilter(50)});
//            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
//        }


        //imageView.setBackgroundColor(ContextCompat.getColor(context,R.color.primaryColor));
        defaultDrawable = BitmapFactory.decodeResource(getResources(), R.drawable.ic_default_img);
        successDrawable = BitmapFactory.decodeResource(getResources(), R.drawable.ic_success_img);
        failedDrawable = BitmapFactory.decodeResource(getResources(), R.drawable.ic_fail_img);

        imageView.setImageBitmap(defaultDrawable);
        editText.setHint(hint);
        LayoutParams editTextParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);
        int margin = pxIntoDp(16);

        editText.setLayoutParams(editTextParams);
        LayoutParams inputLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1);
        inputLayoutParams.setMargins(margin, 0, margin / 2, 0);
        inputLayout.setLayoutParams(inputLayoutParams);
        inputLayout.addView(editText);
        LayoutParams imageViewParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        imageViewParams.gravity = Gravity.CENTER;
        imageViewParams.setMargins(margin / 2, 0, margin, 0);
        imageView.setLayoutParams(imageViewParams);
        addView(inputLayout);
        addView(imageView);
        setOnIndicatorClickListener(null);
    }

    public void setDefaultIndicatorBitmap(Bitmap bitmap) {
        defaultDrawable = bitmap;
        imageView.setImageBitmap(defaultDrawable);
    }

    public void setDefaultIndicatorBitmap() {
        imageView.setImageBitmap(defaultDrawable);
        isValid = false;
    }

    public void setEmpty() {
        editText.setText("");
        setDefaultIndicatorBitmap();
    }

    private void animateIndicator(final boolean isValid) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageView, "scaleX", 0.5F, 0.25F, 1F);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageView, "scaleY", 0.5F, 0.25F, 1F);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(imageView, "alpha", 0.5F, 0.25F, 1F);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(200);
        set.playTogether(scaleX, scaleY, alpha);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                imageView.setImageBitmap(!isValid ? failedDrawable : successDrawable);
                inputLayout.setError(isValid ? null : errorMessage);
                animationStarted = true;
                if (!isValid && showDefaultIndicator) {
                    imageView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int width = imageView.getWidth() / 2;
                            ObjectAnimator translationX = ObjectAnimator.ofFloat(imageView, "translationX", 0, width, 0, -width, 0);
                            translationX.setInterpolator(new OvershootInterpolator(2));
                            translationX.setDuration(200);
                            translationX.start();
                            imageView.setImageBitmap(defaultDrawable);
                        }
                    }, 2000);
                }
            }

        });
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.start();

    }

    public void setShowDefaultIndicator(boolean showDefaultIndicator) {
        this.showDefaultIndicator = showDefaultIndicator;
        requestFocus();
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
        if (!isValid) {
            inputLayout.setError(errorMessage);
        } else {
            inputLayout.setError(null);
        }
        animateIndicator(isValid);

    }

    public boolean isValid() {
        return isValid;
    }

    public void addTextChangeListener(TextWatcher watcher) {
        editText.addTextChangedListener(watcher);
    }

    public void setError(String error) {
        errorMessage = error;
        if (error != null)
            setIsValid(false);
    }

    public void setOnIndicatorClickListener(OnClickListener l) {
        imageView.setOnClickListener(l);
    }

    public ImageView getIndicatorImageView() {
        return imageView;
    }

    public String getText() {
        return editText.getText().toString().trim();
    }

    public void setText(String s) {
        editText.setText(s);
    }

    public void setOnFocusChangedListener(OnFocusChangeListener l) {
        editText.setOnFocusChangeListener(l);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        // super.setOnClickListener(l);
        Log.i(TAG, "setOnClickListener: ");
        editText.setOnClickListener(l);
    }


    public TextInputEditText getEditText() {
        return editText;
    }

    public void setCompoundDrawablesWithIntrinsicBounds(@Nullable Drawable left,
                                                        @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
        if (left != null) {
            left.setBounds(0, 0, left.getIntrinsicWidth(), left.getIntrinsicHeight());
        }
        if (right != null) {
            right.setBounds(0, 0, right.getIntrinsicWidth(), right.getIntrinsicHeight());
        }
        if (top != null) {
            top.setBounds(0, 0, top.getIntrinsicWidth(), top.getIntrinsicHeight());
        }
        if (bottom != null) {
            bottom.setBounds(0, 0, bottom.getIntrinsicWidth(), bottom.getIntrinsicHeight());
        }
        editText.setCompoundDrawablePadding(pxIntoDp(16));
        editText.setCompoundDrawables(left, top, right, bottom);
    }

    public void setHint(String s) {
        inputLayout.setHint(s);
    }

    public static int pxIntoDp(int px) {
        return (int) (px * Resources.getSystem().getDisplayMetrics().density);
    }

}
