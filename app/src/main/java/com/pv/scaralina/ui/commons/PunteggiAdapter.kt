import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pv.scaralina.R
import com.pv.scaralina.data.ColonnaPunteggi

class PunteggiAdapter(
    private val colonne: List<ColonnaPunteggi>
) : RecyclerView.Adapter<PunteggiAdapter.RigaViewHolder>() {

    // numero righe = numero mani
    override fun getItemCount(): Int = colonne.firstOrNull()?.valori?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RigaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_riga_punteggi, parent, false)
        return RigaViewHolder(view)
    }

    override fun onBindViewHolder(holder: RigaViewHolder, position: Int) {
        holder.bind(colonne, position)
    }

    class RigaViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val rowContainer = view.findViewById<LinearLayout>(R.id.rowContainer)

        fun bind(colonne: List<ColonnaPunteggi>, rowIndex: Int) {

            rowContainer.removeAllViews()

            colonne.forEach { colonna ->

                val cella =
                    if (colonna.giocatore == null) {
                        creaTextView(colonna.valori[rowIndex]?.toString() ?: "")
                    } else {
                        creaEditText(colonna, rowIndex)
                    }

                rowContainer.addView(cella)
            }
        }

        private fun creaTextView(text: String): TextView =
            TextView(itemView.context).apply {
                this.text = text
                gravity = Gravity.CENTER
                textSize = 14f
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

        private fun creaEditText(
            colonna: ColonnaPunteggi,
            rowIndex: Int
        ): EditText =
            EditText(itemView.context).apply {

                setText(colonna.valori[rowIndex]?.toString() ?: "")
                gravity = Gravity.CENTER
                inputType = InputType.TYPE_CLASS_NUMBER
                textSize = 14f

                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )

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
