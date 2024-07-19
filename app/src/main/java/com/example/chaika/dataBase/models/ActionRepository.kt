package com.example.chaika.dataBase.models

import androidx.lifecycle.LiveData
import com.example.chaika.dataBase.dao.ActionDao
import com.example.chaika.dataBase.entities.Action

class ActionRepository(private val actionDao: ActionDao) {

    fun getActionsByTripId(tripId: Int): LiveData<List<Action>> {
        return actionDao.getActionsByTripId(tripId)
    }

    suspend fun insert(action: Action) {
        actionDao.insert(action)
    }

    suspend fun addAction(tripId: Int, productId: Int, operationId: Int, count: Int) {
        val action = Action(
            id = 0,
            time = System.currentTimeMillis().toString(),
            tripId = tripId,
            operationId = operationId,
            productId = productId,
            count = count
        )
        actionDao.insert(action)
    }

}
