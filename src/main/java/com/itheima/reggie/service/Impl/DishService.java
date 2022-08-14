package com.itheima.reggie.service.Impl;


import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import lombok.With;

public interface DishService extends IService<Dish> {
    //新增菜品同时插入菜品对应的口味数据，需要操作两张表dish,dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);

    public void removeByIdWithFlavor(String[] ids);
}
