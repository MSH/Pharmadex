package org.msh.pharmadex.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.msh.pharmadex.domain.Excipient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("excipientDAO")
public class ExcipientDAO implements Serializable {

	private static final long serialVersionUID = -854274042415512643L;
	
	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	EntityManager entityManager;

	public Excipient findByName(String n){
		List<Excipient> list = entityManager.createQuery("select excp from Excipient excp where excp.name like :n")
		.setParameter("n", n)
		.getResultList();
		if(list != null && list.size() > 0)
			return list.get(0);
		return null;
	}

	public boolean isNameDuplicated(Excipient exc) {
		if(exc == null)
			return true;
		String q = "select count(excp.id) from Excipient excp where excp.name like :n";
		if(exc.getId() != null && exc.getId() > 0)
			q += " and excp.id != '" + exc.getId() + "'";
		
		Long i = (Long) entityManager.createQuery(q)
				.setParameter("n", exc.getName())
				.getSingleResult();
		if (i > 0)
			return true;
		else
			return false;
	}

	public List<Excipient> findAll() {
		List<Excipient> list = entityManager.createQuery("select excp from Excipient excp order by excp.name")
				.getResultList();
		return list;
	}
	
	public Excipient findExcipientById(long id) {
		Excipient exc = entityManager.find(Excipient.class, id);
        return exc;
    }
	
	@Transactional
	public Excipient saveExcipient(Excipient exc) {
		entityManager.persist(exc);
		return exc;
	}

	@Transactional
	public Excipient update(Excipient exc) {
		exc = entityManager.merge(exc);
		return exc;
	}
}
