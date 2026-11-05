package com.yuyukoshop.yuyukoshop.controller;

import com.yuyukoshop.yuyukoshop.common.Result;
import com.yuyukoshop.yuyukoshop.entity.ProductCategory;
import com.yuyukoshop.yuyukoshop.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    @GetMapping
    public Result<List<ProductCategory>> getCategories() {
        List<ProductCategory> categories = productCategoryService.getCategoryList();
        return Result.success(categories);
    }

    @GetMapping("/tree")
    public Result<List<ProductCategory>> getCategoryTree() {
        List<ProductCategory> categories = productCategoryService.getCategoryTree();
        return Result.success(categories);
    }
}
