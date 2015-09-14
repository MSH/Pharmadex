package org.msh.pharmadex.mbean;

import org.msh.pharmadex.domain.enums.*;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author: usrivastava
 */
@ManagedBean
@ApplicationScoped
public class GlobalLists {

    public List<UserType> getUserTypes() {
        return Arrays.asList(UserType.values());
    }

    public List<CompanyType> getCompanyType() {
        return Arrays.asList(CompanyType.values());
    }

    public List<UseCategory> getUseCategory() {
        return Arrays.asList(UseCategory.values());
    }

    public List<ProdType> getProdTypes() {
        return Arrays.asList(ProdType.values());
    }

    public List<CTDModule> getcTDModules() {
        return Arrays.asList(CTDModule.values());
    }

    public List<ProdCategory> getProdCategories() {
        List<ProdCategory> prodCategories = new ArrayList<ProdCategory>();
        prodCategories.add(ProdCategory.HUMAN);
        prodCategories.add(ProdCategory.VETERINARY);
        return prodCategories;
    }

    public List<AmdmtState> getAmdmtState() {
        return Arrays.asList(AmdmtState.values());
    }

    public List<ProdAppType> getProdAppType() {
        List<ProdAppType> prodAppTypes = new ArrayList<ProdAppType>();
        prodAppTypes.add(ProdAppType.GENERIC);
        prodAppTypes.add(ProdAppType.NEW_CHEMICAL_ENTITY);
        return Arrays.asList(ProdAppType.values());
    }

    public List<ProdDrugType> getDrugTypes() {
        return Arrays.asList(ProdDrugType.values());
    }

    public List<CTDModule> getModules() {
        return Arrays.asList(CTDModule.values());
    }

    public List<LetterType> getLetterTypes() {
        return Arrays.asList(LetterType.values());
    }

    public List<AmdmtType> getAmdmtType() {
        return Arrays.asList(AmdmtType.values());
    }

    public List<RecomendType> getRecomendType() {
        return Arrays.asList(RecomendType.values());
    }

    public List<ForeignAppStatusType> getForeignAppStatusType() {
        return Arrays.asList(ForeignAppStatusType.values());
    }

    public List<AgeGroup> getAgeGroupes() {
        return Arrays.asList(AgeGroup.values());  //To change body of created methods use File | Settings | File Templates.
    }

    public List<YesNoNA> getYesNoNA() {
        return Arrays.asList(YesNoNA.values());
    }

    public List<YesNoNA> getYesNo() {
        List<YesNoNA> yesNoNAs = new ArrayList<YesNoNA>(2);
        yesNoNAs.add(YesNoNA.YES);
        yesNoNAs.add(YesNoNA.NO);
        return yesNoNAs;
    }

    public List<String> getRegAuthority() {
        List<String> regAuth = new ArrayList<String>();
        regAuth.add("USA");
        regAuth.add("Japan");
        regAuth.add("UK");
        return regAuth;
    }

    public List<SampleTestStatus> getSampleTestStatuss() {
        return Arrays.asList(SampleTestStatus.values());
    }

    public List<SampleType> getSampleTypes() {
        return Arrays.asList(SampleType.values());
    }

    public List<SraType> getSras() {
        return Arrays.asList(SraType.values());
    }
}
