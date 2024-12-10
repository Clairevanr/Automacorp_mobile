package com.automacorp.services

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RoomApiService {

        //Read all rooms
        @GET("rooms")
        fun findAll(): Call<List<RoomDto>>

        //Read a room by its ID
        @GET("rooms/{id}")
        fun findById(@Path("id") id: Long): Call<RoomDto>

        //Update a room by its ID
        @PUT("rooms/{id}")
        fun updateRoom(@Path("id") id: Long, @Body room: RoomCommandDto): Call<RoomDto>

        //Create a room
        @POST("rooms")
        fun createRoom(@Body room: RoomCommandDto): Call<RoomDto>

        //Delete a Room
        @DELETE("rooms/{id}")
        fun deleteRoom(@Path("id") id: Long): Call<Void>


    }
