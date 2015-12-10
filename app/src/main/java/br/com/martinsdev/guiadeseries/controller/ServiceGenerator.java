package br.com.martinsdev.guiadeseries.controller;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by gabriel on 08/12/15.
 */
public class ServiceGenerator {
    private static final String API_BASE_URL = "http://api.themoviedb.org/3/";
    private static final String api_key = "176b0a42041cf5fddf3647fec27eeff5";

    private static OkHttpClient httpClient = new OkHttpClient();
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        if (api_key != null){

            // Inserindo a api_key em todas as requisições
            httpClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    HttpUrl url = request.httpUrl()
                            .newBuilder()
                            .addQueryParameter("api_key", api_key)
                            .build();
                    request = request.newBuilder().url(url).build();

                    return chain.proceed(request);
                }
            });
        }

        Retrofit retrofit = builder.client(httpClient).build();
        return retrofit.create(serviceClass);
    }
}
