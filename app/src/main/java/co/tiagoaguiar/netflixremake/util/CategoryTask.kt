package co.tiagoaguiar.netflixremake.util

import android.util.Log
import co.tiagoaguiar.netflixremake.model.Category
import co.tiagoaguiar.netflixremake.model.Movie
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class CategoryTask {

    fun execute(url: String) {
        val executor = Executors.newSingleThreadExecutor()

        //New Thread (Paralel)
        executor.execute {

            var urlConnection: HttpsURLConnection? = null
            var stream: InputStream? = null

          try {
            val requestURL = URL(url)
            urlConnection = requestURL.openConnection() as HttpsURLConnection
            urlConnection.readTimeout = 2000 // reading time (2s)
            urlConnection.connectTimeout = 2000 // conection time (2s)

            val statusCode: Int = urlConnection.responseCode
            if (statusCode > 400) {
                throw IOException("Erro na comunicação com o servidor!")
            }
              stream  = urlConnection.inputStream
              val jsonAsString = stream.bufferedReader().use { it.readText() }

              val categories = toCategories(jsonAsString)
              Log.i("Teste", categories.toString())

          } catch (e: IOException) {
                  Log.e("Teste", e.message ?: "erro desconhecido", e)
          } finally {
              urlConnection?.disconnect()
              stream?.close()
          }
        }
    }
    private fun toCategories(jsonAsString: String) : List<Category> {
        val categories = mutableListOf<Category>()

        val jsonRoot = JSONObject(jsonAsString)
        val jsonCategories = jsonRoot.getJSONArray("category")
        for (i in 0 until jsonCategories.length()) {
            val jsonCategory = jsonCategories.getJSONObject(i)

            val title = jsonCategory.getString("title")
            val jsonMovies = jsonCategory.getJSONArray("movie")

            val movies = mutableListOf<Movie>()
            for (j in 0 until jsonMovies.length()) {
                val jsonMovie = jsonMovies.getJSONObject(j)
                val id = jsonMovie.getInt("id")
                val coverUrl = jsonMovie.getString("cover_url")

                movies.add(Movie(id, coverUrl))
            }

            categories.add(Category(title, movies))

        }

        return categories
    }



}