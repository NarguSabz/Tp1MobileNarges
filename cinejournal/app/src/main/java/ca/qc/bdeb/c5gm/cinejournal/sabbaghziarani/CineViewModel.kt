package ca.qc.bdeb.c5gm.cinejournal.sabbaghziarani

import android.net.Uri
import androidx.lifecycle.ViewModel

class CineViewModel : ViewModel() {
    var listeFilms: List<Film> ?= null
    var imageLocale: Uri? = null
    var supprimer: Boolean? = null

}
