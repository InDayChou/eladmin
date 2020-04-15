package hundsun.pdpm.modules.system.repository;


import hundsun.pdpm.modules.system.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
* @author yantt
* @date 2019-11-29
*/
public interface CustomerRepository extends JpaRepository<Customer, String>, JpaSpecificationExecutor<Customer> {

    List<Customer> findAllByIdIn(List<String> idlist);

    void  deleteAllByIdIn(List<String> idlist);

    List<Customer> findAllByCustTypeEqualsAndAreaEqualsAndCustNameEquals(String sCustType, String sArea, String sCustName);


    @Query(value = "select distinct c.parentId from Customer c where (coalesce(?1,null) is  not null and c.parentId in (?1))")
    List<String> findHasChildNodeId(List<String> id);


    List<Customer> findAllByParentIdIn(List<String> parentId);

}
