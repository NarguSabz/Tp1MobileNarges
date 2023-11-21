package ca.qc.bdeb.c5gm.cinejournal.sabbaghziarani

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActivityReconnaissanceCelebrite : AppCompatActivity() {
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    val donnesVM: CineViewModel by viewModels()

    val debutUrl = "https://image.tmdb.org/t/p/w500"

    lateinit var imageCelebrite: ImageView
    lateinit var soumettre: Button
    lateinit var ajouterImage: Button
    lateinit var recycleView: RecyclerView
    lateinit var adapteur: ListeFilmAdaptor
    var imageLocale: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reconnaissance_celebrite)

        soumettre = findViewById(R.id.soumettre)

        imageCelebrite = findViewById(R.id.imageCelebrite)
        if (donnesVM.imageLocaleCelebrite != null) {
            imageLocale = donnesVM.imageLocaleCelebrite
            imageCelebrite.setImageURI(imageLocale)
            soumettre.isEnabled = true

        }

        ajouterImage = findViewById(R.id.ajouterImage)
        recycleView = findViewById(R.id.listeFilmsCelebs)
        if (donnesVM.listeFilmsDacteur.isNotEmpty()) {
            adapteur = ListeFilmAdaptor(
                applicationContext,
                this@ActivityReconnaissanceCelebrite,
                donnesVM.listeFilmsDacteur
            )
            recycleView.adapter = adapteur
        }
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        var reconnaitreCelebrite = ReconnaitreCelebrite()
        soumettre.setOnClickListener {
            soumettre.isEnabled = false
            ajouterImage.isEnabled = false

            lifecycleScope.launch {

                var reponseAPi = withContext(Dispatchers.IO) {
                    reconnaitreCelebrite.recognizeAllCelebrities(imageLocale)
                }
                var toast = Toast.makeText(
                    applicationContext,
                    reponseAPi[1],
                    Toast.LENGTH_LONG
                )
                toast.show()
                if (reponseAPi[0] != "") {
                    rechercherActeur(reponseAPi[1])
                } else {
                    soumettre.isEnabled = true
                    ajouterImage.isEnabled = true
                }
            }
        }
        ajouterImage.setOnClickListener {
            selectionPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }


    fun rechercherActeur(nomActeur: String) {

        // Éviter de permettre de lancer deux requêtes en même temps
        // Quand le réseau est lent, on ne veut pas pouvoir spammer le bouton de clics
        // et démarrer plein de requêtes en parallèle

        lifecycleScope.launch {
            try {
                val reponseActeur = withContext(Dispatchers.IO) {
                    ApiClient.apiService.getActeur(nomActeur)
                }

                if (!reponseActeur.isSuccessful || reponseActeur.body() == null) {
                    Toast.makeText(
                        applicationContext,
                        "Erreur: impossible de se connecter à l'API",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                } else {
                    val reponseFilmActeur = withContext(Dispatchers.IO) {
                        ApiClient.apiService.getListFilmsActeur(reponseActeur.body()!!.resultatsRecherche[0].id)
                    }
                    if (!reponseFilmActeur.isSuccessful || reponseFilmActeur.body() == null) {
                        Toast.makeText(
                            applicationContext,
                            "Erreur: impossible de se connecter à l'API",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }
                    donnesVM.listeFilmsDacteur = mutableListOf()
                    reponseFilmActeur.body()?.resultatsRecherche?.forEach { film ->

                        donnesVM.listeFilmsDacteur.add(
                            Film(
                                null,
                                "$debutUrl${film.pathPoster}",
                                film.titreOriginal,
                                film.dateSortie.split("-")[0],
                                "",
                                film.note.toInt(),
                                null
                            )
                        )

                    }
                    adapteur = ListeFilmAdaptor(
                        applicationContext,
                        this@ActivityReconnaissanceCelebrite,
                        donnesVM.listeFilmsDacteur
                    )
                    recycleView.adapter = adapteur
                }

            } catch (e: SocketTimeoutException) {
                Toast.makeText(
                    applicationContext,
                    "Erreur: Réseau indisponible",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                soumettre.isEnabled = true
                ajouterImage.isEnabled = true
            }
        }
    }


    private fun creerUriPhoto(): Uri {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val photoFile =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "$timeStamp.jpg")
        return photoFile.toUri()
    }

    val selectionPhoto =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {

                imageLocale = creerUriPhoto()
                val inputStream = contentResolver.openInputStream(uri)
                val outputStream = contentResolver.openOutputStream(imageLocale!!)

                inputStream?.use { input ->
                    outputStream.use { output ->
                        output?.let { input.copyTo(it) }
                    }
                }

                imageCelebrite.setImageURI(imageLocale)
                donnesVM.imageLocaleCelebrite = imageLocale
                soumettre.isEnabled = true

            }
        }


    class ItemFilmHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layout: ConstraintLayout
        val image: ImageView
        val titre: TextView
        val anneParution: TextView
        val note: RatingBar
        val slogan: TextView

        init {
            layout = itemView as ConstraintLayout
            image = itemView.findViewById(R.id.imageFilm)
            titre = itemView.findViewById(R.id.titre)
            anneParution = itemView.findViewById(R.id.anneParution)
            note = itemView.findViewById(R.id.note)
            slogan = itemView.findViewById(R.id.slogan)
        }
    }

    class ListeFilmAdaptor(
        val ctx: Context,
        val activity: ActivityReconnaissanceCelebrite,
        var data: List<Film>
    ) : RecyclerView.Adapter<ItemFilmHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int
        ): ItemFilmHolder {
            val view =
                LayoutInflater.from(ctx)
                    .inflate(
                        R.layout.liste_film_item,
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
            val item = data[position]
            holder.layout.setOnClickListener {
            }
            if (item.image != null) Glide.with(this.activity).load(item.image).into(holder.image)
            holder.anneParution.text = "(${item.anneParution})"
            holder.titre.text = item.titre
            holder.note.progress = item.nombreEtoile
            holder.slogan.text = item.slogan
        }

    }

}