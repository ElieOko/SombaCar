package emy.backend.barua.security

import com.fasterxml.jackson.databind.ObjectMapper
import emy.backend.barua.app.user.application.services.UserService
import emy.backend.barua.utils.Mode
import jakarta.servlet.FilterChain
import jakarta.servlet.http.*
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Profile(Mode.DEV)
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val userService: UserService,
): OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val matcher = AntPathMatcher()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI
//        val info = ClientRequestInfo(
//            ip = getClientIp(request),
//            userAgent = request.getHeader("User-Agent"),
//            deviceBrand = request.getHeader("X-Device-Brand"),
//            deviceModel = request.getHeader("X-Device-Model"),
//            os = request.getHeader("X-OS"),
//            osVersion = request.getHeader("X-OS-Version"),
//        )
//        request.setAttribute(ATTR, info)
        // Liste des chemins publics INCLUANT WebSocket
        val publicPaths = listOf("/api/v1/public/**", "/", "/swagger-ui/**", "/swagger-ui.html/*", "/v3/**", "/files/**", "/auth/login", "/auth/register", "/websocket/**") // ← IMPORTANT: WebSocket doit être public pour le handshake)

        try {
            //Vérifie si la route est publique (pattern matching)
            val isPublic = publicPaths.any { pattern ->
                matcher.match(pattern, path)
            }

            if (isPublic) {
                logger.info("🟢 Public route: $path")
                filterChain.doFilter(request, response)
                return
            }

            val authHeader = request.getHeader("Authorization")

            if (authHeader == null || !authHeader.startsWith("Bearer ") || !jwtService.validateAccessToken(authHeader)) {
                sendJsonError(response, request, HttpServletResponse.SC_UNAUTHORIZED,"Invalid or missing JWT token")
                return
            }

            val userId = jwtService.getUserIdFromToken(authHeader)
            val auth = UsernamePasswordAuthenticationToken(userId, null, emptyList()).apply {
                details = WebAuthenticationDetailsSource().buildDetails(request)
            }

            SecurityContextHolder.getContext().authentication = auth
            filterChain.doFilter(request, response)

        } catch (e : AuthorizationDeniedException){
            sendJsonError(response, request, HttpServletResponse.SC_UNAUTHORIZED,"Invalid or missing JWT token")
        }
    }
    private fun getClientIp(req: HttpServletRequest): String? {
        val xff = req.getHeader("X-Forwarded-For")
        if (!xff.isNullOrBlank()) return xff.split(",").first().trim()
        val xRealIp = req.getHeader("X-Real-IP")
        if (!xRealIp.isNullOrBlank()) return xRealIp.trim()
        return req.remoteAddr
    }
    fun sendJsonError(
        response: HttpServletResponse,
        request: HttpServletRequest,
        status: Int,
        message: String
    ) {
        response.status = status
        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        val errorResponse = mapOf(
            "status" to status,
            "error" to HttpServletResponse.SC_UNAUTHORIZED.let { if (status == it) "Unauthorized" else "Error" },
            "message" to message,
            "path" to request.requestURI
        )
        val json = ObjectMapper().writeValueAsString(errorResponse)
        response.writer.write(json)
    }
    companion object {
        const val ATTR = "CLIENT_REQUEST_INFO"
    }
}