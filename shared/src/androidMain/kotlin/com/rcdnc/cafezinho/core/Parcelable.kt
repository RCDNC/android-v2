package com.rcdnc.cafezinho.core

import android.os.Parcelable

/**
 * Esta é a "implementação real" para a plataforma Android.
 * Estamos dizendo que a nossa promessa Parcelable, no Android, é o android.os.Parcelable.
 */
actual typealias Parcelable = Parcelable