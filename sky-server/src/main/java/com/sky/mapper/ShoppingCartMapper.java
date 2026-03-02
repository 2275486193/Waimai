package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

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
    @Insert("insert into shopping_cart (name ,user_id, dish_id, setmeal_id, number,amount,image,create_time,dish_flavor) " +
            "values (#{name}, #{userId}, #{dishId}, #{setmealId}, #{number}, #{amount},#{image}, #{createTime},#{dishFlavor})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 根据id清空购物车
     * @param userId
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);

    /**
     * 动态条件删除购物车商品
     * @param shoppingCart
     */
    void delete(ShoppingCart shoppingCart);

    /**
     * 商品数量-1
     * @param shoppingCart
     */
    void sub(ShoppingCart shoppingCart);
}
