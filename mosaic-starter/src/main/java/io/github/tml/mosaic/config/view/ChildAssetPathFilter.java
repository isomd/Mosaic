package io.github.tml.mosaic.config.view;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import static io.github.tml.mosaic.config.view.MosaicWebMvcConfig.MOSAIC_FRONT_PATH;

/**
 * 子插件页面重定向过滤器
 */
@Slf4j
@Component
public class ChildAssetPathFilter extends OncePerRequestFilter {

    private Map<String, String> pathMap = new ConcurrentHashMap<>();

    private static Set<String> resourcePathSet = new ConcurrentSkipListSet<>();

    // 白名单放行path
    private Set<String> whitePathList = new ConcurrentSkipListSet<>();

    static {
        resourcePathSet.add(MOSAIC_FRONT_PATH);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = req.getRequestURI();

        // 是请求或者是白名单的path直接放行
        if(whitePathList.contains(requestURI)||isApiRequest(req)){
            filterChain.doFilter(req, resp);
            return;

        }

        // 校验是否匹配注册前缀，或者其refer父类为前缀
        String prefixPath = pathMap.getOrDefault(requestURI,
                checkAndGetPath(requestURI).orElseGet(() -> checkReferAndGetPath(req).orElse(null)));

        if(StringUtils.isNotBlank(prefixPath)){
            pathMap.putIfAbsent(requestURI, prefixPath);
            if(requestURI.startsWith(prefixPath)){
                req.getRequestDispatcher(requestURI).forward(req, resp);
            }else{
                req.getRequestDispatcher(prefixPath + requestURI).forward(req, resp);
            }
            return;
        }

        // 不匹配则放行
        whitePathList.add(requestURI);
        filterChain.doFilter(req, resp);
    }

    private Optional<String> checkAndGetPath(String requestURI) {
        if(StringUtils.isEmpty(requestURI)){
            return Optional.empty();
        }

        for (String path : resourcePathSet) {
            if (requestURI.startsWith(path)) {
                return Optional.of(path);
            }
        }
        return Optional.empty();

    }

    private Optional<String> checkReferAndGetPath(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (StringUtils.isNotBlank(referer)) {
            try {
                return Optional.ofNullable(pathMap.get(new URI(referer).getPath()));
            } catch (URISyntaxException e) {
                log.error("get {} referer error", request.getRequestURI(), e);
            }
        }
        return Optional.empty();
    }

    private Boolean isApiRequest(HttpServletRequest request){
        String SecFetchDest = request.getHeader("Sec-Fetch-Dest");
        return "empty".equals(SecFetchDest);
    }
}
