package ca.qc.bdeb.c5gm.cinejournal.sabbaghziarani

/**
 * Exemple simplifié et annoté basé sur cet article : https://www.axopen.com/blog/2021/01/retrofit-projet-android-kotlin/
 *
 * Les données utilisées sont tirées de l'API de test public
 * https://jsonplaceholder.typicode.com/
 *
 * ===================     IMPORTANT     ===================
 * Pour intégrer ce code dans un autre projet, ATTENTION À DEUX CHOSES
 *     1. Les permissions dans le manifest.xml de l'application
 *     2. Les dépendences dans le build.gradle.kts du Module
 * =========================================================
 *
 *
 * Cet exemple utilise la librairie Retrofit pour faire le pont entre
 * un API Rest et un ensemble de classes de données, dans un format
 * très similaire à ce que vous avez déjà fait avec la librairie Room
 * pour gérer votre base de données
 *
 *
 * Dans Room, on avait :
 *
 * - Des Entity, qui étaient nos données à stocker dans la BD
 * - Un DAO (DataAccessObject), une interface qui définissait les requêtes possibles
 * - Un singleton pour accéder à la BD
 *
 * Dans le cas de Retrofit, on a :
 *
 * - Des data class, qui représentent les données que notre API Rest peut retourner ou recevoir en JSON
 * - Une interface qui définit les requêtes possibles
 * - Un singleton pour accéder au gestionnaire de requêtes à l'API
 *
 *
 * Comme dans le cas de la base de données, les opérations faites sur
 * un API sont __potentiellement lourdes__.
 *
 * On va devoir procéder de la même façon, soit en utilisant :
 *
 *         lifecycleScope.launch {
 *             // Une tâche qui va demander d'attendre après des requêtes
 *
 *             val reponse = withContext(Dispatchers.IO) {
 *                 // Faire la requête dans un Thread différent du thread principal
 *                 // question de ne pas bloquer l'interface graphique
 *                 // (souvenez-vous du gif de banane qui danse, on ne veut pas
 *                 // que notre banane arrête de danser le temps que l'accès à Internet
 *                 // se complète!)
 *             }
 *          ...
 */


import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.logging.Logger

/**
 * Ensemble de data class pour représenter les données fournies par l'API
 */
data class FilmDApi(
    /**
     * Dans le cas où on veut donner un nom plus clair à notre classe
     * que le nom qui est disponible dans l'API, on peut utiliser
     *
     * @SerializedName("nomDansLAPI")
     */
    val id: Int,
    @SerializedName("original_language") val langue: String,
    @SerializedName("original_title") val titreOriginal: String,
    @SerializedName("overview") val sommaire: String,
    @SerializedName("popularity") val popularite: Double,
    @SerializedName("poster_path") val pathPoster: String?,
    @SerializedName("release_date") val dateSortie: String,
    @SerializedName("title") val titre: String,
    @SerializedName("vote_average") val note: Double,
    @SerializedName("video") val contientVideo: Boolean,
    @SerializedName("adult") val pourAdulte: Boolean,
    @SerializedName("backdrop_path") val pathBackdrop: String,
    @SerializedName("genre_ids") val idsGenres: List<Int>,
)

data class ReponseRecherche(
    /**
     * Dans le cas où on veut donner un nom plus clair à notre classe
     * que le nom qui est disponible dans l'API, on peut utiliser
     *
     * @SerializedName("nomDansLAPI")
     */
    val id: Int,
    @SerializedName("page") val pageActuel: Int,
    @SerializedName("results") val resultatsRecherche: List<FilmDApi>,
    @SerializedName("total_pages") val nbrePageTotal: Int,
    @SerializedName("total_results") val nbreResultatsTotal: Int,
)

data class ReponseRechercheFilmApprofondi(
    /**
     * Dans le cas où on veut donner un nom plus clair à notre classe
     * que le nom qui est disponible dans l'API, on peut utiliser
     *
     * @SerializedName("nomDansLAPI")
     */
    @SerializedName("tagline") val slogan: String

)

/**
 * Interface qui spécifie des méthodes qui seront générées automatiquement par la librairie Retrofit
 *
 * Très similaire au DAO (Data Access Object) dans Room où vous définissiez vos requêtes
 *
 * Les méthodes peuvent composer
 */
interface ApiService {
    @GET("3/search/movie")
    suspend fun getRecherche(@Query("query") name: String): Response<ReponseRecherche>

    @GET("3/movie/{movie_id}")
    suspend fun getRechercheFilmApprofondi(@Path("movie_id") movie_id: Int): Response<ReponseRechercheFilmApprofondi>
}

/**
 * Objet d'accès à l'API : essentiellement équivalent au singleton qui donnait accès à la BD dans le cas de Room
 */
object ApiClient {
    /**
     * URL de base pour toutes les requêtes faites à l'API
     */
    private const val BASE_URL: String = "https://api.themoviedb.org/"

    /** __by lazy__ est un construit de Kotlin qui permet d'initialiser une variable
     * au moment de l'utiliser pour la première fois
     *
     * Ça fait essentiellement ce qu'un singleton ferait avec :
     *     if(INSTANCE == null)
     *         INSTANCE = new Bidule()
     *
     * Mais en plus gracieux, en utilisant une fonctionnalité du langage plutôt qu'on codant
     * cette logique à la main à chaque fois
     */
    private val gson: Gson by lazy {
        GsonBuilder().setLenient().create()
    }

    // Solution basée sur: https://stackoverflow.com/a/51833497
    private val httpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        OkHttpClient.Builder()
            .addNetworkInterceptor(logging)
            .addInterceptor { chain ->
                val original = chain.request()
// Request customization: add request headers
                val requestBuilder = original.newBuilder()
                    .header(
                        "Authorization",
                        "Bearer ${BuildConfig.API_KEY_TMDB}"
                    )
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
    }

    private val retrofit: Retrofit by lazy {
        /**
         *  Le patron Builder est utilisé ici
         *  Voir: https://refactoring.guru/design-patterns/builder
         *
         * Un Builder est un objet de configuration dont les méthodes sont typiquement chaînables
         *
         * C'est une bonne alternative à définir un constructeur avec 2178643 arguments,
         * qui sont tous possiblement optionnels
         */

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
