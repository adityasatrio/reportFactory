/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jasper.report.reportfactory;

import com.csl.cms.product.CMSConstant;
import com.csl.cms.product.entity.Organization;
import com.csl.cms.product.service.OrganizationService;
import java.io.Serializable;
import java.util.HashMap;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.web.context.ContextLoader;

/**
 *
 * @author AdityaSatrioNugroho
 */
public class ReportTools implements Serializable {

    private String defaultReportPath = CMSConstant.DEFAULT_REPORT_PATH;
    protected static final String JASPER = ".jasper";
    protected static final String JRXML = ".jrxml";

    protected JasperPrint getJasperPrint(String pathReport, JRBeanCollectionDataSource dataSource, boolean isJrxml) throws JRException {
        JasperPrint jasperPrint;
        if (isJrxml) {
            JasperReport jasperReport = createJasper(pathReport);
            jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<String, Object>(), dataSource);
        } else {
            String reportPath = getReportPath(pathReport);
            jasperPrint = JasperFillManager.fillReport(reportPath, new HashMap<String, Object>(), dataSource);
        }

        return jasperPrint;
    }

    protected JasperReport createJasper(String reportName) throws JRException {
        String reportPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath(defaultReportPath + reportName);
        JasperReport jasperReport = JasperCompileManager.compileReport(reportPath);
        return jasperReport;
    }

    protected String getReportPath(String reportName) {
        String reportPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath(defaultReportPath + reportName);
        return reportPath;
    }

}
