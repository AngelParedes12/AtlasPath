package edu.ucne.atlaspath.presentation.tareas.profile

sealed class EditProfileEvent {
    data class OnNombreChange(val nombre: String) : EditProfileEvent()
    data class OnPesoChange(val peso: Float) : EditProfileEvent()
    data class OnAlturaChange(val altura: Float) : EditProfileEvent()
    data class OnNivelChange(val nivel: String) : EditProfileEvent()
    data class OnObjetivoChange(val objetivo: String) : EditProfileEvent()
    data object SaveProfile : EditProfileEvent()
}