package com.yuyukoshop.yuyukoshop.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuyukoshop.yuyukoshop.common.PageResult;
import com.yuyukoshop.yuyukoshop.common.Result;
import com.yuyukoshop.yuyukoshop.entity.Product;
import com.yuyukoshop.yuyukoshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public Result<PageResult<Product>> getProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        IPage<Product> result = productService.getProductPage(page, size, categoryId, keyword, sortType, minPrice, maxPrice);
        return Result.success(PageResult.of(result));
    }

    @GetMapping("/{id}")
    public Result<Product> getProduct(@PathVariable Long id) {
        Product product = productService.getProductDetail(id);
        return Result.success(product);
    }

    @GetMapping("/hot")
    public Result<PageResult<Product>> getHotProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        IPage<Product> result = productService.getProductPage(page, size, null, null, "sales_desc", null, null);
        return Result.success(PageResult.of(result));
    }
}
