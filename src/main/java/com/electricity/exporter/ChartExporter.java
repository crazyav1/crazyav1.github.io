package com.electricity.exporter;

import com.electricity.config.OutputConfig;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.VectorGraphicsEncoder;
import org.knowm.xchart.internal.chartpart.Chart;

import java.nio.file.Files;

import static org.knowm.xchart.VectorGraphicsEncoder.VectorGraphicsFormat.SVG;

public final class ChartExporter {

  public static void saveSvg(Chart<?, ?> chart, OutputConfig out, String fileBase) {
    save(chart, out, fileBase, Format.SVG);
  }

  public static void savePng(Chart<?, ?> chart, OutputConfig out, String fileBase) {
    save(chart, out, fileBase, Format.PNG);
  }

  private static void save(Chart<?, ?> chart, OutputConfig out, String fileBase, Format fmt) {
    try {
      Files.createDirectories(out.dir());
      var path = out.dir().resolve(fileBase + fmt.ext);

      switch (fmt) {
        case SVG -> VectorGraphicsEncoder.saveVectorGraphic(chart, path.toString(), SVG);
        case PNG -> BitmapEncoder.saveBitmapWithDPI(chart, path.toString(), BitmapEncoder.BitmapFormat.PNG, out.dpi());
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to export chart: " + fileBase, e);
    }
  }

  private enum Format {
    SVG(".svg"), PNG(".png");
    private final String ext;
    Format(String ext) { this.ext = ext; }
  }

  private ChartExporter() {}
}
