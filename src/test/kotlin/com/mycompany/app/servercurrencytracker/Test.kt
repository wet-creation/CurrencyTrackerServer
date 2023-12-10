package com.mycompany.app.servercurrencytracker

import com.mycompany.app.servercurrencytracker.receiving.dto.Latest
import com.mycompany.app.servercurrencytracker.restapi.models.toRate

fun main() {
    val ratesMap = mapOf(
        "AED" to 3.6724,
        "AFN" to 69.290389,
        "ALL" to 94.537512,
        "AMD" to 403.768708,
        "ANG" to 1.803778,
        "AOA" to 832.5,
        "ARS" to 363.905,
        "AUD" to 1.519058,
        "AWG" to 1.8,
        "AZN" to 1.7,
        "BAM" to 1.81538,
        "BBD" to 2.0,
        "BDT" to 110.094194,
        "BGN" to 1.81814,
        "BHD" to 0.377315,
        "BIF" to 2847.820659,
        "BMD" to 1.0,
        "BND" to 1.340047,
        "BOB" to 6.916044,
        "BRL" to 4.9314,
        "BSD" to 1.0,
        "BTC" to 2.2776714E-5,
        "BTN" to 83.4543,
        "BWP" to 13.635309,
        "BYN" to 3.297443,
        "BZD" to 2.01745,
        "CAD" to 1.35935,
        "CDF" to 2732.363063,
        "CHF" to 0.880061,
        "CLF" to 0.031611,
        "CLP" to 872.25136,
        "CNH" to 7.18575,
        "CNY" to 7.1626,
        "COP" to 3991.3677,
        "CRC" to 528.241037,
        "CUC" to 1.0,
        "CUP" to 25.75,
        "CVE" to 102.348369,
        "CZK" to 22.6436,
        "DJF" to 178.205776,
        "DKK" to 6.9266,
        "DOP" to 57.019535,
        "DZD" to 134.701,
        "EGP" to 30.926533,
        "ERN" to 15.0,
        "ETB" to 55.820027,
        "EUR" to 0.9282,
        "FJD" to 2.2377,
        "FKP" to 0.79694,
        "GBP" to 0.79694,
        "GEL" to 2.665,
        "GGP" to 0.79694,
        "GHS" to 12.040655,
        "GIP" to 0.79694,
        "GMD" to 67.35,
        "GNF" to 8602.587926,
        "GTQ" to 7.834037,
        "GYD" to 209.560425,
        "HKD" to 7.8103,
        "HNL" to 24.684642,
        "HRK" to 7.00168,
        "HTG" to 132.380387,
        "HUF" to 354.573872,
        "IDR" to 15573.0,
        "ILS" to 3.702139,
        "IMP" to 0.79694,
        "INR" to 83.430353,
        "IQD" to 1310.160033,
        "IRR" to 42275.0,
        "ISK" to 138.998435,
        "JEP" to 0.79694,
        "JMD" to 155.771099,
        "JOD" to 0.7095,
        "JPY" to 144.864,
        "KES" to 154.842852,
        "KGS" to 89.32,
        "KHR" to 4118.428978,
        "KMF" to 457.849847,
        "KPW" to 900.0,
        "KRW" to 1317.66,
        "KWD" to 0.308535,
        "KYD" to 0.834084,
        "KZT" to 459.209839,
        "LAK" to 20741.259245,
        "LBP" to 15043.135762,
        "LKR" to 327.591776,
        "LRD" to 188.250075,
        "LSL" to 18.929132,
        "LYD" to 4.837796,
        "MAD" to 10.138859,
        "MDL" to 17.800158,
        "MGA" to 4588.13064,
        "MKD" to 57.098436,
        "MMK" to 2101.817731,
        "MNT" to 3450.0,
        "MOP" to 8.0543,
        "MRU" to 39.543324,
        "MUR" to 44.299068,
        "MVR" to 15.35,
        "MWK" to 1684.784169,
        "MXN" to 17.348,
        "MYR" to 4.665,
        "MZN" to 63.899991,
        "NAD" to 18.98,
        "NGN" to 790.179934,
        "NIO" to 36.626909,
        "NOK" to 10.906,
        "NPR" to 133.526702,
        "NZD" to 1.631322,
        "OMR" to 0.385,
        "PAB" to 1.0,
        "PEN" to 3.760895,
        "PGK" to 3.731378,
        "PHP" to 55.553504,
        "PKR" to 283.993493,
        "PLN" to 4.027382,
        "PYG" to 7357.877923,
        "QAR" to 3.650161,
        "RON" to 4.6158,
        "RSD" to 108.75947,
        "RUB" to 92.154999,
        "RWF" to 1259.385985,
        "SAR" to 3.750483,
        "SBD" to 8.475946,
        "SCR" to 13.181,
        "SDG" to 601.0,
        "SEK" to 10.4669,
        "SGD" to 1.341,
        "SHP" to 0.79694,
        "SLL" to 20969.5,
        "SOS" to 571.959139,
        "SRD" to 37.539,
        "SSP" to 130.26,
        "STD" to 22281.8,
        "STN" to 22.740985,
        "SVC" to 8.757599,
        "SYP" to 2512.53,
        "SZL" to 18.921428,
        "THB" to 35.402608,
        "TJS" to 10.934422,
        "TMT" to 3.5,
        "TND" to 3.1305,
        "TOP" to 2.359259,
        "TRY" to 28.927399,
        "TTD" to 6.798162,
        "TWD" to 31.4245,
        "TZS" to 2507.163165,
        "UAH" to 36.763636,
        "UGX" to 3773.134597,
        "USD" to 1.0,
        "UYU" to 39.145473,
        "UZS" to 12322.885288,
        "VES" to 35.557328,
        "VND" to 24230.340863,
        "VUV" to 118.722,
        "WST" to 2.8,
        "XAF" to 608.859287,
        "XAG" to 0.04345748,
        "XAU" to 4.9887E-4,
        "XCD" to 2.70255,
        "XDR" to 0.753237,
        "XOF" to 608.859287,
        "XPD" to 0.00105661,
        "XPF" to 110.763723,
        "XPT" to 0.0010887,
        "YER" to 250.324978,
        "ZAR" to 18.96191,
        "ZMW" to 24.145356,
        "ZWL" to 322.0
    )

    val latest = Latest(base="USD", disclaimer="Usage subject to terms: https://openexchangerates.org/terms"," license=https://openexchangerates.org/license", ratesMap, timestamp=1702224024)

    val rates = latest.toRate()
    println(rates.toString())
}