package com.example.javaweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.javaweb.dto.NewsCommentCreateDTO;
import com.example.javaweb.dto.PageQueryDTO;
import com.example.javaweb.dto.PortalNewsListQueryDTO;
import com.example.javaweb.dto.PortalNewsManageCommentQueryDTO;
import com.example.javaweb.dto.PortalNewsManageListQueryDTO;
import com.example.javaweb.dto.NewsPublishDTO;
import com.example.javaweb.entity.UpNews;
import com.example.javaweb.entity.UpNewsCategory;
import com.example.javaweb.vo.news.NewsCardVO;
import com.example.javaweb.vo.news.NewsCategoryVO;
import com.example.javaweb.vo.news.NewsCommentVO;
import com.example.javaweb.vo.news.NewsDetailVO;

import java.util.List;
import java.util.Map;

public interface PortalNewsService {

    Map<String, Object> home(String token);

    List<NewsCategoryVO> categories();

    IPage<NewsCardVO> list(PortalNewsListQueryDTO queryDTO, String token);

    NewsDetailVO detail(Long id, String token);

    Long publish(NewsPublishDTO dto);

    IPage<UpNews> manageList(PortalNewsManageListQueryDTO queryDTO);

    void updateNews(Long id, NewsPublishDTO dto);

    void deleteNews(Long id);

    List<UpNewsCategory> manageCategories();

    void addCategory(UpNewsCategory category);

    void updateCategory(UpNewsCategory category);

    void deleteCategory(Long id);

    IPage<Map<String, Object>> manageComments(PortalNewsManageCommentQueryDTO queryDTO);

    void deleteComment(Long id);

    void like(Long id);

    void unlike(Long id);

    void favorite(Long id);

    void unfavorite(Long id);

    List<NewsCommentVO> comments(Long id);

    void addComment(Long id, NewsCommentCreateDTO dto);

    IPage<NewsCardVO> myFavorites(PageQueryDTO queryDTO);

    IPage<NewsCardVO> myHistory(PageQueryDTO queryDTO);

    void clearHistory();
}
