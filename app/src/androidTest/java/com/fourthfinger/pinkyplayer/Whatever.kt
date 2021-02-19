package com.fourthfinger.pinkyplayer

import android.util.Rational
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Whatever {

    @Test fun iterativeFunctionLimit(){
        var i = Rational(1, 1)
        val ten = Rational(1, 1)

        for(j in 0..100){
            i = ((ten*i)-i)/ten
        }
        print("$i")
    }

}

private operator fun Rational.div(i: Rational): Rational {
    return this*Rational(i.denominator, i.numerator)
}

private operator fun Rational.minus(i: Rational): Rational {
    return Rational((numerator*i.denominator)-(i.numerator*denominator), denominator*i.denominator)
}

private operator fun Rational.times(i: Rational): Rational {
    return Rational(numerator*i.numerator, denominator*i.denominator)
}
