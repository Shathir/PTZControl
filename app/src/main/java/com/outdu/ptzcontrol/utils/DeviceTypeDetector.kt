package com.outdu.ptzcontrol.utils

import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.view.WindowManager
import kotlin.math.sqrt

/**
 * Utility class to detect device type (phone or tablet)
 */
class DeviceTypeDetector(private val context: Context) {
    
    /**
     * Device types
     */
    enum class DeviceType {
        PHONE,
        TABLET
    }
    
    /**
     * Detects if the current device is a tablet or phone
     * Uses multiple methods for accurate detection
     */
    fun getDeviceType(): DeviceType {
        return when {
            isTabletByScreenSize() -> DeviceType.TABLET
            isTabletByConfiguration() -> DeviceType.TABLET
            else -> DeviceType.PHONE
        }
    }
    
    /**
     * Check if device is tablet using screen size in inches
     * Tablets typically have screens >= 7 inches
     */
    private fun isTabletByScreenSize(): Boolean {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        
        val widthInches = displayMetrics.widthPixels / displayMetrics.xdpi
        val heightInches = displayMetrics.heightPixels / displayMetrics.ydpi
        val diagonalInches = sqrt((widthInches * widthInches + heightInches * heightInches).toDouble())
        
        // Tablets are typically 7+ inches diagonal
        return diagonalInches >= 7.0
    }
    
    /**
     * Check if device is tablet using Android configuration
     * Uses screen layout size configuration
     */
    private fun isTabletByConfiguration(): Boolean {
        val configuration = context.resources.configuration
        val screenLayout = configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        
        return screenLayout == Configuration.SCREENLAYOUT_SIZE_LARGE ||
               screenLayout == Configuration.SCREENLAYOUT_SIZE_XLARGE
    }
    
    /**
     * Get screen size category as string
     */
    fun getScreenSizeCategory(): String {
        val configuration = context.resources.configuration
        return when (configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) {
            Configuration.SCREENLAYOUT_SIZE_SMALL -> "Small"
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> "Normal"
            Configuration.SCREENLAYOUT_SIZE_LARGE -> "Large"
            Configuration.SCREENLAYOUT_SIZE_XLARGE -> "XLarge"
            else -> "Unknown"
        }
    }
    
    /**
     * Get screen density category
     */
    fun getScreenDensity(): String {
        val displayMetrics = context.resources.displayMetrics
        return when (displayMetrics.densityDpi) {
            DisplayMetrics.DENSITY_LOW -> "LDPI"
            DisplayMetrics.DENSITY_MEDIUM -> "MDPI"
            DisplayMetrics.DENSITY_HIGH -> "HDPI"
            DisplayMetrics.DENSITY_XHIGH -> "XHDPI"
            DisplayMetrics.DENSITY_XXHIGH -> "XXHDPI"
            DisplayMetrics.DENSITY_XXXHIGH -> "XXXHDPI"
            else -> "Unknown (${displayMetrics.densityDpi})"
        }
    }
    
    /**
     * Get screen diagonal size in inches
     */
    fun getScreenDiagonalInches(): Double {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        
        val widthInches = displayMetrics.widthPixels / displayMetrics.xdpi
        val heightInches = displayMetrics.heightPixels / displayMetrics.ydpi
        
        return sqrt((widthInches * widthInches + heightInches * heightInches).toDouble())
    }
    
    /**
     * Get detailed device information
     */
    fun getDeviceInfo(): DeviceInfo {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        
        return DeviceInfo(
            deviceType = getDeviceType(),
            screenSizeCategory = getScreenSizeCategory(),
            screenDensity = getScreenDensity(),
            screenDiagonalInches = getScreenDiagonalInches(),
            screenWidthPixels = displayMetrics.widthPixels,
            screenHeightPixels = displayMetrics.heightPixels,
            densityDpi = displayMetrics.densityDpi,
            xdpi = displayMetrics.xdpi,
            ydpi = displayMetrics.ydpi
        )
    }
    
    /**
     * Data class containing comprehensive device information
     */
    data class DeviceInfo(
        val deviceType: DeviceType,
        val screenSizeCategory: String,
        val screenDensity: String,
        val screenDiagonalInches: Double,
        val screenWidthPixels: Int,
        val screenHeightPixels: Int,
        val densityDpi: Int,
        val xdpi: Float,
        val ydpi: Float
    ) {
        override fun toString(): String {
            return """
                Device Type: $deviceType
                Screen Size: $screenSizeCategory
                Screen Density: $screenDensity
                Diagonal: ${"%.1f".format(screenDiagonalInches)}"
                Resolution: ${screenWidthPixels}x${screenHeightPixels}
                DPI: $densityDpi
            """.trimIndent()
        }
    }
    
    companion object {
        /**
         * Quick utility method to check if device is tablet
         */
        fun isTablet(context: Context): Boolean {
            return DeviceTypeDetector(context).getDeviceType() == DeviceType.TABLET
        }
        
        /**
         * Quick utility method to check if device is phone
         */
        fun isPhone(context: Context): Boolean {
            return DeviceTypeDetector(context).getDeviceType() == DeviceType.PHONE
        }
    }
}
