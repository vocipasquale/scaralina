package com.pv.scaralina.ui.sigla

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pv.scaralina.ScaralinaApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.pv.scaralina.R
import com.pv.scaralina.data.room.TermEntity

class SiglaActivity : AppCompatActivity() {

    private lateinit var btnAggiungi: Button
    private lateinit var btnTornaIndietro: Button
    private lateinit var rvTermini: RecyclerView
    private lateinit var termAdapter: TermAdapter

    private val database by lazy { (application as ScaralinaApp).database }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sigla)

        btnAggiungi = findViewById(R.id.btnAggiungi)
        btnTornaIndietro = findViewById(R.id.btnTornaIndietro)
        rvTermini = findViewById(R.id.rvTermini)

        // Setup RecyclerView
        termAdapter = TermAdapter(emptyList(),
            onItemClick = {  },
            onDeleteClick = { term ->
                CoroutineScope(Dispatchers.IO).launch {
                    database.termDao().deleteByParola(term.parola) // DELETE diretta
                    withContext(Dispatchers.Main) {
                        loadTermini() // aggiorna la lista
                    }
                }
            }
        )

        rvTermini.apply {
            layoutManager = LinearLayoutManager(this@SiglaActivity)
            adapter = termAdapter
        }

        // Carica termini da DB
        loadTermini()

        // Pulsante Aggiungi sigla
        btnAggiungi.setOnClickListener { showAddWordDialog() }

        btnTornaIndietro.setOnClickListener {
            finish()
        }
    }

    private fun loadTermini() {
        CoroutineScope(Dispatchers.IO).launch {
            val termini = database.termDao().getAll().sortedBy { it.parola.lowercase() }
            withContext(Dispatchers.Main) {
                termAdapter.updateList(termini)
            }
        }
    }

    private fun showAddWordDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_sigla)

        val editTextParola = dialog.findViewById<EditText>(R.id.editTextParola)
        val editTextDefinizione = dialog.findViewById<EditText>(R.id.editTextDefinizione)
        val buttonAdd = dialog.findViewById<Button>(R.id.buttonAdd)

        buttonAdd.setOnClickListener {
            val parola = editTextParola.text.toString().trim()
            val definizione = editTextDefinizione.text.toString().trim().ifEmpty { null }

            if (parola.isEmpty()) {
                editTextParola.error = "Inserisci parola"
                return@setOnClickListener
            }

            // Inserimento nel database in background
            CoroutineScope(Dispatchers.IO).launch {
                database.termDao().insertAll(
                    listOf(TermEntity(parola = parola, definizione = definizione))
                )

                withContext(Dispatchers.Main) {
                    // Chiudi il dialogo e ricarica i dati
                    dialog.dismiss()
                    loadTermini()
                }
            }
        }

        dialog.show()
    }



}
