package com.dev.ashish.weather

import com.dev.ashish.weather.utils.WeatherAppUtils
import org.junit.Test

import org.junit.Assert.*

/**
 * WeatherAppUtilsUnitTest  unit test, which will execute on the development machine (host).
 */
class WeatherAppUtilsUnitTest {
    @Test
    fun weatherUtils_ValidateFahrenheitToCelsius() {
        val fahrenheitVal1 = 300.12
        assertEquals(148.95, WeatherAppUtils.convertFahrenheitToCelsius(fahrenheitVal1),0.1)

        val fahrenheitVal2 = 200.00
        assertEquals(93.33, WeatherAppUtils.convertFahrenheitToCelsius(fahrenheitVal2),0.1)

        val fahrenheitVal3 = 95.12
        assertEquals(35.06, WeatherAppUtils.convertFahrenheitToCelsius(fahrenheitVal3),0.1)
    }
}