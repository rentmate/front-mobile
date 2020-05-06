package com.example.rentmater.data

import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.rentmater.data.model.LoggedInUser
import okhttp3.OkHttpClient
import java.io.IOException
import com.kotlin.graphql .*
import com.kotlin.graphql.type.PostLogin
import java.util.concurrent.CompletableFuture

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */

private const   val BASE_URL = "http://192.168.1.5:3030/graphql"
private lateinit var client: ApolloClient

class LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            // TODO: handle loggedInUser authentication

            var inputMail: Input<String> = Input.fromNullable(username)
            var inputPassword: Input<String> = Input.fromNullable(password)

            client = setUpApolloClient()
            var data :   LoginMutation.Login?
            var postlogin = PostLogin( inputMail , inputPassword)

           var  User = CompletableFuture<LoggedInUser>()
           var status = CompletableFuture<Boolean>()
            var  c =  client.newBuilder().build().mutate(
                LoginMutation(postlogin)
            ).enqueue(object : ApolloCall.Callback<LoginMutation.Data>(){
                override fun onFailure(e: ApolloException) {
                    Log.d("Data", "Failed")

                }
                override fun onResponse(response: Response<LoginMutation.Data>) {
                    data = response.data?.login
                    if (data == null){
                        status.complete(false)
                    }else {
                        Log.d("logged User 2 ", data.toString() )
                        User.complete(LoggedInUser( data?.token.toString(), data?.user?.email.toString()))
                        status.complete(true)
                    }

                }

            })
            var statusdata = status.get()
            Log.d("Status", statusdata.toString())
            if (!statusdata){
                return Result.Error(IOException("Invalid user or password"))
            }else {
                var userdata = User.get()
                Log.d("logged User", userdata.toString())
                return Result.Success(userdata)
            }
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }

    /**
     * Basic set up for graphql API, OkHttp is used for graphql with apollo client
     */
    private fun setUpApolloClient(): ApolloClient {

        val okHttp = OkHttpClient
            .Builder()
        return ApolloClient.builder()
            .serverUrl(BASE_URL)
            .okHttpClient(okHttp.build())
            .build()
    }
}

