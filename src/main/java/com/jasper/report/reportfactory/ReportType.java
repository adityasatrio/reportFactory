/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jasper.report.reportfactory;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author AdityaSatrioNugroho
 */
public interface ReportType {

    public void generateSingleReport(String reportName, List<Map<String, Object>> listOfMap, String outputReportNm, HttpServletResponse response);

    public void generateMasterSubReport(HttpServletResponse response, Map<String, Object> parameterMap, List<Map<String, Object>> listOfMap) throws Exception;
}
