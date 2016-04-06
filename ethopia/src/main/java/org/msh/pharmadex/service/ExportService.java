package org.msh.pharmadex.service;

import org.apache.poi.ss.usermodel.*;


import org.msh.pharmadex.dao.*;
import org.msh.pharmadex.dao.iface.ProdCompanyDAO;
import org.msh.pharmadex.domain.*;

import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.utils.ExcelTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    @Autowired
    private ProdCompanyDAO prodCompanyDAO;

    //@ManagedProperty (value = "#{dosageFormService}")
    @Autowired
    CustomDictionaryDAO dictionaryDAO;
    @Autowired
    ProductDAO productDAO;

    private Row currrow;
    private  boolean errorDetected;

    public boolean importRow(Row row) {
      errorDetected=false;
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
            Company co=new Company();
            cell = row.getCell(0);   //Presentation -   prod description
            if (cell != null) prod.setProdDesc(cell.getStringCellValue());

            cell = row.getCell(1);  //Route Of Admin  catalog   in table adminroute
            if (cell != null)
                prod.setAdminRoute(fingAdminRouteAcc(cell.getStringCellValue()));
            cell = row.getCell(2);  //Therapeutic Group    catalog  in table adminroute
            if (cell != null)
                prod.setAtcs(findAtcList(cell.getStringCellValue()));
            cell = row.getCell(3);  //Indication   catalog in table pharmSlassif
            if (cell != null)
                prod.setIndications(cell.getStringCellValue());//prod.setPharmClassif(findpharmSlassifAcc(cell.getStringCellValue()));
            //col 4 classification is empty
            cell = row.getCell(5);//Brand Name
            if (cell != null) prod.setProdName(cell.getStringCellValue());
            cell = row.getCell(6);  //Generic Name
            if (cell != null) prod.setGenName(cell.getStringCellValue());
            cell = row.getCell(7);  //Dose strength
            if (cell != null) {
                prod.setDosStrength(cutNumberPart(cell.getStringCellValue()));
                prod.setDosUnit(findDocUnit(cell.getStringCellValue()));
            }
            cell = row.getCell(8);  //Dosage Form
            prod.setDosForm(findDosFormAcc(cell.getStringCellValue()));
            cell = row.getCell(9);//prod_desc -  conttype
            if (cell != null) prod.setContType(cell.getStringCellValue());
            cell = row.getCell(10);  //shelf_life(Months)
            if (cell != null) prod.setShelfLife(cell.getStringCellValue());
            cell = row.getCell(11);  //Licence Holder/manufacturer
            if (cell != null) lic = findLicHolderByName(cell.getStringCellValue());
            //if (cell != null) co=FindCompany(cell.getStringCellValue());
            cell = row.getCell(12); //Local Agent
            //if (cell != null) lic.setFirstAgent(cell.getStringCellValue());
            if (cell != null) a= findApplicant(cell.getStringCellValue());
            cell = row.getCell(13);//date
           if (cell!=null)pa.setRegistrationDate(getDateValue(cell));

            cell = row.getCell(14); //RExpiry Date
      if (cell!=null) pa.setRegistrationDate(getDateValue(cell));
            cell = row.getCell(15);//Manufacturer/Actual site/
            if (cell != null) {
                String[]all= new String[3];
                all=cell.getStringCellValue().split(", ",2);
                co=FindCompany(all[0]);
                if (all.length>1) addr.setAddress1(all[all.length-1]);

            }
            cell = row.getCell(16); //Country of Origin
            if (cell != null)     addr.setCountry(findCountry(cell.getStringCellValue()));
            if (errorDetected) return false;
            co.setAddress(addr);
            Product oldprod=findExistingProd(prod);
            if(oldprod!=null)
                prod=oldprod;
            return addToDatabase(prod, co, a, pa, lic);
        }catch (Exception e){
            String colNo="";
            if (cell!=null){
                colNo = String.valueOf(cell.getColumnIndex());
            }
            System.out.println(String.valueOf(rowNo)+" " + colNo + " " + e.getMessage());
            return false;
        }
    }


    public boolean addToDatabase(Product prod, Company co, Applicant a, ProdApplications pa, LicenseHolder lic) {

        try{

            pa.setApplicant(a);
            pa.setProduct(prod);
            pa.setRegState(RegState.REGISTERED);
            //set manufacrurer
            List<ProdCompany> comlist=new ArrayList<ProdCompany>();
            ProdCompany com=new ProdCompany();
            com.setProduct(prod);
            com.setCompany(co);
            comlist.add(com);
      //List<ProdApplications>
           List<Product> old=lic.getProducts();
           if (old==null) old=new ArrayList<Product>();
                old.add(prod);
            lic.setProducts(old);
            String s = prodApplicationsDAO.saveApplication(pa);

    }  catch (Exception ex) {
            return false;
        }

        return true;
    }

    private Product findExistingProd(Product prod) {
        //TODO check by name, docform and dosagepl
        Product p=null;
        ProductFilter filter=new ProductFilter();
        filter.setProdName(prod.getProdName());

        HashMap<String, Object> params = filter.getFilters();
        List<Product> pl=productDAO.findProductByFilter(params);
        if (pl==null) return p;
        if (pl.size()==0) return p;
        for (int i=0;i<pl.size();i++){
            p=pl.get(i);
            if(p.getDosForm()==prod.getDosForm() & p.getDosUnit()==prod.getDosUnit()& p.getDosStrength()==prod.getDosStrength()) return p;
        }
        return p;
    }
    private String cutNumberPart(String s) {
        String res="";
        if (s==null)return res;
        s=s.trim();
        for (int i=0;i<s.length();i++){
          if(  Character.isDigit(s.charAt(i)))
              res=res+s.charAt(i);
            else return res;
        }
        return res;
    }
    private DosUom findDocUnit(String s) {
        String res="";
        int ind=0;
        if (s == null) return null;
        s=s.trim();
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                ind = i;
                break;
            }

        }
        res=s.substring(ind);
        res=res.trim();
        DosUom  r=dictionaryDAO.findDosUomByName(res);
        if (r!=null)return r;
        ExcelTools.setCellBackground(currrow.getCell(7), IndexedColors.GREY_25_PERCENT.getIndex());
        errorDetected=true;
        return r;
    }

    public Company FindCompany(String s){
        Company r= dictionaryDAO.findCompanyByName(s);
        if (r!=null)return r;
        r=new Company();
        r.setCompanyName(s);
        return r;
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
    public DosageForm findDosFormAcc(String s) {
        DosageForm  r=dictionaryDAO.findDosFormByName(s);
        if (r!=null)return r;
        ExcelTools.setCellBackground(currrow.getCell(8), IndexedColors.GREY_25_PERCENT.getIndex());
        errorDetected=true;
        return r;
    }

    public AdminRoute fingAdminRouteAcc(String s) {
        AdminRoute r = dictionaryDAO.findAdminRouteByName(s);
        if (r!=null)return r;
        ExcelTools.setCellBackground(currrow.getCell(2), IndexedColors.GREY_25_PERCENT.getIndex());
        errorDetected=true;
        return r;
    }

    public List<Atc> findAtcList(String s) {
        List<Atc> res=new ArrayList<Atc>();
        String[]allnames= new String[10];
        allnames=s.split(", ",10);
        for(int i=0;i<= allnames.length-1;i=i+2) {
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

    public PharmClassif findpharmSlassifAcc(String s) {
        PharmClassif r = dictionaryDAO.findPharmSlassifByName(s);
        if (r!=null)return r;
        ExcelTools.setCellBackground(currrow.getCell(3), IndexedColors.GREY_25_PERCENT.getIndex());
        errorDetected=true;
        return r;
    }




    public Country findCountry(String s) {
        Country r=countryDAO.findCountryByName(s);
        if (r!=null)return r;
            ExcelTools.setCellBackground(currrow.getCell(16), IndexedColors.GREY_25_PERCENT.getIndex());
       errorDetected=true;
        return r;
    }
Date getDateValue(Cell cell){
    Date dt=null;

    try {
        String s = cell.getStringCellValue().trim();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        dt = formatter.parse(s);
    }  catch (ParseException e) {
        ExcelTools.setCellBackground(cell, IndexedColors.GREY_25_PERCENT.getIndex());
        errorDetected=true;

    }
    return dt;
}
      public Applicant findApplicant(String s){
        Applicant a=new Applicant();
        a=dictionaryDAO.findApplicantByName(s);
        if (a==null) a.setAppName(s);
                  return a;
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