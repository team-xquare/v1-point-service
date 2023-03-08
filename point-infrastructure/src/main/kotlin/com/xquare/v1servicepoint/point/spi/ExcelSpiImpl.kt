package com.xquare.v1servicepoint.point.spi

import com.xquare.v1servicepoint.point.exception.UserNotFoundException
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
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
        val attributes = listOf("학번", "이름", "상점", "벌점", "상점내역", "벌점내역", "교육 단계")
        val pointStatus = pointStatusSpi.findAll()
        val userPointStatus = userSpi.getUserInfo(pointStatus.map { it.userId })
        val goodPointHistory = pointHistorySpi.findAllByIdAndType(userPointStatus.map { it.id }, true)
        val badPointHistory = pointHistorySpi.findAllByIdAndType(userPointStatus.map { it.id }, false)
        val userData: List<List<String>> = userPointStatus.map { user ->
            val userStatus = pointStatus.find { it.userId == user.id }
                ?: throw UserNotFoundException(UserNotFoundException.USER_ID_NOT_FOUND)

            val goodPointHistoryString = goodPointHistory.joinToString(separator = "") { pointStatus ->
                "{${pointStatus.date}} ${pointStatus.reason} (${pointStatus.point}점)\n"
            }

            val badPointHistoryString = badPointHistory.joinToString(separator = "") { pointStatus ->
                "{${pointStatus.date}} ${pointStatus.reason} (${pointStatus.point}점)\n"
            }

            listOf(
                user.grade.toString() + user.classNum.toString() + user.num.toString().padStart(2, '0'),
                user.name,
                userStatus.goodPoint.toString(),
                userStatus.badPoint.toString(),
                goodPointHistoryString.replace(Regex("[\\[\\]]"), ""),
                badPointHistoryString.replace(Regex("[\\[\\]]"), ""),
            )
        }.sortedBy { it[1] }

        val createExcelSheet = createExcelSheet(attributes, userData)
        val workbook: Workbook = WorkbookFactory.create(createExcelSheet.inputStream())
        val outputStream = ByteArrayOutputStream()
        workbook.write(outputStream)
        return outputStream.toByteArray()
    }

    private fun createExcelSheet(
        attributes: List<String>,
        datasList: List<List<Any>>,
    ): ByteArray {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet()

        val headerRow = sheet.createRow(0)
        insertDatasAtRow(headerRow, attributes, getHeaderCellStyle(workbook))

        datasList.forEachIndexed { idx, datas ->
            val row = sheet.createRow(idx + 1)
            insertDatasAtRow(row, datas, getDefaultCellStyle(workbook))
        }

        ByteArrayOutputStream().use { stream ->
            workbook.write(stream)
            return stream.toByteArray()
        }
    }

    private fun insertDatasAtRow(
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
                alignment = HorizontalAlignment.LEFT.ordinal.toShort()
                verticalAlignment = VerticalAlignment.CENTER.ordinal.toShort()
            }

    private fun getDefaultCellStyle(workbook: XSSFWorkbook): XSSFCellStyle =
        workbook.createCellStyle()
            .apply {
                alignment = HorizontalAlignment.LEFT.ordinal.toShort()
                verticalAlignment = VerticalAlignment.CENTER.ordinal.toShort()
            }
}
