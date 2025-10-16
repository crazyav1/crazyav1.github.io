package com.electricity.viz;

import tech.tablesaw.api.*;
import tech.tablesaw.api.Table;

public class Impute {
    public static long missingCount(Table df, String col) {
        return df.column(col).asList().stream().filter(o -> {
            if (o == null) return true;
            if (o instanceof Number) {
                ((Number) o).doubleValue();
            }
            return false;
        }).count();
    }

    public static DoubleColumn univariateImpute(DoubleColumn col) {
        DoubleColumn out = DoubleColumn.create(col.name(), col.size());
        // forward fill
        Double last = null;
        for (int i = 0; i < col.size(); i++) {
            double v = col.getDouble(i);
            if (!Double.isNaN(v)) { last = v; }
            out.set(i, last != null ? last : Double.NaN);
        }
        // backward fill for leading NaNs
        Double next = null;
        for (int i = col.size()-1; i >= 0; i--) {
            double v = out.getDouble(i);
            if (!Double.isNaN(v)) { next = v; }
            else if (next != null) { out.set(i, next); }
        }
        // simple rolling median (window 3)
        DoubleColumn smoothed = DoubleColumn.create(col.name(), col.size());
        for (int i = 0; i < col.size(); i++) {
            int a = Math.max(0, i-1), b = Math.min(col.size()-1, i+1);
            java.util.List<Double> w = new java.util.ArrayList<>();
            for (int j=a; j<=b; j++) { double vv = out.getDouble(j); if (!Double.isNaN(vv)) w.add(vv); }
            w.sort(Double::compareTo);
            smoothed.set(i, w.get(w.size()/2));
        }
        return smoothed;
    }


}