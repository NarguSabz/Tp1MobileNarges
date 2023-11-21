package ca.qc.bdeb.c5gm.cinejournal.sabbaghziarani

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

class RechercheFilm : Fragment() {
    lateinit var boutonRechercher: TextView
    lateinit var champsRecherche: EditText
    val debutUrl = "https://image.tmdb.org/t/p/w500"
    val data: CineViewModel by activityViewModels()
    lateinit var mainLayout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recherche_film, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainLayout = view.findViewById(R.id.layoutRechercher)
        boutonRechercher = view.findViewById(R.id.boutonRechercher)
        champsRecherche = view.findViewById(R.id.champsRecherche)
        boutonRechercher.setOnClickListener {
            rechercherResultatsFilms()
        }
    }

    fun rechercherResultatsFilms() {
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
                    val snackbar = Snackbar.make(
                        mainLayout,
                        "Erreur: impossible de se connecter à l'API",
                        Snackbar.LENGTH_LONG
                    ).setAction("Réessayer") { rechercherResultatsFilms() }
                    snackbar.show()

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
                data.listeFilmsDAPI.value = listeFilms


            } catch (e: SocketTimeoutException) {
                val snackbar =
                    Snackbar.make(mainLayout, "Erreur: Réseau indisponible", Snackbar.LENGTH_LONG)
                        .setAction("Réessayer") { rechercherResultatsFilms() }
                snackbar.show()
            } finally {
                boutonRechercher.isEnabled = true
                champsRecherche.isEnabled = true
            }
        }
    }

}