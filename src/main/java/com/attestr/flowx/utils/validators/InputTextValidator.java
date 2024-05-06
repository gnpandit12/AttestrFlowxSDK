package com.attestr.flowx.utils.validators;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Copyright (c) 2021 Pegadroid IQ Solutions Pvt. Ltd.
 * @Author Gaurav Naresh Pandit
 **/
public abstract class InputTextValidator implements TextWatcher {
    private final EditText mEditText;

    public InputTextValidator(EditText editText) {
        this.mEditText = editText;
    }

    public abstract void validate(EditText editText, CharSequence s, int start, int before, int count);

    @Override
    final public void afterTextChanged(Editable s) {

    }

    @Override
    final public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    final public void onTextChanged(CharSequence s, int start, int before, int count) {
        validate(mEditText, s, start, before, count);
    }
}
