package com.example.chaika.data.room.repo.old

import androidx.lifecycle.LiveData
import com.example.chaika.data.room.dao.old.ActionDao
import com.example.chaika.data.room.entities.old.Action

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

    suspend fun deleteActionsByTripId(tripId: Int) {
        actionDao.deleteActionsByTripId(tripId)
    }

    suspend fun deleteActionsForProductInTrip(productId: Int, tripId: Int) {
        actionDao.deleteActionsForProductInTrip(productId, tripId)
    }

}
