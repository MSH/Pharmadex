package org.msh.pharmadex.dao;

import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.RegState;
import org.msh.pharmadex.service.UserService;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Created by Одиссей on 05.01.2017.
 * User:Одиссей
 * Date: 05/01/2016
 * Time:17:00
 */
@Repository
public class CustomTimelineDAO {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * Collect all PIP order for given employee that was create in given interval of dates
     * @param startDate - start date of interval
     * @param endDate - end date of interval
     * @param employeeId - id of employee user
     * @param orderType - PIP or PO
     * @return list of application (array of objects, result set)
     */
    public List findOrderByExecutor(Date startDate, Date endDate, Long employeeId, String orderType){
        String tableName = "pip_order";
        String tableDiscr = "PIP";
        if ("PO".equalsIgnoreCase(orderType)){
            tableDiscr="PO";
            tableName="pur_order";
        }

        String q="SELECT po.id,po.responsiblePerson_userId " +
                "from timeline as tl, " + tableName + " as po " +
                "where tl.PROD_APP_ID=po.id " +
                "and tl.DTYPE='" + orderType + "' " +
                "and po.responsiblePerson_userId = :employeeId " +
                "and (po.createdDate BETWEEN :startDate and :endDate) " +
                "group by po.id,po.responsiblePerson_userId";
        Query query = entityManager.createNativeQuery(q);
        query.setParameter("startDate",startDate);
        query.setParameter("endDate",endDate);
        query.setParameter("employeeId",employeeId);
        List<Object[]> result = query.getResultList();
        return result;
    }


    /**
     * Get all events for special PIP order
     * @param startDate
     * @param endDate
     * @param employeeId
     * @param appId
     * @return
     */
    public List findOrderByExecutorAndApp(Date startDate, Date endDate, Long employeeId, Long appId, String orderType){
        String tableName = "pip_order";
        String tableDiscr = "PIP";
        if ("PO".equalsIgnoreCase(orderType)){
            tableDiscr="PO";
            tableName="pur_order";
        }
        String q="SELECT po.id, tl.regState, tl.statusdate, tl.USER_ID, po.pipNo,po.createdDate, tl.dtype " +
                "from timeline as tl, " + tableName +" as po " +
                "where tl.PROD_APP_ID=po.id "+
                "and tl.DTYPE='" + tableDiscr+ "' "+
                "and po.responsiblePerson_userId = :employeeId " +
                "and po.id = :appId " +
                "and (po.createdDate BETWEEN :startDate and :endDate) "+
                "ORDER BY po.id DESC";
        Query query = entityManager.createNativeQuery(q);
        query.setParameter("appId",appId);
        query.setParameter("startDate",startDate);
        query.setParameter("endDate",endDate);
        query.setParameter("employeeId",employeeId);
        List<Object[]> result = query.getResultList();
        return result;
    }

    /**
     * Collect all application for given CSO that made pre-screening in given interval of dates
     * @param startDate - start date of interval
     * @param endDate - end date of interval
     * @param employeeId - id of employee user
     * @return list of application (array of objects, result set)
     */
    public List findApplicationByEmployee(Date startDate, Date endDate, Long employeeId){
        String q="SELECT pa.id,tl.user_id " +
                "from timeline as tl, prodapplications as pa, user as usr " +
                "where tl.PROD_APP_ID=pa.id " +
                "and tl.DTYPE='PA' " +
                "and tl.user_Id = :employeeId " +
                "and (pa.createdDate BETWEEN :startDate and :endDate) " +
                "group by pa.prodAppNo";
        Query query = entityManager.createNativeQuery(q);
        query.setParameter("startDate",startDate);
        query.setParameter("endDate",endDate);
        query.setParameter("employeeId",employeeId);
        List<Object[]> result = query.getResultList();
        return result;
    }

    public List findEventsByApplication(Date startDate, Date endDate, Long appId){
        String q="SELECT pa.id, tl.regState, tl.statusdate, tl.USER_ID, pa.prodAppNo,pa.createdDate, tl.dtype " +
                "from timeline as tl, prodapplications as pa " +
                "where tl.PROD_APP_ID=pa.id "+
                "and tl.DTYPE='PA' "+
                "and pa.id = :appId " +
                "and (pa.createdDate BETWEEN :startDate and :endDate) "+
                "ORDER BY pa.id DESC";
        Query query = entityManager.createNativeQuery(q);
        query.setParameter("appId",appId);
        query.setParameter("startDate",startDate);
        query.setParameter("endDate",endDate);
        List<Object[]> result = query.getResultList();
        return result;
    }

    public List findEventsByType(Date startDate, Date endDate, RegState state, Long employeeId){
        Query query;
        if (employeeId==null) {
            String q = "SELECT * " +
                    "from events " +
                    "where dtype!='PA' and regState = :regState " +
                    "and (statusDate BETWEEN :startDate and :endDate) " +
                    "order by dtype";
            query = entityManager.createNativeQuery(q);
        }else{
            String q = "SELECT * " +
                    "from events " +
                    "where dtype!='PA' and regState = :regState " +
                    "and (statusDate BETWEEN :startDate and :endDate) " +
                    "and user_id = :user_id " +
                    "order by dtype";
            query = entityManager.createNativeQuery(q);
            query.setParameter("user_id",employeeId);
        }
        query.setParameter("regState",state.toString());
        query.setParameter("startDate",startDate);
        query.setParameter("endDate",endDate);
        List<Object[]> result = query.getResultList();
        return result;
    }

    public List findApplicationsByType(Date startDate, Date endDate, RegState state){
        String q="SELECT tl.id, pa.prodAppType, tl.regState, tl.statusDate, tl.prod_app_id, count(pa.prodAppType), tl.dtype " +
                "from timeline as tl, prodapplications as pa " +
                "where tl.PROD_APP_ID=pa.id "+
                "and tl.regState = :regState " +
                "and (pa.createdDate BETWEEN :startDate and :endDate) "+
                "group by pa.prodAppType";
        Query query = entityManager.createNativeQuery(q);
        query.setParameter("regState",state.toString());
        query.setParameter("startDate",startDate);
        query.setParameter("endDate",endDate);
        List<Object[]> result = query.getResultList();
        return result;
    }

    public List findAllApplicationsByType(Date startDate, Date endDate, String state){
        String q="SELECT tl.id, tl.dtype, tl.regState, tl.statusDate, tl.id,  COUNT(tl.dtype), tl.dtype " +
                "from events as tl " +
                "where tl.regState IN (:regState) " +
                "and (tl.statusDate BETWEEN :startDate and :endDate) "+
                "group by tl.dtype";
        Query query = entityManager.createNativeQuery(q);
        query.setParameter("regState",state);
        query.setParameter("startDate",startDate);
        query.setParameter("endDate",endDate);
        List<Object[]> result = query.getResultList();
        return result;
    }

    public List findAllApplicationsByType(Date startDate, Date endDate, RegState state){
        String q="SELECT tl.id, tl.dtype, tl.regState, tl.statusDate, tl.id,  COUNT(tl.dtype), tl.dtype " +
                "from events as tl " +
                "where tl.regState = :regState " +
                "and (tl.statusDate BETWEEN :startDate and :endDate) "+
                "group by tl.dtype";
        Query query = entityManager.createNativeQuery(q);
        query.setParameter("regState",state.toString());
        query.setParameter("startDate",startDate);
        query.setParameter("endDate",endDate);
        List<Object[]> result = query.getResultList();
        return result;
    }


    /**
     * Return timeline events for all applications that was processed by given CSO, regardless of
     * event was generated another employee
     * @param startDate
     * @param endDate
     * @param state
     * @param employeeId
     * @return
     */
    public List countApplicationProcesedByTheCSO(Date startDate, Date endDate, RegState state, Long employeeId){
        Query query = null;
        String q = "SELECT src.dtype, round(avg(src.td)/60/60,2), src.regState, src.statusDate, COUNT(src.dtype), user_id " +
                "FROM (SELECT * " +
                "from csos_events " +
                "where regState = :regState " +
                "and user_id = :user_id " +
                "and (statusDate BETWEEN :startDate and :endDate)) as src "
                + " GROUP BY src.dtype";
        query = entityManager.createNativeQuery(q);
        query.setParameter("user_id", employeeId);
        query.setParameter("regState",state.toString());
        query.setParameter("startDate",startDate);
        query.setParameter("endDate",endDate);
        List<Object[]> result = query.getResultList();
        return result;
    }

    public List countApplicationProcesedByTheRegDept(Date startDate, Date endDate, RegState state, Long employeeId){
        Query query = null;
        if (employeeId!=null) {
            String q = "SELECT src.dtype, round(avg(src.td)/60/60,2), src.regState, src.statusDate, COUNT(src.dtype), user_id, appkind " +
                    "FROM (SELECT * " +
                    "from events " +
                    "where regState = :regState " +
                    "and user_id = :user_id " +
                    "and (statusDate BETWEEN :startDate and :endDate)) as src "
                    + " GROUP BY src.dtype,src.appkind";
            query = entityManager.createNativeQuery(q);
            query.setParameter("user_id", employeeId);
        }else{
            String q = "SELECT src.dtype, round(avg(src.td)/60/60,2), src.regState, src.statusDate, COUNT(src.dtype), user_id, appKind " +
                    "FROM (SELECT * " +
                    "from events " +
                    "where regState = :regState " +
                    "and (statusDate BETWEEN :startDate and :endDate)) as src "
                    + " GROUP BY src.dtype,src.appkind";
            query = entityManager.createNativeQuery(q);
        }
        query.setParameter("regState",state.toString());
        query.setParameter("startDate",startDate);
        query.setParameter("endDate",endDate);

        List<Object[]> result = query.getResultList();
        return result;
    }

    public List calculateApplicationsRegProcesingTimeByEmployee(Date startDate, Date endDate, RegState state, Long employeeId){
        Query query = null;
        if (employeeId!=null) {
            String q = "SELECT src.dtype, round(avg(src.td)/60/60,2), src.regState, src.statusDate, COUNT(src.dtype), user_id, COUNT(dtype) as cnt " +
                    "FROM (SELECT * " +
                    "from reg_events " +
                    "where regState = :regState " +
                    "and user_id = :user_id " +
                    "and (statusDate BETWEEN :startDate and :endDate)) as src "
                    + " GROUP BY src.dtype";
            query = entityManager.createNativeQuery(q);
            query.setParameter("user_id", employeeId);
        }else{
            String q = "SELECT src.dtype, round(avg(src.td)/60/60,2), src.regState, src.statusDate, COUNT(src.dtype), user_id, COUNT(dtype) as cnt " +
                    "FROM (SELECT * " +
                    "from reg_events " +
                    "where regState = :regState " +
                    "and (statusDate BETWEEN :startDate and :endDate)) as src "
                    + " GROUP BY src.dtype";
            query = entityManager.createNativeQuery(q);
        }
        query.setParameter("regState",state.toString());
        query.setParameter("startDate",startDate);
        query.setParameter("endDate",endDate);

        List<Object[]> result = query.getResultList();
        return result;
    }


    public List calculateApplicationsProcesingTimeByEmployee(Date startDate, Date endDate, RegState state, Long employeeId){
        Query query = null;
        if (employeeId!=null) {
            String q = "SELECT src.dtype, round(avg(src.td)/60/60,2), src.regState, src.statusDate, COUNT(src.dtype), user_id " +
                    "FROM (SELECT * " +
                    "from events " +
                    "where regState = :regState " +
                    "and user_id = :user_id " +
                    "and (statusDate BETWEEN :startDate and :endDate)) as src "
                    + " GROUP BY src.dtype";
            query = entityManager.createNativeQuery(q);
            query.setParameter("user_id", employeeId);
        }else{
            String q = "SELECT src.dtype, round(avg(src.td)/60/60,2), src.regState, src.statusDate, COUNT(src.dtype), user_id " +
                    "FROM (SELECT * " +
                    "from events " +
                    "where regState = :regState " +
                    "and (statusDate BETWEEN :startDate and :endDate)) as src "
                    + " GROUP BY src.dtype";
            query = entityManager.createNativeQuery(q);
        }
        query.setParameter("regState",state.toString());
        query.setParameter("startDate",startDate);
        query.setParameter("endDate",endDate);

        List<Object[]> result = query.getResultList();
        return result;
    }

    /**
     * calculate average processing time for all type of application (PIP,PO,registration, cancellation)
     * @param startDate
     * @param endDate
     * @param state
     * @return
     */
    public List calculateApplicationsProcesingTime(Date startDate, Date endDate, RegState state){
        String q="SELECT src.dtype, round(avg(src.td)/60/60,2), src.regState, src.statusDate, COUNT(src.dtype) " +
                "FROM (SELECT * " +
                "from aproval_times " +
                "where regState = :regState " +
                "and (statusDate BETWEEN :startDate and :endDate)) as src "
                +" GROUP BY src.dtype";
        Query query = entityManager.createNativeQuery(q);
        query.setParameter("regState",state.toString());
        query.setParameter("startDate",startDate);
        query.setParameter("endDate",endDate);

        List<Object[]> result = query.getResultList();
        return result;
    }

    public Long findCancelations(Date startDate, Date endDate, RegState state){
        String q="SELECT tl.id, sc.susp_no " +
                "from timeline as tl, susp_detail as sc " +
                "where tl.PROD_APP_ID=sc.id "+
                "and tl.regState = :regState " +
                "and (sc.createdDate BETWEEN :startDate and :endDate) ";
        Query query = entityManager.createNativeQuery(q);
        query.setParameter("regState",state.toString());
        query.setParameter("startDate",startDate);
        query.setParameter("endDate",endDate);
        List<Object[]> result = query.getResultList();
        return Long.valueOf(result.size());
    }

    public List<Object[]> findOrdersByType(Date startDate, Date endDate, RegState state, String orderType){
        String tableName="pur_order";
        if ("PIP".equalsIgnoreCase(orderType))
            tableName="pip_order";
        String q="SELECT 'PIP/PO',po.pipNo,tl.regState, tl.prod_app_id, tl.id " +
                "from timeline as tl, "+tableName + " as po " +
                "where tl.PROD_APP_ID=po.id "+
                "and tl.regState = :regState " +
                "and (po.createdDate BETWEEN :startDate and :endDate) "+
                "order by po.pipNo,tl.regState";
        Query query = entityManager.createNativeQuery(q);
        query.setParameter("regState",state.toString());
        query.setParameter("startDate",startDate);
        query.setParameter("endDate",endDate);
        List<Object[]> result = query.getResultList();
        return result;
    }

    public List<Object[]> findAllEventsByAppId(Long appId, String appType){
        String q="SELECT * " +
                "from timeline " +
                "where dtype = :appType and prod_app_id = :appId";
        Query query = entityManager.createNativeQuery(q);
        query.setParameter("appType",appType);
        query.setParameter("appId",appId);
        List<Object[]> result = query.getResultList();
        return result;

    }
}
