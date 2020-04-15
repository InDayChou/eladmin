package hundsun.pdpm.modules.system.service;


import hundsun.pdpm.modules.system.domain.Customer;
import hundsun.pdpm.modules.system.service.dto.CustomerDTO;
import hundsun.pdpm.modules.system.service.dto.CustomerQueryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author yantt
* @date 2019-11-29
*/
public interface CustomerService {

    /**
    * 查询数据分页
    * @param criteria 条件参数
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(CustomerQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<CustomerDTO>
    */
    List<CustomerDTO> queryAll(CustomerQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return CustomerDTO
     */
    CustomerDTO findById(String id);

    List<CustomerDTO> findByIdlist(List<Customer> customerList);

    CustomerDTO create(Customer resources);

    void update(Customer resources);

    void batchUpdate(List<Customer> resources);

    void delete(String id);

    void download(List<CustomerDTO> all, HttpServletResponse response) throws IOException;

    List<CustomerDTO> upload(MultipartFile multipartFiles) throws Exception;
}
