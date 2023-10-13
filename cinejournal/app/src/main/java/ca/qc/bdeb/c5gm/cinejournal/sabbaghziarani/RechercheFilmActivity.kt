package ca.qc.bdeb.c5gm.cinejournal.sabbaghziarani

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class RechercheFilmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recherche_film)
        setSupportActionBar(findViewById(R.id.toolbar))
    }
}