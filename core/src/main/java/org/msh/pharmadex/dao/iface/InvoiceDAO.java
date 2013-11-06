package org.msh.pharmadex.dao.iface;

import org.msh.pharmadex.domain.Invoice;
import org.msh.pharmadex.domain.User;
import org.msh.pharmadex.domain.enums.PaymentStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:25 PM
 * To change this template use File | Settings | File Templates.
 */

public interface InvoiceDAO extends CrudRepository<Invoice, Long> {

    public List<Invoice> findByProdApplications_Id(Long prodApplications_Id);

    public List<Invoice> findByProdApplications_ProdApplicant_UsersAndPaymentStatus(List<User> prodApplications_ProdApplicant_Users,
                                                                                    PaymentStatus paymentStatus);


}

