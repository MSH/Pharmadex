package org.msh.pharmadex.mbean.product;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.msh.pharmadex.domain.*;

import org.msh.pharmadex.service.ExportService;
import org.msh.pharmadex.service.LicenseHolderService;
import org.springframework.beans.factory.annotation.Autowired;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * Created by wing on 23.03.2016.
 */


@ManagedBean
@ViewScoped

public class ExportMBean implements Serializable {
    private String filename;
    public static Workbook wb;
    private static java.util.regex.Pattern numeric = java.util.regex.Pattern.compile("\\d*");
    private static int success=0;
    private static int failure=0;
    private static int total=0;

    @ManagedProperty(value = "#{exportService}")
    private ExportService exportService ;

    public void setExportService(ExportService exportService){
     this.exportService=exportService;
 }

    public ExportService getExportService() {
        return exportService;
    }


    @ManagedProperty(value = "#{licenseHolderService}")
    private LicenseHolderService licenseHolderService;

    public LicenseHolderService getLicenseHolderService() {
        return licenseHolderService;
    }

    public void setLicenseHolderService(LicenseHolderService licenseHolderService) {
        this.licenseHolderService = licenseHolderService;
    }



    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }


    public String startExport(){
        FacesContext context = FacesContext.getCurrentInstance();
        if (filename==null) return "";
        File f =new File(filename) ;
        if (f.isFile()) try {
            wb = WorkbookFactory.create(new FileInputStream(filename));
            Sheet sheet = wb.getSheetAt(0);
            if (sheet == null) return "";
            boolean res;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                res = exportService.importRow(sheet.getRow(i));
                if (res) success++;
                else failure++;
            }
            File outf = new File("C:/Temp/res.xlsx");
            FileOutputStream out = new FileOutputStream(outf);
            wb.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return "";
    }



    /*private void importRow(Row row) {

        Product prod=new Product();
        LicenseHolder lic=new LicenseHolder();
        Applicant a=new Applicant();
        ProdApplications pa=new ProdApplications();
        Address addr=new Address();

        Cell cell = row.getCell(0);   //Presentation -
        if (cell!=null) prod.setPackSize(cell.getStringCellValue());

        cell = row.getCell(1);  //Route Of Admin  catalog   in table adminroute
        if (cell!=null)
         prod.setAdminRoute(exportService.getAdminRouteAcc(cell.getStringCellValue()));
        cell = row.getCell(2);  //Therapeutic Group    catalog  in table adminroute
        if (cell!=null)
        //temporary    prod.setAtcs(exportService.getAtcAcc(cell.getStringCellValue()));
        cell = row.getCell(3);  //Indication   catalog in table pharmSlassif
        if (cell!=null)
         prod.setPharmClassif(exportService.getpharmSlassifAcc(cell.getStringCellValue()));
        //col 4 classification is empty
        cell = row.getCell(5);//Brand Name
        if (cell!=null) prod.setProdName(cell.getStringCellValue());
        cell = row.getCell(6);  //Generic Name
        if (cell!=null)  prod.setGenName(cell.getStringCellValue());
        cell = row.getCell(7);  //Dose strength
        if (cell!=null)  prod.setDosStrength(cell.getStringCellValue());
        cell = row.getCell(8);  //Dosage Form
          prod.setDosForm(exportService.getDosFormAcc(cell.getStringCellValue()));
        cell = row.getCell(9);//prod_desc
        if (cell!=null)  prod.setDosForm(exportService.getDosFormAcc(cell.getStringCellValue()));
        cell = row.getCell(10);  //shelf_life(Months)
        if (cell!=null)prod.setShelfLife(cell.getStringCellValue());
        cell = row.getCell(11);  //Licence Holder/manufacturer
        if (cell!=null) lic.setName(cell.getStringCellValue());
        cell = row.getCell(12); //Local Agent
        if (cell!=null) lic.setFirstAgent(cell.getStringCellValue());

         cell = row.getCell(13); //Registration Date
       //if (cell!=null)pa.setRegistrationDate(cell.getDateCellValue());
        cell = row.getCell(14); //RExpiry Date
        //if (cell!=null) pa.setRegExpiryDate(cell.getDateCellValue());
         cell = row.getCell(15);//Manufacturer/Actual site/
        if (cell!=null) a.setAppName(cell.getStringCellValue());
        cell = row.getCell(16); //Country of Origin
        if (cell!=null) {

            addr.setCountry(exportService.getCountry(cell.getStringCellValue()));
            lic.setAddress(addr);
        }


    }*/


}
