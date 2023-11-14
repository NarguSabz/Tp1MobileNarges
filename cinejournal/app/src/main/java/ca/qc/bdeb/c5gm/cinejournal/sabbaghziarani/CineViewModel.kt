package ca.qc.bdeb.c5gm.cinejournal.sabbaghziarani

import android.net.Uri
import androidx.lifecycle.ViewModel

class CineViewModel : ViewModel() {
    /*la liste de films est conserve donc apres une simple changement on ne va pas encore demander la
     * liste a la base de donnee
     */
    var listeFilms: List<Film> = listOf()

    var aucunFilmDansBD = false

    /*l image local qui est choisi est conservee alors en changeant l orientation, on va toujours voir l image choisi
     */
    var imageLocale: Uri? = null

    /*cette variable permet d avoir la boite de dialogue ouvert lors d un changement d orientation
     */
    var supprimer: Boolean? = null

}
