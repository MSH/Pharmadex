package org.msh.pharmadex.service;

import org.apache.poi.ss.usermodel.*;


import org.msh.pharmadex.dao.CountryDAO;
import org.msh.pharmadex.dao.CustomLicHolderDAO;
import org.msh.pharmadex.dao.CustomDictionaryDAO;
import org.msh.pharmadex.dao.ProdApplicationsDAO;
import org.msh.pharmadex.domain.*;

import org.msh.pharmadex.utils.ExcelTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;

/**
 * Created by wing on 24.03.2016.
 */
//@Service ("exportService")
@Service
public class ExportService implements Serializable {
    @Autowired
    ProdApplicationsDAO prodApplicationsDAO;

    @Autowired
    CountryDAO countryDAO;

    @Autowired
    private CustomLicHolderDAO customLicHolderDAO;

    //@ManagedProperty (value = "#{dosageFormService}")
    @Autowired
    CustomDictionaryDAO dictionaryDAO;
    //private DosageFormService dosageFormService;


    /*public DosageFormService getDosageFormService() {
        return dosageFormService;
    }

    public void setDosageFormService(DosageFormService dosageFormService) {
        this.dosageFormService = dosageFormService;
    }*/

    Row currrow;
    private  boolean errorDetected;

    public boolean importRow(Row row) {
        int rowNo=0;
        Cell cell = null;
        try {
            currrow = row;
            rowNo++;
            errorDetected = false;
            Product prod = new Product();
            LicenseHolder lic = new LicenseHolder();
            Applicant a = new Applicant();
            ProdApplications pa = new ProdApplications();
            Address addr = new Address();

            cell = row.getCell(0);   //Presentation -
            if (cell != null) prod.setPackSize(cell.getStringCellValue());

            cell = row.getCell(1);  //Route Of Admin  catalog   in table adminroute
            if (cell != null)
                prod.setAdminRoute(getAdminRouteAcc(cell.getStringCellValue()));
            cell = row.getCell(2);  //Therapeutic Group    catalog  in table adminroute
            if (cell != null)
                prod.setAtcs(getAtcList(cell.getStringCellValue()));
            cell = row.getCell(3);  //Indication   catalog in table pharmSlassif
            if (cell != null)
                prod.setPharmClassif(getpharmSlassifAcc(cell.getStringCellValue()));
            //col 4 classification is empty
            cell = row.getCell(5);//Brand Name
            if (cell != null) prod.setProdName(cell.getStringCellValue());
            cell = row.getCell(6);  //Generic Name
            if (cell != null) prod.setGenName(cell.getStringCellValue());
            cell = row.getCell(7);  //Dose strength
            if (cell != null) prod.setDosStrength(cell.getStringCellValue());
            cell = row.getCell(8);  //Dosage Form
            prod.setDosForm(getDosFormAcc(cell.getStringCellValue()));
            cell = row.getCell(9);//prod_desc
            if (cell != null) prod.setDosForm(getDosFormAcc(cell.getStringCellValue()));
            cell = row.getCell(10);  //shelf_life(Months)
            if (cell != null) prod.setShelfLife(cell.getStringCellValue());
            cell = row.getCell(11);  //Licence Holder/manufacturer
            if (cell != null) lic = findLicHolderByName(cell.getStringCellValue());
            cell = row.getCell(12); //Local Agent
            if (cell != null) lic.setFirstAgent(cell.getStringCellValue());

            cell = row.getCell(13); //Registration Date
            //if (cell!=null)pa.setRegistrationDate(cell.getDateCellValue());
            cell = row.getCell(14); //RExpiry Date
            //if (cell!=null) pa.setRegExpiryDate(cell.getDateCellValue());
            cell = row.getCell(15);//Manufacturer/Actual site/
            if (cell != null) a.setAppName(cell.getStringCellValue());
            cell = row.getCell(16); //Country of Origin
            if (cell != null) {

                addr.setCountry(getCountry(cell.getStringCellValue()));
                lic.setAddress(addr);
            }
            if (errorDetected) return false;
            return addToDatabase(prod, lic, a, pa, addr);
        }catch (Exception e){
            String colNo="";
            if (cell!=null){
                colNo = String.valueOf(cell.getColumnIndex());
            }
            System.out.println(String.valueOf(rowNo)+" " + colNo + " " + e.getMessage());
            return false;
        }
    }

    public boolean addToDatabase(Product prod, LicenseHolder lic, Applicant a, ProdApplications pa, Address addr) {
        return true;
    }

    // /check dictionary methods
    public LicenseHolder findLicHolderByName(String s){
        LicenseHolder  r=customLicHolderDAO.findLicHolderByName(s);

        if (r!=null)return r;
        r=new LicenseHolder();
        r.setName(s);
        //ExcelTools.setCellBackground(currrow.getCell(16), IndexedColors.GREY_25_PERCENT.getIndex());

        return r;
    }
    public DosageForm getDosFormAcc(String s) {
        DosageForm  r=dictionaryDAO.findDosFormByName(s);
        if (r!=null)return r;
        ExcelTools.setCellBackground(currrow.getCell(9), IndexedColors.GREY_25_PERCENT.getIndex());
        errorDetected=true;
        return r;
    }

    public AdminRoute getAdminRouteAcc(String s) {
        AdminRoute r = dictionaryDAO.findAdminRouteByName(s);
        if (r!=null)return r;
        ExcelTools.setCellBackground(currrow.getCell(2), IndexedColors.GREY_25_PERCENT.getIndex());
        errorDetected=true;
        return r;
    }

    public List<Atc> getAtcList(String s) {
        List<Atc> res=new ArrayList<Atc>();
        String[]allnames= new String[10];
        allnames=s.split(", ",10);
        for(int i=0;i<= allnames.length;i=i+2) {
            if (!allnames[i].equalsIgnoreCase("")) {
                Atc atc = dictionaryDAO.findAtsbyCode(allnames[i]);
                if (atc != null) res.add(atc);
                else {
                    ExcelTools.setCellBackground(currrow.getCell(2), IndexedColors.GREY_25_PERCENT.getIndex());
                    errorDetected = true;
                    return null;
                }
            }
        }
        return res;
    }

    public PharmClassif getpharmSlassifAcc(String s) {
        PharmClassif r = dictionaryDAO.findPharmSlassifByName(s);
        if (r!=null)return r;
        ExcelTools.setCellBackground(currrow.getCell(3), IndexedColors.GREY_25_PERCENT.getIndex());
        errorDetected=true;
        return r;
    }




    public Country getCountry(String s) {
        Country r=countryDAO.findCountryByName(s);
        if (r!=null)return r;
            ExcelTools.setCellBackground(currrow.getCell(16), IndexedColors.GREY_25_PERCENT.getIndex());
       errorDetected=true;
        return r;
    }


/*    public static void errorToLog(Workbook wb, ErrorCell errorCell){
        if (errorCell==null) return;
        RequestService.logEvent("Імпорт", errorCell.getErrorMsg(), author);
        try {
            CreationHelper factory = ImportXL.wb.getCreationHelper();
            ClientAnchor anchor = factory.createClientAnchor();
            //CellReference cr = new CellReference(errorCell.getCellName());
            Row row = wb.getSheet(errorCell.getTabName()).getRow(errorCell.getRowNo());
            Cell cell = row.getCell(errorCell.getColumnNo());
            if (cell==null){
                cell = row.createCell(errorCell.getColumnNo());
                cell.setCellValue(" ");
            }
            anchor.setAnchorType(1);
            anchor.setCol1(cell.getColumnIndex());
            anchor.setCol2(cell.getColumnIndex()+1);
            anchor.setRow1(row.getRowNum()-1);
            anchor.setRow2(row.getRowNum()+3);
            Drawing drawing = cell.getSheet().createDrawingPatriarch();
            Comment comment = cell.getCellComment();
            if (comment==null)
                comment = drawing.createCellComment(anchor);
            RichTextString str = comment.getString();
            if (str==null)
                str = factory.createRichTextString(errorCell.getErrorMsg());
            else{
                if (!Tools.isEmptyStr(str.getString())){
                    String addStr = str.getString() + ";" + errorCell.getErrorMsg();
                    str = factory.createRichTextString(addStr);
                }else
                    str = factory.createRichTextString(errorCell.getErrorMsg());
            }
            comment.setVisible(Boolean.FALSE);
            comment.setString(str);
            ExcelTools.setCellBackground(cell, IndexedColors.GREY_25_PERCENT.getIndex());
            cell.setCellComment(comment);
            //запишем файл с изменениями в специальную папку
            String outFileName=getOutFileName();
            FileOutputStream out = new FileOutputStream(outFileName);
            wb.write(out);
            out.close();
        } catch (FileNotFoundException e) {
            RequestService.logEvent("Помилка", "Неможливо відкрити електронну таблицю для аналізу помилок", author);
        } catch (IOException e) {
            RequestService.logEvent("Помилка", "Неможливо записати файл з аналізом помилок", author);		}
        catch (IllegalStateException e){
            // nothing to do - попытка вставить второй комментарий
        } catch (IllegalArgumentException e){
            // nothing to do  - попытка вставить второй комментарий
        }
    }

    public static void errorsToLog(Workbook wb, List<ErrorCell> errorCells, Clerk author){
        if (errorCells.size()==0) return;
        //записали в лог базы
        for(int i=0;i<errorCells.size();i++){
            ErrorCell ec = errorCells.get(i);
            RequestService.logEvent("Імпорт", ec.getErrorMsg(), author);
        }
        //теперь делаем отметки в файле Excel (в местах ошибок)
        for(int i=0;i<errorCells.size();i++){
            errorToLog(wb, errorCells.get(i), author);
        }
    }*/
}