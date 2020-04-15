package hundsun.pdpm.rest;

import cn.hutool.core.util.PageUtil;
import hundsun.pdpm.domain.vo.ColumnInfo;
import hundsun.pdpm.exception.BadRequestException;
import hundsun.pdpm.service.GenConfigService;
import hundsun.pdpm.service.GeneratorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @author Zheng Jie
 * @date 2019-01-02
 */
@RestController
@RequestMapping("/api/generator")
@Api(tags = "系统：代码生成管理")
public class GeneratorController {

    private final GeneratorService generatorService;

    private final GenConfigService genConfigService;

    @Value("${generator.enabled}")
    private Boolean generatorEnabled;

    public GeneratorController(GeneratorService generatorService, GenConfigService genConfigService) {
        this.generatorService = generatorService;
        this.genConfigService = genConfigService;
    }

    @ApiOperation("查询数据库元数据")
    @GetMapping(value = "/tables")
    public ResponseEntity getTables(@RequestParam(defaultValue = "") String name,
                                    @RequestParam(defaultValue = "0")Integer page,
                                    @RequestParam(defaultValue = "10")Integer size){
        int[] startEnd = PageUtil.transToStartEnd(page, size);
        return new ResponseEntity<>(generatorService.getTables(name,startEnd), HttpStatus.OK);
    }

    @ApiOperation("查询表内元数据")
    @GetMapping(value = "/columns")
    public ResponseEntity getTables(@RequestParam String tableName){
        return new ResponseEntity<>(generatorService.getColumns(tableName), HttpStatus.OK);
    }

    @ApiOperation("生成代码")
    @PostMapping
    public ResponseEntity generator(@RequestBody List<ColumnInfo> columnInfos,@RequestParam String tableCode, @RequestParam String tableName){
        if(!generatorEnabled){
            throw new BadRequestException("此环境不允许生成代码！");
        }
        generatorService.generator(columnInfos,genConfigService.find(),tableCode,tableName);
        return new ResponseEntity(HttpStatus.OK);
    }
}
