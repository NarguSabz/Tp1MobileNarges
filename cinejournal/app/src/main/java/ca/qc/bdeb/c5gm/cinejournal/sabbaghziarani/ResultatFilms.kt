package ca.qc.bdeb.c5gm.cinejournal.sabbaghziarani

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

class ResultatFilms : Fragment() {
    val data: CineViewModel by activityViewModels()
    lateinit var adapteur: ResultatFilms.ListeFilmAdaptor
    lateinit var recycleView: RecyclerView
    lateinit var mainLayout: ConstraintLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resultat_films, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycleView = view.findViewById(R.id.listeFilmsRechercher)
        mainLayout = view.findViewById(R.id.mainlayoutResultat)


        data.listeFilmsDAPI.observe(viewLifecycleOwner) {
            afficherFilmDAPI()
        }
    }

    fun afficherFilmDAPI() {

        adapteur = ListeFilmAdaptor(
            requireContext(),
            this@ResultatFilms,
            data.listeFilmsDAPI.value!!
        )
        recycleView.adapter = adapteur
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
        val activity: ResultatFilms,
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
                    val snackbar = Snackbar.make(mainLayout,"Erreur: impossible de se connecter à l'API",Snackbar.LENGTH_LONG).setAction("Réessayer"){chercherSlogan(film)}
                    snackbar.show()
                    return@launch
                }

                val resultatRecherche = reponse.body()!!
                film.slogan = resultatRecherche.slogan
                ouvrirAjouter(film)

            } catch (e: SocketTimeoutException) {
                val snackbar = Snackbar.make(mainLayout,"Erreur: impossible de se connecter à l'API",Snackbar.LENGTH_LONG).setAction("Réessayer"){chercherSlogan(film)}
                snackbar.show()
            }
        }
    }

    fun ouvrirAjouter(film: Film) {
        val intent = Intent(requireContext(), AjouterEditerActivity::class.java)
        intent.putExtra("action", "AjouterFilmDApi")
        intent.putExtra("Film", film)
        startActivity(intent)
    }

}