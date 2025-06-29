package com.lourenc.trolly.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.lourenc.trolly.data.local.entity.ItemLista

@Dao
interface ItemListaDao {
    @Insert
    suspend fun inserir(item: ItemLista)

    @Update
    suspend fun atualizar(item: ItemLista)

    @Delete
    suspend fun deletar(item: ItemLista)

    @Query("SELECT * FROM item_lista WHERE idLista = :idLista")
    suspend fun getItensPorLista(idLista: Int): List<ItemLista>
    
    @Query("SELECT * FROM item_lista WHERE idLista = :idLista AND name = :nome LIMIT 1")
    suspend fun getItemPorNome(idLista: Int, nome: String): ItemLista?
}
