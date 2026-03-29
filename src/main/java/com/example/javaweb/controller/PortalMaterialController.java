package com.example.javaweb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.javaweb.common.log.ApiLog;
import com.example.javaweb.common.result.Result;
import com.example.javaweb.dto.MaterialCategorySaveDTO;
import com.example.javaweb.dto.MaterialManageListQueryDTO;
import com.example.javaweb.dto.MaterialManageOrderQueryDTO;
import com.example.javaweb.dto.MaterialOrderCreateDTO;
import com.example.javaweb.dto.MaterialOrderPayDTO;
import com.example.javaweb.dto.MaterialOrderQueryDTO;
import com.example.javaweb.dto.MaterialPublishDTO;
import com.example.javaweb.dto.MaterialPurchasedQueryDTO;
import com.example.javaweb.dto.PortalMaterialListQueryDTO;
import com.example.javaweb.entity.UpMaterial;
import com.example.javaweb.entity.UpMaterialCategory;
import com.example.javaweb.service.PortalMaterialService;
import com.example.javaweb.vo.material.MaterialCardVO;
import com.example.javaweb.vo.material.MaterialCategoryVO;
import com.example.javaweb.vo.material.MaterialCreateOrderVO;
import com.example.javaweb.vo.material.MaterialDetailVO;
import com.example.javaweb.vo.material.MaterialOrderVO;
import com.example.javaweb.vo.material.MaterialPayInitVO;
import com.example.javaweb.vo.material.PurchasedMaterialVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/portal/material")
public class PortalMaterialController {

    @Autowired
    private PortalMaterialService portalMaterialService;

    @ApiLog("资料分类")
    @GetMapping("/categories")
    public Result<List<MaterialCategoryVO>> categories() {
        return Result.success(portalMaterialService.categories());
    }

    @ApiLog("资料列表")
    @GetMapping("/list")
    public Result<IPage<MaterialCardVO>> list(PortalMaterialListQueryDTO queryDTO) {
        return Result.success(portalMaterialService.list(queryDTO));
    }

    @ApiLog("资料详情")
    @GetMapping("/detail/{id}")
    public Result<MaterialDetailVO> detail(@PathVariable Long id) {
        return Result.success(portalMaterialService.detail(id));
    }

    @ApiLog("创建资料订单")
    @PostMapping("/orders/create")
    public Result<MaterialCreateOrderVO> createOrder(@Validated @RequestBody MaterialOrderCreateDTO dto) {
        return Result.success(portalMaterialService.createOrder(dto));
    }

    @ApiLog("支付资料订单")
    @PostMapping("/orders/{orderNo}/pay")
    public Result<MaterialPayInitVO> payOrder(@PathVariable String orderNo,
                                              @Validated @RequestBody MaterialOrderPayDTO dto) {
        return Result.success(portalMaterialService.payOrder(orderNo, dto));
    }

    @PostMapping("/pay/notify/alipay")
    public String alipayNotify(HttpServletRequest request) {
        Map<String, String> params = request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue() != null && entry.getValue().length > 0 ? entry.getValue()[0] : ""
                ));
        portalMaterialService.handleAlipayNotify(params);
        return "success";
    }

    @PostMapping("/pay/notify/wechat")
    public String wechatNotify(@RequestHeader("Wechatpay-Timestamp") String timestamp,
                               @RequestHeader("Wechatpay-Nonce") String nonce,
                               @RequestHeader("Wechatpay-Signature") String signature,
                               @RequestHeader("Wechatpay-Serial") String serial,
                               @RequestBody String requestBody) {
        portalMaterialService.handleWechatNotify(timestamp, nonce, signature, serial, requestBody);
        return "{\"code\":\"SUCCESS\",\"message\":\"成功\"}";
    }

    @ApiLog("我的资料订单")
    @GetMapping("/orders/my")
    public Result<IPage<MaterialOrderVO>> myOrders(MaterialOrderQueryDTO queryDTO) {
        return Result.success(portalMaterialService.myOrders(queryDTO));
    }

    @ApiLog("我的订单详情")
    @GetMapping("/orders/my/{orderNo}")
    public Result<MaterialOrderVO> myOrderDetail(@PathVariable String orderNo) {
        return Result.success(portalMaterialService.myOrderDetail(orderNo));
    }

    @ApiLog("我的已购资源")
    @GetMapping("/purchased")
    public Result<IPage<PurchasedMaterialVO>> purchased(MaterialPurchasedQueryDTO queryDTO) {
        return Result.success(portalMaterialService.purchased(queryDTO));
    }

    @ApiLog("管理端资料列表")
    @GetMapping("/manage/list")
    public Result<IPage<UpMaterial>> manageList(MaterialManageListQueryDTO queryDTO) {
        return Result.success(portalMaterialService.manageList(queryDTO));
    }

    @ApiLog("管理端新增资料")
    @PostMapping("/manage")
    public Result<Long> createMaterial(@Validated @RequestBody MaterialPublishDTO dto) {
        return Result.success(portalMaterialService.createMaterial(dto));
    }

    @ApiLog("管理端修改资料")
    @PutMapping("/manage/{id}")
    public Result<Void> updateMaterial(@PathVariable Long id, @Validated @RequestBody MaterialPublishDTO dto) {
        portalMaterialService.updateMaterial(id, dto);
        return Result.success();
    }

    @ApiLog("管理端删除资料")
    @DeleteMapping("/manage/{id}")
    public Result<Void> deleteMaterial(@PathVariable Long id) {
        portalMaterialService.deleteMaterial(id);
        return Result.success();
    }

    @ApiLog("管理端分类列表")
    @GetMapping("/manage/categories")
    public Result<List<UpMaterialCategory>> manageCategories() {
        return Result.success(portalMaterialService.manageCategories());
    }

    @ApiLog("管理端新增分类")
    @PostMapping("/manage/categories")
    public Result<Void> createCategory(@Validated @RequestBody MaterialCategorySaveDTO dto) {
        portalMaterialService.createCategory(dto);
        return Result.success();
    }

    @ApiLog("管理端修改分类")
    @PutMapping("/manage/categories")
    public Result<Void> updateCategory(@Validated @RequestBody MaterialCategorySaveDTO dto) {
        portalMaterialService.updateCategory(dto);
        return Result.success();
    }

    @ApiLog("管理端删除分类")
    @DeleteMapping("/manage/categories/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        portalMaterialService.deleteCategory(id);
        return Result.success();
    }

    @ApiLog("管理端订单列表")
    @GetMapping("/manage/orders")
    public Result<IPage<MaterialOrderVO>> manageOrders(MaterialManageOrderQueryDTO queryDTO) {
        return Result.success(portalMaterialService.manageOrders(queryDTO));
    }

    @ApiLog("管理端订单补发货")
    @PostMapping("/manage/orders/{orderNo}/resend")
    public Result<Void> resendMaterial(@PathVariable String orderNo) {
        portalMaterialService.resendMaterial(orderNo);
        return Result.success();
    }

    @ApiLog("管理端关闭订单")
    @PostMapping("/manage/orders/{orderNo}/close")
    public Result<Void> closeOrder(@PathVariable String orderNo) {
        portalMaterialService.closeOrder(orderNo);
        return Result.success();
    }
}
