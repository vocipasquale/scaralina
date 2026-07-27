package com.pv.scaralina.data

data class ColonnaPunteggi(
    val header: String,
    val giocatore: Giocatore?,          // null per la colonna indice
    val valori: MutableList<Int?>
)


