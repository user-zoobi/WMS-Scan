package com.example.wms_scan.utils

import com.pdfview.PDFView
import java.io.File

object PDFView {
    fun fromFile(file: File): com.example.wms_scan.utils.PDFView {
        val mfile = file
        return this
    }

    fun fromFile(filePath: String): com.example.wms_scan.utils.PDFView {
        val mfile = File(filePath)
        return this
    }
}