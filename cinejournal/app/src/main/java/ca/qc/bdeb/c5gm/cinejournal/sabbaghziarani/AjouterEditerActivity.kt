package ca.qc.bdeb.c5gm.cinejournal.sabbaghziarani

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AjouterEditerActivity : AppCompatActivity() {
    val donnesVM: CineViewModel by viewModels()
    lateinit var imageFilm: ImageView
    lateinit var titreModifierouAjouter: TextView
    lateinit var titreFilm: EditText
    lateinit var sloganFilm: EditText
    lateinit var noteFilm: RatingBar
    lateinit var anneeFilm: EditText
    val titreModifier = "Modifier un film"
    val titreAjouter = "Nouveau film"
    var action: String? = null
    var filmAModifier: Film? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajouter_editer)
        setSupportActionBar(findViewById(R.id.toolbar))
        imageFilm = findViewById(R.id.imageChoisiFilm)
        titreFilm = findViewById(R.id.titreFilm)
        anneeFilm = findViewById(R.id.anneeFilm)
        sloganFilm = findViewById(R.id.sloganFilm)
        noteFilm = findViewById(R.id.noteFilm)
        titreModifierouAjouter = findViewById(R.id.titreAjouterModifier)
        action = intent?.extras?.getString("action")
        if (action == "Modifier") {
            titreModifierouAjouter.text = titreModifier
            filmAModifier = intent?.extras?.getParcelable("Film")
            if (donnesVM.imageLocale == null) {
                imageFilm.setImageURI(filmAModifier?.image?.toUri())
            } else {
                imageFilm.setImageURI(donnesVM.imageLocale)

            }
            titreFilm.text = Editable.Factory.getInstance().newEditable(filmAModifier?.titre)
            anneeFilm.text = Editable.Factory.getInstance().newEditable(filmAModifier?.anneParution)
            sloganFilm.text = Editable.Factory.getInstance().newEditable(filmAModifier?.slogan)
            noteFilm.progress = filmAModifier?.nombreEtoile!!
        } else {
            titreModifierouAjouter.text = titreAjouter
            imageFilm.setImageURI(donnesVM.imageLocale)
        }
        findViewById<Button>(R.id.imageBouton).setOnClickListener {
            selectionPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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

                donnesVM.imageLocale = creerUriPhoto()
                val inputStream = contentResolver.openInputStream(uri)
                val outputStream = contentResolver.openOutputStream(donnesVM.imageLocale!!)

                inputStream?.use { input ->
                    outputStream.use { output ->
                        output?.let { input.copyTo(it) }
                    }
                }

                imageFilm.setImageURI(donnesVM.imageLocale)
            }
        }

    fun sauvegarder(view: View) {
        if (titreFilm.text.isBlank() || anneeFilm.text.isBlank()) {
            var toast = Toast.makeText(
                applicationContext,
                "Vous devez entrez le titre et l'année du film",
                Toast.LENGTH_LONG
            )
            toast.show()
        } else if (action == "Ajouter") {
            var filmAAjouter = Film(
                null,
                donnesVM.imageLocale.toString(),
                titreFilm.text.toString(),
                anneeFilm.text.toString(),
                sloganFilm.text.toString(),
                noteFilm.progress
            )
            lifecycleScope.launch(Dispatchers.IO) {
                val database: AppDatabase =
                    AppDatabase.getDatabase(applicationContext)
                database.filmDao().insertAll(filmAAjouter)
                runOnUiThread {
                }
            }
            Toast.makeText(baseContext, "${filmAAjouter.titre} ajouté à la liste", Toast.LENGTH_SHORT).show()
            finish()
        } else if (action == "Modifier") {
            var imageModifie =
                if (donnesVM.imageLocale == null) filmAModifier?.image.toString() else donnesVM.imageLocale.toString()
            var filmAModifier = Film(
                filmAModifier?.uid,
                imageModifie,
                titreFilm.text.toString(),
                anneeFilm.text.toString(),
                sloganFilm.text.toString(),
                noteFilm.progress
            )
            lifecycleScope.launch(Dispatchers.IO) {
                val database: AppDatabase =
                    AppDatabase.getDatabase(applicationContext)
                database.filmDao().updateAll(filmAModifier)
                runOnUiThread {
                }
            }
            Toast.makeText(baseContext, "${filmAModifier.titre} modifié", Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    fun annuler(view: View) {
        finish()
    }
}