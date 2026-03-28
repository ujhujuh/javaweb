package com.example.javaweb.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.javaweb.common.log.ApiLog;
import com.example.javaweb.common.result.Result;
import com.example.javaweb.dto.NewsCommentCreateDTO;
import com.example.javaweb.dto.NewsPublishDTO;
import com.example.javaweb.dto.PageQueryDTO;
import com.example.javaweb.dto.PortalNewsListQueryDTO;
import com.example.javaweb.dto.PortalNewsManageCommentQueryDTO;
import com.example.javaweb.dto.PortalNewsManageListQueryDTO;
import com.example.javaweb.entity.UpNews;
import com.example.javaweb.entity.UpNewsCategory;
import com.example.javaweb.service.PortalNewsService;
import com.example.javaweb.vo.news.NewsCardVO;
import com.example.javaweb.vo.news.NewsCategoryVO;
import com.example.javaweb.vo.news.NewsCommentVO;
import com.example.javaweb.vo.news.NewsDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/portal/news")
public class PortalNewsController {

    @Autowired
    private PortalNewsService portalNewsService;

    @ApiLog("资讯门户首页聚合")
    @GetMapping("/home")
    public Result<Map<String, Object>> home() {
        return Result.success(portalNewsService.home());
    }

    @ApiLog("资讯分类")
    @GetMapping("/categories")
    public Result<List<NewsCategoryVO>> categories() {
        return Result.success(portalNewsService.categories());
    }

    @ApiLog("资讯列表")
    @GetMapping("/list")
    public Result<IPage<NewsCardVO>> list(PortalNewsListQueryDTO queryDTO) {
        return Result.success(portalNewsService.list(queryDTO));
    }

    @ApiLog("资讯详情")
    @GetMapping("/detail/{id}")
    public Result<NewsDetailVO> detail(@PathVariable Long id) {
        return Result.success(portalNewsService.detail(id));
    }

    @ApiLog("发布资讯")
    @PostMapping("/publish")
    public Result<Long> publish(@Validated @RequestBody NewsPublishDTO dto) {
        return Result.success(portalNewsService.publish(dto));
    }

    @ApiLog("管理端资讯列表")
    @GetMapping("/manage/list")
    public Result<IPage<UpNews>> manageList(PortalNewsManageListQueryDTO queryDTO) {
        return Result.success(portalNewsService.manageList(queryDTO));
    }

    @ApiLog("管理端更新资讯")
    @PutMapping("/manage/{id}")
    public Result<Void> updateNews(@PathVariable Long id,
                                   @Validated @RequestBody NewsPublishDTO dto) {
        portalNewsService.updateNews(id, dto);
        return Result.success();
    }

    @ApiLog("管理端删除资讯")
    @DeleteMapping("/manage/{id}")
    public Result<Void> deleteNews(@PathVariable Long id) {
        portalNewsService.deleteNews(id);
        return Result.success();
    }

    @ApiLog("管理端分类列表")
    @GetMapping("/manage/categories")
    public Result<List<UpNewsCategory>> manageCategories() {
        return Result.success(portalNewsService.manageCategories());
    }

    @ApiLog("管理端新增分类")
    @PostMapping("/manage/categories")
    public Result<Void> addCategory(@RequestBody UpNewsCategory category) {
        portalNewsService.addCategory(category);
        return Result.success();
    }

    @ApiLog("管理端修改分类")
    @PutMapping("/manage/categories")
    public Result<Void> updateCategory(@RequestBody UpNewsCategory category) {
        portalNewsService.updateCategory(category);
        return Result.success();
    }

    @ApiLog("管理端删除分类")
    @DeleteMapping("/manage/categories/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        portalNewsService.deleteCategory(id);
        return Result.success();
    }

    @ApiLog("管理端评论分页")
    @GetMapping("/manage/comments")
    public Result<IPage<Map<String, Object>>> manageComments(PortalNewsManageCommentQueryDTO queryDTO) {
        return Result.success(portalNewsService.manageComments(queryDTO));
    }

    @ApiLog("管理端删除评论")
    @DeleteMapping("/manage/comments/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        portalNewsService.deleteComment(id);
        return Result.success();
    }

    @ApiLog("资讯点赞")
    @PostMapping("/{id}/like")
    public Result<Void> like(@PathVariable Long id) {
        portalNewsService.like(id);
        return Result.success();
    }

    @ApiLog("取消资讯点赞")
    @DeleteMapping("/{id}/like")
    public Result<Void> unlike(@PathVariable Long id) {
        portalNewsService.unlike(id);
        return Result.success();
    }

    @ApiLog("资讯收藏")
    @PostMapping("/{id}/favorite")
    public Result<Void> favorite(@PathVariable Long id) {
        portalNewsService.favorite(id);
        return Result.success();
    }

    @ApiLog("取消资讯收藏")
    @DeleteMapping("/{id}/favorite")
    public Result<Void> unfavorite(@PathVariable Long id) {
        portalNewsService.unfavorite(id);
        return Result.success();
    }

    @ApiLog("资讯评论列表")
    @GetMapping("/{id}/comments")
    public Result<List<NewsCommentVO>> comments(@PathVariable Long id) {
        return Result.success(portalNewsService.comments(id));
    }

    @ApiLog("新增资讯评论")
    @PostMapping("/{id}/comments")
    public Result<Void> addComment(@PathVariable Long id, @Validated @RequestBody NewsCommentCreateDTO dto) {
        portalNewsService.addComment(id, dto);
        return Result.success();
    }

    @ApiLog("我的收藏")
    @GetMapping("/my/favorites")
    public Result<IPage<NewsCardVO>> myFavorites(PageQueryDTO queryDTO) {
        return Result.success(portalNewsService.myFavorites(queryDTO));
    }

    @ApiLog("我的浏览历史")
    @GetMapping("/my/history")
    public Result<IPage<NewsCardVO>> myHistory(PageQueryDTO queryDTO) {
        return Result.success(portalNewsService.myHistory(queryDTO));
    }

    @ApiLog("清空浏览历史")
    @DeleteMapping("/my/history")
    public Result<Void> clearHistory() {
        portalNewsService.clearHistory();
        return Result.success();
    }
}
