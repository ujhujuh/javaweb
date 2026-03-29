package com.example.javaweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaweb.common.context.CurrentUserContext;
import com.example.javaweb.common.exception.BusinessException;
import com.example.javaweb.dto.MaterialCategorySaveDTO;
import com.example.javaweb.dto.MaterialManageListQueryDTO;
import com.example.javaweb.dto.MaterialManageOrderQueryDTO;
import com.example.javaweb.dto.MaterialOrderCreateDTO;
import com.example.javaweb.dto.MaterialOrderPayDTO;
import com.example.javaweb.dto.MaterialOrderQueryDTO;
import com.example.javaweb.dto.MaterialPublishDTO;
import com.example.javaweb.dto.MaterialPurchasedQueryDTO;
import com.example.javaweb.dto.PortalMaterialListQueryDTO;
import com.example.javaweb.entity.SysUser;
import com.example.javaweb.entity.UpMaterial;
import com.example.javaweb.entity.UpMaterialCategory;
import com.example.javaweb.entity.UpMaterialOrder;
import com.example.javaweb.mapper.SysUserMapper;
import com.example.javaweb.service.PortalMaterialService;
import com.example.javaweb.service.UpMaterialCategoryService;
import com.example.javaweb.service.UpMaterialOrderService;
import com.example.javaweb.service.UpMaterialService;
import com.example.javaweb.vo.material.MaterialCardVO;
import com.example.javaweb.vo.material.MaterialCategoryVO;
import com.example.javaweb.vo.material.MaterialCreateOrderVO;
import com.example.javaweb.vo.material.MaterialDetailVO;
import com.example.javaweb.vo.material.MaterialOrderVO;
import com.example.javaweb.vo.material.MaterialPayInitVO;
import com.example.javaweb.vo.material.PurchasedMaterialVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PortalMaterialServiceImpl implements PortalMaterialService {

    private static final String CATEGORY_NORMAL = "0";
    private static final String MATERIAL_DRAFT = "0";
    private static final String MATERIAL_ONLINE = "1";
    private static final String MATERIAL_OFFLINE = "2";
    private static final String YES = "1";
    private static final String ORDER_PENDING = "PENDING";
    private static final String ORDER_PAID = "PAID";
    private static final String ORDER_COMPLETED = "COMPLETED";
    private static final String ORDER_CLOSED = "CLOSED";
    private static final String PAY_WECHAT = "WECHAT";
    private static final String PAY_ALIPAY = "ALIPAY";

    @Autowired
    private UpMaterialService upMaterialService;

    @Autowired
    private UpMaterialCategoryService upMaterialCategoryService;

    @Autowired
    private UpMaterialOrderService upMaterialOrderService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private MaterialPaymentGatewayService materialPaymentGatewayService;

    @Override
    public List<MaterialCategoryVO> categories() {
        List<UpMaterialCategory> categories = upMaterialCategoryService.lambdaQuery()
                .eq(UpMaterialCategory::getStatus, CATEGORY_NORMAL)
                .orderByAsc(UpMaterialCategory::getParentId)
                .orderByAsc(UpMaterialCategory::getSortNum)
                .list();
        if (categories.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Long> countMap = upMaterialService.lambdaQuery()
                .eq(UpMaterial::getStatus, MATERIAL_ONLINE)
                .list()
                .stream()
                .collect(Collectors.groupingBy(UpMaterial::getCategoryId, Collectors.counting()));

        List<MaterialCategoryVO> list = new ArrayList<>(categories.size());
        for (UpMaterialCategory category : categories) {
            MaterialCategoryVO vo = new MaterialCategoryVO();
            vo.setId(category.getId());
            vo.setName(category.getCategoryName());
            vo.setParentId(category.getParentId());
            vo.setCount(countMap.getOrDefault(category.getId(), 0L));
            list.add(vo);
        }
        return list;
    }

    @Override
    public IPage<MaterialCardVO> list(PortalMaterialListQueryDTO queryDTO) {
        Integer current = queryDTO.getCurrent();
        Integer size = queryDTO.getSize();
        LambdaQueryWrapper<UpMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UpMaterial::getStatus, MATERIAL_ONLINE);

        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(UpMaterial::getCategoryId, queryDTO.getCategoryId());
        }
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().trim().isEmpty()) {
            String keyword = queryDTO.getKeyword().trim();
            wrapper.and(w -> w.like(UpMaterial::getTitle, keyword)
                    .or().like(UpMaterial::getSummary, keyword)
                    .or().like(UpMaterial::getTags, keyword));
        }

        String sort = queryDTO.getSort() == null ? "latest" : queryDTO.getSort();
        if ("sales".equalsIgnoreCase(sort)) {
            wrapper.orderByDesc(UpMaterial::getSalesCount).orderByDesc(UpMaterial::getPublishTime);
        } else if ("priceAsc".equalsIgnoreCase(sort)) {
            wrapper.orderByAsc(UpMaterial::getPrice).orderByDesc(UpMaterial::getPublishTime);
        } else if ("priceDesc".equalsIgnoreCase(sort)) {
            wrapper.orderByDesc(UpMaterial::getPrice).orderByDesc(UpMaterial::getPublishTime);
        } else {
            wrapper.orderByDesc(UpMaterial::getPublishTime);
        }

        IPage<UpMaterial> materialPage = upMaterialService.page(new Page<>(current, size), wrapper);
        Map<Long, String> categoryNameMap = getCategoryNameMap();
        List<MaterialCardVO> records = materialPage.getRecords().stream()
                .map(item -> toCardVO(item, categoryNameMap))
                .collect(Collectors.toList());
        Page<MaterialCardVO> page = new Page<>(current, size, materialPage.getTotal());
        page.setRecords(records);
        return page;
    }

    @Override
    public MaterialDetailVO detail(Long id) {
        UpMaterial material = upMaterialService.getById(id);
        if (material == null || !MATERIAL_ONLINE.equals(material.getStatus())) {
            throw new BusinessException("资料不存在或已下架");
        }
        MaterialDetailVO vo = new MaterialDetailVO();
        BeanUtils.copyProperties(material, vo);
        vo.setFree(YES.equals(material.getIsFree()));
        vo.setCategory(getCategoryNameMap().getOrDefault(material.getCategoryId(), "未分类"));

        Long userId = CurrentUserContext.getUserId();
        boolean purchased = false;
        if (userId != null) {
            purchased = upMaterialOrderService.lambdaQuery()
                    .eq(UpMaterialOrder::getUserId, userId)
                    .eq(UpMaterialOrder::getMaterialId, material.getId())
                    .in(UpMaterialOrder::getStatus, ORDER_PAID, ORDER_COMPLETED)
                    .count() > 0;
        }
        vo.setPurchased(purchased);
        if (!purchased && !YES.equals(material.getIsFree())) {
            vo.setFileUrl(null);
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MaterialCreateOrderVO createOrder(MaterialOrderCreateDTO dto) {
        Long userId = ensureLogin();
        closeExpiredPendingOrders(userId);

        UpMaterial material = upMaterialService.getById(dto.getMaterialId());
        if (material == null || !MATERIAL_ONLINE.equals(material.getStatus())) {
            throw new BusinessException("资料不存在或未上架");
        }

        long paidCount = upMaterialOrderService.lambdaQuery()
                .eq(UpMaterialOrder::getUserId, userId)
                .eq(UpMaterialOrder::getMaterialId, material.getId())
                .in(UpMaterialOrder::getStatus, ORDER_PAID, ORDER_COMPLETED)
                .count();
        if (paidCount > 0) {
            throw new BusinessException("该资料已购买，请到已购资源查看");
        }

        UpMaterialOrder pendingOrder = upMaterialOrderService.lambdaQuery()
                .eq(UpMaterialOrder::getUserId, userId)
                .eq(UpMaterialOrder::getMaterialId, material.getId())
                .eq(UpMaterialOrder::getStatus, ORDER_PENDING)
                .orderByDesc(UpMaterialOrder::getCreateTime)
                .last("LIMIT 1")
                .one();
        if (pendingOrder != null) {
            return toCreateOrderVO(pendingOrder);
        }

        UpMaterialOrder order = new UpMaterialOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setMaterialId(material.getId());
        order.setMaterialTitle(material.getTitle());
        order.setMaterialCover(material.getCoverImage());
        order.setPayAmount(YES.equals(material.getIsFree()) ? BigDecimal.ZERO : safePrice(material.getPrice()));
        order.setStatus(ORDER_PENDING);
        order.setDownloadCount(0);
        order.setMaxDownloadCount(defaultMaxDownloadCount(material.getMaxDownloadCount()));
        upMaterialOrderService.save(order);

        if (YES.equals(material.getIsFree())) {
            completeOrder(order, PAY_ALIPAY, "FREE-" + order.getOrderNo());
        }
        return toCreateOrderVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MaterialPayInitVO payOrder(String orderNo, MaterialOrderPayDTO dto) {
        Long userId = ensureLogin();
        closeExpiredPendingOrders(userId);
        UpMaterialOrder order = getOrderByOrderNo(orderNo);
        if (!Objects.equals(order.getUserId(), userId)) {
            throw new BusinessException("无权操作该订单");
        }
        if (ORDER_COMPLETED.equals(order.getStatus())) {
            MaterialPayInitVO vo = new MaterialPayInitVO();
            vo.setOrderNo(order.getOrderNo());
            vo.setPayType(order.getPayType());
            return vo;
        }
        if (!ORDER_PENDING.equals(order.getStatus())) {
            throw new BusinessException("当前订单状态不允许支付");
        }
        String payType = normalizePayType(dto.getPayType());
        order.setPayType(payType);
        upMaterialOrderService.updateById(order);
        return materialPaymentGatewayService.initiatePay(order, payType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleAlipayNotify(Map<String, String> params) {
        MaterialPaymentGatewayService.AlipayNotifyResult notifyResult = materialPaymentGatewayService.parseAlipayNotify(params);
        if (!notifyResult.isSuccess()) {
            return;
        }
        UpMaterialOrder order = getOrderByOrderNo(notifyResult.getOutTradeNo());
        if (ORDER_COMPLETED.equals(order.getStatus())) {
            return;
        }
        if (order.getPayAmount() != null && notifyResult.getTotalAmount() != null) {
            BigDecimal callbackAmount = new BigDecimal(notifyResult.getTotalAmount());
            if (order.getPayAmount().compareTo(callbackAmount) != 0) {
                throw new BusinessException("支付宝回调金额不匹配");
            }
        }
        markOrderPaidAndDelivered(order, PAY_ALIPAY, notifyResult.getTradeNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleWechatNotify(String timestamp, String nonce, String signature, String serial, String requestBody) {
        MaterialPaymentGatewayService.WechatNotifyResult notifyResult =
                materialPaymentGatewayService.parseWechatNotify(timestamp, nonce, signature, serial, requestBody);
        if (!notifyResult.isSuccess()) {
            return;
        }
        UpMaterialOrder order = getOrderByOrderNo(notifyResult.getOutTradeNo());
        if (ORDER_COMPLETED.equals(order.getStatus())) {
            return;
        }
        markOrderPaidAndDelivered(order, PAY_WECHAT, notifyResult.getTransactionId());
    }

    @Override
    public IPage<MaterialOrderVO> myOrders(MaterialOrderQueryDTO queryDTO) {
        Long userId = ensureLogin();
        closeExpiredPendingOrders(userId);
        LambdaQueryWrapper<UpMaterialOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UpMaterialOrder::getUserId, userId);
        if (queryDTO.getStatus() != null && !queryDTO.getStatus().trim().isEmpty()) {
            wrapper.eq(UpMaterialOrder::getStatus, queryDTO.getStatus().trim().toUpperCase());
        }
        wrapper.orderByDesc(UpMaterialOrder::getCreateTime);
        IPage<UpMaterialOrder> page = upMaterialOrderService.page(new Page<>(queryDTO.getCurrent(), queryDTO.getSize()), wrapper);
        return toOrderPage(page, Collections.emptyMap(), false);
    }

    @Override
    public IPage<PurchasedMaterialVO> purchased(MaterialPurchasedQueryDTO queryDTO) {
        Long userId = ensureLogin();
        LambdaQueryWrapper<UpMaterialOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UpMaterialOrder::getUserId, userId)
                .eq(UpMaterialOrder::getStatus, ORDER_COMPLETED);
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().trim().isEmpty()) {
            wrapper.like(UpMaterialOrder::getMaterialTitle, queryDTO.getKeyword().trim());
        }
        wrapper.orderByDesc(UpMaterialOrder::getDeliverTime).orderByDesc(UpMaterialOrder::getCreateTime);
        IPage<UpMaterialOrder> page = upMaterialOrderService.page(new Page<>(queryDTO.getCurrent(), queryDTO.getSize()), wrapper);

        Set<Long> materialIds = page.getRecords().stream().map(UpMaterialOrder::getMaterialId).collect(Collectors.toSet());
        Map<Long, UpMaterial> materialMap = materialIds.isEmpty()
                ? Collections.emptyMap()
                : upMaterialService.lambdaQuery().in(UpMaterial::getId, materialIds).list().stream()
                .collect(Collectors.toMap(UpMaterial::getId, item -> item));

        Page<PurchasedMaterialVO> result = new Page<>(queryDTO.getCurrent(), queryDTO.getSize(), page.getTotal());
        List<PurchasedMaterialVO> records = new ArrayList<>();
        for (UpMaterialOrder order : page.getRecords()) {
            PurchasedMaterialVO vo = new PurchasedMaterialVO();
            vo.setOrderNo(order.getOrderNo());
            vo.setMaterialId(order.getMaterialId());
            vo.setMaterialTitle(order.getMaterialTitle());
            vo.setMaterialCover(order.getMaterialCover());
            vo.setDownloadCount(defaultZero(order.getDownloadCount()));
            vo.setMaxDownloadCount(defaultMaxDownloadCount(order.getMaxDownloadCount()));
            vo.setPurchaseTime(order.getDeliverTime() == null ? order.getPayTime() : order.getDeliverTime());
            UpMaterial material = materialMap.get(order.getMaterialId());
            vo.setFileUrl(material == null ? null : material.getFileUrl());
            records.add(vo);
        }
        result.setRecords(records);
        return result;
    }

    @Override
    public MaterialOrderVO myOrderDetail(String orderNo) {
        Long userId = ensureLogin();
        closeExpiredPendingOrders(userId);
        UpMaterialOrder order = getOrderByOrderNo(orderNo);
        if (!Objects.equals(order.getUserId(), userId)) {
            throw new BusinessException("无权查看该订单");
        }
        Map<Long, UpMaterial> materialMap = upMaterialService.lambdaQuery()
                .eq(UpMaterial::getId, order.getMaterialId()).list().stream()
                .collect(Collectors.toMap(UpMaterial::getId, item -> item));
        return toOrderVO(order, Collections.emptyMap(), true, materialMap);
    }

    @Override
    public IPage<UpMaterial> manageList(MaterialManageListQueryDTO queryDTO) {
        LambdaQueryWrapper<UpMaterial> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().trim().isEmpty()) {
            String keyword = queryDTO.getKeyword().trim();
            wrapper.and(w -> w.like(UpMaterial::getTitle, keyword).or().like(UpMaterial::getSummary, keyword));
        }
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(UpMaterial::getCategoryId, queryDTO.getCategoryId());
        }
        if (queryDTO.getStatus() != null && !queryDTO.getStatus().trim().isEmpty()) {
            wrapper.eq(UpMaterial::getStatus, queryDTO.getStatus().trim());
        }
        wrapper.orderByDesc(UpMaterial::getPublishTime).orderByDesc(UpMaterial::getCreateTime);
        return upMaterialService.page(new Page<>(queryDTO.getCurrent(), queryDTO.getSize()), wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMaterial(MaterialPublishDTO dto) {
        assertCategoryExists(dto.getCategoryId());
        UpMaterial material = new UpMaterial();
        fillMaterial(material, dto);
        material.setSalesCount(0);
        material.setPublishTime(LocalDateTime.now());
        upMaterialService.save(material);
        return material.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMaterial(Long id, MaterialPublishDTO dto) {
        UpMaterial material = upMaterialService.getById(id);
        if (material == null) {
            throw new BusinessException("资料不存在");
        }
        assertCategoryExists(dto.getCategoryId());
        fillMaterial(material, dto);
        upMaterialService.updateById(material);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMaterial(Long id) {
        UpMaterial material = upMaterialService.getById(id);
        if (material == null) {
            return;
        }
        long soldCount = upMaterialOrderService.lambdaQuery()
                .eq(UpMaterialOrder::getMaterialId, id)
                .in(UpMaterialOrder::getStatus, ORDER_PAID, ORDER_COMPLETED)
                .count();
        if (soldCount > 0) {
            throw new BusinessException("已售出资料仅支持下架，不允许删除");
        }
        upMaterialService.removeById(id);
        upMaterialOrderService.lambdaUpdate().eq(UpMaterialOrder::getMaterialId, id).remove();
    }

    @Override
    public List<UpMaterialCategory> manageCategories() {
        return upMaterialCategoryService.lambdaQuery()
                .orderByAsc(UpMaterialCategory::getParentId)
                .orderByAsc(UpMaterialCategory::getSortNum)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createCategory(MaterialCategorySaveDTO dto) {
        UpMaterialCategory category = new UpMaterialCategory();
        category.setCategoryName(dto.getCategoryName().trim());
        category.setParentId(dto.getParentId());
        category.setSortNum(dto.getSortNum());
        category.setStatus(normalizeCategoryStatus(dto.getStatus()));
        upMaterialCategoryService.save(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(MaterialCategorySaveDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("分类ID不能为空");
        }
        UpMaterialCategory category = upMaterialCategoryService.getById(dto.getId());
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        category.setCategoryName(dto.getCategoryName().trim());
        category.setParentId(dto.getParentId());
        category.setSortNum(dto.getSortNum());
        category.setStatus(normalizeCategoryStatus(dto.getStatus()));
        upMaterialCategoryService.updateById(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        long count = upMaterialService.lambdaQuery().eq(UpMaterial::getCategoryId, id).count();
        if (count > 0) {
            throw new BusinessException("分类下存在资料，无法删除");
        }
        upMaterialCategoryService.removeById(id);
    }

    @Override
    public IPage<MaterialOrderVO> manageOrders(MaterialManageOrderQueryDTO queryDTO) {
        closeExpiredPendingOrders(null);
        LambdaQueryWrapper<UpMaterialOrder> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO.getOrderNo() != null && !queryDTO.getOrderNo().trim().isEmpty()) {
            wrapper.like(UpMaterialOrder::getOrderNo, queryDTO.getOrderNo().trim());
        }
        if (queryDTO.getStatus() != null && !queryDTO.getStatus().trim().isEmpty()) {
            wrapper.eq(UpMaterialOrder::getStatus, queryDTO.getStatus().trim().toUpperCase());
        }
        if (queryDTO.getPayType() != null && !queryDTO.getPayType().trim().isEmpty()) {
            wrapper.eq(UpMaterialOrder::getPayType, queryDTO.getPayType().trim().toUpperCase());
        }
        if (queryDTO.getBeginTime() != null) {
            wrapper.ge(UpMaterialOrder::getCreateTime, queryDTO.getBeginTime());
        }
        if (queryDTO.getEndTime() != null) {
            wrapper.le(UpMaterialOrder::getCreateTime, queryDTO.getEndTime());
        }

        Map<Long, String> userMap = Collections.emptyMap();
        if (queryDTO.getUsername() != null && !queryDTO.getUsername().trim().isEmpty()) {
            String keyword = queryDTO.getUsername().trim();
            List<SysUser> users = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                    .like(SysUser::getUsername, keyword));
            if (users.isEmpty()) {
                return new Page<>(queryDTO.getCurrent(), queryDTO.getSize(), 0);
            }
            List<Long> userIds = users.stream().map(SysUser::getId).collect(Collectors.toList());
            wrapper.in(UpMaterialOrder::getUserId, userIds);
        }

        wrapper.orderByDesc(UpMaterialOrder::getCreateTime);
        IPage<UpMaterialOrder> page = upMaterialOrderService.page(new Page<>(queryDTO.getCurrent(), queryDTO.getSize()), wrapper);
        if (!page.getRecords().isEmpty()) {
            Set<Long> userIds = page.getRecords().stream().map(UpMaterialOrder::getUserId).collect(Collectors.toSet());
            userMap = userIds.isEmpty() ? Collections.emptyMap() : sysUserMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(SysUser::getId, SysUser::getUsername));
        }
        return toOrderPage(page, userMap, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resendMaterial(String orderNo) {
        UpMaterialOrder order = getOrderByOrderNo(orderNo);
        if (ORDER_COMPLETED.equals(order.getStatus())) {
            return;
        }
        if (!ORDER_PAID.equals(order.getStatus())) {
            throw new BusinessException("仅已支付订单支持补发货");
        }
        order.setStatus(ORDER_COMPLETED);
        order.setDeliverTime(LocalDateTime.now());
        upMaterialOrderService.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeOrder(String orderNo) {
        UpMaterialOrder order = getOrderByOrderNo(orderNo);
        if (ORDER_COMPLETED.equals(order.getStatus())) {
            throw new BusinessException("已完成订单不允许关闭");
        }
        if (ORDER_CLOSED.equals(order.getStatus())) {
            return;
        }
        order.setStatus(ORDER_CLOSED);
        order.setCloseTime(LocalDateTime.now());
        upMaterialOrderService.updateById(order);
    }

    private void fillMaterial(UpMaterial material, MaterialPublishDTO dto) {
        material.setTitle(dto.getTitle().trim());
        material.setSummary(dto.getSummary());
        material.setDetailContent(dto.getDetailContent());
        material.setCoverImage(dto.getCoverImage());
        material.setPreviewUrl(dto.getPreviewUrl());
        material.setFileUrl(dto.getFileUrl());
        material.setFileFormat(dto.getFileFormat());
        material.setFileSize(dto.getFileSize());
        material.setPrice(safePrice(dto.getPrice()));
        material.setTags(dto.getTags());
        material.setCategoryId(dto.getCategoryId());
        material.setStatus(normalizeMaterialStatus(dto.getStatus()));
        material.setMaxDownloadCount(defaultMaxDownloadCount(dto.getMaxDownloadCount()));
        material.setIsFree(YES.equals(dto.getIsFree()) || BigDecimal.ZERO.compareTo(dto.getPrice()) == 0 ? YES : "0");
    }

    private void completeOrder(UpMaterialOrder order, String payType, String tradeNo) {
        markOrderPaidAndDelivered(order, payType, tradeNo);
    }

    private void markOrderPaidAndDelivered(UpMaterialOrder order, String payType, String tradeNo) {
        order.setPayType(payType);
        order.setPayTradeNo(tradeNo);
        order.setPayTime(LocalDateTime.now());
        order.setStatus(ORDER_PAID);
        upMaterialOrderService.updateById(order);

        order.setStatus(ORDER_COMPLETED);
        order.setDeliverTime(LocalDateTime.now());
        upMaterialOrderService.updateById(order);

        upMaterialService.lambdaUpdate()
                .eq(UpMaterial::getId, order.getMaterialId())
                .setSql("sales_count = IFNULL(sales_count, 0) + 1")
                .update();
    }

    private void assertCategoryExists(Long categoryId) {
        UpMaterialCategory category = upMaterialCategoryService.getById(categoryId);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
    }

    private UpMaterialOrder getOrderByOrderNo(String orderNo) {
        UpMaterialOrder order = upMaterialOrderService.lambdaQuery()
                .eq(UpMaterialOrder::getOrderNo, orderNo)
                .one();
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    private IPage<MaterialOrderVO> toOrderPage(IPage<UpMaterialOrder> page,
                                                Map<Long, String> userMap,
                                                boolean includeFileUrl) {
        Set<Long> materialIds = page.getRecords().stream().map(UpMaterialOrder::getMaterialId).collect(Collectors.toSet());
        Map<Long, UpMaterial> materialMap = materialIds.isEmpty()
                ? Collections.emptyMap()
                : upMaterialService.lambdaQuery().in(UpMaterial::getId, materialIds).list().stream()
                .collect(Collectors.toMap(UpMaterial::getId, item -> item));

        Page<MaterialOrderVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<MaterialOrderVO> records = page.getRecords().stream()
                .map(order -> toOrderVO(order, userMap, includeFileUrl, materialMap))
                .collect(Collectors.toList());
        result.setRecords(records);
        return result;
    }

    private MaterialOrderVO toOrderVO(UpMaterialOrder order,
                                      Map<Long, String> userMap,
                                      boolean includeFileUrl,
                                      Map<Long, UpMaterial> materialMap) {
        MaterialOrderVO vo = new MaterialOrderVO();
        BeanUtils.copyProperties(order, vo);
        vo.setUsername(userMap.get(order.getUserId()));
        vo.setDownloadCount(defaultZero(order.getDownloadCount()));
        vo.setMaxDownloadCount(defaultMaxDownloadCount(order.getMaxDownloadCount()));
        if (includeFileUrl && ORDER_COMPLETED.equals(order.getStatus())) {
            UpMaterial material = materialMap.get(order.getMaterialId());
            vo.setFileUrl(material == null ? null : material.getFileUrl());
        }
        return vo;
    }

    private Map<Long, String> getCategoryNameMap() {
        return upMaterialCategoryService.lambdaQuery().list().stream()
                .collect(Collectors.toMap(UpMaterialCategory::getId, UpMaterialCategory::getCategoryName, (a, b) -> a, LinkedHashMap::new));
    }

    private MaterialCardVO toCardVO(UpMaterial material, Map<Long, String> categoryNameMap) {
        MaterialCardVO vo = new MaterialCardVO();
        vo.setId(material.getId());
        vo.setTitle(material.getTitle());
        vo.setSummary(material.getSummary());
        vo.setCoverImage(material.getCoverImage());
        vo.setTags(material.getTags());
        vo.setCategoryId(material.getCategoryId());
        vo.setCategory(categoryNameMap.getOrDefault(material.getCategoryId(), "未分类"));
        vo.setPrice(safePrice(material.getPrice()));
        vo.setFree(YES.equals(material.getIsFree()));
        vo.setSalesCount(defaultZero(material.getSalesCount()));
        vo.setPublishTime(material.getPublishTime() == null ? material.getCreateTime() : material.getPublishTime());
        return vo;
    }

    private MaterialCreateOrderVO toCreateOrderVO(UpMaterialOrder order) {
        MaterialCreateOrderVO vo = new MaterialCreateOrderVO();
        vo.setOrderNo(order.getOrderNo());
        vo.setMaterialId(order.getMaterialId());
        vo.setMaterialTitle(order.getMaterialTitle());
        vo.setPayAmount(order.getPayAmount());
        vo.setPayMethods(Arrays.asList(PAY_WECHAT, PAY_ALIPAY));
        return vo;
    }

    private void closeExpiredPendingOrders(Long userId) {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(30);
        LambdaQueryWrapper<UpMaterialOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UpMaterialOrder::getStatus, ORDER_PENDING).lt(UpMaterialOrder::getCreateTime, deadline);
        if (userId != null) {
            wrapper.eq(UpMaterialOrder::getUserId, userId);
        }
        List<UpMaterialOrder> orders = upMaterialOrderService.list(wrapper);
        for (UpMaterialOrder order : orders) {
            order.setStatus(ORDER_CLOSED);
            order.setCloseTime(LocalDateTime.now());
        }
        if (!orders.isEmpty()) {
            upMaterialOrderService.updateBatchById(orders);
        }
    }

    private Long ensureLogin() {
        Long userId = CurrentUserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return userId;
    }

    private String normalizeMaterialStatus(String status) {
        if (MATERIAL_DRAFT.equals(status) || MATERIAL_OFFLINE.equals(status)) {
            return status;
        }
        return MATERIAL_ONLINE;
    }

    private String normalizeCategoryStatus(String status) {
        return CATEGORY_NORMAL.equals(status) ? CATEGORY_NORMAL : "1";
    }

    private String normalizePayType(String payType) {
        if (payType == null || payType.trim().isEmpty()) {
            throw new BusinessException("支付方式不能为空");
        }
        String value = payType.trim().toUpperCase();
        if (!PAY_WECHAT.equals(value) && !PAY_ALIPAY.equals(value)) {
            throw new BusinessException("仅支持微信或支付宝支付");
        }
        return value;
    }

    private String generateOrderNo() {
        String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = 100000 + new Random().nextInt(900000);
        return "M" + timePart + random;
    }

    private int defaultZero(Integer value) {
        return value == null ? 0 : value;
    }

    private int defaultMaxDownloadCount(Integer value) {
        if (value == null || value <= 0) {
            return 99;
        }
        return value;
    }

    private BigDecimal safePrice(BigDecimal price) {
        return price == null ? BigDecimal.ZERO : price.max(BigDecimal.ZERO);
    }
}
