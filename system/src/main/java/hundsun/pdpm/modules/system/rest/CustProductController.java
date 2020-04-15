package hundsun.pdpm.modules.system.rest;

import hundsun.pdpm.aop.log.Log;
import java.util.List;
import hundsun.pdpm.modules.system.domain.CustProduct;
import hundsun.pdpm.modules.system.service.CustProductService;
import hundsun.pdpm.modules.system.service.dto.CustProductDTO;
import hundsun.pdpm.modules.system.service.dto.CustProductQueryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
/**
* @author yantt
* @date 2020-03-30
*/
@Api(tags = "CustProduct管理")
@RestController
@RequestMapping("/api/custProduct")
public class CustProductController {

    private final CustProductService custProductService;

    public CustProductController(CustProductService custProductService) {
        this.custProductService = custProductService;
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @PostMapping(value = "/download")
    @PreAuthorize("@el.check('custProduct:export')")
    public void download(HttpServletResponse response,@RequestBody List<CustProductDTO> data) throws IOException {
        List<CustProductDTO> custProductDTOList;
        if(CollectionUtils.isEmpty(data)){
           custProductDTOList = custProductService.queryAll(new CustProductQueryCriteria());
        }else{
           custProductDTOList = custProductService.findByIdlist(data);
        }
        custProductService.download(custProductDTOList, response);
    }

    @Log("导入数据")
    @ApiOperation("导入数据")
    @PostMapping(value = "/upload")
    @PreAuthorize("@el.check('custProduct:import')")
    public  ResponseEntity upload(HttpServletResponse response,@RequestParam("file") MultipartFile file)throws Exception{
       return new ResponseEntity<>(custProductService.upload(file),HttpStatus.CREATED);
    }

    @GetMapping
    @Log("查询客户产品信息")
    @ApiOperation("查询客户产品信息")
    @PreAuthorize("@el.check('custProduct:list')")
    public ResponseEntity getCustProducts(CustProductQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(custProductService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增客户产品信息")
    @ApiOperation("新增客户产品信息")
    @PreAuthorize("@el.check('custProduct:add')")
    public ResponseEntity create(@Validated @RequestBody CustProduct resources){
        return new ResponseEntity<>(custProductService.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改客户产品信息")
    @ApiOperation("修改客户产品信息")
    @PreAuthorize("@el.check('custProduct:edit')")
    public ResponseEntity update(@Validated @RequestBody CustProduct resources){
        custProductService.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/delete")
    @Log("删除客户产品信息")
    @ApiOperation("删除客户产品信息")
    @PreAuthorize("@el.check('custProduct:del')")
    public ResponseEntity delete(@RequestBody List<String> id){
        custProductService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
