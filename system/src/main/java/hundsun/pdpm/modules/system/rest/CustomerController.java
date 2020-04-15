package hundsun.pdpm.modules.system.rest;

import hundsun.pdpm.aop.log.Log;
import hundsun.pdpm.modules.datapermission.service.DataPermissionService;
import hundsun.pdpm.modules.system.domain.Customer;
import hundsun.pdpm.modules.system.service.CustomerService;
import hundsun.pdpm.modules.system.service.dto.CustomerDTO;
import hundsun.pdpm.modules.system.service.dto.CustomerQueryCriteria;
import hundsun.pdpm.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
* @author yantt
* @date 2019-11-29
*/
@RestController
@Api(tags = "Customer管理")
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    private DataPermissionService dataPermission;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Log("导出客户信息数据")
    @ApiOperation("导出客户信息数据")
    @PostMapping(value = "/download")
    @PreAuthorize("@el.check('customer:export')")
    public void download(HttpServletResponse response,@RequestBody List<Customer> data) throws IOException {
        List<CustomerDTO> customerDTOList;
        if(CollectionUtils.isEmpty(data)){
            customerDTOList = customerService.queryAll(new CustomerQueryCriteria());
        }else {
            customerDTOList = customerService.findByIdlist(data);
        }
        customerService.download(customerDTOList, response);
    }
    @Log("导入客户信息数据")
    @ApiOperation("导入客户信息数据")
    @PostMapping(value = "/upload")
    @PreAuthorize("@el.check('customer:import')")
    public  ResponseEntity upload(HttpServletResponse response,@RequestParam("file") MultipartFile file)throws Exception{
        return new ResponseEntity<>(customerService.upload(file),HttpStatus.CREATED);
    }

    @GetMapping
    @Log("查询客户信息")
    @ApiOperation("查询客户信息")
    @PreAuthorize("@el.check('customer:list')")
    public ResponseEntity getCustomers(CustomerQueryCriteria criteria, Pageable pageable){
        if(!CollectionUtils.isEmpty(criteria.getId())) {
             criteria.setIds(criteria.getId());
        }
        return new ResponseEntity<>(customerService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增客户信息")
    @ApiOperation("新增客户信息")
    @PreAuthorize("@el.check('customer:add')")
    public ResponseEntity create(@Validated @RequestBody Customer resources){
        return new ResponseEntity<>(customerService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改客户信息")
    @ApiOperation("修改客户信息")
    @PreAuthorize("@el.check('customer:edit')")
    public ResponseEntity update(@Validated @RequestBody Customer resources){
        customerService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/batchupdate")
    @Log("批量修改客户信息")
    @ApiOperation("批量修改客户信息")
    @PreAuthorize("@el.check('customer:edit')")
    public ResponseEntity batchUpdate( @RequestBody List<Customer> resources){
        customerService.batchUpdate(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/{id}")
    @Log("删除客户信息")
    @ApiOperation("删除客户信息")
    @PreAuthorize("@el.check('customer:del')")
    public ResponseEntity delete(@PathVariable String id){
        customerService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
