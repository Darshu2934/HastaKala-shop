package com.hastakala.app.data.room

import androidx.room.*
import com.hastakala.app.data.Product
import com.hastakala.app.data.Sale
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProduct(id: String)

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: String): Product?

    @Query("SELECT * FROM sales ORDER BY date DESC")
    fun getAllSales(): Flow<List<Sale>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: Sale)

    @Query("DELETE FROM sales WHERE id = :id")
    suspend fun deleteSale(id: String)

    @Query("SELECT * FROM sales WHERE id = :id LIMIT 1")
    suspend fun getSaleById(id: String): Sale?

    @Query("DELETE FROM sales WHERE productId = :productId")
    suspend fun deleteSalesByProductId(productId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSales(sales: List<Sale>)
}
