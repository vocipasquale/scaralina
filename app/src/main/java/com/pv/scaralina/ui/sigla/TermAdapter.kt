package com.pv.scaralina.ui.sigla

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pv.scaralina.R
import com.pv.scaralina.data.room.TermEntity

class TermAdapter(
    private var termini: List<TermEntity>,
    private val onItemClick: (TermEntity) -> Unit,
    private val onDeleteClick: (TermEntity) -> Unit // nuovo callback per delete
) : RecyclerView.Adapter<TermAdapter.TermViewHolder>() {

    inner class TermViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvParola: TextView = itemView.findViewById(R.id.tvParola)
        val tvDefinizione: TextView = itemView.findViewById(R.id.tvDefinizione)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete) // riferimento al pulsante

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(termini[position])
                }
            }

            btnDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick(termini[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TermViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_termine, parent, false)
        return TermViewHolder(view)
    }

    override fun onBindViewHolder(holder: TermViewHolder, position: Int) {
        val term = termini[position]
        holder.tvParola.text = term.parola
        holder.tvDefinizione.text = term.definizione ?: ""
    }

    override fun getItemCount(): Int = termini.size

    fun updateList(newList: List<TermEntity>) {
        termini = newList
        notifyDataSetChanged()
    }
}
