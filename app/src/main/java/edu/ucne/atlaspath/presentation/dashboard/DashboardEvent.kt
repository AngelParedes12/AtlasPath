package edu.ucne.atlaspath.presentation.dashboard


sealed class DashboardEvent {
    object RefreshData : DashboardEvent()
    data class UpdatePeso(val nuevoPeso: Double) : DashboardEvent()

    data class UpdateName(val nuevoNombre: String) : DashboardEvent()
}