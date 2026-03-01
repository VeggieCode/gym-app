package com.tlatoltech.gym.framework.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.tlatoltech.gym.GymApplication

class RoutineSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("RoutineSyncWorker", "Iniciando sincronización en segundo plano...")

        return try {
            // Obtenemos el repositorio desde nuestra clase Application (Manual DI)
            val app = applicationContext as GymApplication
            val repository = app.routineRepository

            // Delegamos la lógica al Repositorio (Clean Architecture)
            repository.syncPendingRoutines()

            Log.d("RoutineSyncWorker", "Sincronización completada con éxito.")
            Result.success()
        } catch (e: Exception) {
            Log.e("RoutineSyncWorker", "Fallo al sincronizar en segundo plano", e)
            // Si el servidor falla, le decimos a Android que lo intente más tarde
            Result.retry()
        }
    }
}