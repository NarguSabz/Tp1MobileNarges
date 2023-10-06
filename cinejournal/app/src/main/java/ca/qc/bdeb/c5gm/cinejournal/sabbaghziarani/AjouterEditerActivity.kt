package ca.qc.bdeb.c5gm.cinejournal.sabbaghziarani

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AjouterEditerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajouter_editer)
        setSupportActionBar(findViewById(R.id.toolbar))
    }
}