package com.jaorcas.fightnet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jaorcas.fightnet.utils.fragments.ChatFragment;
import com.jaorcas.fightnet.utils.fragments.FiltersFragment;
import com.jaorcas.fightnet.utils.fragments.HomeFragment;
import com.jaorcas.fightnet.utils.fragments.ProfileFragment;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        openFragment(new HomeFragment());

    }


    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.itemHome:
                            openFragment(HomeFragment.newInstance("", ""));
                            return true;
                        case R.id.itemProfile:
                            openFragment(ProfileFragment.newInstance("", ""));
                            return true;
                        case R.id.itemFilters:
                            openFragment(FiltersFragment.newInstance("", ""));
                            return true;
                    }
                    return true;
                }
            };



    //ESTO ES PARA CERRAR LA APLICACION CUANDO PULSAMOS HACIA ATRÁS
    //PENDIENTE DE PRUEBAS
    /*
    @Override
    public void onBackPressed() {
        finishAffinity();

    }
*/
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}