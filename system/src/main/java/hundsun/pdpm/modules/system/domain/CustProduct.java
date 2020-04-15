package hundsun.pdpm.modules.system.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
* @author yantt
* @date 2020-03-30
*/
@Entity
@Data
@Table(name="cust_product")
public class CustProduct implements Serializable {

    // 标识符
    @Id
    @Column(name = "id")
    private String id;

    // 客户id
    @Column(name = "cust_id")
    private String custId;

    // 产品名称
    @Column(name = "product_id")
    private String productId;

    // 存量账户数
    @Column(name = "stock_account_num")
    private String stockAccountNum;

    // 公募数量
    @Column(name = "public_num")
    private String publicNum;

    // 大集合数量
    @Column(name = "big_collection_num")
    private String bigCollectionNum;

    // 专户数量
    @Column(name = "special_account_num")
    private String specialAccountNum;

    // 小集合数量
    @Column(name = "small_collection_num")
    private String smallCollectionNum;

    // 客户产品类型
    @Column(name = "cust_product_type")
    private String custProductType;

    // 是否有余额理财
    @Column(name = "have_yeb")
    private String haveYeb;

    // 统计申请日期
    @Column(name = "stat_request_date")
    private String statRequestDate;

    // 统计确认日期
    @Column(name = "stat_confirm_date")
    private String statConfirmDate;

    // 统计人员
    @Column(name = "stat_person")
    private String statPerson;

    // 备注
    @Column(name = "memo")
    private String memo;

    public void copy(CustProduct source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
