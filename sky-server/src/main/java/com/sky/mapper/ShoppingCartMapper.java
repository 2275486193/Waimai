package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 根据动态条件查询购物车
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 购物车数量加一
     * @param shoppingCart
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    /**
     * 新增购物车项目
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (name ,user_id, dish_id, setmeal_id, number,amount,image,create_time) " +
            "values (#{name}, #{userId}, #{dishId}, #{setmealId}, #{number}, #{amount},#{image}, #{createTime})")
    void insert(ShoppingCart shoppingCart);
}
