package com.pv.scaralina.ui.dialogs

import PunteggiAdapter
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pv.scaralina.R
import com.pv.scaralina.data.ColonnaPunteggi
import com.pv.scaralina.data.Partita

class PunteggiDialogFragment : DialogFragment() {

    interface OnPunteggiChangedListener {
        fun onPunteggiChanged()
    }

    private var listener: OnPunteggiChangedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnPunteggiChangedListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.punteggi_partita, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val headerContainer = view.findViewById<LinearLayout>(R.id.headerGiocatori)

        Partita.giocatori.forEach { giocatore ->
            val tv = TextView(requireContext()).apply {
                text = giocatore.nome
                textSize = 16f
                gravity = Gravity.CENTER
                setTypeface(null, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            headerContainer.addView(tv)
        }

        val rv = view.findViewById<RecyclerView>(R.id.rvPunteggi)
        rv.layoutManager = LinearLayoutManager(requireContext())

        val colonne = mutableListOf<ColonnaPunteggi>()

        // COLONNE GIOCATORI
        Partita.giocatori.forEach { giocatore ->
            colonne.add(
                ColonnaPunteggi(
                    header = giocatore.nome,
                    giocatore = giocatore,
                    valori = giocatore.getPunteggi().toMutableList()
                )
            )
        }

        rv.adapter = PunteggiAdapter(colonne)

        view.findViewById<ImageButton>(R.id.btnClose).setOnClickListener {
            listener?.onPunteggiChanged()
            dismiss()
        }
    }
}
