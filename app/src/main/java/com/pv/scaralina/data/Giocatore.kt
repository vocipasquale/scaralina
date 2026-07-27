package com.pv.scaralina.data

data class Giocatore(
    val nome: String,
    private var punteggi: MutableList<Int> = mutableListOf()

){
    fun aggiungiPunteggio(punteggio: Int){
        punteggi.add(punteggio)
    }

    fun getPunteggi(): MutableList<Int>{
        return punteggi
    }

    fun getpunteggioTotale(): Int{
        return punteggi.sum()
    }


}