package se.aigr20.tidrapport.fx.navigation.trait;

import se.aigr20.tidrapport.fx.report.ReportService;

public non-sealed interface CreatesReports extends ControllerTrait {
  ReportService getReportService();

  void setReportService(final ReportService reportService);
}
