package hundsun.pdpm.modules.system.repository;

import hundsun.pdpm.modules.system.domain.CustProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
/**
* @author yantt
* @date 2020-03-30
*/
public interface CustProductRepository extends JpaRepository<CustProduct, String>, JpaSpecificationExecutor<CustProduct> {
List<CustProduct> findAllByIdIn(List<String> idlist);

void  deleteAllByIdIn(List<String> idlist);
}
