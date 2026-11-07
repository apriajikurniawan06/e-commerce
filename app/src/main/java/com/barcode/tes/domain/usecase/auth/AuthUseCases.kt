package com.barcode.tes.domain.usecase.auth

data class AuthUseCases(
    val login: LoginUseCase,
    val signUp: SignUpUseCase,
    val logout: LogoutUseCase,
    val getProfile: GetProfileUseCase,
    val updateProfile: UpdateProfileUseCase,
    val uploadAvatar: UploadAvatarUseCase,
    val deleteAccount: DeleteAccountUseCase
)
