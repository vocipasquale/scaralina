package com.pv.scaralina.ui.commons


import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.pv.scaralina.R
import com.pv.scaralina.ScaralinaApp
import com.pv.scaralina.data.room.TermEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val PAROLA_TROVATA_SNZ_DEF = "Parola trovata ma senza definizione"

class CercaParolaDialogFragment : DialogFragment() {

    private lateinit var btnClear: ImageButton
    private lateinit var btnSearch: ImageButton
    private lateinit var tilParola: TextInputLayout
    private lateinit var etParola: TextInputEditText
    private lateinit var tvDefinizione: TextView
    private lateinit var ivDefinizione: ImageView

    private lateinit var btnAggiungi: ImageButton

    private lateinit var rvTermini: RecyclerView
    private lateinit var termAdapter: TermAdapter
    private lateinit var chipGroupIndice: ChipGroup

    private val database by lazy { (requireActivity().application as ScaralinaApp).database }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cerca_parola, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnClear = view.findViewById(R.id.btnClear)
        btnSearch = view.findViewById(R.id.btnSearch)
        tilParola = view.findViewById(R.id.tilParola)
        etParola = view.findViewById(R.id.etParola)
        tvDefinizione = view.findViewById(R.id.tvDefinizione)
        ivDefinizione = view.findViewById(R.id.ivDefinizione)
        btnAggiungi = view.findViewById(R.id.btnAggiungi)
        rvTermini = view.findViewById(R.id.rvTermini)
        chipGroupIndice = view.findViewById(R.id.chipGroupIndice)

        // ===========================
        // PULISCI CAMPI
        // ===========================
        btnClear.setOnClickListener {
            etParola.text?.clear()
            tvDefinizione.text = ""
            etParola.clearFocus()
            ivDefinizione.visibility = View.GONE
        }

        // ===========================
        // CERCA PAROLA
        // ===========================
        btnSearch.setOnClickListener {
            val parola = etParola.text.toString().trim()
            if (parola.isEmpty()) {
                tvDefinizione.text = "Inserisci una parola"
                ivDefinizione.setImageResource(R.drawable.ic_infelice_96)
                ivDefinizione.visibility = View.VISIBLE
                return@setOnClickListener
            }

            lifecycleScope.launch {
                var definizione = cercaParolaInStorage(parola)

                if (definizione.isBlank()) {
                    definizione = cercaParolaOnline(parola)
                }

                if (definizione.isBlank()) {
                    definizione = "Parola non trovata"
                    ivDefinizione.setImageResource(R.drawable.ic_infelice_96)
                } else {
                    ivDefinizione.setImageResource(R.drawable.ic_felice_96)
                }

                tvDefinizione.text = definizione
                ivDefinizione.visibility = View.VISIBLE
            }
        }

        val btnClose = view.findViewById<ImageButton>(R.id.btnClose)

        btnClose.setOnClickListener {
            dismiss()
        }

        setupRecyclerView()
        creaIndiceAlfabetico()
        loadTermini()

        btnAggiungi.setOnClickListener { showAddWordDialog() }
    }

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
        rvTermini.layoutManager = LinearLayoutManager(requireContext())
        rvTermini.adapter = termAdapter
    }

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
        Chip(requireContext()).apply {
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

    private fun showAddWordDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_add_sigla)

        val editTextParola = dialog.findViewById<EditText>(R.id.editTextParola)
        val editTextDefinizione = dialog.findViewById<EditText>(R.id.editTextDefinizione)
        val btnAdd = dialog.findViewById<ImageButton>(R.id.btnAdd)
        val btnClose = dialog.findViewById<ImageButton>(R.id.btnClose)

        btnClose.setOnClickListener{
            dialog.dismiss()
        }

        btnAdd.setOnClickListener {
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

    // ===========================
    // STORAGE
    // ===========================
    private suspend fun cercaParolaInStorage(parola: String): String =
        withContext(Dispatchers.IO) {
            database.termDao().getByParola(parola)
                ?.let { "${it.parola}: ${it.definizione}" }
                ?: ""
        }

    // ===========================
    // NETWORK
    // ===========================
    interface WiktionaryApi {
        @GET("w/api.php?action=query&format=json&prop=extracts|categories&exintro=&explaintext=")
        suspend fun getDefinizione(@Query("titles") titolo: String): Map<String, Any>
    }

    private suspend fun cercaParolaOnline(parola: String): String = withContext(Dispatchers.IO) {
        try {
            // Configurazione OkHttpClient con User-Agent
            val okHttpClient = okhttp3.OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header("User-Agent", "Mozilla/5.0 (Android) ScaralinaApp")
                        .build()
                    Log.i("cercaParolaOnline", "Request URL: ${request.url()}")
                    chain.proceed(request)
                }
                .build()

            // Retrofit setup
            val retrofit = Retrofit.Builder()
                .baseUrl("https://it.wiktionary.org/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(WiktionaryApi::class.java)
            val response = service.getDefinizione(parola)
            //val response = testParsing("{\"batchcomplete\":\"\",\"query\":{\"pages\":{\"622410\":{\"pageid\":622410,\"ns\":0,\"title\":\"aaa\",\"extract\":\"\",\"categories\":[{\"ns\":14,\"title\":\"Categoria:Avverbi in luganda\"}]}}}}")

            // Parsing della response
            val query = response["query"] as? Map<*, *>
            val pages = query?.get("pages") as? Map<*, *>
            val firstPage = pages?.values?.firstOrNull() as? Map<*, *>

            when {
                firstPage == null -> "" // PAROLA NON TROVATA
                firstPage.containsKey("missing") -> "" // PAROLA NON TROVATA
                else -> {
                    // Controllo categorie per lingua italiana
                    val categories = firstPage["categories"] as? List<*>
                    val isItaliano = categories
                        ?.mapNotNull { it as? Map<*, *> }
                        ?.mapNotNull { it["title"] as? String }
                        ?.any { it.contains("italiano", ignoreCase = true) }
                        ?: false

                    if (!isItaliano) {
                        "" // PAROLA NON TROVATA (non italiana)
                    } else {
                        // Estratto definizione breve
                        val extract = firstPage["extract"] as? String
                        if (extract.isNullOrBlank()) PAROLA_TROVATA_SNZ_DEF else extract
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ricerca online", "errore", e)
            e.cause?.toString() ?: e.toString()
        }
    }


    // Per test JSON
    fun testParsing(jsonString: String): Map<String, Any> {
        val gson = Gson()
        val map: Map<String, Any> = gson.fromJson(jsonString, Map::class.java) as Map<String, Any>
        return map
    }
}