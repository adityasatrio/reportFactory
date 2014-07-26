/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jasper.report.reportfactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author AdityaSatrioNugroho
 */
/**
 *
 * @author AdityaSatrioNugroho
 */
public class GeneratePdf extends ReportTools implements ReportType {

    private static final Logger logger = LoggerFactory.getLogger(GeneratePdf.class);

    /**
     * Generate PDF report
     *
     * @param reportName : report name *.jrxml or *.jasper </p><br/>
     * @param listOfMap : List of Map<key, value>
     * @param response: HttpServletResponse <br/>
     *
     * @throws JRException
     * @throws IOException
     */
    @Override
    public void generateSingleReport(String reportName, List<Map<String, Object>> listOfMap, String outputReportNm, HttpServletResponse response) {
        try {
            JasperPrint jasperPrint;
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listOfMap);

            jasperPrint = getJasperPrint(reportName, dataSource, false);

            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment;filename=" + outputReportNm + ".pdf");
            response.getOutputStream().write(pdfBytes);

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

        //validate if each parameter null
        String masterRptNm = (String) parameterMap.get("master_report");
        String subRptNm = ((String) parameterMap.get("sub_report"));
        String titleRpt = (String) parameterMap.get("title_report");
        String outputNm = (String) parameterMap.get("output_report");
        String company = (String) parameterMap.get("company_report");

        if (masterRptNm.toLowerCase().contains(JRXML) && subRptNm.toLowerCase().contains(JRXML)) {
            logger.info("EXECUTE SUB REPORT - JRXML");
            try {
                JasperReport masterJasper = createJasper(masterRptNm);
                JasperReport subJasper = createJasper(subRptNm);
                parameterMap.put("SUBREPORT_DIR", subJasper);
                parameterMap.put("RPT_DETAIL_VALUE", listOfMap);
                parameterMap.put("TITLE", titleRpt);
                parameterMap.put("COMPANY", company == null ? "COMPANY" : "DUTY FREE");//getOrganization());
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

        try {
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment;filename=" + outputNm + ".pdf");
            response.getOutputStream().write(pdfBytes);

        } catch (JRException ex) {
            logger.error(ex.getMessage(), ex.getCause());
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex.getCause());
        }
    }
}

