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
    private int success=0;
    private  int failure=0;
    private  int total=0;

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

    public int getSuccess() {
        return success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public void setSuccess(int success) {
        this.success = success;


    }

    public String startExport(){
        FacesContext context = FacesContext.getCurrentInstance();
        if (filename==null) return "";
        File f =new File(filename) ;  //c:/temp/LEGACY DATA.xlsx
        if (f.isFile()) try {
            wb = WorkbookFactory.create(new FileInputStream(filename));
            Sheet sheet = wb.getSheetAt(0);
            if (sheet == null) return "";
            boolean res;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                res = exportService.importRow(sheet.getRow(i));
                if (res) {
                    success++;
                    CellStyle style = wb.createCellStyle();
                    style.setFillBackgroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
                    //style.setFillPattern(CellStyle.BIG_SPOTS);
                    sheet.getRow(i).setRowStyle(style);
                }
                else failure++;
            }
            File outf = new File("C:/Temp/res.xlsx");
            FileOutputStream out = new FileOutputStream(outf);
            wb.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            setFilename("file not found");
        }
        return "";
    }

}
