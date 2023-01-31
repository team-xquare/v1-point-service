package com.xquare.v1servicepoint.configuration.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap

class AuthenticationFilter
//class AuthenticationFilter : WebFilter {
//
//    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
//        val authenticationMap = ConcurrentHashMap(
//            mapOf(
//                "userId" to exchange.response.headers["Request-User-Id"],
//                "userRole" to exchange.response.headers["Request-User-Role"],
//                "userAuthorities" to exchange.response.headers["Request-User-Authorities"]
//            )
//        ) // TODO UnAuthorizedException 발생조건 추가
//
//        val authorities: MutableCollection<SimpleGrantedAuthority> = arrayListOf()
//        authenticationMap["userAuthorities"]?.forEach { userAuthority ->
//            authorities.add(SimpleGrantedAuthority(userAuthority))
//        }
//
//        authorities.add(SimpleGrantedAuthority("ROLE_${authenticationMap["userRole"]}"))
//        val userDetails: UserDetails = User(authenticationMap["userId"].toString(), "", authorities)
//        val authentication: Authentication =
//            UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
//        SecurityContextHolder.getContext().authentication = authentication
//
//        return chain.filter(exchange)
//    }
//}
