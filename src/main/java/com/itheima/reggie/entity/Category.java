package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    //类型1菜品分类2套餐分类
    private Integer type;
    //分类名称
    private String name;
    //顺序
    private Integer sort;
    //创建时间
    @TableField(fill = FieldFill.INSERT)//插入时填充字段
    private LocalDateTime createTime;
    //创建人
    @TableField(fill = FieldFill.INSERT_UPDATE)//插入和更新时填充字段
    private LocalDateTime updateTime;
    //修改时间
    @TableField(fill = FieldFill.INSERT)//插入时填充字段
    private Long createUser;
    //修改人
    @TableField(fill = FieldFill.INSERT_UPDATE)//插入和更新时填充字段
    private Long updateUser;
    //是否删除
//    private Integer isDeleted;
}
