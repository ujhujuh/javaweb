package com.example.javaweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
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
import com.example.javaweb.vo.material.MaterialCardVO;
import com.example.javaweb.vo.material.MaterialCategoryVO;
import com.example.javaweb.vo.material.MaterialCreateOrderVO;
import com.example.javaweb.vo.material.MaterialDetailVO;
import com.example.javaweb.vo.material.MaterialOrderVO;
import com.example.javaweb.vo.material.PurchasedMaterialVO;

import java.util.List;

public interface PortalMaterialService {

    List<MaterialCategoryVO> categories();

    IPage<MaterialCardVO> list(PortalMaterialListQueryDTO queryDTO);

    MaterialDetailVO detail(Long id);

    MaterialCreateOrderVO createOrder(MaterialOrderCreateDTO dto);

    void payOrder(String orderNo, MaterialOrderPayDTO dto);

    IPage<MaterialOrderVO> myOrders(MaterialOrderQueryDTO queryDTO);

    IPage<PurchasedMaterialVO> purchased(MaterialPurchasedQueryDTO queryDTO);

    MaterialOrderVO myOrderDetail(String orderNo);

    IPage<UpMaterial> manageList(MaterialManageListQueryDTO queryDTO);

    Long createMaterial(MaterialPublishDTO dto);

    void updateMaterial(Long id, MaterialPublishDTO dto);

    void deleteMaterial(Long id);

    List<UpMaterialCategory> manageCategories();

    void createCategory(MaterialCategorySaveDTO dto);

    void updateCategory(MaterialCategorySaveDTO dto);

    void deleteCategory(Long id);

    IPage<MaterialOrderVO> manageOrders(MaterialManageOrderQueryDTO queryDTO);

    void resendMaterial(String orderNo);

    void closeOrder(String orderNo);
}
