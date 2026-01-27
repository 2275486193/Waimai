package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /**
     *
     * @param dishDTO
     */
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //菜品表插入一条数据
        dishMapper.insert(dish);

        //获取insert语句生成的主键值：
        Long id = dish.getId();

        //口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(id));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 菜品批量删除
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断是否能删除：起售中
        for(Long id : ids){
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                //起售中
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //是否跟套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIds != null && setmealIds.size() > 0){
            //当前菜品被关联
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //可以删除：
        //删除菜品
//        for(Long id : ids){
//            dishMapper.deleteById(id);
//            //删除对应口味数据
//            dishFlavorMapper.deleteByDishId(id);
//        }

        //改为批量删除
        dishMapper.deleteByIds(ids);

        //口味批量:
        dishFlavorMapper.deleteByDishIds(ids);

    }

    /**
     * 根据id查菜品和口味数据
     * @param id
     * @return
     */
    public DishVO getByIdWithFlavor(Long id) {

        //根据id查菜品
        Dish dish = dishMapper.getById(id);

        //根据id查口味
        List<DishFlavor> dishFlavors =  dishFlavorMapper.getByDishId(id);

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     * 根据id修改信息和口味信息
     * @param dishDTO
     */
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        //改菜品
        dishMapper.update(dish);
        //改口味:   先删再插入
        dishFlavorMapper.deleteByDishId(dish.getId());

        //重新插入
        //口味表插入n条数据,可能需要重新获取id（新口味）
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dish.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
