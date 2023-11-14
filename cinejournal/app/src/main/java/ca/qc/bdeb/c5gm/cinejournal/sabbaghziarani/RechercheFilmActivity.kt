package ca.qc.bdeb.c5gm.cinejournal.sabbaghziarani

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

class RechercheFilmActivity : AppCompatActivity() {
    lateinit var boutonRechercher: TextView
    lateinit var contenuFilmRecherche: TextView
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
        boutonRechercher = findViewById(R.id.boutonRechercher)
        contenuFilmRecherche = findViewById(R.id.contenuFilmRecherchee)
    }

    fun afficherResultatsFilms(view: View) {

        // Éviter de permettre de lancer deux requêtes en même temps
        // Quand le réseau est lent, on ne veut pas pouvoir spammer le bouton de clics
        // et démarrer plein de requêtes en parallèle
        boutonRechercher.isEnabled = false
        lifecycleScope.launch {
            try {
                val reponse = withContext(Dispatchers.IO) {
                    ApiClient.apiService.getRecherche("star")
                }

                if (!reponse.isSuccessful || reponse.body() == null) {
                    Toast.makeText(
                        applicationContext,
                        "Erreur: impossible de se connecter à l'API",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                val ResultatRecherche = reponse.body()!!

                contenuFilmRecherche.text = """
                Il y a ${ResultatRecherche.nbreResultatsTotal} films connus par cet requete
                
                Le premier s'appelle ${ResultatRecherche.resultatsRecherche[0].titre}
            """.trimIndent()

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
}
