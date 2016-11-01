package org.msh.pharmadex.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.msh.pharmadex.domain.Inn;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("innDAO")
public class InnDAO implements Serializable {

	private static final long serialVersionUID = 3862074088641765308L;

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	EntityManager entityManager;

	public Inn findByName(String n){
		List<Inn> list = entityManager.createQuery("select inns from Inn inns where inns.name like :n")
		.setParameter("n", n)
		.getResultList();
		if(list != null && list.size() > 0)
			return list.get(0);
		return null;
	}

	public boolean isNameDuplicated(Inn inn) {
		if(inn == null)
			return true;
		String q = "select count(inns.id) from Inn inns where inns.name like :n";
		if(inn.getId() != null && inn.getId() > 0)
			q += " and inns.id != '" + inn.getId() + "'";
		
		Long i = (Long) entityManager.createQuery(q)
				.setParameter("n", inn.getName())
				.getSingleResult();
		if (i > 0)
			return true;
		else
			return false;
	}

	public List<Inn> findAll() {
		List<Inn> list = entityManager.createQuery("select i from Inn i")
				.getResultList();
		return list;
	}
	
	public Inn findInnById(long id) {
		Inn inn = entityManager.find(Inn.class, id);
        return inn;
    }
	
	@Transactional
	public Inn saveInn(Inn inn) {
		entityManager.persist(inn);
		return inn;
	}

	@Transactional
	public Inn update(Inn inn) {
		inn = entityManager.merge(inn);
		return inn;
	}
}
