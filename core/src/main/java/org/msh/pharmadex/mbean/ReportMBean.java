package org.msh.pharmadex.mbean;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.msh.pharmadex.service.ProductService;
import org.msh.pharmadex.service.ReportService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

@ManagedBean
@ViewScoped
public class ReportMBean implements Serializable {

    @ManagedProperty(value = "#{reportService}")
    ReportService reportService;

    @ManagedProperty(value = "#{productService}")
    ProductService productService;


    JasperPrint jasperPrint;

    public void init() throws JRException {
////        JRBeanCollectionDataSource beanCollectionDataSource=new JRBeanCollectionDataSource(listOfUser);
//
//        Applicant app = new Applicant();
//        app.setAppName("Test Applicant");
//
//        URL resource = getClass().getResource("/reports/letter.jasper");
//        HashMap param = new HashMap();
//        param.put("prodId", new Long(1));
//        param.put("appName", "Testing ");
//
//
//
//
//        jasperPrint= JasperFillManager.fillReport(resource.getFile(),
//                param);
    }

    public void PDF(ActionEvent actionEvent) throws JRException, IOException {
        URL resource = getClass().getResource("/reports/letter.jasper");
        jasperPrint = reportService.reportinit(productService.findProduct(Long.valueOf(4772)));
//        init();
        HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        httpServletResponse.addHeader("Content-disposition", "attachment; filename=letter.pdf");
        httpServletResponse.setContentType("application/pdf");
        ServletOutputStream servletOutputStream = httpServletResponse.getOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, servletOutputStream);
        FacesContext.getCurrentInstance().responseComplete();


    }


    public ReportService getReportService() {
        return reportService;
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
}