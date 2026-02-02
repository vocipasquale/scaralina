package com.pv.scaralina.ui.sigla

import android.app.Dialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.pv.scaralina.R
import com.pv.scaralina.ScaralinaApp
import com.pv.scaralina.data.room.TermEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SiglaActivity : AppCompatActivity() {

    private lateinit var btnAggiungi: Button
    private lateinit var btnTornaIndietro: Button
    private lateinit var rvTermini: RecyclerView
    private lateinit var termAdapter: TermAdapter
    private lateinit var chipGroupIndice: ChipGroup

    private val database by lazy { (application as ScaralinaApp).database }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sigla)

        btnAggiungi = findViewById(R.id.btnAggiungi)
        btnTornaIndietro = findViewById(R.id.btnTornaIndietro)
        rvTermini = findViewById(R.id.rvTermini)
        chipGroupIndice = findViewById(R.id.chipGroupIndice)

        setupRecyclerView()
        creaIndiceAlfabetico()
        loadTermini()

        btnAggiungi.setOnClickListener { showAddWordDialog() }
        btnTornaIndietro.setOnClickListener { finish() }
    }

    // -----------------------------
    // RecyclerView
    // -----------------------------
    private fun setupRecyclerView() {
        termAdapter = TermAdapter(
            emptyList(),
            onItemClick = {},
            onDeleteClick = { term ->
                lifecycleScope.launch(Dispatchers.IO) {
                    database.termDao().deleteByParola(term.parola)
                    withContext(Dispatchers.Main) { loadTermini() }
                }
            }
        )
        rvTermini.layoutManager = LinearLayoutManager(this)
        rvTermini.adapter = termAdapter
    }

    // -----------------------------
    // Indice alfabetico (Chip)
    // -----------------------------
    private fun creaIndiceAlfabetico() {

        val chipTutte = creaChip("TUTTE") {
            loadTermini()
        }
        chipTutte.isChecked = true
        chipGroupIndice.addView(chipTutte)

        ('A'..'Z').forEach { lettera ->
            chipGroupIndice.addView(
                creaChip(lettera.toString()) {
                    filtraPerLettera(lettera.toString())
                }
            )
        }
    }

    private fun creaChip(testo: String, onClick: () -> Unit): Chip =
        Chip(this).apply {
            text = testo
            isCheckable = true
            isClickable = true
            setOnClickListener { onClick() }

            // 🔥 RIMOZIONE TOTALE DELLA "PILLOLA"
            background = null
            setBackgroundColor(android.graphics.Color.TRANSPARENT)

            // Evita altezze minime Material
            minHeight = 0
            minimumHeight = 0

            // Niente effetti grafici
            isCloseIconVisible = false
            rippleColor = null
            elevation = 0f

            // Testo coerente
            setTextColor(resources.getColor(R.color.black))

            // Padding manuale (solo area touch)
            setPadding(12, 8, 12, 8)
        }




    private fun filtraPerLettera(lettera: String) {
        lifecycleScope.launch {
            val termini = withContext(Dispatchers.IO) {
                database.termDao().getByIniziale(lettera)
            }
            termAdapter.updateList(termini)
        }
    }

    // -----------------------------
    // Caricamento dati
    // -----------------------------
    private fun loadTermini() {
        lifecycleScope.launch {
            val termini = withContext(Dispatchers.IO) {
                database.termDao().getAll().sortedBy { it.parola.lowercase() }
            }
            termAdapter.updateList(termini)
        }
    }

    // -----------------------------
    // Dialog aggiunta parola
    // -----------------------------
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

            lifecycleScope.launch(Dispatchers.IO) {
                database.termDao().insertAll(
                    listOf(TermEntity(parola = parola, definizione = definizione))
                )

                withContext(Dispatchers.Main) {
                    dialog.dismiss()
                    loadTermini()
                }
            }
        }

        dialog.show()
    }
}
