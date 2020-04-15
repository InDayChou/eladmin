package hundsun.pdpm.modules.system.service.impl;


import hundsun.pdpm.modules.datapermission.utils.PermissionUtils;
import hundsun.pdpm.modules.execl.ExcelHelper;
import hundsun.pdpm.modules.system.domain.Customer;
import hundsun.pdpm.modules.system.domain.DictDetail;
import hundsun.pdpm.modules.system.repository.CustomerRepository;
import hundsun.pdpm.modules.system.service.CustomerService;
import hundsun.pdpm.modules.system.service.DictDetailService;
import hundsun.pdpm.modules.system.service.dto.CustomerDTO;
import hundsun.pdpm.modules.system.service.dto.CustomerQueryCriteria;
import hundsun.pdpm.modules.system.service.mapper.CustomerMapper;
import hundsun.pdpm.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author yantt
* @date 2019-11-29
*/
@Service
@CacheConfig(cacheNames = "customer")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    @Autowired
    private DictDetailService dictDetailService;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    @Cacheable
    public Map<String,Object> queryAll(CustomerQueryCriteria criteria, Pageable pageable){
        Page<Customer> page = customerRepository.findAll((root, criteriaQuery, criteriaBuilder) -> PermissionUtils.getPredicate(root,QueryHelp.getPredicate(root,criteria,criteriaBuilder),criteriaBuilder,CustomerDTO.class),pageable);
        List<CustomerDTO> customerDTOList = customerMapper.toDto(page.getContent());
        if(!CollectionUtils.isEmpty(page.getContent())){
            List<String> idList = page.getContent().stream().map(Customer::getId).collect(Collectors.toList());
            CustomerQueryCriteria criteria1 = new CustomerQueryCriteria();
            criteria1.setParentId(StringUtils.listToString(idList,','));
            criteria1.setParentIds(criteria1.getParentId());
            criteria1.setParentId(null);
            List<CustomerDTO> childrens =  queryAll(criteria1);
            Map<String,List<CustomerDTO>> groupCustomer = new HashMap<>();
            for(CustomerDTO customer:childrens){
                List<CustomerDTO> customerDTOS = new ArrayList<>();
                if(groupCustomer.containsKey(customer.getParentId())){
                    customerDTOS = groupCustomer.get(customer.getParentId());
                }
                customerDTOS.add(customer);
                groupCustomer.put(customer.getParentId(),customerDTOS);
            }
            for(CustomerDTO customerDTO:customerDTOList){
                if(groupCustomer.containsKey(customerDTO.getId())){
                    customerDTO.setChildrens(groupCustomer.get(customerDTO.getId()));
                }
            }

        }
        return PageUtil.toPage(customerDTOList,page.getTotalElements());
    }

    @Override
    @Cacheable
    public List<CustomerDTO> queryAll(CustomerQueryCriteria criteria){
        return customerMapper.toDto(customerRepository.findAll((root, criteriaQuery, criteriaBuilder) ->
                PermissionUtils.getPredicate(root,QueryHelp.getPredicate(root,criteria,criteriaBuilder),criteriaBuilder,CustomerDTO.class)));
    }

    @Override
    @Cacheable(key = "#p0")
    public CustomerDTO findById(String id) {
        Customer customer = customerRepository.findById(id).orElseGet(Customer::new);
        ValidationUtil.isNull(customer.getId(),"Customer","id",id);
        return customerMapper.toDto(customer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CustomerDTO> findByIdlist(List<Customer> customerList) {
        if (CollectionUtils.isEmpty(customerList)){
            return  new ArrayList<>();
        }
        List<String> idlist = customerList.stream().map(Customer::getId).collect(Collectors.toList());
        return customerMapper.toDto(customerRepository.findAllByIdIn(idlist));
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public CustomerDTO create(Customer resources) {
        resources.setId(StringUtils.get32UUID());
        return customerMapper.toDto(customerRepository.save(resources));
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void update(Customer resources) {
        Customer customer = customerRepository.findById(resources.getId()).orElseGet(Customer::new);
        ValidationUtil.isNull( customer.getId(),"Customer","id",resources.getId());
        customer.copy(resources);
        customerRepository.save(customer);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdate(List<Customer> resources) {
        if(!CollectionUtils.isEmpty(resources)){
            resources.forEach(item->{
                update(item);
            });
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        customerRepository.deleteById(id);
    }


    @Override
    public void download(List<CustomerDTO> all, HttpServletResponse response) throws IOException {
        Map<String, List<DictDetail>> dictMap = dictDetailService.queryAll(CustomerDTO.class);
        ExcelHelper.exportExcel(response,all,dictMap,CustomerDTO.class,false);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public List<CustomerDTO> upload(MultipartFile multipartFiles) throws Exception {
        Map<String, List<DictDetail>> dictMap = dictDetailService.queryAll(CustomerDTO.class);
        List<CustomerDTO> data = ExcelHelper.importExcel(multipartFiles,CustomerDTO.class,dictMap,false);
        if(!CollectionUtils.isEmpty(data)){
            List<Customer> savelist =  new ArrayList<>();
            List<String> idlist = new ArrayList<>();
            for(CustomerDTO customerDTO:data){
                if(!StringUtils.isEmpty(customerDTO.getId())){
                    //删除库中
                    idlist.add(customerDTO.getId());
                }else {
                    customerDTO.setId(StringUtils.get32UUID());
                }
                savelist.add(customerMapper.toEntity(customerDTO));
            }
            customerRepository.deleteAllByIdIn(idlist);
            customerRepository.saveAll(savelist);
        }
        return  data;
    }
}
