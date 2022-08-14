package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {
    //用于接收flavors
    private List<DishFlavor> flavors = new ArrayList<>();
    //暂时不用
    private String categoryName;
    private Integer copies;
}
