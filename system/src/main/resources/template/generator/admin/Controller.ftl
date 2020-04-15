package ${package}.rest;

import hundsun.pdpm.aop.log.Log;
import java.util.List;
import ${package}.domain.${className};
import ${package}.service.${className}Service;
import ${package}.service.dto.${className}DTO;
import ${package}.service.dto.${className}QueryCriteria;
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
* @author ${author}
* @date ${date}
*/
@Api(tags = "${className}管理")
@RestController
@RequestMapping("/api/${changeClassName}")
public class ${className}Controller {

    private final ${className}Service ${changeClassName}Service;

    public ${className}Controller(${className}Service ${changeClassName}Service) {
        this.${changeClassName}Service = ${changeClassName}Service;
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @PostMapping(value = "/download")
    @PreAuthorize("@el.check('${changeClassName}:export')")
    public void download(HttpServletResponse response,@RequestBody List<${className}DTO> data) throws IOException {
        List<${className}DTO> ${changeClassName}DTOList;
        if(CollectionUtils.isEmpty(data)){
           ${changeClassName}DTOList = ${changeClassName}Service.queryAll(new ${className}QueryCriteria());
        }else{
           ${changeClassName}DTOList = ${changeClassName}Service.findByIdlist(data);
        }
        ${changeClassName}Service.download(${changeClassName}DTOList, response);
    }

    @Log("导入数据")
    @ApiOperation("导入数据")
    @PostMapping(value = "/upload")
    @PreAuthorize("@el.check('${changeClassName}:import')")
    public  ResponseEntity upload(HttpServletResponse response,@RequestParam("file") MultipartFile file)throws Exception{
       return new ResponseEntity<>(${changeClassName}Service.upload(file),HttpStatus.CREATED);
    }

    @GetMapping
    @Log("查询${tableRealName}")
    @ApiOperation("查询${tableRealName}")
    @PreAuthorize("@el.check('${changeClassName}:list')")
    public ResponseEntity get${className}s(${className}QueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(${changeClassName}Service.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增${tableRealName}")
    @ApiOperation("新增${tableRealName}")
    @PreAuthorize("@el.check('${changeClassName}:add')")
    public ResponseEntity create(@Validated @RequestBody ${className} resources){
        return new ResponseEntity<>(${changeClassName}Service.create(resources),HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改${tableRealName}")
    @ApiOperation("修改${tableRealName}")
    @PreAuthorize("@el.check('${changeClassName}:edit')")
    public ResponseEntity update(@Validated @RequestBody ${className} resources){
        ${changeClassName}Service.update(resources);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/delete")
    @Log("删除${tableRealName}")
    @ApiOperation("删除${tableRealName}")
    @PreAuthorize("@el.check('${changeClassName}:del')")
    public ResponseEntity delete(@PathVariable List<${pkColumnType}> ${pkChangeColName}){
        ${changeClassName}Service.delete(${pkChangeColName});
        return new ResponseEntity(HttpStatus.OK);
    }
}
