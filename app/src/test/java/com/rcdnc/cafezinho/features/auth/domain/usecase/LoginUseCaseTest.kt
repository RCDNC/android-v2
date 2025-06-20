package com.rcdnc.cafezinho.features.auth.domain.usecase

import com.rcdnc.cafezinho.features.auth.domain.model.User
import com.rcdnc.cafezinho.features.auth.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var loginUseCase: LoginUseCase

    private val mockUser = User(
        id = "123",
        firstName = "João",
        email = "joao@test.com",
        phone = "11999999999"
    )

    @Before
    fun setup() {
        authRepository = mockk()
        loginUseCase = LoginUseCase(authRepository)
    }

    @Test
    fun `invoke with valid social provider should return success`() = runTest {
        // Arrange
        val provider = "google"
        val params = mapOf("token" to "abc123")
        coEvery { authRepository.authenticateWithSocial(provider, params) } returns Result.success(mockUser)

        // Act
        val result = loginUseCase(provider, params)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(mockUser, result.getOrNull())
    }

    @Test
    fun `invoke with empty provider should return failure`() = runTest {
        // Arrange
        val provider = ""
        val params = mapOf("token" to "abc123")

        // Act
        val result = loginUseCase(provider, params)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Provider cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with empty params should return failure`() = runTest {
        // Arrange
        val provider = "google"
        val params = emptyMap<String, Any>()

        // Act
        val result = loginUseCase(provider, params)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Authentication parameters are required", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with user missing ID should return failure`() = runTest {
        // Arrange
        val provider = "google"
        val params = mapOf("token" to "abc123")
        val userWithoutId = mockUser.copy(id = "")
        coEvery { authRepository.authenticateWithSocial(provider, params) } returns Result.success(userWithoutId)

        // Act
        val result = loginUseCase(provider, params)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Invalid user ID received from authentication", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke with user missing first name should return failure`() = runTest {
        // Arrange
        val provider = "google"
        val params = mapOf("token" to "abc123")
        val userWithoutName = mockUser.copy(firstName = null)
        coEvery { authRepository.authenticateWithSocial(provider, params) } returns Result.success(userWithoutName)

        // Act
        val result = loginUseCase(provider, params)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("User first name is required", result.exceptionOrNull()?.message)
    }

    @Test
    fun `loginWithPhone with valid credentials should return success`() = runTest {
        // Arrange
        val phone = "11999999999"
        val otp = "123456"
        coEvery { authRepository.loginWithPhone(phone, otp) } returns Result.success(mockUser)

        // Act
        val result = loginUseCase.loginWithPhone(phone, otp)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(mockUser, result.getOrNull())
    }

    @Test
    fun `loginWithPhone with empty phone should return failure`() = runTest {
        // Arrange
        val phone = ""
        val otp = "123456"

        // Act
        val result = loginUseCase.loginWithPhone(phone, otp)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Phone number cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `loginWithPhone with empty OTP should return failure`() = runTest {
        // Arrange
        val phone = "11999999999"
        val otp = ""

        // Act
        val result = loginUseCase.loginWithPhone(phone, otp)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("OTP cannot be empty", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should handle repository exception`() = runTest {
        // Arrange
        val provider = "google"
        val params = mapOf("token" to "abc123")
        val exception = RuntimeException("Network error")
        coEvery { authRepository.authenticateWithSocial(provider, params) } returns Result.failure(exception)

        // Act
        val result = loginUseCase(provider, params)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}