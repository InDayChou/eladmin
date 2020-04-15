package hundsun.pdpm.modules.datapermission.utils;

import hundsun.pdpm.annotation.PermissionField;
import hundsun.pdpm.annotation.Query;
import hundsun.pdpm.modules.datapermission.domain.DataPermission;
import hundsun.pdpm.modules.datapermission.service.DataPermissionService;
import hundsun.pdpm.modules.datapermission.service.UserPermissionService;
import hundsun.pdpm.modules.datapermission.service.dto.DataPermissionFieldDTO;
import hundsun.pdpm.utils.QueryHelp;
import hundsun.pdpm.utils.SecurityUtils;
import hundsun.pdpm.utils.StringUtils;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author ：yantt21019
 * @date ：Created in 2019/12/16 12:57
 * @description：
 * @version:
 */
@Component
public class PermissionUtils {


        //小于
        static final String  LESS = "0";
        //等于
        static final String  EQUAL = "1";
        //大于
        static final String  GREATER = "2";
        //不等于
        static final String  UNEQUAL = "3";
        //包含
        static final String  CONTAIN = "4";
        //不包含
        static final String  UNCONTAIN = "5";
        //小于等于
        static final String  LESS_EQUAL = "6";
        //大于等于
        static final String  GREATER_EQUAL = "7";
        //存在于
        static final String  IN = "8";


        static DataPermissionService dataPermissionService;


        static UserPermissionService userPermissionService;

    @Autowired
    private PermissionUtils(DataPermissionService dataPermissionService, UserPermissionService userPermissionService){
        PermissionUtils.dataPermissionService = dataPermissionService;
        PermissionUtils.userPermissionService = userPermissionService;
    }

    public  static List<Field> getPermissionFieldsByClass(Class clzz){
        Field[] fields = clzz.getDeclaredFields();
        List<Field> fieldList = new ArrayList<>();
        for (Field field:fields){
            PermissionField permissionField = field.getAnnotation(PermissionField.class);
            if (permissionField != null){
                fieldList.add(field);
            }
        }
        return fieldList;
    }


    private  static  String getObejctValue(Object obj,DataPermissionFieldDTO fieldDTO){
       try {
           Field field = obj.getClass().getDeclaredField(fieldDTO.getFieldCode());
           field.setAccessible(true);
           return  (String) field.get(obj);
       }catch (Exception e){
           e.printStackTrace();
           return  null;
       }
    }

    public  static  boolean isNotLimit(Object obj, List<DataPermissionFieldDTO> fieldDTOs){
        boolean result = true;
        if(!CollectionUtils.isEmpty(fieldDTOs)){
            for (DataPermissionFieldDTO dto : fieldDTOs){
                String limitValue = dto.getOperateValue();
                String operateType = dto.getOperateType();
                String value = getObejctValue(obj,dto);
                switch (operateType){
                    case LESS:
                        result = StringUtils.validCompare(value,limitValue) < 0 || StringUtils.isEmpty(value);
                        break;
                    case EQUAL:
                        result = StringUtils.validCompare(value,limitValue) == 0;
                        break;
                    case GREATER:
                        result = StringUtils.validCompare(value,limitValue) > 0;
                        break;
                    case UNEQUAL:
                        result = StringUtils.validCompare(value,limitValue) != 0;
                        break;
                    case CONTAIN:
                        result = StringUtils.contains(value,limitValue);
                        break;
                    case UNCONTAIN:
                        result = !StringUtils.contains(value,limitValue);
                        break;
                    default:
                        result = true;
                        break;
                }
                if (!result){
                    break;
                }
            }
        }
        return  result;
    }



    private  static  List<DataPermissionFieldDTO> getFieldLimit(Field field,List<DataPermissionFieldDTO> fieldDTOS){
        List<DataPermissionFieldDTO> fieldDTOList = new ArrayList<>();
        for(DataPermissionFieldDTO dto:fieldDTOS){
            if(StringUtils.equals(field.getName(),dto.getFieldCode())){
                fieldDTOList.add(dto);
            }
        }
        return  fieldDTOList;
    }

    @SuppressWarnings("unchecked")
    public  static <R> Predicate  getPredicate(Root<R> root, Predicate predicate, CriteriaBuilder cb, Class clzz){
        /*业务数据权限*/
        List<DataPermissionFieldDTO> fieldDTOS = dataPermissionService.getFieldByTableCode(clzz);
        /*用户数据权限*/
        List<String> idList = userPermissionService.getUserDataId(clzz, SecurityUtils.getUserId());
        List<Field> fieldList = QueryHelp.getAllFields(clzz,new ArrayList<>());
        List<Predicate> list = new ArrayList<>();
        //如果没有设置任何数据限制则限制查询任何数据
        if(CollectionUtils.isEmpty(fieldDTOS) && CollectionUtils.isEmpty(idList)){
            list.add(cb.equal(QueryHelp.getExpression("id",null,root),"*"));
            return   cb.and(list.toArray(new Predicate[list.size()]));
        }
        for (Field field:fieldList){
            List<DataPermissionFieldDTO> fieldDTOList = getFieldLimit(field,fieldDTOS);
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            for(DataPermissionFieldDTO fieldDTO:fieldDTOList){
                String val = fieldDTO.getOperateValue();
                String fieldCode = fieldDTO.getFieldCode();
                String operateType = fieldDTO.getOperateType();
                switch (operateType){
                    case LESS:
                        list.add(cb.and(cb.lessThan(QueryHelp.getExpression(fieldCode,null,root)
                                .as((Class<? extends Comparable>) fieldType), (Comparable) val)));
                        break;
                    case EQUAL:
                        list.add(cb.and(cb.equal(QueryHelp.getExpression(fieldCode,null,root)
                                .as((Class<? extends Comparable>) fieldType),val)));
                        break;
                    case GREATER:
                        list.add(cb.and(cb.greaterThan(QueryHelp.getExpression(fieldCode,null,root)
                                .as((Class<? extends Comparable>) fieldType), (Comparable) val)));
                        break;
                    case UNEQUAL:
                        list.add(cb.and(cb.notEqual(QueryHelp.getExpression(fieldCode,null,root)
                                .as((Class<? extends Comparable>) fieldType),  val)));
                        break;
                    case CONTAIN:
                        list.add(cb.and(cb.like(QueryHelp.getExpression(fieldCode,null,root)
                                .as(String.class), "%" + val + "%")));
                        break;
                    case LESS_EQUAL:
                        list.add(cb.and(cb.lessThanOrEqualTo(QueryHelp.getExpression(fieldCode,null,root)
                                .as((Class<? extends Comparable>) fieldType), (Comparable) val)));
                        break;
                    case GREATER_EQUAL:
                        list.add(cb.and(cb.greaterThanOrEqualTo(QueryHelp.getExpression(fieldCode,null,root)
                                .as((Class<? extends Comparable>) fieldType), (Comparable) val)));
                        break;
                    default:break;
                }
            }
            field.setAccessible(accessible);
        }
        /*如果设置了业务权限且数据权限是包含'全选:*'*/
        if(idList.contains("*")){
            list.add(predicate);
            return   cb.and(list.toArray(new Predicate[list.size()]));
        }else {
            /*如果两个都设置了则需要用或的关系连接*/
            Path<Object> path = root.get("id");
            CriteriaBuilder.In<Object> in = cb.in(path);
            for(String id : idList){
                in.value(id);
            }
            if(!CollectionUtils.isEmpty(idList) && !CollectionUtils.isEmpty(list)){
                return  cb.and(predicate,
                        cb.or(
                                cb.and(list.toArray(new Predicate[list.size()]))
                                ,cb.and(in)
                        )
                );
            }else if(!CollectionUtils.isEmpty(idList) && CollectionUtils.isEmpty(list)){
                return  cb.and(predicate, cb.and(in));
            }
            list.add(predicate);
            return  cb.and(list.toArray(new Predicate[list.size()]));
        }
    }

}
