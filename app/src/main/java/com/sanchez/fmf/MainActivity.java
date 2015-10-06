package com.sanchez.fmf;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.sanchez.fmf.event.PermissionResultEvent;
import com.sanchez.fmf.fragment.MainFragment;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar_main_activity)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        // color nav and status bar with app color
        Window w = getWindow();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            w.setNavigationBarColor(getResources().getColor(R.color.primary_dark));
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment mainFragment = fm.findFragmentById(R.id.container_main_activity);

        if (mainFragment == null) {
            mainFragment = MainFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.container_main_activity, mainFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
            case R.id.action_about:
                AppCompatDialogFragment frag = new AboutDialogFragment();
                frag.show(getSupportFragmentManager(), "dialog1");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MainFragment.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                Map<String, Integer> perms = new HashMap<>();
                // Initial state
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);

                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }

                if (perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    EventBus.getDefault().post(new PermissionResultEvent(true));
                } else {
                    EventBus.getDefault().post(new PermissionResultEvent(false));
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public static class AboutDialogFragment extends AppCompatDialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_about, null);

            TextView licenseText = (TextView) dialogView.findViewById(R.id.license);
            licenseText.setText(Html.fromHtml(
                    "<a href=\"https://github.com/dakotasanchez/farmers-market-finder/blob/master/README.md#license\">LICENSE</a>"
            ));
            licenseText.setMovementMethod(LinkMovementMethod.getInstance());

            TextView sourceText = (TextView) dialogView.findViewById(R.id.source);
            sourceText.setText(Html.fromHtml(
                    "<a href=\"https://github.com/dakotasanchez/farmers-market-finder\">SOURCE</a>"
            ));
            sourceText.setMovementMethod(LinkMovementMethod.getInstance());

            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.about)
                    .setView(dialogView)
                    .setPositiveButton(R.string.ok, null)
                    .create();
        }
    }
}
