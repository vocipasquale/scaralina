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

    fun reset() {
        _giocatori.clear() // Pulisce la lista dei giocatori
        timerAbilitato = false // Resetta lo stato del timer
        durataTimer = 0 // Resetta la durata del timer
        idxGiocatoreCorr = 0
    }

}