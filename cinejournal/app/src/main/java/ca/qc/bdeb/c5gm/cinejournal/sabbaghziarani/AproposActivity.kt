package ca.qc.bdeb.c5gm.cinejournal.sabbaghziarani

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AproposActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apropos)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.title = "À propos"
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
    }
}