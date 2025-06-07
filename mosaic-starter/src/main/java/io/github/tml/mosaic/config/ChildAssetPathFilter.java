package io.github.tml.mosaic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 子插件页面重定向过滤器
 */
@Configuration
public class ChildAssetPathFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = req.getRequestURI();

        // 处理子页面的静态资源
        if(isChildPluginRequest(req) && requestURI.startsWith("/assets/")) {
            String newPath = "/mosaic-child-plugin" + requestURI;
            req.getRequestDispatcher(newPath).forward(req, resp);
            return;
        }

        filterChain.doFilter(req, resp);
    }


    private boolean isChildPluginRequest(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        return referer != null && referer.contains("/mosaic-child-plugin");
    }
}
