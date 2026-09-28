package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.PlanStatus
import com.example.data.model.StudyPlanItem
import com.example.data.model.StudyType
import java.io.File
import java.io.FileOutputStream

data class SubjectBreakdown(
    val subject: String,
    val totalMinutes: Int,
    val konkurMinutes: Int,
    val finalMinutes: Int,
    val totalTests: Int,
    val completedParts: Int,
    val partialParts: Int,
    val notDoneParts: Int
)

data class WeeklyReportSummary(
    val startDateIso: String,
    val endDateIso: String,
    val startDateShamsi: String,
    val endDateShamsi: String,
    val totalStudyMinutes: Int,
    val dailyAverageMinutes: Int,
    val totalKonkurMinutes: Int,
    val totalFinalMinutes: Int,
    val totalTests: Int,
    val completedPartsCount: Int,
    val partialPartsCount: Int,
    val notDonePartsCount: Int,
    val subjectBreakdowns: List<SubjectBreakdown>,
    val uncompletedItems: List<StudyPlanItem>
)

object PdfReportGenerator {

    fun generateAndShareWeeklyReportPdf(
        context: Context,
        report: WeeklyReportSummary,
        usePersianDigits: Boolean = true
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 at 72dpi
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        drawPdfContent(canvas, report, usePersianDigits)

        pdfDocument.finishPage(page)

        return try {
            val file = File(context.cacheDir, "weekly_report_${report.startDateIso}.pdf")
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()

            // Launch share intent
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "گزارش هفتگی مطالعه کنکور - CONTINUE")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "ارسال یا ذخیره گزارش هفتگی")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)

            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    private fun drawPdfContent(
        canvas: Canvas,
        report: WeeklyReportSummary,
        usePersianDigits: Boolean
    ) {
        val width = 595f
        val height = 842f

        val bgPaint = Paint().apply { color = Color.parseColor("#0C0E14") }
        canvas.drawRect(0f, 0f, width, height, bgPaint)

        // Header Background Bar
        val headerBarPaint = Paint().apply { color = Color.parseColor("#141926") }
        canvas.drawRect(0f, 0f, width, 110f, headerBarPaint)

        val accentLinePaint = Paint().apply {
            color = Color.parseColor("#F59E0B")
            strokeWidth = 3f
        }
        canvas.drawLine(0f, 110f, width, 110f, accentLinePaint)

        val textPaint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            textAlign = Paint.Align.RIGHT
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
        }

        // Title
        canvas.drawText("گزارش تحلیلی هفتگی مطالعه — CONTINUE", width - 36f, 44f, textPaint)

        val subTextPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#94A3B8")
            textAlign = Paint.Align.RIGHT
            textSize = 12f
        }
        val dateRangeText = "هفته: ${formatDigits(report.startDateShamsi, usePersianDigits)}  الی  ${formatDigits(report.endDateShamsi, usePersianDigits)}"
        canvas.drawText(dateRangeText, width - 36f, 68f, subTextPaint)
        canvas.drawText("تفکیک دقیق ساعت مطالعه کنکوری، نهایی و وضعیت پارت‌ها", width - 36f, 88f, subTextPaint)

        // 1. Overall Stats Cards (Row of 4 KPI boxes)
        val kpiY = 130f
        val kpiHeight = 72f
        val kpiGap = 10f
        val kpiWidth = (width - 72f - (kpiGap * 3)) / 4f

        drawKpiBox(
            canvas,
            x = 36f + (kpiWidth + kpiGap) * 3,
            y = kpiY,
            w = kpiWidth,
            h = kpiHeight,
            title = "کل مطالعه هفته",
            value = formatDuration(report.totalStudyMinutes, usePersianDigits),
            accentColor = Color.parseColor("#F59E0B")
        )

        drawKpiBox(
            canvas,
            x = 36f + (kpiWidth + kpiGap) * 2,
            y = kpiY,
            w = kpiWidth,
            h = kpiHeight,
            title = "میانگین روزانه",
            value = formatDuration(report.dailyAverageMinutes, usePersianDigits),
            accentColor = Color.parseColor("#10B981")
        )

        drawKpiBox(
            canvas,
            x = 36f + (kpiWidth + kpiGap) * 1,
            y = kpiY,
            w = kpiWidth,
            h = kpiHeight,
            title = "مطالعه کنکوری",
            value = formatDuration(report.totalKonkurMinutes, usePersianDigits),
            accentColor = Color.parseColor("#38BDF8")
        )

        drawKpiBox(
            canvas,
            x = 36f,
            y = kpiY,
            w = kpiWidth,
            h = kpiHeight,
            title = "مطالعه نهایی",
            value = formatDuration(report.totalFinalMinutes, usePersianDigits),
            accentColor = Color.parseColor("#A855F7")
        )

        // 2. Parts Summary Pill Bar
        val partsY = 216f
        val partsBgPaint = Paint().apply { color = Color.parseColor("#151A24") }
        val partsRect = RectF(36f, partsY, width - 36f, partsY + 44f)
        canvas.drawRoundRect(partsRect, 8f, 8f, partsBgPaint)

        val partsLabelPaint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            textSize = 12f
            textAlign = Paint.Align.RIGHT
            typeface = Typeface.DEFAULT_BOLD
        }

        val totalTestsText = "تست‌های کل هفته: ${formatDigits(report.totalTests, usePersianDigits)}"
        val completedText = "انجام‌شده: ${formatDigits(report.completedPartsCount, usePersianDigits)}"
        val partialText = "ناقص: ${formatDigits(report.partialPartsCount, usePersianDigits)}"
        val notDoneText = "انجام‌نشده: ${formatDigits(report.notDonePartsCount, usePersianDigits)}"

        canvas.drawText(totalTestsText, width - 48f, partsY + 26f, partsLabelPaint)

        val partsValuePaint = Paint().apply {
            isAntiAlias = true
            textSize = 11f
            textAlign = Paint.Align.RIGHT
        }
        partsValuePaint.color = Color.parseColor("#10B981")
        canvas.drawText(completedText, width - 200f, partsY + 26f, partsValuePaint)

        partsValuePaint.color = Color.parseColor("#F59E0B")
        canvas.drawText(partialText, width - 310f, partsY + 26f, partsValuePaint)

        partsValuePaint.color = Color.parseColor("#F87171")
        canvas.drawText(notDoneText, width - 420f, partsY + 26f, partsValuePaint)

        // 3. Subject Breakdown Table Header
        val tableStartY = 280f
        val tableHeaderPaint = Paint().apply { color = Color.parseColor("#1E2536") }
        val tableHeaderRect = RectF(36f, tableStartY, width - 36f, tableStartY + 28f)
        canvas.drawRoundRect(tableHeaderRect, 6f, 6f, tableHeaderPaint)

        val thPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#94A3B8")
            textSize = 11f
            textAlign = Paint.Align.RIGHT
            typeface = Typeface.DEFAULT_BOLD
        }

        canvas.drawText("درس", width - 48f, tableStartY + 18f, thPaint)
        canvas.drawText("کل مطالعه", width - 150f, tableStartY + 18f, thPaint)
        canvas.drawText("کنکوری", width - 230f, tableStartY + 18f, thPaint)
        canvas.drawText("نهایی", width - 300f, tableStartY + 18f, thPaint)
        canvas.drawText("تست", width - 370f, tableStartY + 18f, thPaint)
        canvas.drawText("وضعیت پارت‌ها", width - 430f, tableStartY + 18f, thPaint)

        // Rows
        var currentY = tableStartY + 34f
        val rowPaint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            textSize = 11f
            textAlign = Paint.Align.RIGHT
        }
        val rowBgPaint = Paint().apply { color = Color.parseColor("#121620") }
        val linePaint = Paint().apply {
            color = Color.parseColor("#1C2333")
            strokeWidth = 1f
        }

        report.subjectBreakdowns.forEachIndexed { index, item ->
            if (index % 2 == 0) {
                canvas.drawRect(36f, currentY - 6f, width - 36f, currentY + 18f, rowBgPaint)
            }
            rowPaint.color = Color.WHITE
            rowPaint.typeface = Typeface.DEFAULT_BOLD
            canvas.drawText(item.subject, width - 48f, currentY + 12f, rowPaint)

            rowPaint.color = Color.parseColor("#F59E0B")
            canvas.drawText(formatDuration(item.totalMinutes, usePersianDigits), width - 150f, currentY + 12f, rowPaint)

            rowPaint.color = Color.parseColor("#38BDF8")
            canvas.drawText(formatDuration(item.konkurMinutes, usePersianDigits), width - 230f, currentY + 12f, rowPaint)

            rowPaint.color = Color.parseColor("#C084FC")
            canvas.drawText(formatDuration(item.finalMinutes, usePersianDigits), width - 300f, currentY + 12f, rowPaint)

            rowPaint.color = Color.parseColor("#E2E8F0")
            rowPaint.typeface = Typeface.DEFAULT
            canvas.drawText(formatDigits(item.totalTests, usePersianDigits), width - 370f, currentY + 12f, rowPaint)

            val partStatusSummary = "✓${formatDigits(item.completedParts, usePersianDigits)}  !${formatDigits(item.partialParts, usePersianDigits)}  ✕${formatDigits(item.notDoneParts, usePersianDigits)}"
            canvas.drawText(partStatusSummary, width - 430f, currentY + 12f, rowPaint)

            canvas.drawLine(36f, currentY + 20f, width - 36f, currentY + 20f, linePaint)
            currentY += 26f
        }

        // 4. Section: Uncompleted & Incomplete Parts Pathology
        currentY += 16f
        val secHeaderPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#FCA5A5")
            textSize = 13f
            textAlign = Paint.Align.RIGHT
            typeface = Typeface.DEFAULT_BOLD
        }
        canvas.drawText("آسیب‌شناسی پارت‌های ناقص و انجام‌نشده:", width - 36f, currentY, secHeaderPaint)

        currentY += 14f
        val itemPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#CBD5E1")
            textSize = 10f
            textAlign = Paint.Align.RIGHT
        }

        if (report.uncompletedItems.isEmpty()) {
            canvas.drawText("خوشبختانه تمام پارت‌های برنامه‌ریزی‌شده این هفته به طور کامل اجرا شدند. استمرار فوق‌العاده!", width - 36f, currentY + 14f, itemPaint)
        } else {
            report.uncompletedItems.take(7).forEach { planItem ->
                val typeLabel = if (planItem.studyType == StudyType.FINAL.code) "نهایی" else "کنکوری"
                val statusLabel = if (planItem.status == PlanStatus.PARTIAL.code) "ناقص" else "انجام‌نشده"
                val reasonText = if (!planItem.reason.isNullOrBlank()) " | علت: ${planItem.reason}" else ""
                val line = "• ${planItem.subject} (${typeLabel}): ${planItem.title} [${statusLabel}$reasonText]"
                canvas.drawText(line, width - 36f, currentY + 14f, itemPaint)
                currentY += 18f
            }
        }

        // Footer with motto
        val footerY = height - 36f
        val footerPaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#64748B")
            textSize = 11f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("«هدف، یک روز عالی نیست؛ هدف، ادامه دادن است.» — CONTINUE Study Engine", width / 2f, footerY, footerPaint)
    }

    private fun drawKpiBox(
        canvas: Canvas,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        title: String,
        value: String,
        accentColor: Int
    ) {
        val boxPaint = Paint().apply { color = Color.parseColor("#141924") }
        val rect = RectF(x, y, x + w, y + h)
        canvas.drawRoundRect(rect, 10f, 10f, boxPaint)

        val borderPaint = Paint().apply {
            color = Color.parseColor("#263147")
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        canvas.drawRoundRect(rect, 10f, 10f, borderPaint)

        // Accent top edge line
        val accentPaint = Paint().apply {
            color = accentColor
            strokeWidth = 3f
        }
        canvas.drawLine(x + 12f, y, x + w - 12f, y, accentPaint)

        val titlePaint = Paint().apply {
            isAntiAlias = true
            color = Color.parseColor("#94A3B8")
            textSize = 10f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(title, x + w / 2f, y + 26f, titlePaint)

        val valPaint = Paint().apply {
            isAntiAlias = true
            color = Color.WHITE
            textSize = 15f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }
        canvas.drawText(value, x + w / 2f, y + 52f, valPaint)
    }

    private fun formatDuration(minutes: Int, usePersianDigits: Boolean): String {
        val h = minutes / 60
        val m = minutes % 60
        val text = "${h}h ${m}m"
        return formatDigits(text, usePersianDigits)
    }

    private fun formatDigits(value: Any, usePersianDigits: Boolean): String {
        return if (usePersianDigits) JalaliDate.toPersianDigits(value) else value.toString()
    }
}
