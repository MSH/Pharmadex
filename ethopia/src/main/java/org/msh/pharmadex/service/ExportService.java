package org.msh.pharmadex.service;

import org.apache.poi.ss.usermodel.*;


import org.msh.pharmadex.dao.*;
import org.msh.pharmadex.dao.iface.*;
import org.msh.pharmadex.domain.*;

import org.msh.pharmadex.domain.enums.*;
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
    @Autowired
    CompanyDAO companyDAO;
    @Autowired
    LicenseHolderDAO licenseHolderDAO;
    //@Autowired
    //AgentInfoDAO agentInfoDAO;
    @Autowired
    ApplicantTypeDAO applicantTypeDAO;
    @Autowired
    ApplicantDAO applicantDAO;
    @Autowired
    UserDAO userDAO;
    private Row currrow;
    private  boolean errorDetected;
    ApplicantType at=null;
    User user=null;
private int lastCol=0;
    public boolean importRow(Row row, boolean importData) {
      errorDetected=false;
        int rowNo=0;
        int curCol=0;
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
            cell = row.getCell(curCol);   // A Presentation -   prod description
            if (cell.getCellType()==Cell.CELL_TYPE_NUMERIC) cell.setCellType(Cell.CELL_TYPE_STRING);

            if (cell != null) prod.setProdDesc(cell.getStringCellValue());
            curCol++;
            cell = row.getCell(curCol);  // B Route Of Admin  catalog   in table adminroute
            if (cell != null)

                prod.setIndications(cell.getStringCellValue());
            //prod.setAdminRoute(fingAdminRouteAcc(cell.getStringCellValue(),curCol));
            curCol++;
            cell = row.getCell(curCol);  //C Therapeutic Group    catalog  in table adminroute
            if (cell != null)
                prod.setAtcs(findAtcList(cell.getStringCellValue(),curCol));
            /*cell = row.getCell(3);  //Indication   catalog in table pharmSlassif
            if (cell != null)
                prod.setIndications(cell.getStringCellValue());//prod.setPharmClassif(findpharmSlassifAcc(cell.getStringCellValue()));
            //col 4 classification is empty
       */
            curCol++;
            cell = row.getCell(curCol);// D Brand Name
            if (cell != null) prod.setProdName(cell.getStringCellValue());
            curCol++;
            cell = row.getCell(curCol);  // E Generic Name
            if (cell != null) prod.setGenName(cell.getStringCellValue());
            curCol++;
            cell = row.getCell(curCol);  //F Dose strength
            if (cell != null) {
                if (cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                    prod.setDosStrength(String.valueOf(cell.getNumericCellValue()*100));
                    prod.setDosUnit(findDocUnit("%",curCol));
                }else {
                    prod.setDosStrength(cutNumberPart(cell.getStringCellValue()));
                    prod.setDosUnit(findDocUnit(cell.getStringCellValue(), curCol));
                    if (prod.getDosUnit()==null) prod.setDosStrength(cell.getStringCellValue());
                }
            }
            curCol++;
            cell = row.getCell(curCol);  //G Dosage Form
            prod.setDosForm(findDosFormAcc(cell.getStringCellValue(),curCol));
            curCol++;
            cell = row.getCell(curCol);//H prod_desc -  conttype
            if (cell != null) prod.setContType(cell.getStringCellValue());
            curCol++;
            cell = row.getCell(curCol);  //I shelf_life(Months)
            if (cell != null) prod.setShelfLife(cell.getStringCellValue());
            curCol++;
            cell = row.getCell(curCol);  //J Licence Holder/manufacturer
            if (cell != null) lic = findLicHolderByName(cell.getStringCellValue());
            //if (cell != null) co=FindCompany(cell.getStringCellValue());
            curCol++;
            cell = row.getCell(curCol); //K Local Agent
            if (cell != null) a= findApplicant(cell.getStringCellValue());
            curCol++;
            cell = row.getCell(curCol);//L date
           if (cell!=null)pa.setRegistrationDate(getDateValue(cell));
            curCol++;
            cell = row.getCell(curCol); //M Expiry Date
            if (cell!=null) pa.setRegExpiryDate(getDateValue(cell));
            curCol++;
            cell = row.getCell(curCol);//N Manufacturer/Actual site/
            if (cell != null) {
                String all=cell.getStringCellValue();
                int pos=all.indexOf(",");
                if (pos==-1) {
                    co=FindCompany(all);
                }else{
                    co=FindCompany(all.substring(0,pos));
                    addr.setAddress1(all.substring(pos+1));
                }
             }
            curCol++;
            cell = row.getCell(curCol); //Country of Origin
            if (cell != null)     addr.setCountry(findCountry(cell.getStringCellValue(),curCol));
            if (errorDetected) return false;
            if (addr.getAddress1()==null) addr.setAddress1(addr.getCountry().getCountryName());
            co.setAddress(addr);
            Product oldprod=findExistingProd(prod);
            if(oldprod!=null)  prod=oldprod;
            curCol++;
            lastCol=curCol;
            cell = row.getCell(curCol); //additional comment
            if (cell!=null) importData=false;
            if (importData)   return addToDatabase(prod, co, a, pa, lic);
                   else return true;
        }catch (Exception e){
            String colNo="";
            if (cell!=null)   colNo = String.valueOf(cell.getColumnIndex());
            System.out.println(String.valueOf(rowNo)+" " + colNo + " " + e.getMessage());
            return false;
        }
    }


    public boolean addToDatabase(Product prod, Company co, Applicant a, ProdApplications pa, LicenseHolder lic) {
        if(ifProdExist(prod,pa)==true){
            Cell t = currrow.createCell(lastCol);
            t.setCellValue("Exist");
            return false;
        }
        if (at==null) at=applicantTypeDAO.findOne((long) 2);  //set applicant type = importer
        if (user==null) user=userDAO.findUser((long) 1); //admin
        try{

            pa.setApplicant(a);
            pa.setProduct(prod);
            pa.setRegState(RegState.REGISTERED);
            pa.setActive(true);
            pa.setCreatedBy(user);
            if (prod.getAgeGroup()==null) prod.setAgeGroup(AgeGroup.BOTH);
            if(prod.getDrugType()==null)prod.setDrugType(ProdDrugType.PHARMACEUTICAL);
            if(prod.getProdCategory()==null) prod.setProdCategory(ProdCategory.HUMAN);
            if (prod.getCreatedBy()==null) prod.setCreatedBy(user);
            if (co.getCreatedBy()==null) co.setCreatedBy(user);
            co=companyDAO.save(co);
            if (a.getState()==null)a.setState(ApplicantState.REGISTERED);
            if (a.getApplicantType()==null) a.setApplicantType(at);
          //      a=applicantDAO.saveApplicant(a);
            //set manufacrurer
            List<ProdCompany> comlist=new ArrayList<ProdCompany>();
            if (prod.getProdCompanies()!=null)comlist=prod.getProdCompanies();
            ProdCompany com=new ProdCompany();
            com.setProduct(prod);
            com.setCompany(co);
            com.setCompanyType(CompanyType.FIN_PROD_MANUF);
            comlist.add(com);
            prod.setProdCompanies(comlist);
            String s = prodApplicationsDAO.saveApplication(pa);
            // arent_info
            List<AgentInfo> agInfoList=lic.getAgentInfos();
            if (agInfoList==null) agInfoList=new  ArrayList<AgentInfo>();
            AgentInfo ai=new AgentInfo();
            ai.setApplicant(a);
            ai.setLicenseHolder(lic);
            ai.setAgentType(AgentType.FIRST);
            ai.setStartDate(pa.getRegistrationDate());
            ai.setEndDate(pa.getRegExpiryDate());
            lic=licenseHolderDAO.save(lic);
            ai.setCreatedBy(user);
            agInfoList.add(ai);
            //agentInfoDAO.save(ai);
            lic.setAgentInfos(agInfoList);

            //if (cell != null) lic.setFirstAgent(cell.getStringCellValue());
           List<Product> old=lic.getProducts();
           if (old==null) old=new ArrayList<Product>();
                old.add(prod);
            lic.setProducts(old);
            if (lic.getAddress().getAddress1()==null)lic.setAddress(co.getAddress());
            if (lic.getCreatedBy()==null) lic.setCreatedBy(user);
            lic=licenseHolderDAO.save(lic);
            Cell t=currrow.createCell(lastCol);
            t.setCellValue("Done");
         }  catch (Exception ex) {
            return false;
        }

        return true;
    }

    private boolean ifProdExist(Product prod,ProdApplications pa ){
       if (prod.getId()==null)return false;
        List<ProdApplications> lpa=prod.getProdApplicationses();
        if (lpa!=null){
            for (ProdApplications item : lpa){
               if  (item.getRegExpiryDate()==null) return false;
                if( item.getRegExpiryDate().equals(pa.getRegExpiryDate())) return true;
            }
        }
        return false;

    }
    private Product findExistingProd(Product prod) {
        // check by name, docform and dosagepl
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
          if(  Character.isDigit(s.charAt(i)) )
              res=res+s.charAt(i);
            else return res;
        }
        return res;
    }
    private DosUom findDocUnit(String s, int col) {
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
        ExcelTools.setCellBackground(currrow.getCell(col), IndexedColors.GREY_25_PERCENT.getIndex());
        //errorDetected=true;
        return r;
    }

    public Company FindCompany(String s){
        s=s.trim();
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
    public DosageForm findDosFormAcc(String s, int col) {
        s=s.trim();
        DosageForm  r=dictionaryDAO.findDosFormByName(s);
        if (r!=null)return r;
        ExcelTools.setCellBackground(currrow.getCell(col), IndexedColors.GREY_25_PERCENT.getIndex());
        errorDetected=true;
        return r;
    }

    public AdminRoute fingAdminRouteAcc(String s,int col) {
        int pos=s.indexOf(",");
        if (pos==0){
            s=s.trim();
        }else{
           s=s.substring(pos+1).trim();
        }
        AdminRoute r = dictionaryDAO.findAdminRouteByName(s);
        if (r!=null)return r;
        ExcelTools.setCellBackground(currrow.getCell(col), IndexedColors.GREY_25_PERCENT.getIndex());
        errorDetected=true;
        return r;
    }

    public List<Atc> findAtcList(String s,int col) {
        List<Atc> res=new ArrayList<Atc>();
        String[]allnames= new String[10];
        allnames=s.split(", ",10);
        for(int i=0;i<= allnames.length-1;i=i+2) {
            if (!allnames[i].equalsIgnoreCase("")) {
                Atc atc = dictionaryDAO.findAtsbyCode(allnames[i]);
                if (atc != null) res.add(atc);
                else {
                    ExcelTools.setCellBackground(currrow.getCell(col), IndexedColors.GREY_25_PERCENT.getIndex());
                    //errorDetected = true;
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




    public Country findCountry(String s,int col) {
        s=s.trim();
        Country r=countryDAO.findCountryByName(s);
        if (r!=null)return r;
            ExcelTools.setCellBackground(currrow.getCell(col), IndexedColors.GREY_25_PERCENT.getIndex());
       errorDetected=true;
        return r;
    }
Date getDateValue(Cell cell){
    Date dt=null;
if (cell.getCellType()==Cell.CELL_TYPE_STRING)
    try {
        String s = cell.getStringCellValue().trim();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        dt = formatter.parse(s);
    }  catch (ParseException e) {
        ExcelTools.setCellBackground(cell, IndexedColors.GREY_25_PERCENT.getIndex());
        errorDetected=true;

    }else
dt=cell.getDateCellValue();
    return dt;
}
      public Applicant findApplicant(String s){
        Applicant a=dictionaryDAO.findApplicantByName(s);
        if (a!=null)return a;
        a=new Applicant();
        a.setAppName(s);
        return a;
      }

}