package com.example.wms_scan.utils

import android.util.Base64

class ZatcaQRCodeGeneration {

    class Builder {

        val CARTON_NO = "1"
        val CARTON_CODE = "2"
        val ITEM_CODE = "3"
        val PALLET_NO = "4"
        val PALLET_NAME = "5"
        val ANALYTICAL_NO = "6"
        val CARTON_SNO = "7"
        val TOT_CARTON = "8"

        var cartonNo: String = ""
        var cartonCode: String = ""
        var itemCode: String = ""
        var paletteNo: String = ""
        var paletteName: String = ""
        var analyticalNo: String = ""
        var cartonSNo: String = ""
        var totCarton: String = ""

        fun cartonNo(value: String?) = apply {
            if (value != null) {
                this.cartonNo = value
            }
        }

        fun cartonCode(value: String?) = apply {
            if (value != null) {
                this.cartonCode = value
            }
        }

        fun itemCode(value: String?) = apply {
            if (value != null) {
                this.itemCode = value
            }
        }

        fun paletteNo(value: String?) = apply {
            if (value != null) {
                this.paletteNo = value
            }
        }

        fun paletteName(value: String?) = apply {
            if (value != null) {
                this.paletteName = value
            }
        }

        fun analyticalNo(value: String?) = apply {
            if (value != null) {
                this.analyticalNo = value
            }
        }

        fun cartonSNo(value: String?) = apply {
            if (value != null) {
                this.cartonSNo = value
            }
        }

        fun totCarton(value: String?) = apply {
            if (value != null) {
                this.totCarton = value
            }
        }

        private fun convertTagsAndLengthToHexValues(
            tag: String,
            length: String,
            value: String
        ): ByteArray {
            return byteArrayOf(tag.toByte(), length.toByte()).plus(value.toByteArray())
        }

        fun getBase64(): String {

            val cartonNoTV = convertTagsAndLengthToHexValues(
                CARTON_NO,
                cartonNo.toByteArray().size.toString(),
                cartonNo
            )

            val cartonCodeTV = convertTagsAndLengthToHexValues(
                CARTON_CODE,
                cartonCode.toByteArray().size.toString(),
                cartonCode
            )

            val itemCodeTV = convertTagsAndLengthToHexValues(
                ITEM_CODE,
                itemCode.toByteArray().size.toString(),
                itemCode
            )

            val palletNoTV = convertTagsAndLengthToHexValues(
                PALLET_NO,
                paletteNo.toByteArray().size.toString(),
                paletteNo
            )

            val palletNameTV = convertTagsAndLengthToHexValues(
                PALLET_NAME,
                paletteName.toByteArray().size.toString(),
                paletteName
            )

            val analyticalNoTV = convertTagsAndLengthToHexValues(
                ANALYTICAL_NO,
                analyticalNo.toByteArray().size.toString(),
                analyticalNo
            )

            val cartonSNoTV = convertTagsAndLengthToHexValues(
                CARTON_SNO,
                cartonSNo.toByteArray().size.toString(),
                cartonSNo
            )

            val totCarton = convertTagsAndLengthToHexValues(
                TOT_CARTON,
                totCarton.toByteArray().size.toString(),
                totCarton
            )

            var tlvs = cartonNoTV + cartonCodeTV + itemCodeTV + palletNoTV + palletNameTV + analyticalNoTV + cartonSNoTV+ totCarton
            var text = Base64.encodeToString(tlvs, Base64.DEFAULT)
            return text.replace("\n", "")

        }
    }

}