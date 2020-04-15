package hundsun.pdpm.modules.system.service.dto;

import lombok.Data;
import hundsun.pdpm.annotation.Excel;
import java.io.Serializable;
import java.math.BigDecimal;


/**
* @author yantt
* @date 2020-03-30
*/
@Data
public class CustProductDTO implements Serializable {

    // 标识符
    @Excel(title = "标识符")
    private String id;


    // 产品名称
    @Excel(title = "产品名称", dictname = "product_id")
    private String productId;

    // 存量账户数
    @Excel(title = "存量账户数")
    private String stockAccountNum;

    // 公募数量
    @Excel(title = "公募数量")
    private String publicNum;

    // 大集合数量
    @Excel(title = "大集合数量")
    private String bigCollectionNum;

    // 专户数量
    @Excel(title = "专户数量")
    private String specialAccountNum;

    // 小集合数量
    @Excel(title = "小集合数量")
    private String smallCollectionNum;

    // 客户产品类型
    @Excel(title = "客户产品类型", dictname = "cust_product_type")
    private String custProductType;

    // 是否有余额理财
    @Excel(title = "是否有余额理财", dictname = "yes_no")
    private String haveYeb;

    // 统计申请日期
    @Excel(title = "统计申请日期")
    private String statRequestDate;

    // 统计确认日期
    @Excel(title = "统计确认日期")
    private String statConfirmDate;

    // 统计人员
    @Excel(title = "统计人员")
    private String statPerson;

    // 备注
    @Excel(title = "备注")
    private String memo;
}
