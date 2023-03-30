package com.xquare.v1servicepoint.point.spi

import com.xquare.v1servicepoint.point.exception.UserNotFoundException
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream

@Component
class ExcelSpiImpl(
    private val userSpi: UserSpi,
    private val pointStatusSpi: PointStatusSpi,
    private val pointHistorySpi: PointHistorySpi,
) : ExcelSpi {

    override suspend fun writeUserPointHistoryExcelFile(): ByteArray {
        val attributes = listOf("학번", "이름", "상점", "벌점", "상점내역", "벌점내역", "교육 단계", "교육 필요 여부")
        val pointStatus = pointStatusSpi.findAll()
        val userPointStatus = userSpi.getStudent()
        val goodPointHistories = pointHistorySpi.findAllByType(true)
        val badPointHistories = pointHistorySpi.findAllByType(false)
        val userData: List<List<String>> = userPointStatus.map { user ->
            val userStatus = pointStatus.find { it.userId == user.id }
                ?: throw UserNotFoundException(UserNotFoundException.USER_ID_NOT_FOUND)

            val goodPointHistory = goodPointHistories.filter { it.userId == user.id }
            val goodPointHistoryString = goodPointHistory.joinToString(separator = "") { pointStatus ->
                "[${pointStatus.date}] ${pointStatus.reason} (${pointStatus.point}점)\n"
            }

            val badPointHistory = badPointHistories.filter { it.userId == user.id }
            val badPointHistoryString = badPointHistory.joinToString(separator = "") { pointStatus ->
                "[${pointStatus.date}] ${pointStatus.reason} (${pointStatus.point}점)\n"
            }

            listOf(
                user.grade.toString() + user.classNum.toString() + user.num.toString().padStart(2, '0'),
                user.name,
                userStatus.goodPoint.toString(),
                userStatus.badPoint.toString(),
                goodPointHistoryString.replace(Regex("[\\[\\]]"), ""),
                badPointHistoryString.replace(Regex("[\\[\\]]"), ""),
                userStatus.penaltyLevel.toString(),
                convertPenaltyRequiredToString(userStatus.isPenaltyRequired),
            )
        }.sortedBy { it[0] } // 학번순으로 정렬

        return createExcelSheet(attributes, userData, "학생 상벌점 내역")
    }

    private fun convertPenaltyRequiredToString(isPenaltyRequired: Boolean): String {
        return if (isPenaltyRequired) "필요" else "필요 없음"
    }

    private fun createExcelSheet(
        attributes: List<String>,
        dataList: List<List<Any>>,
        sheetName: String,
    ): ByteArray {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet(sheetName)

        val headerRow = sheet.createRow(0)
        insertDataListAtRow(headerRow, attributes, getHeaderCellStyle(workbook))

        dataList.forEachIndexed { idx, dataLists ->
            val row = sheet.createRow(idx + 1)
            sheet.autoSizeColumn(idx + 1)
            insertDataListAtRow(row, dataLists, getDefaultCellStyle(workbook))
        }
        
        sheet.setColumnWidth(1, 7 * 256)
        sheet.setColumnWidth(2, 5 * 256)
        sheet.setColumnWidth(3, 5 * 256)
        sheet.setColumnWidth(4, 45 * 256)
        sheet.setColumnWidth(5, 45 * 256)
        sheet.setColumnWidth(6, 10 * 256)

        ByteArrayOutputStream().use { stream ->
            workbook.write(stream)
            return stream.toByteArray()
        }
    }

    private fun insertDataListAtRow(
        headerRow: XSSFRow,
        attributes: List<Any>,
        style: XSSFCellStyle,
    ) {
        attributes.forEachIndexed { j, text ->
            val cell = headerRow.createCell(j)
            cell.setCellValue(text.toString())
            cell.cellStyle = style
        }
    }

    private fun getHeaderCellStyle(workbook: XSSFWorkbook): XSSFCellStyle =
        workbook.createCellStyle()
            .apply {
                fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
                fillPattern = CellStyle.SOLID_FOREGROUND
                alignment = CellStyle.ALIGN_CENTER
                verticalAlignment = VerticalAlignment.CENTER.ordinal.toShort()
            }

    private fun getDefaultCellStyle(workbook: XSSFWorkbook): XSSFCellStyle =
        workbook.createCellStyle()
            .apply {
                alignment = CellStyle.ALIGN_CENTER
                verticalAlignment = VerticalAlignment.CENTER.ordinal.toShort()
                wrapText = true
            }
}
