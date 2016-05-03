package org.msh.pharmadex.service;

import org.apache.poi.ss.usermodel.*;


import org.msh.pharmadex.auth.PassPhrase;
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
    ApplicantType at=null;
    User user=null;
    boolean isNewUom;
    boolean isNewATC;
    boolean isNewForm;
    private int lastCol=0;
    private Country ourCountry;
    private User admin;
    private  Role staffRole;
    private Role companyRole;
    private List<ApplicantType> atList;

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

    public boolean importRow(Row row, boolean importData) {
        errorDetected=false;
        int rowNo=0;
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
            if (cell != null) {
                prod.setAdminRoute(fingAdminRouteAcc(cell.getStringCellValue(), curCol));
                if (prod.getAdminRoute()==null) prod.setIndications(cell.getStringCellValue());
            }
            curCol++;
            cell = row.getCell(curCol);  //C Therapeutic Group    ATC name
            String atc="";
            if (cell != null) atc=cell.getStringCellValue().trim();
            cell = row.getCell(curCol+1);  //D Therapeutic Group    ATC code
            String atccode="";
            if (cell != null) atccode=cell.getStringCellValue().trim();
            prod.setAtcs(findAtcList(atccode,atc,curCol+1));
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
                    // prod.setDosUnit(findDocUnit("%",curCol));
                }else {
                    prod.setDosStrength(cell.getStringCellValue().trim());
                    //  prod.setDosUnit(findDocUnit(cell.getStringCellValue(), curCol));
                    //if (prod.getD ()==null) prod.setDosStrength(cell.getStringCellValue());
                }
            }
            curCol++;
            cell = row.getCell(curCol); //h - DosUnit
            prod.setDosUnit(findDocUnit(cell.getStringCellValue(), curCol));
            if (prod.getDosUnit().getUom().equalsIgnoreCase("%")) {
                Double val=new Double(prod.getDosStrength())*100;
                prod.setDosStrength(String.valueOf(val));
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
            //if (cell != null) co=findCompany(cell.getStringCellValue());
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
            String addrStr="";
            if (cell != null) {
                String all=cell.getStringCellValue();
                int pos=all.indexOf(",");
                if (pos==-1) {
                    co= findCompany(all);
                }else{
                    co= findCompany(all.substring(0,pos));
                    if (co.getAddress()==null) {
                        addrStr = all.substring(pos + 1);
                        addr.setAddress1(addrStr);
                    }
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
            if (cell!=null) {
                if (! cell.getStringCellValue().equalsIgnoreCase(""))importData=false;
            }

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
            //todo проверить записывает ли atc
            if (isNewUom){
                DosUom u = prod.getDosUnit();
                dosUomDAO.save(u);
            }
            if (isNewATC){
                List<Atc> la=prod.getAtcs();
                if (la.get(0)!=null)
                    atcDAO.save(la.get(0));
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
            if(p.getDosForm()==prod.getDosForm() & p.getDosUnit()==prod.getDosUnit()& p.getDosStrength().equalsIgnoreCase(prod.getDosStrength())) return p;
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
        if (r!=null)return r;
        ExcelTools.setCellBackground(currrow.getCell(col), IndexedColors.GREY_25_PERCENT.getIndex());
        r=new DosUom();
        r.setUom(s);
        r.setDiscontinued(true);
        isNewUom=true;
        return r;
    }

    public Company findCompany(String s){
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

        return r;
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

    public Country findCountry(String s,int col) {
        s=s.trim();
        Country r=countryDAO.findCountryByName(s);
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
        if (a!=null)return a;
        a=new Applicant();
        a.setAppName(s);
        Cell cell = currrow.getCell(12);
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
            return res;
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

    private Company createUpdateCompany(String name, String email, Address addr, String phones, String contact){
        try {
            init();
            Company company = findCompany(name);
            if (company.getAddress()!=null)
                if (company.getAddress().getAddress1()==null) {
                    company.setAddress(addr);
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
            User user = userDAO.findByUsername(fullName);
            if (user==null)
                user = userDAO.findByUsername(lastName+" "+firstName);
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
            if (company!=null) {
                app.setAddress(company.getAddress());
                app.setEmail(company.getEmail());
                app.setPhoneNo(company.getPhoneNo());
                app.setContactName(company.getContactName());
            }
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
        String orgName=getCellValue(1);
        Applicant applicant = createUpdateApplicant(orgName,null);
        if (applicant==null) return "Error registration of single applicant";
        if (applicant.getApplcntId()==null)
            applicant = applicantDAO.saveApplicant(applicant);
        String result = " :"+applicant.getApplcntId()+": ";
        saveResultOfRowImport(row,result);
        return result;
    }

    public String createUpdateLicenseHolder(Row row){
        init();
        currrow = row;
        String licenseHolder=getCellValue(1);
        String localAgent=getCellValue(2);
        LicenseHolder lh = customLicHolderDAO.findLicHolderByName(licenseHolder);
        if (lh==null){
            lh = new LicenseHolder();
            lh.setName(licenseHolder);
            licenseHolderDAO.save(lh);
        }
        lh.setState(UserState.ACTIVE);
        lh = (LicenseHolder) this.updateRecInfo(lh);
        Applicant applicant = findApplicant(localAgent); //local agent
        if (applicant.getApplcntId()==null) return "Error: applicant "+localAgent+" missing in database";

        List<AgentInfo> agInfoList=lh.getAgentInfos();
        if (agInfoList==null) agInfoList=new  ArrayList<AgentInfo>();
        boolean found=false;
        if (agInfoList.size()>0){// search, if this local agent present in list
            for(AgentInfo a:agInfoList){
                if (a.getApplicant().getApplcntId()==applicant.getApplcntId()){
                    found=true;
                    break;
                }
            }
        }
        if (!found) {
            AgentInfo ai = new AgentInfo();
            ai.setApplicant(applicant);
            ai.setLicenseHolder(lh);
            ai.setAgentType(AgentType.FIRST);
            ai.setCreatedBy(user);
            ai = (AgentInfo) updateRecInfo(ai);
            agInfoList.add(ai);
            lh.setAgentInfos(agInfoList);
            licenseHolderDAO.save(lh);
        }
        String result = " :"+lh.getId()+": ";
        saveResultOfRowImport(row,result);
        return result;

    }


    public String importApplicants(Row row) {
        currrow = row;
        String firstName = getCellValue(1);
        String lastName = getCellValue(2);
        String email = getCellValue(7);
        String login = getLogin(email);
        String address1 = getCellValue(4);
        String poBox = getCellValue(5);
        String zipCode = "";
        String orgName = getCellValue(6);
        String phone = getCellValue(8);
        String jobTittle = getCellValue(9);
        String role = getCellValue(10);
        String password = getCellValue(11);
        init();
        //detect company
        String result = "";
        Address address = createAddress(address1, poBox, zipCode);
        Company company = createUpdateCompany(orgName, email, address, phone, firstName + " " + lastName);
        if (company == null) return "Error registration of company";
        company = companyDAO.save(company);
        Applicant applicant = createUpdateApplicant(orgName, company);
        if (applicant != null)
            applicant = applicantDAO.saveApplicant(applicant);
        User user = createUpdateUser(firstName, lastName, email, phone, address, company, applicant, role, password);
        if (user == null) return "Error registration of user";
        if (applicant != null) {
            List<User> users;
            users = applicant.getUsers();
            if (users == null) users = new ArrayList<User>();
            users.add(user);
            applicant.setUsers(users);
            try {
                applicantDAO.updateApplicant(applicant);
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
        try{
            userService.passwordGenerator(user);
            //user = userService.updateUser(userService.passwordGenerator(user));
            //TODO Убрать комментарий с отправки почты
            mailService.sendMail(mail,false);
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
        obj.setCreatedDate(today);
        obj.setUpdatedDate(today);
        return obj;
    }

}