package hundsun.pdpm.modules.system.service.dto;

import hundsun.pdpm.annotation.Query;
import hundsun.pdpm.utils.StringUtils;
import lombok.Data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author yantt
* @date 2019-11-29
*/
@Data
public class CustomerQueryCriteria {


    private String id;

    public Set<String> getId() {
        if(StringUtils.isEmpty(this.id)){
            return  new HashSet<>();
        }
        String[] idArr = this.id.split(",");
        List<String> idList = Arrays.asList(idArr).stream().filter(item-> !StringUtils.isEmpty(item)).collect(Collectors.toList());
        return new HashSet<String>(idList);
    }

    @Query(type = Query.Type.IN ,propName = "id")
    private Set<String> ids;

    // 精确
    @Query
    private String custType;

    // 精确
    @Query
    private String area;


    @Query
    private String parentId;


    public  Set<String>  getParentId() {
        if(StringUtils.isEmpty(this.parentId)){
            return  new HashSet<>();
        }
        String[] idArr = this.parentId.split(",");
        List<String> idList = Arrays.asList(idArr).stream().filter(item-> !StringUtils.isEmpty(item)).collect(Collectors.toList());
        return new HashSet<String>(idList);
    }



    @Query(type = Query.Type.IN ,propName = "parentId")
    private Set<String> parentIds;

    // 模糊
    @Query(type = Query.Type.INNER_LIKE)
    private String custName;

    // 模糊
    @Query(type = Query.Type.INNER_LIKE)
    private String memo;
}
