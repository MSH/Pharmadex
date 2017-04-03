package org.msh.pharmadex.dao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.msh.pharmadex.domain.ProdApplications;
import org.msh.pharmadex.domain.enums.ProdCategory;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.mbean.ItemDashboard;
import org.msh.pharmadex.mbean.product.ProdTable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class PaymentRequiredDAO implements Serializable {

	private static final long serialVersionUID = -8431575045817570352L;
	@PersistenceContext
	EntityManager entityManager;

	public List<ProdTable> getPaymentReqApplications(Long currentUserID) {
		List<ProdTable> list = null;
		
		String sql = "SELECT p.id, p.prod_name, p.gen_name, p.prod_cat, a.appName, pa.regState, pa.id "
					+ " FROM prodapplications pa"
					+ " join product p on p.id = pa.PROD_ID"
					+ " join applicant a on a.applcntId=pa.APP_ID"
					+ " where"
					+ " (pa.createdBy_userId = " + currentUserID
					+ " or"
					+ " pa.applicantUser = " + currentUserID
					+ ") and ("
					+ " ((pa.regState like '" + RegState.NEW_APPL + "' "
							+ "or pa.regState like '" + RegState.FEE + "' "
							+ "or pa.regState like '" + RegState.VERIFY + "')"
					+ " and pa.prescreenfeeReceived=0)"
					+ " or"
					+ " ((pa.regState like '" + RegState.SCREENING + "' "
							+ "or pa.regState like '" + RegState.APPL_FEE + "')"
					+ " and pa.feeReceived=0) )";

		List<Object[]> values = entityManager.createNativeQuery(sql).getResultList();
		list = new ArrayList<ProdTable>();
		ProdTable prodTable;
		for (Object[] objArr : values) {
			prodTable = new ProdTable();
			Long id = ((BigInteger)objArr[0]).longValue();
			prodTable.setId(id);
			prodTable.setProdName((String) objArr[1]);
			prodTable.setGenName((String) objArr[2]);
			prodTable.setProdCategory(ProdCategory.valueOf((String)objArr[3]));
			prodTable.setAppName((String) objArr[4]);
			prodTable.setRegState(RegState.valueOf((String)objArr[5]));
			id = ((BigInteger)objArr[6]).longValue();
			prodTable.setProdAppID(id);
			list.add(prodTable);
		}

		return list;
	}
	
}
