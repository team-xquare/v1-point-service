package com.xquare.v1servicepoint.configuration.security

class AuthenticationFilter
// class AuthenticationFilter : WebFilter {
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
// }
