package com.pv.scaralina.data

import android.util.Log

object Partita {
    private val _giocatori: MutableList<Giocatore> = mutableListOf()
    private var idxGiocatoreCorr : Int = 0 //primo della lista
    val giocatori: List<Giocatore> get() = _giocatori
    var timerAbilitato: Boolean = false
    var durataTimer: Int = 0


    fun aggiungiGiocatore(giocatore: Giocatore) {
        _giocatori.add(giocatore)
    }

    fun resetGiocatori() {
        _giocatori.clear()
    }

    fun getGiocatoreCorrente(): Giocatore{
        return _giocatori.get(idxGiocatoreCorr)
    }

    fun passaGiocatoreSuccessivo(){
        if(idxGiocatoreCorr == _giocatori.size-1){
            idxGiocatoreCorr = 0
        }else{
            idxGiocatoreCorr++
        }
    }

    fun aggiornaPunteggio(giocatoreCorrente: Giocatore, punteggio: Int) {
        // Trova l'indice del giocatore corrente
        val index = _giocatori.indexOf(giocatoreCorrente)

        // Se il giocatore esiste nella lista, aggiorna il punteggio
        if (index != -1) {
            val giocatoreAggiornato = Giocatore(giocatoreCorrente.nome, giocatoreCorrente.punteggio+punteggio)
            _giocatori[index] = giocatoreAggiornato
            Log.d("aggiornaPunteggio", _giocatori[index].nome+" "+_giocatori[index].punteggio )
        }else{
            Log.e("aggiornaPunteggio", giocatoreCorrente.nome+" non presente!" )
        }
    }

}