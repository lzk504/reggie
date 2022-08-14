package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.Impl.OrderDetailService;
import com.itheima.reggie.service.Impl.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("order")
public class OrderController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 提交购物车
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        ordersService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 客户端订单分页
     */
    @GetMapping("/userPage")
    public R<Page> userPage(int page, int pageSize) {
        //构建分页对象
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        Page<OrdersDto> dtoPage = new Page<>();
        //构造条件筛选器
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //筛选条件
        queryWrapper.eq(Orders::getUserId, userId);
        //排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //执行分页查询
        ordersService.page(pageInfo, queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Orders> records = pageInfo.getRecords();
        List<OrdersDto> ordersDtoList = records.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            //根据id查分类对象
            Long id = item.getId();
            List<OrderDetail> orderDetailList = orderDetailService.getOrderDetailsByOrderId(id);
            //对ordersDto进行赋值
            BeanUtils.copyProperties(item, ordersDto);
            ordersDto.setOrderDetails(orderDetailList);
            return ordersDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(ordersDtoList);
        return R.success(dtoPage);
    }

    /**
     * 管理员端订单展示
     *
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, Long number, String beginTime, String endTime) {
        //构建分页对象
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        //构建查询对象
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(number != null, Orders::getNumber, number)
                .gt(StringUtils.isNotEmpty(beginTime), Orders::getOrderTime, beginTime)
                .lt(StringUtils.isNotEmpty(endTime), Orders::getOrderTime, endTime);
        ordersService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 订单派送
     *
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> send(@RequestBody Orders orders) {
        Long id = orders.getId();
        Integer status = orders.getStatus();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getId, id);
        Orders one = ordersService.getOne(queryWrapper);
        one.setStatus(status);
        ordersService.updateById(one);
        return R.success("派送成功");
    }

    /**
     * 再来一单
     *
     * @param orders
     * @return
     */
    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders) {
        //获取id
        Long id = orders.getId();
        Orders newOrders = ordersService.getById(id);
        //设置新订单订单号
        Long orderId = IdWorker.getId();
        newOrders.setId(orderId);
        //设置订单号码
        String number = String.valueOf(IdWorker.getId());
        orders.setNumber(number);
        //设置新订单创建时间
        newOrders.setOrderTime(LocalDateTime.now());
        newOrders.setCheckoutTime(LocalDateTime.now());
        newOrders.setStatus(2);

        ordersService.save(newOrders);
        orderDetailService.updateOrderDetailById(id, orderId);

        return R.success("再来一单");
        //
    }
}
