package hundsun.pdpm.modules.system.service;

import hundsun.pdpm.modules.system.domain.CustProduct;
import hundsun.pdpm.modules.system.service.dto.CustProductDTO;
import hundsun.pdpm.modules.system.service.dto.CustProductQueryCriteria;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.List;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
/**
* @author yantt
* @date 2020-03-30
*/
public interface CustProductService {

    /**
    * 查询数据分页
    * @param criteria 条件参数
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(CustProductQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<CustProductDTO>
    */
    List<CustProductDTO> queryAll(CustProductQueryCriteria criteria);

    /**
     * 根据ID查询
     * @param id ID
     * @return CustProductDTO
     */
    CustProductDTO findById(String id);

    List<CustProductDTO> findByIdlist(List<CustProductDTO> custProductList);

    CustProductDTO create(CustProduct resources);

    void update(CustProduct resources);

    void delete(List<String> id);

    void download(List<CustProductDTO> all, HttpServletResponse response) throws IOException;

    List<CustProductDTO> upload(MultipartFile multipartFiles) throws Exception;
}
