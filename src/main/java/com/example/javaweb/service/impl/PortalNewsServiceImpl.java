package com.example.javaweb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javaweb.common.exception.BusinessException;
import com.example.javaweb.dto.NewsCommentCreateDTO;
import com.example.javaweb.dto.NewsPublishDTO;
import com.example.javaweb.dto.PageQueryDTO;
import com.example.javaweb.dto.PortalNewsListQueryDTO;
import com.example.javaweb.dto.PortalNewsManageCommentQueryDTO;
import com.example.javaweb.dto.PortalNewsManageListQueryDTO;
import com.example.javaweb.entity.SysUser;
import com.example.javaweb.entity.UpBrowserBehavior;
import com.example.javaweb.entity.UpNews;
import com.example.javaweb.entity.UpNewsCategory;
import com.example.javaweb.entity.UpNewsComment;
import com.example.javaweb.entity.UpNewsFavorite;
import com.example.javaweb.entity.UpNewsLike;
import com.example.javaweb.mapper.SysUserMapper;
import com.example.javaweb.service.PortalNewsService;
import com.example.javaweb.service.UpBrowserBehaviorService;
import com.example.javaweb.service.UpNewsCategoryService;
import com.example.javaweb.service.UpNewsCommentService;
import com.example.javaweb.service.UpNewsFavoriteService;
import com.example.javaweb.service.UpNewsLikeService;
import com.example.javaweb.service.UpNewsService;
import com.example.javaweb.util.JwtUtil;
import com.example.javaweb.vo.news.NewsCardVO;
import com.example.javaweb.vo.news.NewsCategoryVO;
import com.example.javaweb.vo.news.NewsCommentVO;
import com.example.javaweb.vo.news.NewsDetailVO;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PortalNewsServiceImpl implements PortalNewsService {

    @Autowired
    private UpNewsService upNewsService;

    @Autowired
    private UpNewsCategoryService upNewsCategoryService;

    @Autowired
    private UpNewsLikeService upNewsLikeService;

    @Autowired
    private UpNewsFavoriteService upNewsFavoriteService;

    @Autowired
    private UpNewsCommentService upNewsCommentService;

    @Autowired
    private UpBrowserBehaviorService upBrowserBehaviorService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Map<String, Object> home(String token) {
        Long userId = getUserIdFromToken(token);
        Map<Long, String> categoryMap = getCategoryNameMap();

        List<UpNews> bannerList = upNewsService.lambdaQuery()
                .eq(UpNews::getStatus, "1")
                .eq(UpNews::getIsTop, "1")
                .orderByDesc(UpNews::getPublishTime)
                .last("LIMIT 6")
                .list();

        List<UpNews> featuredList = upNewsService.lambdaQuery()
                .eq(UpNews::getStatus, "1")
                .orderByDesc(UpNews::getIsTop)
                .orderByDesc(UpNews::getPublishTime)
                .last("LIMIT 8")
                .list();

        List<UpNews> latestList = upNewsService.lambdaQuery()
                .eq(UpNews::getStatus, "1")
                .orderByDesc(UpNews::getPublishTime)
                .last("LIMIT 10")
                .list();

        List<UpNews> hotList = upNewsService.lambdaQuery()
                .eq(UpNews::getStatus, "1")
                .orderByDesc(UpNews::getViewCount)
                .orderByDesc(UpNews::getPublishTime)
                .last("LIMIT 10")
                .list();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("banner", toCardList(bannerList, userId, categoryMap));
        data.put("featured", toCardList(featuredList, userId, categoryMap));
        data.put("latest", toCardList(latestList, userId, categoryMap));
        data.put("hot", toCardList(hotList, userId, categoryMap));
        return data;
    }

    @Override
    public List<NewsCategoryVO> categories() {
        List<UpNewsCategory> categories = upNewsCategoryService.lambdaQuery()
                .eq(UpNewsCategory::getStatus, "0")
                .orderByAsc(UpNewsCategory::getParentId)
                .orderByAsc(UpNewsCategory::getSortNum)
                .list();

        List<NewsCategoryVO> data = new ArrayList<>();
        for (UpNewsCategory category : categories) {
            NewsCategoryVO vo = new NewsCategoryVO();
            vo.setId(category.getId());
            vo.setName(category.getCategoryName());
            vo.setParentId(category.getParentId());
            vo.setCount(upNewsService.lambdaQuery()
                    .eq(UpNews::getStatus, "1")
                    .eq(UpNews::getCategoryId, category.getId())
                    .count());
            data.add(vo);
        }
        return data;
    }

    @Override
    public IPage<NewsCardVO> list(PortalNewsListQueryDTO queryDTO, String token) {
        Integer current = queryDTO.getCurrent();
        Integer size = queryDTO.getSize();
        String keyword = queryDTO.getKeyword();
        Long categoryId = queryDTO.getCategoryId();
        String sort = queryDTO.getSort();
        Long userId = getUserIdFromToken(token);
        Map<Long, String> categoryMap = getCategoryNameMap();

        LambdaQueryWrapper<UpNews> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UpNews::getStatus, "1");

        if (keyword != null && !keyword.trim().isEmpty()) {
            String likeValue = keyword.trim();
            wrapper.and(w -> w.like(UpNews::getTitle, likeValue)
                    .or().like(UpNews::getSummary, likeValue)
                    .or().like(UpNews::getTags, likeValue)
                    .or().like(UpNews::getContent, likeValue));
        }

        if (categoryId != null) {
            wrapper.eq(UpNews::getCategoryId, categoryId);
        }

        String orderType = sort == null ? "latest" : sort.toLowerCase();
        if ("hot".equals(orderType)) {
            wrapper.orderByDesc(UpNews::getViewCount).orderByDesc(UpNews::getPublishTime);
        } else if ("top".equals(orderType)) {
            wrapper.orderByDesc(UpNews::getIsTop).orderByDesc(UpNews::getPublishTime);
        } else {
            wrapper.orderByDesc(UpNews::getIsTop).orderByDesc(UpNews::getPublishTime);
        }

        IPage<UpNews> newsPage = upNewsService.page(new Page<>(current, size), wrapper);
        List<NewsCardVO> cards = toCardList(newsPage.getRecords(), userId, categoryMap);

        Page<NewsCardVO> page = new Page<>(current, size, newsPage.getTotal());
        page.setRecords(cards);
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NewsDetailVO detail(Long id, String token) {
        UpNews news = upNewsService.lambdaQuery().eq(UpNews::getId, id).eq(UpNews::getStatus, "1").one();
        if (news == null) {
            throw new BusinessException("资讯不存在或已下线");
        }

        Long userId = getUserIdFromToken(token);
        boolean locked = isLoginVisible(news.getVisibleScope()) && userId == null;

        Map<Long, String> categoryMap = getCategoryNameMap();
        NewsDetailVO vo = new NewsDetailVO();
        vo.setId(news.getId());
        vo.setTitle(news.getTitle());
        vo.setSummary(news.getSummary());
        vo.setCoverImage(news.getCoverImage());
        vo.setTags(news.getTags());
        vo.setCategoryId(news.getCategoryId());
        vo.setCategory(categoryMap.getOrDefault(news.getCategoryId(), "未分类"));
        vo.setAuthor(news.getCreateBy());
        vo.setPublishTime(news.getPublishTime() == null ? news.getCreateTime() : news.getPublishTime());
        vo.setViewCount(defaultInt(news.getViewCount()));
        vo.setLikeCount(defaultInt(news.getLikeCount()));
        vo.setFavoriteCount(defaultInt(news.getFavoriteCount()));
        vo.setCommentCount(defaultInt(news.getCommentCount()));
        vo.setLocked(locked);

        if (locked) {
            vo.setContent(buildPreviewContent(news.getContent(), news.getSummary()));
            vo.setLiked(false);
            vo.setFavorited(false);
            return vo;
        }

        vo.setContent(news.getContent());

        if (userId != null) {
            vo.setLiked(upNewsLikeService.lambdaQuery()
                    .eq(UpNewsLike::getUserId, userId)
                    .eq(UpNewsLike::getNewsId, id)
                    .count() > 0);
            vo.setFavorited(upNewsFavoriteService.lambdaQuery()
                    .eq(UpNewsFavorite::getUserId, userId)
                    .eq(UpNewsFavorite::getNewsId, id)
                    .count() > 0);
            saveHistory(userId, id, news.getTitle());
        } else {
            vo.setLiked(false);
            vo.setFavorited(false);
        }

        upNewsService.lambdaUpdate()
                .eq(UpNews::getId, id)
                .setSql("view_count = IFNULL(view_count, 0) + 1")
                .update();
        vo.setViewCount(vo.getViewCount() + 1);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long publish(NewsPublishDTO dto) {
        SysUser currentUser = requireLoginUser(null);

        UpNews news = new UpNews();
        news.setTitle(dto.getTitle().trim());
        news.setSummary(dto.getSummary());
        news.setContent(dto.getContent());
        news.setCoverImage(dto.getCoverImage());
        news.setTags(dto.getTags());
        news.setCategoryId(dto.getCategoryId());
        news.setVisibleScope(toVisibleScope(dto.getVisibleScope()));
        news.setStatus("0".equals(dto.getStatus()) ? "0" : "1");
        news.setIsTop("0");
        news.setViewCount(0);
        news.setLikeCount(0);
        news.setFavoriteCount(0);
        news.setCommentCount(0);
        news.setPublishTime(LocalDateTime.now());
        news.setCreateBy(currentUser.getUsername());
        upNewsService.save(news);

        return news.getId();
    }

    @Override
    public IPage<UpNews> manageList(PortalNewsManageListQueryDTO queryDTO) {
        requireLoginUser(null);
        Integer current = queryDTO.getCurrent();
        Integer size = queryDTO.getSize();
        String keyword = queryDTO.getKeyword();
        Long categoryId = queryDTO.getCategoryId();
        String status = queryDTO.getStatus();
        LambdaQueryWrapper<UpNews> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(UpNews::getTitle, keyword.trim())
                    .or().like(UpNews::getSummary, keyword.trim()));
        }
        if (categoryId != null) {
            wrapper.eq(UpNews::getCategoryId, categoryId);
        }
        if (status != null && !status.trim().isEmpty()) {
            wrapper.eq(UpNews::getStatus, status.trim());
        }
        wrapper.orderByDesc(UpNews::getPublishTime);
        return upNewsService.page(new Page<>(current, size), wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNews(Long id, NewsPublishDTO dto) {
        requireLoginUser(null);
        UpNews existing = upNewsService.getById(id);
        if (existing == null) {
            throw new BusinessException("资讯不存在");
        }
        existing.setTitle(dto.getTitle().trim());
        existing.setSummary(dto.getSummary());
        existing.setContent(dto.getContent());
        existing.setCoverImage(dto.getCoverImage());
        existing.setTags(dto.getTags());
        existing.setCategoryId(dto.getCategoryId());
        existing.setVisibleScope(toVisibleScope(dto.getVisibleScope()));
        existing.setStatus("0".equals(dto.getStatus()) ? "0" : "1");
        upNewsService.updateById(existing);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNews(Long id) {
        requireLoginUser(null);
        upNewsService.removeById(id);
        upNewsLikeService.lambdaUpdate().eq(UpNewsLike::getNewsId, id).remove();
        upNewsFavoriteService.lambdaUpdate().eq(UpNewsFavorite::getNewsId, id).remove();
        upNewsCommentService.lambdaUpdate().eq(UpNewsComment::getNewsId, id).remove();
    }

    @Override
    public List<UpNewsCategory> manageCategories() {
        requireLoginUser(null);
        return upNewsCategoryService.lambdaQuery()
                .orderByAsc(UpNewsCategory::getParentId)
                .orderByAsc(UpNewsCategory::getSortNum)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCategory(UpNewsCategory category) {
        requireLoginUser(null);
        category.setId(null);
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getSortNum() == null) {
            category.setSortNum(0);
        }
        if (category.getStatus() == null || category.getStatus().trim().isEmpty()) {
            category.setStatus("0");
        }
        upNewsCategoryService.save(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(UpNewsCategory category) {
        requireLoginUser(null);
        if (category.getId() == null) {
            throw new BusinessException("分类ID不能为空");
        }
        upNewsCategoryService.updateById(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        requireLoginUser(null);
        long newsCount = upNewsService.lambdaQuery().eq(UpNews::getCategoryId, id).count();
        if (newsCount > 0) {
            throw new BusinessException("该分类下存在资讯，无法删除");
        }
        upNewsCategoryService.removeById(id);
    }

    @Override
    public IPage<Map<String, Object>> manageComments(PortalNewsManageCommentQueryDTO queryDTO) {
        requireLoginUser(null);
        Integer current = queryDTO.getCurrent();
        Integer size = queryDTO.getSize();
        String keyword = queryDTO.getKeyword();
        IPage<UpNewsComment> page = upNewsCommentService.page(new Page<>(current, size),
                new LambdaQueryWrapper<UpNewsComment>()
                        .like(keyword != null && !keyword.trim().isEmpty(), UpNewsComment::getContent, keyword)
                        .orderByDesc(UpNewsComment::getCreateTime));

        List<Long> newsIds = page.getRecords().stream().map(UpNewsComment::getNewsId).distinct().collect(Collectors.toList());
        List<Long> userIds = page.getRecords().stream().map(UpNewsComment::getUserId).distinct().collect(Collectors.toList());
        Map<Long, UpNews> newsMap = newsIds.isEmpty() ? Collections.emptyMap() : upNewsService.listByIds(newsIds).stream()
                .collect(Collectors.toMap(UpNews::getId, item -> item));
        Map<Long, SysUser> userMap = userIds.isEmpty() ? Collections.emptyMap() : sysUserMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, item -> item));

        List<Map<String, Object>> records = new ArrayList<>();
        for (UpNewsComment comment : page.getRecords()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", comment.getId());
            item.put("newsId", comment.getNewsId());
            item.put("newsTitle", newsMap.containsKey(comment.getNewsId()) ? newsMap.get(comment.getNewsId()).getTitle() : "-");
            item.put("userId", comment.getUserId());
            item.put("nickname", userMap.containsKey(comment.getUserId()) ? userMap.get(comment.getUserId()).getNickname() : "-");
            item.put("content", comment.getContent());
            item.put("createTime", comment.getCreateTime());
            records.add(item);
        }

        Page<Map<String, Object>> result = new Page<>(current, size, page.getTotal());
        result.setRecords(records);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long id) {
        requireLoginUser(null);
        UpNewsComment comment = upNewsCommentService.getById(id);
        if (comment == null) {
            return;
        }
        upNewsCommentService.removeById(id);
        upNewsService.lambdaUpdate()
                .eq(UpNews::getId, comment.getNewsId())
                .setSql("comment_count = GREATEST(IFNULL(comment_count, 0) - 1, 0)")
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void like(Long id) {
        Long userId = requireLoginUserId(null);
        if (upNewsService.getById(id) == null) {
            throw new BusinessException("资讯不存在");
        }

        boolean exists = upNewsLikeService.lambdaQuery()
                .eq(UpNewsLike::getUserId, userId)
                .eq(UpNewsLike::getNewsId, id)
                .count() > 0;
        if (!exists) {
            UpNewsLike like = new UpNewsLike();
            like.setUserId(userId);
            like.setNewsId(id);
            upNewsLikeService.save(like);
            upNewsService.lambdaUpdate().eq(UpNews::getId, id)
                    .setSql("like_count = IFNULL(like_count, 0) + 1").update();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlike(Long id) {
        Long userId = requireLoginUserId(null);
        boolean removed = upNewsLikeService.lambdaUpdate()
                .eq(UpNewsLike::getUserId, userId)
                .eq(UpNewsLike::getNewsId, id)
                .remove();
        if (removed) {
            upNewsService.lambdaUpdate().eq(UpNews::getId, id)
                    .setSql("like_count = GREATEST(IFNULL(like_count, 0) - 1, 0)").update();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void favorite(Long id) {
        Long userId = requireLoginUserId(null);
        if (upNewsService.getById(id) == null) {
            throw new BusinessException("资讯不存在");
        }

        boolean exists = upNewsFavoriteService.lambdaQuery()
                .eq(UpNewsFavorite::getUserId, userId)
                .eq(UpNewsFavorite::getNewsId, id)
                .count() > 0;
        if (!exists) {
            UpNewsFavorite favorite = new UpNewsFavorite();
            favorite.setUserId(userId);
            favorite.setNewsId(id);
            upNewsFavoriteService.save(favorite);
            upNewsService.lambdaUpdate().eq(UpNews::getId, id)
                    .setSql("favorite_count = IFNULL(favorite_count, 0) + 1").update();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfavorite(Long id) {
        Long userId = requireLoginUserId(null);
        boolean removed = upNewsFavoriteService.lambdaUpdate()
                .eq(UpNewsFavorite::getUserId, userId)
                .eq(UpNewsFavorite::getNewsId, id)
                .remove();
        if (removed) {
            upNewsService.lambdaUpdate().eq(UpNews::getId, id)
                    .setSql("favorite_count = GREATEST(IFNULL(favorite_count, 0) - 1, 0)").update();
        }
    }

    @Override
    public List<NewsCommentVO> comments(Long id) {
        List<UpNewsComment> comments = upNewsCommentService.lambdaQuery()
                .eq(UpNewsComment::getNewsId, id)
                .eq(UpNewsComment::getStatus, "0")
                .orderByDesc(UpNewsComment::getCreateTime)
                .list();

        if (comments.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> userIds = comments.stream().map(UpNewsComment::getUserId).distinct().collect(Collectors.toList());
        Map<Long, SysUser> userMap = sysUserMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId, item -> item));

        List<NewsCommentVO> data = new ArrayList<>();
        for (UpNewsComment comment : comments) {
            NewsCommentVO vo = new NewsCommentVO();
            vo.setId(comment.getId());
            vo.setUserId(comment.getUserId());
            vo.setContent(comment.getContent());
            vo.setCreateTime(comment.getCreateTime());
            SysUser user = userMap.get(comment.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setNickname(user.getNickname());
                vo.setAvatar(user.getAvatar());
            }
            data.add(vo);
        }
        return data;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addComment(Long id, NewsCommentCreateDTO dto) {
        Long userId = requireLoginUserId(null);
        if (upNewsService.getById(id) == null) {
            throw new BusinessException("资讯不存在");
        }

        UpNewsComment comment = new UpNewsComment();
        comment.setNewsId(id);
        comment.setUserId(userId);
        comment.setContent(dto.getContent().trim());
        comment.setStatus("0");
        upNewsCommentService.save(comment);

        upNewsService.lambdaUpdate().eq(UpNews::getId, id)
                .setSql("comment_count = IFNULL(comment_count, 0) + 1")
                .update();
    }

    @Override
    public IPage<NewsCardVO> myFavorites(PageQueryDTO queryDTO) {
        Integer current = queryDTO.getCurrent();
        Integer size = queryDTO.getSize();
        Long userId = requireLoginUserId(null);
        Map<Long, String> categoryMap = getCategoryNameMap();

        IPage<UpNewsFavorite> favoritePage = upNewsFavoriteService.page(new Page<>(current, size),
                new LambdaQueryWrapper<UpNewsFavorite>()
                        .eq(UpNewsFavorite::getUserId, userId)
                        .orderByDesc(UpNewsFavorite::getCreateTime));

        List<Long> newsIds = favoritePage.getRecords().stream()
                .map(UpNewsFavorite::getNewsId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<UpNews> newsList = newsIds.isEmpty() ? Collections.emptyList() : upNewsService.listByIds(newsIds);
        Map<Long, UpNews> newsMap = newsList.stream().collect(Collectors.toMap(UpNews::getId, item -> item));

        List<NewsCardVO> cards = new ArrayList<>();
        for (Long newsId : newsIds) {
            UpNews news = newsMap.get(newsId);
            if (news != null && "1".equals(news.getStatus())) {
                cards.add(toCard(news, userId, categoryMap));
            }
        }

        Page<NewsCardVO> page = new Page<>(current, size, favoritePage.getTotal());
        page.setRecords(cards);
        return page;
    }

    @Override
    public IPage<NewsCardVO> myHistory(PageQueryDTO queryDTO) {
        Integer current = queryDTO.getCurrent();
        Integer size = queryDTO.getSize();
        Long userId = requireLoginUserId(null);
        Map<Long, String> categoryMap = getCategoryNameMap();

        IPage<UpBrowserBehavior> historyPage = upBrowserBehaviorService.page(new Page<>(current, size),
                new LambdaQueryWrapper<UpBrowserBehavior>()
                        .eq(UpBrowserBehavior::getUserId, userId)
                        .likeRight(UpBrowserBehavior::getUrl, "/news/detail/")
                        .orderByDesc(UpBrowserBehavior::getVisitTime));

        List<Long> newsIds = historyPage.getRecords().stream()
                .map(item -> parseNewsId(item.getUrl()))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        List<UpNews> newsList = newsIds.isEmpty() ? Collections.emptyList() : upNewsService.listByIds(newsIds);
        Map<Long, UpNews> newsMap = newsList.stream().collect(Collectors.toMap(UpNews::getId, item -> item));

        List<NewsCardVO> cards = new ArrayList<>();
        for (Long newsId : newsIds) {
            UpNews news = newsMap.get(newsId);
            if (news != null && "1".equals(news.getStatus())) {
                cards.add(toCard(news, userId, categoryMap));
            }
        }

        Page<NewsCardVO> page = new Page<>(current, size, historyPage.getTotal());
        page.setRecords(cards);
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearHistory() {
        Long userId = requireLoginUserId(null);
        upBrowserBehaviorService.lambdaUpdate()
                .eq(UpBrowserBehavior::getUserId, userId)
                .likeRight(UpBrowserBehavior::getUrl, "/news/detail/")
                .remove();
    }

    private List<NewsCardVO> toCardList(List<UpNews> newsList, Long userId, Map<Long, String> categoryMap) {
        List<NewsCardVO> cards = new ArrayList<>();
        for (UpNews news : newsList) {
            cards.add(toCard(news, userId, categoryMap));
        }
        return cards;
    }

    private NewsCardVO toCard(UpNews news, Long userId, Map<Long, String> categoryMap) {
        NewsCardVO vo = new NewsCardVO();
        vo.setId(news.getId());
        vo.setTitle(news.getTitle());
        vo.setSummary(news.getSummary());
        vo.setCoverImage(news.getCoverImage());
        vo.setTags(news.getTags());
        vo.setCategoryId(news.getCategoryId());
        vo.setCategory(categoryMap.getOrDefault(news.getCategoryId(), "未分类"));
        vo.setAuthor(news.getCreateBy());
        vo.setViewCount(defaultInt(news.getViewCount()));
        vo.setLikeCount(defaultInt(news.getLikeCount()));
        vo.setFavoriteCount(defaultInt(news.getFavoriteCount()));
        vo.setCommentCount(defaultInt(news.getCommentCount()));
        vo.setPublishTime(news.getPublishTime() == null ? news.getCreateTime() : news.getPublishTime());
        vo.setLocked(isLoginVisible(news.getVisibleScope()) && userId == null);
        return vo;
    }

    private Map<Long, String> getCategoryNameMap() {
        List<UpNewsCategory> categories = upNewsCategoryService.lambdaQuery().eq(UpNewsCategory::getStatus, "0").list();
        Map<Long, String> map = new HashMap<>();
        for (UpNewsCategory category : categories) {
            map.put(category.getId(), category.getCategoryName());
        }
        return map;
    }

    private Integer defaultInt(Integer value) {
        return value == null ? 0 : value;
    }

    private String toVisibleScope(String raw) {
        return isLoginVisible(raw) ? "1" : "0";
    }

    private boolean isLoginVisible(String raw) {
        if (raw == null) {
            return false;
        }
        String value = raw.trim();
        return "1".equals(value)
                || "true".equalsIgnoreCase(value)
                || "y".equalsIgnoreCase(value)
                || "yes".equalsIgnoreCase(value)
                || "login".equalsIgnoreCase(value);
    }

    private void saveHistory(Long userId, Long newsId, String title) {
        UpBrowserBehavior behavior = new UpBrowserBehavior();
        behavior.setUserId(userId);
        behavior.setUrl("/news/detail/" + newsId);
        behavior.setPageTitle(title);
        behavior.setVisitTime(LocalDateTime.now());
        behavior.setDuration(0);
        behavior.setBrowserType("portal");
        upBrowserBehaviorService.save(behavior);

        long count = upBrowserBehaviorService.lambdaQuery()
                .eq(UpBrowserBehavior::getUserId, userId)
                .likeRight(UpBrowserBehavior::getUrl, "/news/detail/")
                .count();
        if (count > 30) {
            List<UpBrowserBehavior> oldList = upBrowserBehaviorService.lambdaQuery()
                    .eq(UpBrowserBehavior::getUserId, userId)
                    .likeRight(UpBrowserBehavior::getUrl, "/news/detail/")
                    .orderByAsc(UpBrowserBehavior::getVisitTime)
                    .last("LIMIT " + (count - 30))
                    .list();
            if (!oldList.isEmpty()) {
                upBrowserBehaviorService.removeByIds(oldList.stream().map(UpBrowserBehavior::getId).collect(Collectors.toList()));
            }
        }
    }

    private String buildPreviewContent(String content, String summary) {
        if (summary != null && !summary.trim().isEmpty()) {
            return "<p>" + summary + "</p><p><strong>登录后查看完整详情</strong></p>";
        }
        if (content == null || content.trim().isEmpty()) {
            return "<p>登录后查看完整详情</p>";
        }
        int length = content.length();
        int previewLength = Math.max(50, length / 3);
        return content.substring(0, Math.min(length, previewLength)) + "<p><strong>登录后查看完整详情</strong></p>";
    }

    private Long parseNewsId(String url) {
        if (url == null || !url.startsWith("/news/detail/")) {
            return null;
        }
        String value = url.substring("/news/detail/".length());
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Long requireLoginUserId(String token) {
        SysUser user = requireLoginUser(token);
        return user.getId();
    }

    private SysUser requireLoginUser(String token) {
        SysUser user = getCurrentUser();
        if (user == null) {
            user = getUserFromToken(token);
        }
        if (user == null) {
            throw new IllegalArgumentException("未登录或登录已过期");
        }
        return user;
    }

    private Long getUserIdFromToken(String token) {
        SysUser user = getUserFromToken(token);
        return user == null ? null : user.getId();
    }

    private SysUser getUserFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        try {
            String username = jwtUtil.getUsernameFromToken(token);
            if (username == null || username.trim().isEmpty()) {
                return null;
            }
            return sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        } catch (Exception e) {
            return null;
        }
    }

    private SysUser getCurrentUser() {
        Object principal = SecurityUtils.getSubject().getPrincipal();
        if (principal instanceof SysUser) {
            return (SysUser) principal;
        }
        return null;
    }
}
