package ca.qc.bdeb.c5gm.cinejournal.sabbaghziarani

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

class RechercheFilmActivity : AppCompatActivity() {
    lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recherche_film)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }}
    /*lateinit var boutonRechercher: TextView
    lateinit var recycleView: RecyclerView
    lateinit var adapteur: ListeFilmAdaptor
    lateinit var champsRecherche: EditText
    val debutUrl = "https://image.tmdb.org/t/p/w500"
    lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recherche_film)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        recycleView = findViewById(R.id.listeFilmsRechercher)
        boutonRechercher = findViewById(R.id.boutonRechercher)
        champsRecherche = findViewById(R.id.champsRecherche)
    }

    fun afficherResultatsFilms(view: View) {

        // Éviter de permettre de lancer deux requêtes en même temps
        // Quand le réseau est lent, on ne veut pas pouvoir spammer le bouton de clics
        // et démarrer plein de requêtes en parallèle
        champsRecherche.isEnabled = false
        boutonRechercher.isEnabled = false
        lifecycleScope.launch {
            try {
                val reponse = withContext(Dispatchers.IO) {
                    ApiClient.apiService.getRecherche(champsRecherche.text.toString())
                }

                if (!reponse.isSuccessful || reponse.body() == null) {
                    Toast.makeText(
                        applicationContext,
                        "Erreur: impossible de se connecter à l'API",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                val resultatRecherche = reponse.body()!!
                var listeFilms = mutableListOf<Film>()
                resultatRecherche.resultatsRecherche.forEach { film ->

                    listeFilms.add(
                        Film(
                            null,
                            "$debutUrl${film.pathPoster}",
                            film.titre,
                            film.dateSortie.split("-")[0],
                            null,
                            film.note.toInt(),
                            film.id
                        )
                    )

                }

                adapteur = ListeFilmAdaptor(
                    applicationContext,
                    this@RechercheFilmActivity,
                    listeFilms
                )
                recycleView.adapter = adapteur

            } catch (e: SocketTimeoutException) {
                Toast.makeText(
                    applicationContext,
                    "Erreur: Réseau indisponible",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                boutonRechercher.isEnabled = true
                champsRecherche.isEnabled = true
            }
        }
    }

    class ItemFilmHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layout: ConstraintLayout
        val image: ImageView
        val titre: TextView
        val anneParution: TextView

        init {
            layout = itemView as ConstraintLayout
            image = itemView.findViewById(R.id.imageFilm)
            titre = itemView.findViewById(R.id.titre)
            anneParution = itemView.findViewById(R.id.anneParution)
        }
    }

    class ListeFilmAdaptor(
        val ctx: Context,
        val activity: RechercheFilmActivity,
        var data: List<Film>
    ) : RecyclerView.Adapter<ItemFilmHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int
        ): ItemFilmHolder {
            val view =
                LayoutInflater.from(ctx)
                    .inflate(
                        R.layout.liste_film_rechercher_item,
                        parent, false
                    )
            return ItemFilmHolder(view)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(
            holder: ItemFilmHolder, position: Int
        ) {
            var item = data[position]
            holder.layout.setOnClickListener {
                this.activity.chercherSlogan(item)
            }


            Glide.with(this.activity).load(item.image).into(holder.image)
            holder.anneParution.text = "(${item.anneParution})"
            holder.titre.text = item.titre
        }

    }

    fun chercherSlogan(film: Film) {
        lifecycleScope.launch {
            try {
                val reponse = withContext(Dispatchers.IO) {
                    ApiClient.apiService.getRechercheFilmApprofondi(film.idAPI!!)
                }

                if (!reponse.isSuccessful || reponse.body() == null) {
                    Toast.makeText(
                        applicationContext,
                        "Erreur: impossible de se connecter à l'API",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                val resultatRecherche = reponse.body()!!
                film.slogan = resultatRecherche.slogan
                ouvrirModifier(film)

            } catch (e: SocketTimeoutException) {
                Toast.makeText(
                    applicationContext,
                    "Erreur: Réseau indisponible",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                boutonRechercher.isEnabled = true
            }
        }
    }

    fun ouvrirModifier(film: Film) {
        val intent = Intent(applicationContext, AjouterEditerActivity::class.java)
        intent.putExtra("action", "AjouterFilmDApi")
        intent.putExtra("Film", film)
        startActivity(intent)
    }*/
}
