package org.msh.pharmadex.service;

import org.apache.poi.ss.usermodel.*;


import org.msh.pharmadex.auth.PassPhrase;
import org.msh.pharmadex.dao.*;
import org.msh.pharmadex.dao.iface.*;
import org.msh.pharmadex.domain.*;

import org.msh.pharmadex.domain.enums.*;
import org.msh.pharmadex.util.RetObject;
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

    private Row currrow;
    private  boolean errorDetected;
    ApplicantType at=null;
    User user=null;
    boolean isNewUom;
    boolean isNewATC;
    boolean isNewRoute;
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
                    prod.setDosStrength(cell.getStringCellValue());
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
            if (cell != null) {
                String all=cell.getStringCellValue();
                int pos=all.indexOf(",");
                if (pos==-1) {
                    co= findCompany(all);
                }else{
                    co= findCompany(all.substring(0,pos));
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
        s=s.trim();
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

    private Company createUpdateCompany(String name, String email, Address addr, String phones, String contact){
        try {
            Company company = findCompany(name);
            company.setCompanyName(name);
            company.setAddress(addr);
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

    public  User createUpdateUser(String firstName, String lastName, String email, String phones, Address address, Company company, Applicant app){
        try{
            boolean isNewUser=false;
            String fullName = firstName+" "+lastName;
            User user = userDAO.findByUsername(fullName);
            if (user==null)
                user = userDAO.findByUsername(lastName+" "+firstName);
            if (user==null){
                user = new User();
                isNewUser = true;
            }
            user.setName(fullName);
            user.setEmail(email);
            user.setAddress(address);
            user.setPhoneNo(phones);
            user.setApplicant(app);
            user.setCompanyName(company.getCompanyName());
            user.setEnabled(true);
            user.setTimeZone(TimeZone.getTimeZone("CEST"));
            user.setLanguage(Locale.ENGLISH);
            user.setType(UserType.COMPANY);
            List<Role> roles = new ArrayList<Role>();
            roles.add(companyRole);
            user.setRoles(roles);
            user.setRegistrationDate(getInstance().getTime());
            user.setUsername(getLogin(email));
            user.setComments("automatically imported");
            user = (User) this.updateRecInfo(user);
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

    public String importApplicants(Row row){
        currrow = row;
        String firstName=getCellValue(1);
        String lastName=getCellValue(2);
        String email=getCellValue(4);
        String login=getLogin(email);
        String address1=getCellValue(5);
        String poBox=getCellValue(6);
        String zipCode=getCellValue(7);
        String orgName=getCellValue(8);
        String phone=getCellValue(9);
        String jobTittle=getCellValue(10);
        init();
        //detect company
        Address address = createAddress(address1, poBox, zipCode);
        Company company = createUpdateCompany(orgName,email,address,phone,firstName + " "+lastName);
        if (company==null) return "Error registration of company";
        company = companyDAO.save(company);
        Applicant applicant = createUpdateApplicant(orgName,company);
        if (applicant==null) return "Error registration of applicant";
        applicant = applicantDAO.saveApplicant(applicant);
        User user = createUpdateUser(firstName,lastName,email,phone,address,company,applicant);
        if (user==null) return "Error registration of user";
        userDAO.saveUser(user);
        List<User> users = new ArrayList<User>();
        users.add(user);
        applicant.setUsers(users);
        applicantDAO.saveApplicant(applicant);
        String result = company.getId()+":"+applicant.getApplcntId()+":"+user.getUserId();
        int colNum = row.getLastCellNum();
        Cell resCell = row.createCell(colNum++);
        resCell.setCellValue(result);
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
        String password = PassPhrase.getNext();
        user.setPassword(password);
        System.out.println("Password == " + password);
        user.setUpdatedDate(new Date());
        Mail mail = new Mail();
        mail.setMailto(user.getEmail());
        mail.setSubject("Password Reset");
        mail.setUser(user);
        mail.setDate(new Date());
        mail.setMessage("Your password has been successfully reset in order to access the system please use the username '" + user.getUsername() + "' and password '" + password + "' ");
        try{
            user = userService.updateUser(userService.passwordGenerator(user));
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