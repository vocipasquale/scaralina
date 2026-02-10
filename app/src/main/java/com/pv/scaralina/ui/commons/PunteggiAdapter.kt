import android.graphics.Typeface
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pv.scaralina.R
import com.pv.scaralina.data.ColonnaPunteggi
import com.pv.scaralina.data.Giocatore
import com.pv.scaralina.data.Partita

class PunteggiAdapter(
    private val colonne: List<ColonnaPunteggi>
) : RecyclerView.Adapter<PunteggiAdapter.ColonnaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColonnaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_colonna_punteggi, parent, false)
        return ColonnaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColonnaViewHolder, position: Int) {
        holder.bind(colonne[position])
    }

    override fun getItemCount(): Int = colonne.size

    class ColonnaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val container = view.findViewById<LinearLayout>(R.id.containerColonna)

        fun bind(colonna: ColonnaPunteggi) {
            container.removeAllViews()

            // HEADER
            container.addView(creaHeader(colonna.header))

            // CELLE
            colonna.valori.forEachIndexed { rowIndex, valore ->
                if (colonna.giocatore == null) {
                    // colonna indice → solo testo
                    container.addView(creaTextView(valore?.toString() ?: ""))
                } else {
                    // colonna giocatore → EditText
                    container.addView(
                        creaEditText(
                            valore,
                            colonna,
                            rowIndex
                        )
                    )
                }
            }
        }

        private fun creaHeader(text: String): TextView =
            TextView(container.context).apply {
                this.text = text
                gravity = Gravity.CENTER
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
            }

        private fun creaTextView(text: String): TextView =
            TextView(container.context).apply {
                this.text = text
                gravity = Gravity.CENTER
                textSize = 14f
            }

        private fun creaEditText(
            valore: Int?,
            colonna: ColonnaPunteggi,
            rowIndex: Int
        ): EditText =
            EditText(container.context).apply {
                setText(valore?.toString() ?: "")
                gravity = Gravity.CENTER
                inputType = InputType.TYPE_CLASS_NUMBER
                textSize = 14f

                setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        val nuovoValore = text.toString().toIntOrNull()
                        colonna.valori[rowIndex] = nuovoValore
                        nuovoValore?.let {
                            colonna.giocatore
                                ?.getPunteggi()
                                ?.set(rowIndex, it)
                        }
                    }
                }
            }
    }
}
