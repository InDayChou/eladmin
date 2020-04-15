package ${package}.service.impl;

import ${package}.domain.${className};
<#if columns??>
    <#list columns as column>
        <#if column.columnKey = 'UNI'>
            <#if column_index = 1>
import EntityExistException;
            </#if>
        </#if>
    </#list>
</#if>
import hundsun.pdpm.modules.datapermission.utils.PermissionUtils;
import hundsun.pdpm.modules.system.service.DictDetailService;
import org.springframework.util.CollectionUtils;
import hundsun.pdpm.utils.ValidationUtil;
import hundsun.pdpm.utils.FileUtil;
import ${package}.repository.${className}Repository;
import ${package}.service.${className}Service;
import ${package}.service.dto.${className}DTO;
import ${package}.service.dto.${className}QueryCriteria;
import ${package}.service.mapper.${className}Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
<#if !auto && pkColumnType = 'Long'>
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
</#if>
<#if !auto && pkColumnType = 'String'>
import cn.hutool.core.util.IdUtil;
</#if>
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
* @author ${author}
* @date ${date}
*/
@Service
@CacheConfig(cacheNames = "${changeClassName}")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ${className}ServiceImpl implements ${className}Service {

    private final ${className}Repository ${changeClassName}Repository;

    private final ${className}Mapper ${changeClassName}Mapper;

    @Autowired
    private DictDetailService dictDetailService;

    public ${className}ServiceImpl(${className}Repository ${changeClassName}Repository, ${className}Mapper ${changeClassName}Mapper) {
        this.${changeClassName}Repository = ${changeClassName}Repository;
        this.${changeClassName}Mapper = ${changeClassName}Mapper;
    }

    @Override
    @Cacheable
    public Map<String,Object> queryAll(${className}QueryCriteria criteria, Pageable pageable){
        Page<${className}> page = ${changeClassName}Repository.findAll((root, criteriaQuery, criteriaBuilder) ->
                                   PermissionUtils.getPredicate(root,
                                       QueryHelp.getPredicate(root,criteria,criteriaBuilder),
                                       criteriaBuilder,${className}DTO.class),pageable);
        return PageUtil.toPage(page.map(${changeClassName}Mapper::toDto));
    }

    @Override
    @Cacheable
    public List<${className}DTO> queryAll(${className}QueryCriteria criteria){
        return ${changeClassName}Mapper.toDto(${changeClassName}Repository.findAll((root, criteriaQuery, criteriaBuilder) ->
                                                PermissionUtils.getPredicate(root,
                                                QueryHelp.getPredicate(root,criteria,criteriaBuilder),
                                                criteriaBuilder,${className}DTO.class)));
    }

    @Override
    @Cacheable(key = "#p0")
    public ${className}DTO findById(${pkColumnType} ${pkChangeColName}) {
        ${className} ${changeClassName} = ${changeClassName}Repository.findById(${pkChangeColName}).orElseGet(${className}::new);
        ValidationUtil.isNull(${changeClassName}.get${pkCapitalColName}(),"${className}","${pkChangeColName}",${pkChangeColName});
        return ${changeClassName}Mapper.toDto(${changeClassName});
    }
    @Override
    @Cacheable
    public List<${className}DTO> findByIdlist(List<${className}DTO> ${changeClassName}List) {
        if (CollectionUtils.isEmpty(${changeClassName}List)){
        return  new ArrayList<>();
        }
        List<String> idlist = ${changeClassName}List.stream().map(${className}DTO::getId).collect(Collectors.toList());
        return ${changeClassName}Mapper.toDto(${changeClassName}Repository.findAllByIdIn(idlist));
    }
    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public ${className}DTO create(${className} resources) {
<#if !auto && pkColumnType = 'Long'>
        Snowflake snowflake = IdUtil.createSnowflake(1, 1);
        resources.set${pkCapitalColName}(snowflake.nextId());
</#if>
<#if !auto && pkColumnType = 'String'>
        resources.set${pkCapitalColName}(StringUtils.get32UUID());
</#if>
<#if columns??>
    <#list columns as column>
    <#if column.columnKey = 'UNI'>
        if(${changeClassName}Repository.findBy${column.capitalColumnName}(resources.get${column.capitalColumnName}()) != null){
            throw new EntityExistException(${className}.class,"${column.columnName}",resources.get${column.capitalColumnName}());
        }
    </#if>
    </#list>
</#if>
        return ${changeClassName}Mapper.toDto(${changeClassName}Repository.save(resources));
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void update(${className} resources) {
        ${className} ${changeClassName} = ${changeClassName}Repository.findById(resources.get${pkCapitalColName}()).orElseGet(${className}::new);
        ValidationUtil.isNull( ${changeClassName}.get${pkCapitalColName}(),"${className}","id",resources.get${pkCapitalColName}());
<#if columns??>
    <#list columns as column>
        <#if column.columnKey = 'UNI'>
        <#if column_index = 1>
        ${className} ${changeClassName}1 = null;
        </#if>
        ${changeClassName}1 = ${changeClassName}Repository.findBy${column.capitalColumnName}(resources.get${column.capitalColumnName}());
        if(${changeClassName}1 != null && !${changeClassName}1.get${pkCapitalColName}().equals(${changeClassName}.get${pkCapitalColName}())){
            throw new EntityExistException(${className}.class,"${column.columnName}",resources.get${column.capitalColumnName}());
        }
        </#if>
    </#list>
</#if>
        ${changeClassName}.copy(resources);
        ${changeClassName}Repository.save(${changeClassName});
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<${pkColumnType}> ${pkChangeColName}) {
        ${changeClassName}Repository.deleteAllByIdIn(${pkChangeColName});
    }


    @Override
    public void download(List<${className}DTO> all, HttpServletResponse response) throws IOException {
        Map<String, List<DictDetail>> dictMap = dictDetailService.queryAll(${className}DTO.class);
        ExcelHelper.exportExcel(response,all,dictMap,${className}DTO.class,false);
    }
    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public List<${className}DTO> upload(MultipartFile multipartFiles) throws Exception {
       Map<String, List<DictDetail>> dictMap = dictDetailService.queryAll(${className}DTO.class);
       List<${className}DTO> data = ExcelHelper.importExcel(multipartFiles,${className}DTO.class,dictMap,false);
       if(!CollectionUtils.isEmpty(data)){
          List<${className}> savelist =  new ArrayList<>();
          List<String> idlist = new ArrayList<>();
          for(${className}DTO ${changeClassName}DTO:data){
             if(!StringUtils.isEmpty(${changeClassName}DTO.getId())){
                    //删除库中
                 idlist.add(${changeClassName}DTO.getId());
             }else {
                 ${changeClassName}DTO.setId(StringUtils.get32UUID());
             }
             savelist.add(${changeClassName}Mapper.toEntity(${changeClassName}DTO));
          }
       ${changeClassName}Repository.deleteAllByIdIn(idlist);
       ${changeClassName}Repository.saveAll(savelist);
       }
        return  data;
     }
}
