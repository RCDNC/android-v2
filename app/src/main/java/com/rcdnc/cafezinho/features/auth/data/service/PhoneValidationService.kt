package com.rcdnc.cafezinho.features.auth.data.service

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Phone number validation service using LibPhoneNumber
 * Handles international phone number formatting and validation
 */
@Singleton
class PhoneValidationService @Inject constructor() {
    
    private val phoneNumberUtil = PhoneNumberUtil.getInstance()
    
    /**
     * Validate and format phone number
     * @param phoneNumber Raw phone number input
     * @param defaultCountryCode Default country code (e.g., "BR")
     * @return Formatted phone number result
     */
    fun validateAndFormat(
        phoneNumber: String,
        defaultCountryCode: String = "BR"
    ): PhoneValidationResult {
        return try {
            // Parse the phone number
            val parsedNumber = phoneNumberUtil.parse(phoneNumber, defaultCountryCode)
            
            // Validate the number
            val isValid = phoneNumberUtil.isValidNumber(parsedNumber)
            val isPossible = phoneNumberUtil.isPossibleNumber(parsedNumber)
            
            if (!isValid) {
                return PhoneValidationResult.Invalid(
                    error = if (!isPossible) "Invalid phone number format" else "Invalid phone number"
                )
            }
            
            // Format the number for different purposes
            val nationalFormat = phoneNumberUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
            val internationalFormat = phoneNumberUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
            val e164Format = phoneNumberUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.E164)
            
            // Get country information
            val countryCode = parsedNumber.countryCode
            val regionCode = phoneNumberUtil.getRegionCodeForNumber(parsedNumber)
            
            PhoneValidationResult.Valid(
                originalInput = phoneNumber,
                parsedNumber = parsedNumber,
                nationalFormat = nationalFormat,
                internationalFormat = internationalFormat,
                e164Format = e164Format,
                countryCode = countryCode,
                regionCode = regionCode ?: defaultCountryCode
            )
            
        } catch (e: NumberParseException) {
            PhoneValidationResult.Invalid(
                error = when (e.errorType) {
                    NumberParseException.ErrorType.INVALID_COUNTRY_CODE -> "Invalid country code"
                    NumberParseException.ErrorType.NOT_A_NUMBER -> "Not a valid phone number"
                    NumberParseException.ErrorType.TOO_SHORT_NSN -> "Phone number too short"
                    NumberParseException.ErrorType.TOO_SHORT_AFTER_IDD -> "Phone number too short after country code"
                    NumberParseException.ErrorType.TOO_LONG -> "Phone number too long"
                    else -> "Invalid phone number format"
                }
            )
        } catch (e: Exception) {
            PhoneValidationResult.Invalid(error = "Validation error: ${e.message}")
        }
    }
    
    /**
     * Format phone number for display
     * @param phoneNumber E164 formatted phone number
     * @param format Display format type
     * @return Formatted phone number string
     */
    fun formatForDisplay(
        phoneNumber: String,
        format: PhoneDisplayFormat = PhoneDisplayFormat.NATIONAL
    ): String {
        return try {
            val parsedNumber = phoneNumberUtil.parse(phoneNumber, null)
            
            when (format) {
                PhoneDisplayFormat.NATIONAL -> phoneNumberUtil.format(
                    parsedNumber,
                    PhoneNumberUtil.PhoneNumberFormat.NATIONAL
                )
                PhoneDisplayFormat.INTERNATIONAL -> phoneNumberUtil.format(
                    parsedNumber,
                    PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL
                )
                PhoneDisplayFormat.E164 -> phoneNumberUtil.format(
                    parsedNumber,
                    PhoneNumberUtil.PhoneNumberFormat.E164
                )
            }
        } catch (e: Exception) {
            phoneNumber // Return original if formatting fails
        }
    }
    
    /**
     * Get country code from phone number
     * @param phoneNumber Phone number in any format
     * @return Country code (e.g., 55 for Brazil)
     */
    fun getCountryCode(phoneNumber: String): Int? {
        return try {
            val parsedNumber = phoneNumberUtil.parse(phoneNumber, null)
            parsedNumber.countryCode
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Get region code from phone number
     * @param phoneNumber Phone number in any format
     * @return Region code (e.g., "BR" for Brazil)
     */
    fun getRegionCode(phoneNumber: String): String? {
        return try {
            val parsedNumber = phoneNumberUtil.parse(phoneNumber, null)
            phoneNumberUtil.getRegionCodeForNumber(parsedNumber)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Check if phone number is mobile
     * @param phoneNumber Phone number in any format
     * @return True if mobile number
     */
    fun isMobileNumber(phoneNumber: String): Boolean {
        return try {
            val parsedNumber = phoneNumberUtil.parse(phoneNumber, null)
            val numberType = phoneNumberUtil.getNumberType(parsedNumber)
            
            numberType == PhoneNumberUtil.PhoneNumberType.MOBILE ||
            numberType == PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Get supported regions for phone validation
     */
    fun getSupportedRegions(): Set<String> {
        return phoneNumberUtil.supportedRegions
    }
    
    /**
     * Get example number for a region
     * @param regionCode Region code (e.g., "BR")
     * @param type Number type (mobile, fixed line, etc.)
     * @return Example phone number
     */
    fun getExampleNumber(
        regionCode: String,
        type: PhoneNumberUtil.PhoneNumberType = PhoneNumberUtil.PhoneNumberType.MOBILE
    ): String? {
        return try {
            val exampleNumber = phoneNumberUtil.getExampleNumberForType(regionCode, type)
            phoneNumberUtil.format(exampleNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
        } catch (e: Exception) {
            null
        }
    }
}

/**
 * Phone validation result sealed class
 */
sealed class PhoneValidationResult {
    data class Valid(
        val originalInput: String,
        val parsedNumber: PhoneNumber,
        val nationalFormat: String,
        val internationalFormat: String,
        val e164Format: String,
        val countryCode: Int,
        val regionCode: String
    ) : PhoneValidationResult()
    
    data class Invalid(
        val error: String
    ) : PhoneValidationResult()
}

/**
 * Phone display format options
 */
enum class PhoneDisplayFormat {
    NATIONAL,      // (11) 99999-9999
    INTERNATIONAL, // +55 11 99999-9999
    E164          // +5511999999999
}