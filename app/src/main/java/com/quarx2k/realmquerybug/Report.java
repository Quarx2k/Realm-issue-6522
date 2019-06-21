package com.quarx2k.realmquerybug;


import java.util.Objects;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Nickolay Semendyaev on 09.07.2017.
 */
public class Report extends RealmObject {
    @PrimaryKey
    private Integer guid;
    private RealmList<ReportValue> reportValues;

    public Report() {
    }

    public Integer getGuid() {
        return guid;
    }

    public void setGuid(Integer guid) {
        this.guid = guid;
    }

    public RealmList<ReportValue> getReportValues() {
        return reportValues;
    }

    public void setReportValues(RealmList<ReportValue> reportValues) {
        this.reportValues = reportValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Objects.equals(guid, report.guid) &&
                Objects.equals(reportValues, report.reportValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guid, reportValues);
    }
}
