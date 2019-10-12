package io.synlabs.synvision.auth;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.synlabs.synvision.entity.CurrentUser;
import io.synlabs.synvision.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Value("${synvision.auth.maxentries}")
    private int maxentries;

    @Value("${synvision.auth.apiprefix}")
    private String apiprefix;

    @Value("${synvision.auth.secretkey}")
    private String secretkey;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretkey.getBytes());
    }

    @Autowired
    private UserService userService;

    private Cache<String, Authentication> authCache = CacheBuilder
            .newBuilder()
            .build();

    @Override
    public void doFilter(final ServletRequest req,
                         final ServletResponse res,
                         final FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (!request.getRequestURI().startsWith(apiprefix) || "OPTIONS".equals(request.getMethod())) {
            chain.doFilter(req, res);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        final String tokenParam = request.getParameter("token");

        if (StringUtils.isEmpty(authHeader) && !StringUtils.isEmpty(tokenParam)) {
            authHeader = tokenParam;
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        final String token = authHeader.substring(7); // The part after "Bearer "

        try {
            Claims claims = Jwts.parser().setSigningKey(secretkey)
                    .parseClaimsJws(token).getBody();

            request.setAttribute("claims", claims);
            Authentication result = authCache.getIfPresent(token);

            if (result == null || ((JwtAuthenticationToken) result).isStale()) {

                logger.debug("User {} not found in cache", claims.getSubject());

                CurrentUser user = (CurrentUser) userService.loadUserByUsername(claims.getSubject());

                if (user == null || user.getUser() == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                Set<String> userPrivileges = user.getUser().getPrivileges();
                //convert to granted authority, prefix with ROLE_
                List<GrantedAuthority> authorities = userPrivileges.stream().map(up -> new SimpleGrantedAuthority("ROLE_" + up)).collect(Collectors.toList());

                result = new JwtAuthenticationToken(
                        user,
                        claims.getSubject(),
                        authorities);
                authCache.put(token, result);
            }

            SecurityContextHolder.getContext().setAuthentication(result);

        } catch (JwtException e) {
            logger.error("Error parsing token", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        chain.doFilter(req, res);
    }

}