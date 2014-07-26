/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jasper.report.reportfactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author AdityaSatrioNugroho
 */
public class GenerateXlsx extends ReportTools implements ReportType {

    private static final Logger logger = LoggerFactory.getLogger(GenerateXlsx.class);

    /**
     * Generate report for XLSX <br/>
     *
     * @param reportName : report name (*.jrxml or *.jasper)
     * @param listOfMap : List Of Map<key, value> <br/>
     * @param outputReportNm : output report name <br/>
     * @param response: HttpServletResponse <br/>
     * @throws JRException
     */
    @Override
    public void generateSingleReport(String reportName, List<Map<String, Object>> listOfMap, String outputReportNm, HttpServletResponse response) {
        try {
            JRXlsxExporter xlsxExporter = new JRXlsxExporter();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listOfMap);
            JasperPrint jasperPrint;

            jasperPrint = getJasperPrint(reportName, dataSource, true);

            xlsxExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            xlsxExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outputReportNm);
            xlsxExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);

            xlsxExporter.exportReport();

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + outputReportNm + ".xlsx");
            response.getOutputStream().write(os.toByteArray());

        } catch (JRException ex) {
            logger.error(ex.getMessage(), ex.getCause());
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex.getCause());
        }

    }

    /**
     * @param parameterMap is mandatory, consist of : master_report, sub_report,
     * title_report, output_report
     */
    @Override
    public void generateMasterSubReport(HttpServletResponse response, Map<String, Object> parameterMap, List<Map<String, Object>> listOfMap) throws Exception {
        JasperPrint jasperPrint = null;
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listOfMap);
        JRXlsxExporter xlsxExporter = new JRXlsxExporter();
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        //validate if each parameter null
        String masterRptNm = (String) parameterMap.get("master_report");
        String subRptNm = ((String) parameterMap.get("sub_report"));
        String titleRpt = (String) parameterMap.get("title_report");
        String outputNm = (String) parameterMap.get("output_report");
        String company = (String) parameterMap.get("company_report");

        if (masterRptNm.toLowerCase().contains(JRXML) && subRptNm.toLowerCase().contains(JRXML)) {

            try {
                JasperReport masterJasper = createJasper(masterRptNm);
                JasperReport subJasper = createJasper(subRptNm);
                parameterMap.put("SUBREPORT_DIR", subJasper);
                parameterMap.put("RPT_DETAIL_VALUE", listOfMap);
                parameterMap.put("TITLE", titleRpt);
                parameterMap.put("COMPANY", company == null ? "COMPANY" : "DUTY FREE");//getOrganization()); 
                logger.info("parameterMap " + parameterMap);
                jasperPrint = JasperFillManager.fillReport(masterJasper, parameterMap, dataSource);

            } catch (JRException ex) {
                logger.error(ex.getMessage(), ex.getCause());
            }

        } else if (masterRptNm.toLowerCase().contains(JASPER) && subRptNm.toLowerCase().contains(JASPER)) {
            logger.info("EXECUTE SUB REPORT - JASPER");
            try {
                String masterRptPath = getReportPath(masterRptNm);
                String subRptPath = getReportPath(subRptNm);
                parameterMap.put("SUBREPORT_DIR", subRptPath);
                parameterMap.put("RPT_DETAIL_VALUE", listOfMap);
                parameterMap.put("TITLE", titleRpt);
                parameterMap.put("COMPANY", company == null ? "COMPANY REPORT" : "DUTY FREE REPORT"); //getfrom db
                jasperPrint = JasperFillManager.fillReport(masterRptPath, parameterMap, dataSource);

            } catch (JRException ex) {
                logger.error(ex.getMessage(), ex.getCause());
            }

        } else {
            throw new Exception("com.exception.report.master.sub.type");
        }

        xlsxExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        xlsxExporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outputNm);
        xlsxExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);

        try {
            xlsxExporter.exportReport();

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + outputNm + ".xlsx");
            response.getOutputStream().write(os.toByteArray());

        } catch (JRException ex) {
            logger.error(ex.getMessage(), ex.getCause());
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex.getCause());
        }
    }
}
