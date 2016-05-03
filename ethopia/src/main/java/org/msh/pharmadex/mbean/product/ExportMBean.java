package org.msh.pharmadex.mbean.product;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import org.msh.pharmadex.service.ExportService;
import org.msh.pharmadex.service.LicenseHolderService;
import org.msh.pharmadex.utils.ExcelTools;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by wing on 23.03.2016.
 */


@ManagedBean
@ViewScoped

public class ExportMBean implements Serializable {
    private String filename;
    public static Workbook wb;
    private File f;
    private static java.util.regex.Pattern numeric = java.util.regex.Pattern.compile("\\d*");
    private int success = 0;
    private int failure = 0;
    private int ignore = 0;
    private String taskStatus = "";

    public int getIgnore() {
        return ignore;
    }

    public void setIgnore(int ignore) {
        this.ignore = ignore;
    }


    @ManagedProperty(value = "#{exportService}")
    private ExportService exportService;

    public void setExportService(ExportService exportService) {
        this.exportService = exportService;
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

    private boolean initProcess() {
        FacesContext context = FacesContext.getCurrentInstance();
        setSuccess(0);
        setFailure(0);
        setIgnore(0);
        if (filename == null) return false;
        f = new File(filename);  //c:/temp/LEGACY DATA.xlsx
        if (f == null) return false;
        Path path = Paths.get(filename);
        return true;
    }

    public String startExport(boolean importData) {
        if (!initProcess()) setFilename("Initialisation failure");
        if (f.isFile()) try {
            wb = WorkbookFactory.create(new FileInputStream(filename));
            Sheet sheet = wb.getSheetAt(3);
            if (sheet == null) return "";
            boolean res;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                res = exportService.importRow(sheet.getRow(i), importData);
                if (res) {
                    success++;
                    ExcelTools.setCellBackground(sheet.getRow(i).getCell(3), IndexedColors.GREEN.getIndex());
                } else failure++;

            }
            setIgnore(success + failure);
            File outf = new File("C:/Temp/res.xlsx");
            FileOutputStream out = new FileOutputStream(outf);
            wb.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            setFilename("file not found");
        }
        else
            setFilename("file not found");
        return "";
    }

    private String loadingApplicants(Sheet sheet) {
        success = 0;
        failure = 0;
        String res;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            res = exportService.importApplicants(sheet.getRow(i));
            if (!res.startsWith("Error")) {
                success++;
                ExcelTools.setCellBackground(sheet.getRow(i).getCell(1), IndexedColors.GREEN.getIndex());
            } else
                failure++;
        }
        if (failure == 0)
            return "loading applicants:success";
        else
            return "loading applicants" + String.valueOf(failure) + " errors";
    }

    private String loadingLocalAgents(Sheet sheet) {
        String res;
        success = 0;
        failure = 0;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            res = exportService.createUpdateSimpleLocalAgent(sheet.getRow(i));
            if (!res.startsWith("Error")) {
                success++;
                ExcelTools.setCellBackground(sheet.getRow(i).getCell(1), IndexedColors.GREEN.getIndex());
            } else
                failure++;
        }
        if (failure == 0)
            return "loading applicants:success";
        else
            return "loading applicants - " + String.valueOf(failure) + " errors";
    }

    private String loadingLicenseHolders(Sheet sheet) {
        String res;
        success = 0;
        failure = 0;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            res = exportService.createUpdateLicenseHolder(sheet.getRow(i));
            if (!res.startsWith("Error")) {
                success++;
                ExcelTools.setCellBackground(sheet.getRow(i).getCell(1), IndexedColors.GREEN.getIndex());
            } else
                failure++;
        }
        if (failure == 0)
            return "license holders loading:success";
        else
            return "license holders loading - " + String.valueOf(failure) + " errors";
    }

    private void importUOMS() {
        String values = "%V/V;" +
                "%W/V;" +
                "%W/W;" +
                "mcg/0.5ML;" +
                "mcg/ML;" +
                "10ML;" +
                "5ML;" +
                "CCID50;" +
                "DOSE;" +
                "IU/5ML;" +
                "IU/MG;" +
                "IU/ML/IU;" +
                "MG/mcg;" +
                "MG/0.5ML;" +
                "MG/10ML;" +
                "MG/3ML;" +
                "MG/G;" +
                "MG/IU/IU/IU/MG/MG/MG/ug;" +
                "MG/IU/MG;" +
                "MG/MG/mcg;" +
                "MG/MG/U;" +
                "MG/ML;" +
                "MG/ML,%W/V;" +
                "MG/ug/MG/MG;" +
                "MMOL/ML;" +
                "MOL/ML;" +
                "MOSMOL/L;" +
                "OMEGA;" +
                "U/ML;" +
                "NA";
        String[] lstVal = values.split(";");
        for (String value : lstVal) {
            exportService.findDosUOM(value);
        }
    }

    private void importDosForms() {
        String values = "ENTERED COATED TABLET," +
                "POWDER FOR SUSPENSION," +
                "POWDER FOR INJECTION," +
                "POWDER FOR SOLUTION," +
                "FILM COATED TABLET," +
                "CHEWABLE TABLET," +
                "EYE DROP," +
                "EYE/EAR DROP," +
                "DROP," +
                "NAZAL DROPS," +
                "SOFT GELATIN CAPSULE," +
                "LARGE VOLUME INJECTION," +
                "PESSARIES," +
                "LOZENGES," +
                "DISPERSABLE TABLET," +
                "EFFERVESCENT TABLET," +
                "EYE OINTMENT," +
                "DELAYED RELEASE CAPSULE," +
                "GRANULES FOR SOLUTION," +
                "POWDER FOR INHALATION," +
                "LYOPHILIZED INJECTION," +
                "DELAYED RELEASE ENTERICOTED TABLET," +
                "LYOPHILIZED POWDER FOR INJECTION," +
                "STERILE WATER FOR INJECTION," +
                "SUSTAINED RELEASE FILM-COATED TABLET," +
                "IUD," +
                "SOLUTION FOR INFUSION," +
                "DRY SYRUP," +
                "SUSPENSION FOR INHALATION," +
                "ENTERIC COATED TABLET," +
                "PROLONGED RELEASE TABLET," +
                "GRANULE FOR INJECTION," +
                "INHALATION," +
                "POWDER FOR CONCENTRATE FOR INJECTION," +
                "NASAL SPRAY," +
                "DELAYED RELEASE ENTERIC COATED ";
        String[] lstVal = values.split(",");
        for (String value : lstVal) {
            exportService.findDosForm(value);
        }
    }

    public String loadingUsers(){
        if (!initProcess()) setFilename("Error. Initialisation failure");
        if (f.isFile())
            try {
                wb = WorkbookFactory.create(new FileInputStream(filename));
                Sheet sheet = wb.getSheetAt(0);
                if (sheet == null) return "";
                taskStatus=loadingApplicants(sheet);
                if (!taskStatus.endsWith("success")) return taskStatus;
                setIgnore(success+failure);
                File outf = new File("C:/Temp/res.xlsx");
                FileOutputStream out = new FileOutputStream(outf);
                wb.write(out);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidFormatException e) {
                setFilename("file not found");
            }
        else
            setFilename("file not found");

        return "";
    }

    public String loadingOrganisations(){
        if (!initProcess()) setFilename("Error. Initialisation failure");
        if (f.isFile())
            try {
                //importDosForms();
                //importUOMS();

                wb = WorkbookFactory.create(new FileInputStream(filename));
                Sheet sheet = wb.getSheetAt(1);
                if (sheet == null) return "";

                taskStatus=loadingLocalAgents(sheet);
                if (!taskStatus.endsWith("success")) return taskStatus;
                sheet = wb.getSheetAt(2);
                if (sheet == null) return "";
                taskStatus=loadingLicenseHolders(sheet);

                sheet = wb.getSheetAt(0);
                if (sheet == null) return "";
                taskStatus=loadingApplicants(sheet);
                if (!taskStatus.endsWith("success")) return taskStatus;
                setIgnore(success+failure);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidFormatException e) {
                setFilename("file not found");
            }
        else
            setFilename("file not found");

        return "";
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }


}
