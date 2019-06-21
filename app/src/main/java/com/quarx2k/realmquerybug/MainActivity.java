package com.quarx2k.realmquerybug;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;

public class MainActivity extends AppCompatActivity {
    private static String LOG_TAG = MainActivity.class.getSimpleName();
    private static int REPORTS_NUM = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initRealm();
        fillDatabase();
        checkReports();
        deleteReports();
        checkReports();
    }

    private void initRealm() {
        Realm.init(getApplicationContext());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .name("realm")
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(r -> r.deleteAll());
        }
    }

    private void fillDatabase() {
        try (Realm realm = Realm.getDefaultInstance()) {
            final List<Report> reportList = new ArrayList<>();
            for (int i = 0; i <= REPORTS_NUM; i++) {
                Report report = new Report();
                report.setReportValues(new RealmList<>());
                report.setGuid(i);
                for (int j = 0; j <= 10; j++) {
                    ReportValue reportValue = new ReportValue();
                    if (j < 5) {
                        reportValue.setCategory(2);
                    } else {
                        reportValue.setCategory(1);
                    }
                    reportValue.setId(j+i);
                    report.getReportValues().add(reportValue);
                }
                reportList.add(report);
            }
            realm.executeTransaction(r -> r.copyToRealm(reportList));
        }
    }

    private void checkReports() {
        try (Realm realm = Realm.getDefaultInstance()) {
            for (int i = 1; i <= REPORTS_NUM; i++) {
                Report report = realm.where(Report.class).equalTo("guid", i).findFirst();
                if (report != null) {
                    long rvSize = report.getReportValues().stream().filter(r -> r.getCategory() == 2).count();
                    Log.e(LOG_TAG, String.format("Report %s Values Size: %s ", report.getGuid(), rvSize));
                    long rvQuerySize = report.getReportValues().where().equalTo("category", 2).count();
                    Log.e(LOG_TAG, String.format("Report %s Values Query Size: %s ", report.getGuid(), rvQuerySize));
                    if (rvQuerySize != rvSize) {
                        Log.e(LOG_TAG, "The bug ;(");
                    }
                }
            }
        }
    }

    private void deleteReports() {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(r -> {
                for (int i = 1; i <= 50; i++) {
                    Report report = r.where(Report.class).equalTo("guid", i).findFirst();
                    if (report != null) {
                        report.getReportValues().deleteAllFromRealm();
                        report.deleteFromRealm();
                    }
                }
            });
        }
    }
}
