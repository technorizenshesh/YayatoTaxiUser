package com.yayatotaxi.utils

import com.google.android.gms.maps.model.LatLng

interface LatLngInterpolator {

    fun interpolate(fraction: Float, a: LatLng?, b: LatLng?): LatLng?

    companion object {
        class Spherical : LatLngInterpolator {
            /* From github.com/googlemaps/android-maps-utils */

            private fun computeAngleBetween(
                fromLat: Double,
                fromLng: Double,
                toLat: Double,
                toLng: Double
            ): Double {
                // Haversine's formula
                val dLat = fromLat - toLat
                val dLng = fromLng - toLng
                return 2 * StrictMath.asin(
                    StrictMath.sqrt(
                        StrictMath.pow(StrictMath.sin(dLat / 2), 2.0) +
                                StrictMath.cos(fromLat) * StrictMath.cos(toLat) * StrictMath.pow(
                            StrictMath.sin(dLng / 2),
                            2.0
                        )
                    )
                )
            }

            override fun interpolate(fraction: Float, from: LatLng?, to: LatLng?): LatLng? {
                // http://en.wikipedia.org/wiki/Slerp
                val fromLat = StrictMath.toRadians(from!!.latitude)
                val fromLng = StrictMath.toRadians(from.longitude)
                val toLat = StrictMath.toRadians(to!!.latitude)
                val toLng = StrictMath.toRadians(to.longitude)
                val cosFromLat = StrictMath.cos(fromLat)
                val cosToLat = StrictMath.cos(toLat)
                // Computes Spherical interpolation coefficients.
                val angle = computeAngleBetween(fromLat, fromLng, toLat, toLng)
                val sinAngle = StrictMath.sin(angle)
                if (sinAngle < 1E-6) {
                    return from
                }
                val a = StrictMath.sin((1 - fraction) * angle) / sinAngle
                val b = StrictMath.sin(fraction * angle) / sinAngle
                // Converts from polar to vector and interpolate.
                val x = a * cosFromLat * StrictMath.cos(fromLng) + b * cosToLat * StrictMath.cos(toLng)
                val y = a * cosFromLat * StrictMath.sin(fromLng) + b * cosToLat * StrictMath.sin(toLng)
                val z = a * StrictMath.sin(fromLat) + b * StrictMath.sin(toLat)
                // Converts interpolated vector back to polar.
                val lat = StrictMath.atan2(z, StrictMath.sqrt(x * x + y * y))
                val lng = StrictMath.atan2(y, x)
                return LatLng(StrictMath.toDegrees(lat), StrictMath.toDegrees(lng))
            }

        }
    }

}