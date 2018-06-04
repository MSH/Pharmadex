package org.msh.pharmadex.service;

import org.apache.poi.ss.usermodel.*;


import org.msh.pharmadex.auth.PassPhrase;
import org.msh.pharmadex.dao.*;
import org.msh.pharmadex.dao.iface.*;
import org.msh.pharmadex.domain.*;
import org.msh.pharmadex.domain.ApplicantType;
import org.msh.pharmadex.domain.enums.*;
import org.msh.pharmadex.util.Scrooge;
import org.msh.pharmadex.utils.ExcelTools;
import org.msh.pharmadex.utils.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Calendar.*;

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
    @Autowired
    AgentInfoDAO agentInfoDAO;
    @Autowired
    ApplicantTypeDAO applicantTypeDAO;
    @Autowired
    ApplicantDAO applicantDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    DosUomDAO dosUomDAO;
    @Autowired
    AtcDAO atcDAO;
    @Autowired
    private UserService userService;
    @Autowired
    private MailService mailService;
    @Autowired
    private RoleDAO roleDao;
    @Autowired
    private DosageFormDAO dosageFormDAO;
    private Row currrow;
    private  boolean errorDetected;
    private ApplicantType at=null;
    User user=null;
    boolean isNewUom;
    boolean isNewATC;
    boolean isNewForm;
    private int lastCol=0;
    private Country ourCountry;
    private User admin;
    private Role staffRole;
    private Role companyRole;
    private List<ApplicantType> atList;
    private int rowNo=0;
    private int curCol;

    private boolean init(){
        ourCountry = countryDAO.findCountryByName("Ethiopia");
        admin = userDAO.findUser((long) 1);
        staffRole = roleDao.findOne(3);
        companyRole = roleDao.findOne(4);
        ApplicantType atImport = applicantTypeDAO.findOne((long) 2);
        ApplicantType atManuf = applicantTypeDAO.findOne((long) 1);
        atList = new ArrayList<ApplicantType>();
        atList.add(atImport);
        atList.add(atManuf);
        return true;
    }

    private  Company updateAddress(Company company, Address newAddr){
        Address addr = company.getAddress();
        if (!(Scrooge.FieldEquals(addr,newAddr,"Address1")&&Scrooge.FieldEquals(addr,newAddr,"Address2")&&Scrooge.FieldEquals(addr,newAddr,"ZipCod"))){
            company.setAddress(newAddr);
            company = companyDAO.saveAndFlush(company);
        }
        return company;
    }
    public boolean importRow(Row row, boolean importData) {
        errorDetected=false;
        rowNo=0;
        int curCol=0;
        Cell cell = null;
        isNewATC=false;
        isNewUom=false;
        isNewForm=false;
        try {
            currrow = row;
            rowNo++;
            errorDetected = false;
            Product prod = new Product();
            LicenseHolder lic=null;
            Applicant a = null;
            Address addr = null;
            Company co=null;
            ProdApplications pa = new ProdApplications();
            pa.setProdAppType(ProdAppType.GENERIC);
            cell = row.getCell(curCol);   // A Presentation -   prod description
            if (cell.getCellType()==Cell.CELL_TYPE_NUMERIC) cell.setCellType(Cell.CELL_TYPE_STRING);
            if (cell != null) prod.setProdDesc(cell.getStringCellValue());
            curCol++;
            cell = row.getCell(curCol);  // B Route Of Admin  catalog   in table adminroute
            if (cell != null) {
                AdminRoute route = fingAdminRouteAcc(cell.getStringCellValue(), curCol);
                if (route==null){
                    throw new Exception("Error: Route of administration not found");
                    //prod.setIndications(cell.getStringCellValue());
                }
                prod.setAdminRoute(route);
            }
            curCol++;
            cell = row.getCell(curCol);  //C Therapeutic Group    ATC name
            String atc="";
            if (cell != null) atc=cell.getStringCellValue().trim();
            cell = row.getCell(curCol+1);  //D Therapeutic Group    ATC code
            String atccode="";
            if (cell != null)
                atccode=cell.getStringCellValue().trim();
            if (!Tools.isEmptyString(atccode)&&!!Tools.isEmptyString(atc)) {
                List<Atc> codes = findAtcList(atccode, atc, curCol + 1);
                if (codes.size() > 0) {
                    prod.setAtcs(codes);
                } else {
                    prod.setAtcs(null);
                    throw new Exception("Error: ATC code not found");
                }
            }else {
                System.out.println("atc missed");
            }

            curCol=curCol+2;
            cell = row.getCell(curCol);// E Brand Name
            if (cell != null) prod.setProdName(cell.getStringCellValue());
            curCol++;
            cell = row.getCell(curCol);  // f Generic Name
            if (cell != null) prod.setGenName(cell.getStringCellValue());
            curCol++;
            cell = row.getCell(curCol);  //G Dose strength
            if (cell != null) {
                if (cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
                    prod.setDosStrength(String.valueOf(cell.getNumericCellValue()));
                }else {
                    prod.setDosStrength(cell.getStringCellValue().trim());
                }
            }
            curCol++;
            cell = row.getCell(curCol); //h - DosUnit
            DosUom dosUnits = findDocUnit(cell.getStringCellValue(), curCol);
            if (dosUnits!=null){
                prod.setDosUnit(dosUnits);
            }else{
                throw new Exception("Error: dos unit not found");
            }
           
            curCol++;
            cell = row.getCell(curCol);  //I Dosage Form
            DosageForm dosForm = findDosFormAcc(cell.getStringCellValue(), curCol);
            if (dosForm==null) throw new Exception("Error: Dosage form not found");
            if (dosForm.getUid()==null) throw new Exception("Error: Dosage form not found");
            prod.setDosForm(dosForm);

            curCol++;
            cell = row.getCell(curCol);//J prod_desc -  conttype
            if (cell != null) prod.setContType(cell.getStringCellValue());
            curCol++;
            cell = row.getCell(curCol);  //K shelf_life(Months)
            if (cell != null)
                prod.setShelfLife(cell.getStringCellValue());
            curCol++;
            cell = row.getCell(curCol);  //L Licence Holder/manufacturer
            if (cell != null) lic = findLicHolderByName(getCellValue(cell));
            if (lic!=null)
                prod.setManufName(lic.getName());
            else
                throw new Exception("Error: license holder not found");
            //if (cell != null) co=findCompany(cell.getStringCellValue());
            curCol++;
            cell = row.getCell(curCol); //M Local Agent 1
            if (cell != null) a= findApplicant(getCellValue(cell));
            if (a==null) throw new Exception("Error: applicant not found");
            curCol++;
            curCol++; // ommit 2 cols, only for dictionary
            curCol++;
            cell = row.getCell(curCol);//P date
            if (cell!=null)pa.setRegistrationDate(getDateValue(cell));
            curCol++;
            cell = row.getCell(curCol); //Q Expiry Date
            if (cell!=null) pa.setRegExpiryDate(getDateValue(cell));
            curCol++;
            cell = row.getCell(curCol);//R Manufacturer/Actual site/
            String all="";
            if (cell != null) {
                all = getCellValue(cell);
                co = findCompany(all);
            }
            if (co==null)
                throw new Exception("Error: Company not found("+all+")");
            curCol++;
            cell = row.getCell(curCol);//S Address
            String addrStr=getCellValue(cell);
            addr = co.getAddress();
            addr.setAddress1(addrStr);
            co.setAddress(addr);
            curCol++;
            cell = row.getCell(curCol);//T Country
            String countryName = getCellValue(cell);
            if (countryName!=null){
                Country mnfCountry = countryDAO.findCountryByName(countryName);
                if (mnfCountry!=null)
                    addr.setCountry(mnfCountry);
            }
            if (!"".equals(addrStr)){
                //co = updateAddress(co,addr);
            }
            pa = (ProdApplications) updateRecInfo(pa);
            List<User> aUsers = a.getUsers();
            if (aUsers!=null){
                if (aUsers.size()>0){
                    User usr = aUsers.get(0);
                    a.setCreatedBy(usr);
                    pa.setApplicantUser(usr);
                }
            }
            if (errorDetected) return false;
            Product oldprod=findExistingProd(prod);
            if(oldprod!=null)  prod=oldprod;
            curCol++;
            lastCol=curCol;
            cell = row.getCell(curCol); //additional comment
            if (cell!=null) {
                if (! cell.getStringCellValue().equalsIgnoreCase(""))importData=false;
            }

            if (importData)
                return addToDatabase(currrow.getRowNum(),prod, co, a, pa, lic);
            else
                return true;
        }catch (Exception e){
            String colNo="";
            if (cell!=null) colNo = String.valueOf(cell.getColumnIndex());
            System.out.println(String.valueOf(currrow.getRowNum())+" " + curCol + " " + e.getMessage());
            Cell erCell = currrow.createCell(21);
            erCell.setCellValue(String.valueOf(currrow.getRowNum())+" " + curCol + " " + e.getMessage());
            ExcelTools.setCellBackground(currrow.getCell(curCol), IndexedColors.GREY_25_PERCENT.getIndex());
            return false;
        }
    }


    public boolean addToDatabase(int num,Product prod, Company co, Applicant a, ProdApplications pa, LicenseHolder lic) {
        Cell t = currrow.createCell(lastCol);
        if(ifProdExist(prod,pa)==true){
            t.setCellValue("Exist");
            return false;
        }
        if (at==null) at=applicantTypeDAO.findOne((long) 2);  //set applicant type = importer
        if (user==null) user=userDAO.findUser((long) 1); //admin
        try{
            //todo проверить записывает ли atc
            if (isNewUom){
                DosUom u = prod.getDosUnit();
                dosUomDAO.save(u);
            }
            if (isNewATC){
                List<Atc> la=prod.getAtcs();
                Atc curAtc = la.get(0);
                if (curAtc!=null) {
                    if (!Tools.isEmptyString(curAtc.getAtcName())&&!Tools.isEmptyString(curAtc.getAtcCode()))
                        atcDAO.save(la.get(0));
                }
            }
            if (isNewForm){
                DosageForm d=prod.getDosForm();
                dosageFormDAO.save(d);
            }
            pa.setApplicant(a);
            pa.setProduct(prod);
            pa.setRegState(RegState.REGISTERED);
            pa.setActive(true);
            pa.setCreatedBy(user);
            String no="00000"+String.valueOf(num);
            no=no.substring(no.length()-4,no.length());
            pa.setProdRegNo(no+"/NMR/LD");
            if (prod.getAgeGroup()==null) prod.setAgeGroup(AgeGroup.BOTH);
            if(prod.getDrugType()==null)prod.setDrugType(ProdDrugType.PHARMACEUTICAL);
            if(prod.getProdCategory()==null) prod.setProdCategory(ProdCategory.HUMAN);
            if (prod.getCreatedBy()==null) prod.setCreatedBy(user);
            if (a.getState()==null)a.setState(ApplicantState.REGISTERED);
            if (a.getApplicantType()==null) a.setApplicantType(at);
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

            List<Product> old=lic.getProducts();
            if (old==null) old=new ArrayList<Product>();
            old.add(prod);
            lic.setProducts(old);
            if(lic.getAddress()==null)
                lic.setAddress(co.getAddress());
            else {
                if (lic.getAddress().getAddress1() == null) {
                    lic.setAddress(co.getAddress());
                }
            }
            if (lic.getCreatedBy()==null)
                lic.setCreatedBy(user);
            licenseHolderDAO.save(lic);
            t=currrow.createCell(lastCol);
            t.setCellValue("Done");
        }  catch (Exception ex) {
            System.out.println(ex.getMessage());
            t.setCellValue(ex.getMessage());
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
        // check by name, , proddesc, docform and dosagepl
        Product p=null;
        ProductFilter filter=new ProductFilter();
        filter.setProdName(prod.getProdName());

        HashMap<String, Object> params = filter.getFilters();
        List<Product> pl=productDAO.findProductByFilter(params);
        if (pl==null) return p;
        if (pl.size()==0) return p;
        for (int i=0;i<pl.size();i++){
            p=pl.get(i);
               if(p.getDosForm()==prod.getDosForm() & p.getDosUnit()==prod.getDosUnit()&
            		p.getDosStrength().equalsIgnoreCase(prod.getDosStrength()) & 
            		p.getProdDesc().equalsIgnoreCase(prod.getProdDesc()) & p.getManufName().equalsIgnoreCase(prod.getManufName()))  return p;
           
        }
        return null;
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
    private DosUom findDocUnitOld(String s, int col) {
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
    private DosUom findDocUnit(String s, int col) {
        s=s.trim().toLowerCase();
        DosUom  r=dictionaryDAO.findDosUomByName(s);
/*
        if (r!=null)return r;
        ExcelTools.setCellBackground(currrow.getCell(col), IndexedColors.GREY_25_PERCENT.getIndex());
        r=new DosUom();
        r.setUom(s);
        r.setDiscontinued(true);
        isNewUom=true;
*/
        return r;
    }

    public Company findCompany(String s){
        s=s.trim();
        Company r= dictionaryDAO.findCompanyByName(s);
        if (r!=null)return r;
        //r=new Company();
        //r.setCompanyName(s);
        ExcelTools.setCellBackground(currrow.getCell(1), IndexedColors.GREY_25_PERCENT.getIndex());
        errorDetected=true;
    
        return r;
    }
    // /check dictionary methods
    public LicenseHolder findLicHolderByName(String s){
        LicenseHolder  r=customLicHolderDAO.findLicHolderByName(s);

        if (r!=null)
            return r;
        else
            return null;

        //r=new LicenseHolder();
        //r.setName(s);
        //return r;
    }
    public DosageForm findDosFormAcc(String s, int col) {
        s=s.trim();
        DosageForm  r=dictionaryDAO.findDosFormByName(s);
        if (r!=null)return r;
        ExcelTools.setCellBackground(currrow.getCell(col), IndexedColors.GREY_25_PERCENT.getIndex());
        //errorDetected=true;
        r=new DosageForm();
        r.setDosForm(s);
        r.setInactive(false);
        r.setSampleSize(100);
        isNewForm=true;
        return r;
    }

    public DosageForm findDosForm(String s){
        s=s.trim();
        DosageForm  r=dictionaryDAO.findDosFormByName(s);
        if (r!=null)return r;
        r=new DosageForm();
        r.setDosForm(s);
        r.setInactive(false);
        r.setSampleSize(100);
        dosageFormDAO.save(r);
        return r;
    }

    public DosUom findDosUOM(String s){
        s=s.trim().toUpperCase();
        DosUom res = dictionaryDAO.findDosUomByName(s);
        if (res!=null) return res;
        res = new DosUom();
        res.setDiscontinued(false);
        res.setUom(s);
        res = dosUomDAO.save(res);
        return res;
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

    public List<Atc> findAtcListOld(String s,int col) {
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
    public List<Atc> findAtcList(String code,String s, int col) {
        List<Atc>   la=new ArrayList<Atc>();
        Atc atc = dictionaryDAO.findAtsbyCode(code);
        if (atc != null) la.add(atc);
        else {
            ExcelTools.setCellBackground(currrow.getCell(col), IndexedColors.GREY_25_PERCENT.getIndex());
            atc=new Atc();
            atc.setAtcName(s);
            if (!s.equalsIgnoreCase("")) atc.setLevel(s.length()-1);
            la.add(atc);
            isNewATC=true;
        }
        return la;
    }

    public PharmClassif findpharmSlassifAcc(String s) {
        PharmClassif r = dictionaryDAO.findPharmSlassifByName(s);
        if (r!=null)return r;
        ExcelTools.setCellBackground(currrow.getCell(3), IndexedColors.GREY_25_PERCENT.getIndex());
        errorDetected=true;
        return r;
    }

    public User findUser(String str){
        List<User> users = userService.findAllUsers();
        if (users==null) return null;
        if (users.size()==0) return null;
        for(User user:users){
            if (str.equals(user.getName())){
                return user;
            }
            if (str.equals(user.getEmail())){
                return user;
            }
        }
        return null;
    }

    public Country findCountry(String s,int col) {
        s=s.trim();
        Country r=null;
        try {
            r = countryDAO.findCountryByName(s);
        }catch (Exception e){
            System.out.println(s);
        }
        if (r!=null)return r;
        ExcelTools.setCellBackground(currrow.getCell(col), IndexedColors.GREY_25_PERCENT.getIndex());
        errorDetected=true;
        return r;
    }

    private Date getDateValue(Cell cell){
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
        if (a!=null) return a;
        a=new Applicant();
        Company company=findCompany(s);
        if (company!=null){
            if (a.getAddress().getCountry()==null && (company.getAddress().getCountry()!=null))
                a.getAddress().setCountry(company.getAddress().getCountry());
            if (a.getAddress().getAddress1()==null && (company.getAddress().getAddress1()!=null))
                a.getAddress().setAddress1(company.getAddress().getAddress1());
        }
        a= (Applicant) updateRecInfo(a);
        a.setAppName(s);
        Cell cell = currrow.getCell(1);
        ExcelTools.setCellBackground(cell, IndexedColors.GREY_25_PERCENT.getIndex());
        return a;
    }

    private String getCellValue(int cellNo){
        Cell cell = currrow.getCell(cellNo);
        return  getCellValue(cell);
    }

    private String getCellValue(Cell cell){
        String res="";
        if (cell==null) return res;
        if (cell.getCellType()==Cell.CELL_TYPE_BLANK) return res;
        if (cell.getCellType()==Cell.CELL_TYPE_STRING){
            if (cell.getStringCellValue()==null) return res;
            res=cell.getStringCellValue().trim();
            lastCol = cell.getColumnIndex();
            return res.trim();
        }else if (cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
            double num = cell.getNumericCellValue();
            if (num!=0){
                //res = String.format("%1$,.0f", num);
                res = String.format("%1$.0f", num);
                return res;
            }
        }
        return  "";
    }

    private void saveResultOfRowImport(Row row,String result){
        int lastCol = row.getLastCellNum();
        lastCol++;
        Cell cell = row.createCell(lastCol);
        cell.setCellValue(result);
    }

    private Company updateCompanyPrimary(Company company, String address, String countryName){
        Country country=null;
        if (!"".equals(countryName)){
            country = findCountry(countryName,19);
        }else{
            if (company.getAddress()==null)
                country = ourCountry;
            else{
                if (company.getAddress().getCountry()==null)
                    country = ourCountry;
            }

        }
        Address addr = new Address();
        addr.setCountry(country);
        if (!"".equals(address))
            addr.setAddress1(address.trim());
        company.setAddress(addr);
        company = (Company) updateRecInfo(company);
        try {
            company = companyDAO.save(company);
            companyDAO.flush();
        }catch (DataIntegrityViolationException e){
            System.out.println("Error: company didn't save");
        }
        return company;
    }

    private Company createUpdateCompany(String name, String address, String countryName){
        Company company = new Company();
        company.setCompanyName(name.toUpperCase());
        Country country=null;
        if (!"".equals(countryName)){
            country = findCountry(countryName,19);
        }else{
            country = ourCountry;
        }
        Address addr = new Address();
        addr.setCountry(country);
        if (!"".equals(address))
            addr.setAddress1(address.trim());
        company.setAddress(addr);
        company = (Company) updateRecInfo(company);
        try {
            company = companyDAO.save(company);
            companyDAO.flush();
        }catch (DataIntegrityViolationException e){
            System.out.println("Error: company didn't save");
        }
        return  company;
    }

    private Company createUpdateCompany(String name, String email, Address addr, String phones, String contact){
        try {
            init();
            Company company = findCompany(name);
            if (company.getAddress()!=null){
                if (company.getAddress().getAddress1()==null) {
                    company.setAddress(addr);
                }
            }
            company.setCompanyName(name);
            company.setEmail(email);
            company.setPhoneNo(phones);
            company.setContactName(contact);
            company = (Company) updateRecInfo(company);
            return company;
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }

    private void setUserRoles(User user, String roleNames) {
        List<Role> roles = new ArrayList<Role>();
        if ("".equals(roleNames)){
            user.setType(UserType.COMPANY);
            roles.add(companyRole);
            return;
        }
        String[] roleList = roleNames.split(",");
        for(String roleName:roleList) {
            Role role=null;
            if (roleName.equalsIgnoreCase("company")) {
                user.setType(UserType.COMPANY);
                roles.add(companyRole);
            }else if (roleName.equalsIgnoreCase("inspector")) {
                user.setType(UserType.INSPECTOR);
                role=roleDao.findOne(5);
                roles.add(role);
            }else if (roleName.equalsIgnoreCase("staff")) {
                user.setType(UserType.STAFF);
                roles.add(staffRole);
            }else if (roleName.equalsIgnoreCase("cso")) {
                user.setType(UserType.STAFF);
                role=roleDao.findOne(3);
                roles.add(role);
            }else if ((roleName.equalsIgnoreCase("teamlead"))||(roleName.equalsIgnoreCase("moderator"))) {
                user.setType(UserType.STAFF);
                role=roleDao.findOne(6);
                roles.add(role);
            }else if (roleName.equalsIgnoreCase("reviewer")) {
                user.setType(UserType.STAFF);
                role=roleDao.findOne(7);
                roles.add(role);
            }else if (roleName.equalsIgnoreCase("csd")) {
                user.setType(UserType.STAFF);
                role=roleDao.findOne(9);
                roles.add(role);
            }else if (roleName.equalsIgnoreCase("head")) {
                user.setType(UserType.STAFF);
                role=roleDao.findOne(8);
                roles.add(role);
            }else if (roleName.equalsIgnoreCase("lab")) {
                user.setType(UserType.STAFF);
                role=roleDao.findOne(10);
                roles.add(role);
            }else if (roleName.equalsIgnoreCase("lab moderator")) {
                user.setType(UserType.STAFF);
                role=roleDao.findOne(11);
                roles.add(role);
            }else if (roleName.equalsIgnoreCase("lab head")) {
                user.setType(UserType.STAFF);
                role=roleDao.findOne(14);
                roles.add(role);
            }else if (roleName.equalsIgnoreCase("cs moderator")) {
                user.setType(UserType.STAFF);
                role=roleDao.findOne(13);
                roles.add(role);
            }else if (roleName.equalsIgnoreCase("public")) {
                user.setType(UserType.EXTERNAL);
                role=roleDao.findOne(1);
                roles.add(role);
            }
        }
        if (roles.size()>0)
            user.setRoles(roles);
    }

    public  User createUpdateUser(String firstName, String lastName, String email, String phones, Address address, Company company, Applicant app, String roleNames, String password){
        try{
            boolean isNewUser=false;
            String fullName = firstName+" "+lastName;
            User user = findUser(fullName);
            if (user==null)
                user = findUser(email);;
            if (user==null){
                user = new User();
                isNewUser = true;
            }else{
                return user;
            }
            user.setName(fullName);
            user.setEmail(email);
            user.setAddress(address);
            user.setPhoneNo(phones);
            if (app!=null)
                user.setApplicant(app);
            user.setCompanyName(company.getCompanyName());
            user.setEnabled(true);
            user.setTimeZone(TimeZone.getTimeZone("CEST"));
            user.setLanguage(Locale.ENGLISH);
            user.setRegistrationDate(getInstance().getTime());
            user.setUsername(getLogin(email));
            user.setComments("automatically imported");
            setUserRoles(user,roleNames);
            user = (User) this.updateRecInfo(user);
            if (!"".equals(password))
                user.setPassword(password);
            if (isNewUser){
                setPassword(user); //user saved here
            }else{
                userDAO.updateUser(user);
            }
            return user;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private  Applicant createUpdateApplicant(String name, Company company){
        try {
            init();
            Applicant app = findApplicant(name);
            if (app.getApplcntId()!=null) return app;
            company = findCompany(name);
            if (company.getId()==null)
                return null;
            if (company.getAddress().getCountry()==null){
                company.getAddress().setCountry(ourCountry);
            }

/*
            if (company!=null) {
                app.setAddress(company.getAddress());
                app.setEmail(company.getEmail());
                app.setPhoneNo(company.getPhoneNo());
                app.setContactName(company.getContactName());
            }
*/
            app.setComment("automatically registered");
            Date today = getInstance().getTime();
            app.setRegistrationDate(today);
            app.setApplicantType(atList.get(0));
            app.setState(ApplicantState.REGISTERED);
            app.setSubmitDate(today);
            Calendar expDate = getInstance();
            expDate.add(YEAR,5);
            app.setRegExpiryDate(expDate.getTime());
            app = (Applicant) this.updateRecInfo(app);
            return app;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates apllicants - names only
     * @param row
     * @return
     */
    public String createUpdateSimpleLocalAgent(Row row){
        currrow = row;
        String result="";
        for (int i=11;i<=13;i++) {
            String orgName = getCellValue(i);
            if (!"".equals(orgName)) {
                Applicant applicant = createUpdateApplicant(orgName, null);
                if (applicant == null) return "Error registration of single applicant";
                if (applicant.getApplcntId() == null) {
                    applicant = applicantDAO.saveApplicant(applicant);
                }
                result = String.valueOf(applicant.getApplcntId());
                saveResultOfRowImport(row, result);
            }
        }
        return result;
    }

    public String createUpdateLicenseHolder(Row row){
        init();
        currrow = row;
        List<String> locAgents = new ArrayList<String>();
        String licenseHolder=getCellValue(11);
        String localAgent=getCellValue(12);
        if (localAgent!=null) localAgent = localAgent.trim();
        if (licenseHolder!=null) licenseHolder = licenseHolder.trim();
        locAgents.add(localAgent);
        localAgent=getCellValue(13);
        if (!"".equals(localAgent)) locAgents.add(localAgent);
        localAgent=getCellValue(14);
        if (!"".equals(localAgent)) locAgents.add(localAgent);
        String result;
        try {
            LicenseHolder lh = customLicHolderDAO.findLicHolderByName(licenseHolder);
            if (lh == null) {
                lh = new LicenseHolder();
                lh.setName(licenseHolder);
                lh.setState(UserState.ACTIVE);
                lh = (LicenseHolder) this.updateRecInfo(lh);
                lh = licenseHolderDAO.save(lh);
            }
            result = String.valueOf(lh.getId());
            int count = 0;
            for (String agentName : locAgents) {
                localAgent = agentName;
                Applicant applicant = createUpdateApplicant(localAgent,null);
                if (applicant.getApplcntId() == null) {
                    applicantDAO.saveApplicant(applicant);
                }
                result = result + ":" + String.valueOf(applicant.getApplcntId());
                List<AgentInfo> agInfoList = lh.getAgentInfos();
                if (agInfoList == null)
                    agInfoList = new ArrayList<AgentInfo>();
                boolean found = false;
                if (agInfoList.size() > 0) {// search, if this local agent present in list
                    for (AgentInfo a : agInfoList) {
                        if (a.getApplicant().getApplcntId() == applicant.getApplcntId() &&
                                a.getLicenseHolder().getId() == lh.getId()) {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    count++;
                    AgentInfo ai = new AgentInfo();
                    ai.setApplicant(applicant);
                    ai.setLicenseHolder(lh);
                    if (count == 1)
                        ai.setAgentType(AgentType.FIRST);
                    else if (count == 2)
                        ai.setAgentType(AgentType.SECOND);
                    else
                        ai.setAgentType(AgentType.THIRD);
                    ai.setCreatedBy(user);
                    ai = (AgentInfo) updateRecInfo(ai);
                    agInfoList.add(ai);
                    lh.setAgentInfos(agInfoList);
                    try {
                        licenseHolderDAO.save(lh);
                    } catch (Exception e) {
                        System.out.println("Error: " + licenseHolder);
                        result = "Error: agent info";
                    }
                }
            }
            if (count > 0) {
                System.out.println("License holder " + lh.getName() + " created and " + count + " agents");
            } else {
                result = "omitted";
            }
            System.out.println(row.getRowNum() + ". " + licenseHolder + " -> " + result);
            saveResultOfRowImport(row, result);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(row.getRowNum() + ". " + licenseHolder + " -> " + e.getMessage());
            result = "Error: create license holder";
            return result;
        }
    }


    public  String importCompanies(Row row, int mode, int colNo) {
        currrow = row;
        //11,12,13,14,17,18,19 - LH,LA1,LA2,LA3,M,Address,Country
        init();
        String manuf = "";
        String addr = "";
        String countryName = "";
        String companyName = "";
        Company company = null;
        manuf = getCellValue(colNo);
        addr = getCellValue(18);
        countryName = getCellValue(19);
        if (!"".equals(countryName))
            countryName = countryName.trim();
        if ("".equals(manuf)) return "";
        companyName = manuf;
        company = findCompany(companyName);
        if (company != null){
            if (company.getId() != null)
                updateCompanyPrimary(company,addr,countryName);
            else
                company = createUpdateCompany(companyName,addr,countryName);
        }else
            company = createUpdateCompany(companyName,addr,countryName);

        if (company!=null)
            return "";
        else
            return "Error: company "+companyName+" did not create.";
    }

    public String importLocalAgents(){
        return "";
    }

    public String importUsers(Row row) {
        currrow = row;
        String firstName = getCellValue(1);
        String lastName = getCellValue(2);
        String email = getCellValue(3);
        String login = getLogin(email);
        String address1 = getCellValue(4);
        String poBox = getCellValue(5);
        String zipCode = "";
        String orgName = getCellValue(7);
        String phone = getCellValue(8);
        String jobTittle = getCellValue(9);
        String role = getCellValue(10);
        String password = getCellValue(11);
        if ("".equals(email)) return "no email";
        init();
        //detect company
        String result = "";
        Company company = findCompany(orgName);
        if (company == null) return "Error: company not found";
        Address cAddr = company.getAddress();
        cAddr.setAddress1(address1);
        cAddr.setAddress2(poBox);
        cAddr.setCountry(ourCountry);
        company.setAddress(cAddr);
        companyDAO.saveAndFlush(company);
        Applicant applicant = findApplicant(orgName);
        if (applicant == null) {
            createUpdateApplicant(orgName,company);
        }
        Address aAddr = applicant.getAddress();
        aAddr.setAddress1(address1);
        aAddr.setAddress2(poBox);
        aAddr.setCountry(ourCountry);
        applicant.setAddress(aAddr);
        User user = createUpdateUser(firstName, lastName, email, phone, cAddr, company, applicant, role, password);
        if (user == null) return "Error registration of user";
        if (applicant != null) {
            List<User> users;
            users = applicant.getUsers();
            if (users == null) users = new ArrayList<User>();
            users.add(user);
            applicant.setUsers(users);
            try {
                //applicantDAO.updateApplicant(applicant);
                result = company.getId() + ":" + applicant.getApplcntId() + ":" + user.getUserId();
            } catch (Exception e) {
                result = e.getMessage();
            }
        }
        saveResultOfRowImport(row, result);
        return result;
    }


    private String getLogin(String email){
        String[] parts = email.split("@");
        return parts[0];
    }

    private Address createAddress(String addr1, String addr2, String zipCode){
        Address addr = new Address();
        addr.setAddress1(addr1);
        addr.setAddress2(addr2);
        addr.setCountry(ourCountry);
        addr.setZipcode(zipCode);
        return addr;
    }

    private User setPassword(User user){
        String password;
        if (user.getPassword()==null) {
            password = PassPhrase.getNext();
            user.setPassword(password);
            System.out.println("Password == " + password);
        }else
            password = user.getPassword();
        user.setComments(user.getComments()+"("+password+")");
        user.setUpdatedDate(new Date());
        Mail mail = new Mail();
        mail.setMailto(user.getEmail());
        mail.setSubject("Password Reset");
        mail.setUser(user);
        mail.setDate(new Date());
        mail.setMessage("Your password has been successfully reset in order to access the system please use the username '" + user.getUsername() + "' and password '" + password + "' ");
        try {
            userService.passwordGenerator(user);
            mailService.sendMailFromSender(mail, false,"epharmadex@gmail.com");
            return user;
        } catch (MailSendException me){
            return user;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private CreationDetail updateRecInfo(CreationDetail obj){
        obj.setUpdatedBy(admin);
        obj.setCreatedBy(admin);
        Date today = getInstance().getTime();
        if (obj.getCreatedDate()==null)
            obj.setCreatedDate(today);
        obj.setUpdatedDate(today);
        return obj;
    }

    public boolean checkProductInRow(Row row){
        errorDetected=false;
        rowNo=0;
        lastCol=21;
        Cell cell = null;
        isNewATC=false;
        isNewUom=false;
        isNewForm=false;
        try {
            currrow = row;
            rowNo++;
            errorDetected = false;
            Product prod = new Product();
            LicenseHolder lic=null;
            Applicant a = null;
            Address addr = null;
            Company co=null;
            curCol=1;
            cell = row.getCell(curCol);  // B Route Of Admin  catalog   in table adminroute
            if (cell != null) {
                AdminRoute route = fingAdminRouteAcc(cell.getStringCellValue(), curCol);
                if (route==null){
                    markItWrong("Error: Route of administration not found");
                }
            }else
                markItWrong("Error: Route of administration not found");

            curCol++;//C Therapeutic Group    ATC name
            curCol++;//D ATS code
            curCol++;
            cell = row.getCell(curCol);// E Brand Name
            if (cell == null) markItWrong("No Brand name");
            curCol++;
            cell = row.getCell(curCol);  // f Generic Name
            if (cell == null) markItWrong("no Generic name");
            curCol++;
            cell = row.getCell(curCol);  //G Dose strength
            if (cell == null) markItWrong("No dosage strength");
            curCol++;

            String val="";
            val = getCellValue(curCol); //h - DosUnit
            if ("".equals(val)) markItWrong("dos unit is empty");
            DosUom dosUnits = findDocUnit(cell.getStringCellValue(), curCol);
            if (dosUnits==null) markItWrong("dosage unit not found");
            curCol++;
            cell = row.getCell(curCol);  //I Dosage Form
            if (cell==null) markItWrong("dosage form is empty");
            DosageForm dosForm = findDosFormAcc(cell.getStringCellValue(), curCol);
            if (dosForm==null) markItWrong("dosage form not found");
            if (dosForm.getUid()==null) markItWrong("dosage form not found");
            curCol++;//J prod_desc -  conttype
            curCol++;
            cell = row.getCell(curCol);  //K shelf_life(Months)
            String shelfLife = getCellValue(cell);
            if (Tools.isEmptyString(shelfLife)) markItWrong("shelf lif is empty");
            cell = row.getCell(curCol);  //L Licence Holder/manufacturer
            if (cell != null)
                lic = findLicHolderByName(getCellValue(cell));
            else
                markItWrong("license holder is empty");
            if (lic==null) markItWrong("license holder not found");
            if (lic != null)
                co=findCompany(cell.getStringCellValue());
            if (co==null) markItWrong("Company of license holder not found",22);
            curCol++;
            cell = row.getCell(curCol); //M Local Agent 1
            if (cell==null) markItWrong("applicant is empty");
            if (cell != null) {
                a = findApplicant(getCellValue(cell));
                if (a==null) markItWrong("applicant not found");
                co=findCompany(cell.getStringCellValue());
                if (co==null) markItWrong("Company of applicant not found",22);
            }
            curCol++;// second agent
            if (cell != null) {
                a = findApplicant(getCellValue(cell));
                if (a==null) markItWrong("applicant (2 agent) not found");
                co=findCompany(cell.getStringCellValue());
                if (co==null) markItWrong("Company of second agent not found",22);
            }
            curCol++; // ommit 2 cols, only for dictionary
            if (cell != null) {
                a = findApplicant(getCellValue(cell));
                if (a==null) markItWrong("applicant (3 agent) not found");
                co=findCompany(cell.getStringCellValue());
                if (co==null) markItWrong("Company of third agent not found",22);
            }
            curCol++; //P date
            curCol++;//Q Expiry Date
            curCol++;
            cell = row.getCell(curCol);//R Manufacturer/Actual site/
            String all="";
            if (cell != null) {
                all = getCellValue(cell);
                co = findCompany(all);
                if (co==null) markItWrong("company of manufacturer not found",22);
            }else
                markItWrong("Manufacturer is empty");
            curCol++;//S Address
            curCol++;//T Country
            cell = row.getCell(curCol);//T Country
            String countryName = getCellValue(cell);
            if (countryName!=null){
                Country mnfCountry = countryDAO.findCountryByName(countryName);
                if (mnfCountry==null) markItWrong("Country not found");
            }else{
                markItWrong("country is empty");
            }
            Product oldprod=findExistingProd(prod);
            if(oldprod!=null){
                markItDone();
            }

            curCol++;//additional comment
            return true;
        }catch (Exception e){
            markItWrong(e.getMessage());
            return false;
        }
   }

    private void markItWrong(String msg) {
        markItWrong(msg,21);
    }

    private void markItWrong(String msg, Integer collToMsg){
        errorDetected=true;
        System.out.println(String.valueOf(currrow.getRowNum())+" " + curCol + " " + msg);
        if (collToMsg==null) collToMsg=21;
        Cell erCell = currrow.createCell(collToMsg);
        erCell.setCellValue(String.valueOf(currrow.getRowNum())+" " + curCol + " " + msg);
        ExcelTools.setCellBackground(currrow.getCell(curCol), IndexedColors.GREY_25_PERCENT.getIndex());
        ExcelTools.setCellBackground(currrow.getCell(0), IndexedColors.GREY_25_PERCENT.getIndex());
    }

    private void markItDone(){
        ExcelTools.setCellBackground(currrow.getCell(0), IndexedColors.GREEN.getIndex());
    }


}