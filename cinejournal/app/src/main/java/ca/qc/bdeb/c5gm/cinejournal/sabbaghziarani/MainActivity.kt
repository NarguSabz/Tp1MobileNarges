package ca.qc.bdeb.c5gm.cinejournal.sabbaghziarani

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    val donnesVM: CineViewModel by viewModels()
    lateinit var recycleView: RecyclerView
    lateinit var adapteur: ListeFilmAdaptor
    lateinit var aucunFilmText: TextView
    lateinit var preferencesParatagees: SharedPreferences
    lateinit var editeur: SharedPreferences.Editor
    lateinit var textTri: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        BuildConfig.API_KEY_TMDB
        aucunFilmText = findViewById(R.id.aucunFilm)
        recycleView = findViewById(R.id.listeFilms)
        textTri = findViewById(R.id.textTri)
        preferencesParatagees = getSharedPreferences("mesPreferences", MODE_PRIVATE)
        editeur = preferencesParatagees.edit()
        val premierExecution = preferencesParatagees.getBoolean("premierExecution", true)
        if (premierExecution) {
            partagerPreferences("note")
            editeur.putBoolean("premierExecution", false)
        }
        if (donnesVM.supprimer == true) {
            supprimerBd()
            donnesVM.supprimer = false

        }
    }


    /**on va besoin de obtenir la liste des films seulement au tout debut et apres chaque
    modification ou ajout de films, sinon  on veut pas reenvoyer une requete a la bd
    quand on change l orientation, donc on envoie une requete getall a la bd
    seulement quand la list des films dans viewmodels est vide et que la bd n est n est pas vide, donc au tout debut
    ou quand un nouveau film est ajoute ou un film est modifie, quand la bd est vide, on ne va pas faire une requete **/

    override fun onStart() {
        super.onStart()
        if (donnesVM.listeFilms.isEmpty() and !donnesVM.aucunFilmDansBD) {
            lifecycleScope.launch(Dispatchers.IO) {
                donnesVM.listeFilms = withContext(Dispatchers.IO) {
                    AppDatabase.getDatabase(applicationContext).filmDao().getAll()
                }
                withContext(Dispatchers.Main) {
                    if (donnesVM.listeFilms.isEmpty()) {
                        donnesVM.aucunFilmDansBD = true
                        aucunFilmText.visibility = View.VISIBLE

                    } else {
                        aucunFilmText.visibility = View.GONE
                        recycleView.visibility = View.VISIBLE
                        trierFilms()
                    }
                }
            }

        } else {
            if (donnesVM.listeFilms.isEmpty()) {
                aucunFilmText.visibility = View.VISIBLE
                val modeTri = preferencesParatagees.getString("modeTrie", null)
                textTri.text = "Tri par $modeTri"
            } else {
                aucunFilmText.visibility = View.GONE
                gererAdapteur()
            }

        }
    }

    fun ouvrirAjouter(view: View?) {
        val intent = Intent(applicationContext, AjouterEditerActivity::class.java)
        intent.putExtra("action", "Ajouter")
        donnesVM.listeFilms = listOf()
        donnesVM.aucunFilmDansBD = false
        startActivity(intent)
    }

    fun ouvrirModifier(film: Film) {
        val intent = Intent(applicationContext, AjouterEditerActivity::class.java)
        intent.putExtra("action", "Modifier")
        intent.putExtra("Film", film)
        donnesVM.listeFilms = listOf()
        donnesVM.aucunFilmDansBD = false
        startActivity(intent)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_supprimer -> supprimerBd()
            R.id.trie_titre -> partagerPreferences("titre")
            R.id.trie_annee -> partagerPreferences("annee")
            R.id.trie_note -> partagerPreferences("note")
            R.id.item_propos -> {
                val intent = Intent(applicationContext, AproposActivity::class.java)
                startActivity(intent)
            }
            R.id.item_celebrite -> {
                val intent = Intent(applicationContext, ActivityReconnaissanceCelebrite::class.java)
                startActivity(intent)
            }

            R.id.item_trouver -> {
                val intent = Intent(applicationContext, RechercheFilmActivity::class.java)
                donnesVM.listeFilms = listOf()
                donnesVM.aucunFilmDansBD = false
                startActivity(intent)
            }

            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    fun supprimerBd() {
        donnesVM.supprimer = true
        donnesVM.aucunFilmDansBD = true
        /*pour construit la boite de dialogue je me suis inspiree de cette video: https://youtu.be/uhXn8RcKKbI?si=KVvHwQT0qsEpu6i1*/
        val constructeur = AlertDialog.Builder(this)
        constructeur.setTitle("Tout supprimer")
            .setMessage("Cette action supprimera l'ensemble des films de la base de données, sans possibilité d'annuler")
            .setPositiveButton("Supprimer") { dialog, which ->
                lifecycleScope.launch(Dispatchers.IO) {
                    val database: AppDatabase =
                        AppDatabase.getDatabase(applicationContext)
                    database.filmDao().delete()
                }
                donnesVM.listeFilms = listOf()
                recycleView.visibility = View.GONE
                aucunFilmText.visibility = View.VISIBLE
                donnesVM.supprimer = false
            }
            .setNegativeButton("Annuler") { dialog, which ->
                donnesVM.supprimer = false
                dialog.dismiss()
            }
        val boiteDialogue: AlertDialog = constructeur.create()
        boiteDialogue.show()

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
            holder.layout.setOnClickListener {
                this.activity.ouvrirModifier(item)
            }
            if (item.image != null) Glide.with(this.activity).load(item.image).into(holder.image)
            holder.anneParution.text = "(${item.anneParution})"
            holder.slogan.text = item.slogan
            holder.titre.text = item.titre
            holder.note.progress = item.nombreEtoile
        }

    }

    fun trierFilms() {
        val modeTri = preferencesParatagees.getString("modeTrie", null)
        donnesVM.listeFilms = when (modeTri) {
            "note" -> donnesVM.listeFilms.sortedWith(compareBy { it.nombreEtoile })
            "titre" -> donnesVM.listeFilms.sortedWith(compareBy { it.titre })
            else -> donnesVM.listeFilms.sortedWith(compareBy { it.anneParution })
        }
        gererAdapteur()
    }

    fun gererAdapteur() {
        val modeTri = preferencesParatagees.getString("modeTrie", null)
        textTri.text = "Tri par $modeTri"
        adapteur = ListeFilmAdaptor(applicationContext, this@MainActivity, donnesVM.listeFilms)
        recycleView.adapter = adapteur
    }

    fun partagerPreferences(trie: String) {
        editeur.putString("modeTrie", trie)
        editeur.apply()
        trierFilms()
        return
    }


}