package com.example.bakingapp;

import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.Nullable;
import androidx.test.espresso.IdlingResource;

public class SimpleIdlingResource implements IdlingResource {
    @Nullable
    private volatile IdlingResource.ResourceCallback mCallback;

    private AtomicBoolean mIsIdleNow = new AtomicBoolean(true);

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        return mIsIdleNow.get();
    }

    @Override
    public void registerIdleTransitionCallback(IdlingResource.ResourceCallback callback) {
        mCallback = callback;
    }


    public void setIdleState(boolean isIdleNow) {
        mIsIdleNow.set(isIdleNow);
        if (isIdleNow && mCallback != null) {
            mCallback.onTransitionToIdle();
        }
    }
}