package com.itheima.reggie.service.Impl.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomerException;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.Impl.DishFlavorService;
import com.itheima.reggie.service.Impl.DishService;
import com.itheima.reggie.service.Impl.SetmealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);
        //菜品Id
        Long dishId = dishDto.getId();
        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味
     * @param id
     * @return
     */
    @Override
    @Transactional
    public DishDto getByIdWithFlavor(Long id) {
       //查询菜品基本信息
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();

        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据，dish_flavor表中的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        //添加当前页面提交过来的口味数据，dish_flavor表中的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors= flavors.stream().map((item)->{
          item.setDishId(dishDto.getId());
          return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public void removeByIdWithFlavor(String[] ids) {
        LambdaQueryWrapper<Dish> dishqueryWrapper = new LambdaQueryWrapper();
        LambdaQueryWrapper<SetmealDish> setmealDishQueryWrapper = new LambdaQueryWrapper();
        DishDto dishDto = new DishDto();
        Dish dish = new Dish();
        List<String> list = new ArrayList<>();
        for (String id: ids) {
            //查询菜品基本信息
            dish = this.getById(id);

            list.add(id);
        }
        dishqueryWrapper.in(Dish::getId,ids);
        dishqueryWrapper.eq(Dish::getStatus,1);
        int count = this.count(dishqueryWrapper);
        if(count > 0){
            //如果未停售不能删除，抛出一个异常
            throw new CustomerException("菜品正在售卖中，不能删除");
        }
//        setmealDishQueryWrapper.in(SetmealDish::getDishId,ids);
//        int setmealDishcount = setmealDishService.count(setmealDishQueryWrapper);
//        if(setmealDishcount>0){
//            throw new CustomerException("菜品在套餐中，不能删除");
//        }
        BeanUtils.copyProperties(dish,dishDto);

        //清理当前菜品对应口味数据，dish_flavor表中的delete操作
        LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = new LambdaQueryWrapper();
        dishFlavorQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(dishFlavorQueryWrapper);
        this.removeByIds(list);
    }
}