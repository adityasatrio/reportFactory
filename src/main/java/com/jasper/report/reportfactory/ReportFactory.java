/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jasper.report.reportfactory;

import com.jasper.report.reportfactory.GeneratePdf;

/**
 * notes : the codes still need to be reviewed and to be enhance for
 * effectiveness
 *
 * the static method can be handled by spring, look spring references how to
 * handle it
 *
 * @author AdityaSatrioNugroho
 */
public class ReportFactory {

    private static final String PDF = "Pdf";
    private static final String XLSX = "Xlsx";

    public static ReportType getReportExtension(String reportExt) throws Exception {
        ReportType reportType = null;
        if (PDF.equalsIgnoreCase(reportExt)) {
            reportType = new GeneratePdf();
        } else if (XLSX.equalsIgnoreCase(reportExt)) {
            reportType = new GenerateXlsx();
        } else {
            throw new Exception("com.exception.report.unknown.type");
        }

        return reportType;
    }
}
