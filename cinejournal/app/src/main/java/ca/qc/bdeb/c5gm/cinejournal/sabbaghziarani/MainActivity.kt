package ca.qc.bdeb.c5gm.cinejournal.sabbaghziarani

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var recycleView: RecyclerView
    lateinit var adapteur: ListeFilmAdaptor
    lateinit var listeFilms: List<Film>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        recycleView = findViewById(R.id.listeFilms)

        val films = listOf<Film>( Film(null,R.drawable.big_buck_bunny_poster_big,"Big Buck Bunny","2008","du projet peach open Movie",1))
        lifecycleScope.launch(Dispatchers.IO){
            val database:AppDatabase =
                AppDatabase.getDatabase(applicationContext)
            database.filmDao().insertAll(films)
            runOnUiThread{
                // pour mettre à jour le UI on utilise
            }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val database: AppDatabase =
                AppDatabase.getDatabase(applicationContext)

            listeFilms = database.filmDao().getAll()
            runOnUiThread {
                // pour mettre à jour le UI on utilise
            }
        }
        adapteur = ListeFilmAdaptor(applicationContext, this, listeFilms)
        recycleView.adapter = adapteur


        /*
                lifecycleScope.launch(Dispatchers.IO){
                    val database:AppDatabase =
                        AppDatabase.getDatabase(applicationContext)
                    database.filmDao().delete()
                }*/

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    class ItemFilmHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layout: ConstraintLayout
        val image: ImageView
        val titre: TextView
        val anneParution: TextView
        val slogan: TextView
        val note: RatingBar

        init {
            layout = itemView as ConstraintLayout
            image = itemView.findViewById(R.id.imageFilm)
            titre = itemView.findViewById(R.id.titre)
            anneParution = itemView.findViewById(R.id.anneParution)
            slogan = itemView.findViewById(R.id.slogan)
            note = itemView.findViewById(R.id.note)
        }
    }

    class ListeFilmAdaptor(
        val ctx: Context,
        val activity: MainActivity,
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
// On peut ajouter un événement au clic sur cette rangée
            holder.layout.setOnClickListener {
                Toast.makeText(
                    ctx,
                    "On a cliqué sur l'item numéro $position",
                    Toast.LENGTH_SHORT
                ).show()
            }
            holder.image.setImageResource(item.image)
            holder.anneParution.text = item.anneParution
            holder.slogan.text = item.slogan
            holder.titre.text = item.titre
            holder.note.progress = item.nombreEtoile
        }

    }


}