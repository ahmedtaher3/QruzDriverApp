package com.qruz.data.remote

import com.apollographql.apollo.ApolloClient
import okhttp3.OkHttpClient

object ApolloClientUtils {

    private const val BASE_URL = "https://qruz.xyz/graphql"

    public fun setupApollo(authHeader : String?): ApolloClient? {

        val okHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val builder = chain.request().newBuilder()
            builder.header("Authorization", "Bearer $authHeader")
            chain.proceed(builder.build())
        }.build()
        return ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(okHttpClient).build()
    }
}