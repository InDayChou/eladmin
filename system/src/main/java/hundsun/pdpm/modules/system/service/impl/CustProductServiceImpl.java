package hundsun.pdpm.modules.system.service.impl;

import hundsun.pdpm.modules.system.domain.CustProduct;
import hundsun.pdpm.modules.datapermission.utils.PermissionUtils;
import hundsun.pdpm.modules.system.service.DictDetailService;
import org.springframework.util.CollectionUtils;
import hundsun.pdpm.utils.ValidationUtil;
import hundsun.pdpm.utils.FileUtil;
import hundsun.pdpm.modules.system.repository.CustProductRepository;
import hundsun.pdpm.modules.system.service.CustProductService;
import hundsun.pdpm.modules.system.service.dto.CustProductDTO;
import hundsun.pdpm.modules.system.service.dto.CustProductQueryCriteria;
import hundsun.pdpm.modules.system.service.mapper.CustProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import hundsun.pdpm.modules.execl.ExcelHelper;
import hundsun.pdpm.modules.system.domain.DictDetail;
import org.springframework.web.multipart.MultipartFile;
import hundsun.pdpm.utils.PageUtil;
import hundsun.pdpm.utils.QueryHelp;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import hundsun.pdpm.utils.*;
/**
* @author yantt
* @date 2020-03-30
*/
@Service
@CacheConfig(cacheNames = "custProduct")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class CustProductServiceImpl implements CustProductService {

    private final CustProductRepository custProductRepository;

    private final CustProductMapper custProductMapper;

    @Autowired
    private DictDetailService dictDetailService;

    public CustProductServiceImpl(CustProductRepository custProductRepository, CustProductMapper custProductMapper) {
        this.custProductRepository = custProductRepository;
        this.custProductMapper = custProductMapper;
    }

    @Override
    @Cacheable
    public Map<String,Object> queryAll(CustProductQueryCriteria criteria, Pageable pageable){
        Page<CustProduct> page = custProductRepository.findAll((root, criteriaQuery, criteriaBuilder) ->
                                   PermissionUtils.getPredicate(root,
                                       QueryHelp.getPredicate(root,criteria,criteriaBuilder),
                                       criteriaBuilder,CustProductDTO.class),pageable);
        return PageUtil.toPage(page.map(custProductMapper::toDto));
    }

    @Override
    @Cacheable
    public List<CustProductDTO> queryAll(CustProductQueryCriteria criteria){
        return custProductMapper.toDto(custProductRepository.findAll((root, criteriaQuery, criteriaBuilder) ->
                                                PermissionUtils.getPredicate(root,
                                                QueryHelp.getPredicate(root,criteria,criteriaBuilder),
                                                criteriaBuilder,CustProductDTO.class)));
    }

    @Override
    @Cacheable(key = "#p0")
    public CustProductDTO findById(String id) {
        CustProduct custProduct = custProductRepository.findById(id).orElseGet(CustProduct::new);
        ValidationUtil.isNull(custProduct.getId(),"CustProduct","id",id);
        return custProductMapper.toDto(custProduct);
    }
    @Override
    @Cacheable
    public List<CustProductDTO> findByIdlist(List<CustProductDTO> custProductList) {
        if (CollectionUtils.isEmpty(custProductList)){
        return  new ArrayList<>();
        }
        List<String> idlist = custProductList.stream().map(CustProductDTO::getId).collect(Collectors.toList());
        return custProductMapper.toDto(custProductRepository.findAllByIdIn(idlist));
    }
    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public CustProductDTO create(CustProduct resources) {
        resources.setId(StringUtils.get32UUID());
        return custProductMapper.toDto(custProductRepository.save(resources));
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void update(CustProduct resources) {
        CustProduct custProduct = custProductRepository.findById(resources.getId()).orElseGet(CustProduct::new);
        ValidationUtil.isNull( custProduct.getId(),"CustProduct","id",resources.getId());
        custProduct.copy(resources);
        custProductRepository.save(custProduct);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<String> id) {
        custProductRepository.deleteAllByIdIn(id);
    }


    @Override
    public void download(List<CustProductDTO> all, HttpServletResponse response) throws IOException {
        Map<String, List<DictDetail>> dictMap = dictDetailService.queryAll(CustProductDTO.class);
        ExcelHelper.exportExcel(response,all,dictMap,CustProductDTO.class,false);
    }
    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public List<CustProductDTO> upload(MultipartFile multipartFiles) throws Exception {
       Map<String, List<DictDetail>> dictMap = dictDetailService.queryAll(CustProductDTO.class);
       List<CustProductDTO> data = ExcelHelper.importExcel(multipartFiles,CustProductDTO.class,dictMap,false);
       if(!CollectionUtils.isEmpty(data)){
          List<CustProduct> savelist =  new ArrayList<>();
          List<String> idlist = new ArrayList<>();
          for(CustProductDTO custProductDTO:data){
             if(!StringUtils.isEmpty(custProductDTO.getId())){
                    //删除库中
                 idlist.add(custProductDTO.getId());
             }else {
                 custProductDTO.setId(StringUtils.get32UUID());
             }
             savelist.add(custProductMapper.toEntity(custProductDTO));
          }
       custProductRepository.deleteAllByIdIn(idlist);
       custProductRepository.saveAll(savelist);
       }
        return  data;
     }
}
