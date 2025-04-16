package com.example.heroicorganizer.ui.DevTesting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.heroicorganizer.R;
import com.example.heroicorganizer.model.User;
import com.example.heroicorganizer.presenter.LoginPresenter;
import com.example.heroicorganizer.presenter.LogoutPresenter;
import com.example.heroicorganizer.presenter.RegisterPresenter;

public class DevTestFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dev_test, container, false);

        view.findViewById(R.id.btnRegisterTest).setOnClickListener(v -> {
            User user = new User("Alvaro", "Torres", "1995-10-20", "test@tester.com", "simulint", "password123", "owner");
            RegisterPresenter.registerUser(user);
        });

        view.findViewById(R.id.btnLoginTest).setOnClickListener(v -> {
            User user = new User();
            user.setEmail("test@tester.com");
            user.setPassword("password123");
            LoginPresenter.loginUser(user);
        });

        view.findViewById(R.id.btnLogoutTest).setOnClickListener(v -> {
            LogoutPresenter.logoutUser();
        });

        return view;
    }
}
