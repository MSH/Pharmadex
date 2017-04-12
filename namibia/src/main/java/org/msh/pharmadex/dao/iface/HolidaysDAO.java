package org.msh.pharmadex.dao.iface;

import java.util.Date;
import java.util.List;

import org.msh.pharmadex.domain.Holidays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HolidaysDAO extends JpaRepository<Holidays, Long> {
	@Query("SELECT h FROM Holidays h where h.value between :From and :To")
	public List<Holidays> findByDates(@Param("From") Date from, @Param("To") Date to);
}
