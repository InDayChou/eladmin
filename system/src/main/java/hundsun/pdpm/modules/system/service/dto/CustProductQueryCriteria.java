package hundsun.pdpm.modules.system.service.dto;

import lombok.Data;
import hundsun.pdpm.annotation.Query;

/**
* @author yantt
* @date 2020-03-30
*/
@Data
public class CustProductQueryCriteria{

    // 精确
    @Query
    private String id;

    // 精确
    @Query
    private String custId;

    // 精确
    @Query
    private String productId;

    // 模糊
    @Query(type = Query.Type.INNER_LIKE)
    private String custProductType;

    // 精确
    @Query
    private String haveYeb;

    @Query(type = Query.Type.INNER_LIKE)
    private String statRequestDate;
    // 模糊
    @Query(type = Query.Type.INNER_LIKE)
    private String statPerson;
}
