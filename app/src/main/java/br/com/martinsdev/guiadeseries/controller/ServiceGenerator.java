package br.com.martinsdev.guiadeseries.controller;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by gabriel on 08/12/15.
 */
public class ServiceGenerator {
    private static final String API_BASE_URL = "http://api.themoviedb.org/3/";
    private static final String API_KEY = "176b0a42041cf5fddf3647fec27eeff5";

    private static OkHttpClient httpClient = new OkHttpClient();
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());


    public static <S> S createService(Class<S> serviceClass) {
        if (API_KEY != null) {
            httpClient.interceptors().clear();

            // Interceptor para inserir o chave da API a consulta
            httpClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();
                    HttpUrl url = chain.request().httpUrl().newBuilder()
                            .addQueryParameter("api_key", API_KEY).build();
                    Request.Builder requestBuilder = original.newBuilder()
                            .url(url)
                            .method(original.method(), original.body());

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });

            // Log das informações transferidas
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            httpClient.interceptors().add(loggingInterceptor);

            // Reenvio da solicitação em caso de código HTTP 429 Too Many Requests
            httpClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();

                    // Executamos a requisição
                    Response response = chain.proceed(request);

                    if (response.code() == 429){
                        try {
                            Thread.sleep(10000);
                            return chain.proceed(request);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    return response;
                }
            });
        }

        Retrofit retrofit = builder.client(httpClient).build();
        return retrofit.create(serviceClass);
    }
}
